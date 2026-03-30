#define _GNU_SOURCE

#include <assert.h>
#include <unistd.h>
#include <string.h>
#include <time.h>

#include "ipc.h"
#include "io.h"

static inline void ssleep(void) {
    struct timespec ts = {0, 1000L};
    nanosleep(&ts, NULL);
}

static pipe_fd *get_pipe_fd(IOStruct *handle, local_id from, local_id to){
	if ((to < 0 && to > handle->proc_num) || (from < 0 && from > handle->proc_num)) {
        return NULL;
    }
	return &handle->fd_table[from * handle->proc_num + to];
}

int send(void *self, local_id dst, const Message *msg) {
	IOStruct *h = (IOStruct*)self;
	if (dst == h->src_pid) {
        return 0;
    }
	pipe_fd *c = get_pipe_fd(h, h->src_pid, dst);
	if (c == NULL) {
        return -1;
    }
	size_t msg_size = sizeof(msg->s_header) + msg->s_header.s_payload_len;
	int rc = write(c->write_fd, msg, msg_size);
	if (rc != msg_size || rc < 0) {
        return -1;
    }
	return 0;
}

int send_multicast(void *self, const Message *msg) {
	IOStruct *h = (IOStruct*)self;
	for (local_id pid = 0; pid < h->proc_num; pid++){
		if (send(self, pid, msg) < 0) {
            return -1;
        }
	}
	return 0;
}

int receive(void *self, local_id from, Message *msg) {
	IOStruct *h = (IOStruct*)self;
	if (from == h->src_pid) {
        return 0;
    }
	pipe_fd *c = get_pipe_fd(h, from, h->src_pid);
	if (c == NULL) {
        return -1;
    }
	while (1) {
		if (read(c->read_fd, msg, sizeof(msg->s_header)) <= 0) {
			ssleep();
            continue;
		}
		if (read(c->read_fd, msg->s_payload, msg->s_header.s_payload_len) >= 0) {
			return 0;
		}
		ssleep();
	}
}

int receive_any(void *self, Message *msg) {
	IOStruct *h = (IOStruct*)self;
	while (1) {
		for (local_id pid = 0; pid < h->proc_num; pid++) {
			if (pid == h->src_pid) {
                continue;
            }
			pipe_fd *c = get_pipe_fd(h, pid, h->src_pid);
			if (c == NULL) {
                return -1;
            }
			if (read(c->read_fd, msg, sizeof(msg->s_header)) <= 0) {
				continue;
			}
			if (read(c->read_fd, msg->s_payload, msg->s_header.s_payload_len) >= 0) {
				h->last_msg_pid = pid;
				return 0;
			}
		}
		ssleep();
	}
	return 0;
}
