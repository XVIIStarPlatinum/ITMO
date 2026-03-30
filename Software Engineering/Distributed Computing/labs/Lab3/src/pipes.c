#define _GNU_SOURCE
#include <stdio.h>
#include <getopt.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/wait.h>
#include <fcntl.h>
#include "common.h"
#include "ops.h"
#include "pipes.h"

int create_pipes(IOStruct *handle) {
	int pipes_log_fd = open(pipes_log, O_CREAT | O_WRONLY | O_TRUNC | O_APPEND | O_NONBLOCK, 0644);
	if(pipes_log_fd < 0) return 1;
	char msg[64];
	for(int32_t i = 0; i < handle->proc_num; i++) {
		for(int32_t j = 0; j < handle->proc_num; j++) {
			if(i != j) {
				int	rc = pipe2((int*)&handle->fd_table[i * handle->proc_num + j], O_NONBLOCK);
				snprintf(msg, 64, pipe_opened_msg, i, j);
				if(write(pipes_log_fd, msg, strlen(msg)) < 0 || rc < 0) return 1;
			}
		}
	}
	return 0;
}

void close_pipes(IOStruct *handle) {
	char msg[64];
	for(int i = 0; i < handle->proc_num; i++) {
		for(int j = 0; j < handle->proc_num; j++) {
			if(i != j) {
				if(i != handle->src_pid) {
					snprintf(msg, 64, pipe_closed_msg, i, j);
					close(handle->fd_table[i * handle->proc_num + j].write_fd);
				}
				if(j != handle->src_pid) {
					snprintf(msg, 64, pipe_closed_msg, i, j);
					close(handle->fd_table[i * handle->proc_num + j].read_fd);
				}
			}
		}
	}
}
