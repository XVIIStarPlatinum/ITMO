#include <stdio.h>
#include "util.h"
#include "lamport_time.h"

int cur_time = -1;

int delayed[10];

int request_cs(const void * self) {
	IOStruct *handle = (IOStruct*)self;
	move_time();
	Message msg;
	create_message(&msg, CS_REQUEST, 0);
	int rc = send_multicast(handle, &msg);
	if (rc) {
		return rc;
	}
	cur_time = get_lamport_time();
	int replies = handle->proc_num - 2;
	while (replies > 0){
		rc = receive_any(handle, &msg);
		if (rc) {
			return rc;
		}
		set_lamport_time(msg.s_header.s_local_time);
		move_time();
		int16_t header = msg.s_header.s_type;
		if (header == DONE) {
			handle->done_cnt++;
		} else if (header == CS_REQUEST) {
			if (cur_time != -1 && (
    			msg.s_header.s_local_time > cur_time ||
    				(msg.s_header.s_local_time == cur_time && handle->last_msg_pid >= handle->src_pid))) {
						delayed[handle->last_msg_pid] = 1;
						continue;
			}
			move_time();
			create_message(&msg, CS_REPLY, 0);
			rc = send(handle, handle->last_msg_pid, &msg);
			if (rc) {
				return rc;
			}
		} else if (header == CS_REPLY) {
			replies--;
		}
	}
	return 0;
}

int release_cs(const void * self) {
	IOStruct *handle = (IOStruct*)self;
	move_time();
	Message msg;
	create_message(&msg, CS_REPLY, 0);
	for(int i = 1; i <= handle->proc_num - 1; i++){
		if (delayed[i]) {
			move_time();
			msg.s_header.s_local_time = get_lamport_time();
			int rc = send(handle, i, &msg);
			if (rc) {
				return rc;
			}
		}
	}
	return 0;
}
