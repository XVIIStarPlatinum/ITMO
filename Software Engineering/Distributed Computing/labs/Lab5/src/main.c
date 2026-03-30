#include <getopt.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>

#include "lamport_time.h"
#include "pipes.h"
#include "util.h"

#define MAX(a, b) (a > b ? a : b)

timestamp_t global_time = 0;

timestamp_t get_lamport_time(void){
	return global_time;
}

void set_lamport_time(timestamp_t new_time){
	global_time = MAX(new_time, global_time);
}

void move_time(void){
	global_time++;
}

int child_work(IOStruct *handle, void *data) {
	if (handle->mutexl) {
		request_cs(handle);
  	}
  	char buff[4096];
  	for (int i = 1; i <= handle->src_pid * 5; i++) {
    	snprintf(buff, sizeof(buff), log_loop_operation_fmt, handle->src_pid, i, handle->src_pid * 5);
    	print(buff);
  	}
  	if (!handle->mutexl) {
    	return 0;
 	}
  	release_cs(handle);
	return 0;
}

int main(int argc, char * argv[]) {
	bool mutexl = 0;
	int proc_num = parse_args(argc, argv, &mutexl);
	if (proc_num < 0) {
		return 1;
	}
	IOStruct handle;
	int rc = create_handle(proc_num, &handle);
	if (rc) {
		return rc;
	}
	handle.mutexl = mutexl;

	rc = create_pipes(&handle);
	if (rc) {
		return rc;
	}

	int is_parent = generate_childs(&handle);
	if (is_parent != -1) {
		rc = (!is_parent) ? child(&handle, NULL) : parent(&handle);
		free(handle.fd_table);
		return rc;
	}
	return 2;
}
