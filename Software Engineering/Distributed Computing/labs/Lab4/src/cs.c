#include <stdio.h>
#include "io.h"
#include "ipc.h"
#include "util.h"
#include "lamport_time.h"
#include "queue.h"

Queue *queue = NULL;

int request_cs(const void * self) {
	IOStruct *handle = (IOStruct*)self;
	Message msg;
	move_time();
	create_message(&msg, CS_REQUEST, 0);
	if (queue == NULL) {
		queue_init(&queue);
	}
	queue_push(handle->src_pid, get_lamport_time(), queue);
	int rc = send_multicast(handle, &msg);
	if (rc) {
        return rc;
    }
	int replies = handle->proc_num - 2;
	while (handle->src_pid != queue->front->pid || replies > 0) {
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
            queue_push(handle->last_msg_pid, msg.s_header.s_local_time, queue);
			move_time();
			create_message(&msg, CS_REPLY, 0);
			rc = send(handle, handle->last_msg_pid, &msg);
			if (rc) {
                return rc;
            }
        } else if (header == CS_REPLY) {
            replies--;
        } else if (header == CS_RELEASE) {
            queue_pop(queue);
        }
	}
	return 0;
}

int release_cs(const void * self) {
	IOStruct *handle = (IOStruct*)self;
    queue_pop(queue);
	move_time();
    Message msg;
	create_message(&msg, CS_RELEASE, 0);
	int rc = send_multicast(handle, &msg);
	if (!rc) {
        return 0;
    }
	return rc;
}
