#define _GNU_SOURCE
#include <unistd.h>
#include "pipes.h"
#include "utils.h"

void open_pipes(pipe_fd **pipes_ptr, int fd_pipes_log, int total_processes) {
    *pipes_ptr = (pipe_fd *) calloc(total_processes * (total_processes - 1), sizeof(pipe_fd));
    pipe_fd *desc = *pipes_ptr;

    int fds[2];

    for (int i = 0; i < total_processes; i++) {
        for (int j = 0; j < total_processes; j++) {
            if (i != j) {
                pipe2(fds, O_NONBLOCK); 
                desc = get_struct(i, j);
                desc->fd_write = fds[1];
                log_pipe(0, i, j, fd_pipes_log);

                desc = get_struct(j, i);
                desc->fd_read = fds[0];
                log_pipe(0, j, i, fd_pipes_log);
            }
        }
    }
}

void close_other_pipes(local_id id, int fd_pipes_log, int total_processes) {
    pipe_fd *desc;
    for (local_id i = 0; i < total_processes; i++) {
        if (i != id) {
            for (local_id j = 0; j < total_processes; j++) {
                if (i != j) {
                    desc = get_struct(i, j);
                    close(desc->fd_write);	 
                    close(desc->fd_read);
                    log_pipe(1, i, j, fd_pipes_log);
                }
            }
        }
    }
}

void close_own_pipes(local_id id, int fd_pipes_log, int total_processes) {
    pipe_fd *desc;
    for (local_id i = 0; i < total_processes; i++) {
        if (i != id) {
            desc = get_struct(id, i);
            close(desc->fd_write);
            close(desc->fd_read);
            log_pipe(1, id, i, fd_pipes_log);
        }
    }
}
