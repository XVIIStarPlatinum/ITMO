#define _GNU_SOURCE
#include <fcntl.h>
#include <getopt.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/wait.h>
#include <unistd.h>
 
#include "common.h"
#include "io.h"
#include "ipc.h"
#include "lamport_time.h"
#include "pipes.h"
#include "util.h"
 
static int events_log_fd = -1;
 
static const char* invalid_args_msg = "Usage: %s -p N s1 s2 ... sN [--mutexl]\n";
static const char* invalid_proc_num_msg = "Error while parsing process number (should be between 2 and 10)\n";
 
static const char* open_file_log_error_msg = "Error while creating or opening log files\n";
static const char* send_multicast_error_msg = "Error while using send_multicast (non-zero result)\n";
static const char* send_multicast_error_msg_read = "Error while receiving messages with receive_multicast\n";
 
void create_message(Message *msg, MessageType type, size_t length){
	memset(msg, 0, sizeof(*msg));
		msg->s_header = (MessageHeader) {
			.s_magic = MESSAGE_MAGIC,
			.s_type = type,
			.s_local_time = get_lamport_time(),
			.s_payload_len = length
	};
}
 
int log_event(char *msg){
	if (write(events_log_fd, msg, strlen(msg)) >= 0){
		return 0;
	}
	write(STDERR_FILENO, open_file_log_error_msg, sizeof(open_file_log_error_msg));
	return 1;
}
 
static int receive_all(IOStruct *handle){
	Message msg;
	create_message(&msg, 0, 0);
	for(local_id i = 1; i < handle->proc_num - handle->done_cnt; i++){
		if (receive(handle, i, &msg)){
			write(STDERR_FILENO, send_multicast_error_msg_read, sizeof(send_multicast_error_msg));
			return 1;
		}
		set_lamport_time(msg.s_header.s_local_time);
		move_time();
	}
	return 0;
}
 
int parse_args(int argc, char *const argv[], void *data) {
	if (argc == 1) {
		write(STDERR_FILENO, invalid_args_msg, sizeof(invalid_args_msg));
        return -1;
    }
 
	struct option long_opts[] = {
		{"mutexl", no_argument, (int*)data, 1},
		{0, 0, 0, 0}
	};
 
	int proc_num = 0, opt = 0;
	char *endp = NULL;
	while ((opt = getopt_long(argc, argv, "p:", long_opts, NULL)) != -1) {
		if (opt == -1) {
            return -1;
        } else if (opt == 'p') {
            proc_num = strtoul(optarg, &endp, 10);
			if (proc_num == 0 || proc_num > 10 || *endp != '\0') {
				write(STDERR_FILENO, invalid_proc_num_msg, sizeof(invalid_proc_num_msg));
				return -1;
			}
        }
	}
	return ++proc_num;
}
 
int create_handle(int proc_num, IOStruct *handle) {
	pipe_fd *fd_table = malloc(proc_num * proc_num * sizeof(*fd_table));
	if (fd_table == NULL) {
        return 1;
    }
	memset(handle, 0, sizeof(*handle));
	handle->proc_num = proc_num;
	handle->fd_table = fd_table;
	handle->parent_pid = getpid();
	return 0;
}
 
int generate_childs(IOStruct *handle) {
	events_log_fd = open(events_log, O_CREAT | O_WRONLY | O_TRUNC | O_APPEND, 0644);
	if (events_log_fd < 0) {
		return 1;
	}
	int is_parent = 1;
	for(local_id pid = 1; pid < handle->proc_num; pid++){
		int sys_pid = fork();
		if (!sys_pid){
			is_parent = 0;
			handle->src_pid = pid;
			break;
		}
		if (sys_pid < 0) {
			return -1;
		} 
	}
	return is_parent;
}
 
int parent(IOStruct *handle) {
	close_pipes(handle);
	Message msg;
	create_message(&msg, 0, 0);
	if (receive_all(handle)) {
		return 1;
	}
	while (handle->done_cnt < handle->proc_num - 1) {
		if (receive_any(handle, &msg)) {
			return 1;
		} 
		set_lamport_time(msg.s_header.s_local_time);
		move_time();
		if (msg.s_header.s_type == DONE) {
			handle->done_cnt++;
		}
	}
	for (local_id pid = 1; pid < handle->proc_num; pid++){
		wait(NULL);
	}
	return 0;
}
 
int child(IOStruct *handle, void *data) {
  	close_pipes(handle);
  	move_time();
  	Message msg;
  	create_message(&msg, STARTED, 0);
	snprintf(msg.s_payload, MAX_PAYLOAD_LEN, log_started_fmt, get_lamport_time(), handle->src_pid, getpid(), handle->parent_pid, 0);
  	msg.s_header.s_payload_len = strlen(msg.s_payload);
 
  	if (log_event(msg.s_payload)) {
    	return 1;
  	}
	if (send_multicast(handle, &msg)){
		write(STDERR_FILENO, send_multicast_error_msg, sizeof(send_multicast_error_msg));
		return 1;
	}
	if (receive_all(handle)) {
		return 1;
	}
	char log_buff[MAX_PAYLOAD_LEN];
	snprintf(msg.s_payload, MAX_PAYLOAD_LEN, log_received_all_started_fmt, get_lamport_time(), handle->src_pid);
	if (log_event(log_buff)) {
		return 1;
	}
	int rc = child_work(handle, data);
	if (rc) {
		return rc;
	}
 
	snprintf(msg.s_payload, MAX_PAYLOAD_LEN, log_done_fmt, get_lamport_time(), handle->src_pid, 0);
	move_time();
 
	msg.s_header = (MessageHeader) {
		.s_local_time = get_lamport_time(),
		.s_payload_len = strlen(msg.s_payload),
		.s_type = DONE
	};
 
	if (log_event(msg.s_payload)) {
		return 1;
	}
	if (send_multicast(handle, &msg) < 0){
		write(STDERR_FILENO, send_multicast_error_msg, sizeof(send_multicast_error_msg));
		return 1;
	}
	while (handle->done_cnt + 2 < handle->proc_num){
		if (receive_any(handle, &msg)) {
			return 1;
		}
		set_lamport_time(msg.s_header.s_local_time);
		move_time();
		if (msg.s_header.s_type == DONE) {
			++handle->done_cnt;
		}
	}
	snprintf(msg.s_payload, MAX_PAYLOAD_LEN, log_received_all_done_fmt, get_lamport_time(), handle->src_pid);
	if (!log_event(log_buff)) {
		return 0;
	}
	return 1;
}
