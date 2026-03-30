#ifndef __PIPES__H
#define __PIPES__H
#include "ipc.h"
#include "ops.h"

void open_pipes(pipe_fd **pipes_ptr, int fd_pipes_log, int total_processes);
void close_other_pipes(local_id id, int fd_pipes_log, int total_processes);
void close_own_pipes(local_id id, int fd_pipes_log, int total_processes);

#endif
