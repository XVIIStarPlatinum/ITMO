#ifndef __IFMO_DISTRIBUTED_CLASS_IO__H
#define __IFMO_DISTRIBUTED_CLASS_IO__H

#include <sys/types.h>
#include <stdbool.h>

#include "ipc.h"

typedef struct {
	int read_fd;
    int write_fd;
} pipe_fd;

typedef struct {
	local_id src_pid;
    local_id last_msg_pid;
    pid_t parent_pid;
	pipe_fd *fd_table;
	size_t proc_num;
	size_t done_cnt;
    bool mutexl;
} IOStruct;

#endif
