#ifndef _IO_H_
#define _IO_H_

#include <sys/types.h>

#include "ipc.h"

typedef struct {
	int read_fd;
	int write_fd;
} pipe_fd;

typedef struct {
	local_id src_pid;
	pipe_fd *fd_table;
	size_t proc_num;
	pid_t parent_pid;
} IOStruct;

#endif
