#define _DEFAULT_SOURCE
#include <unistd.h>
#include <stdbool.h>
#include <string.h>

#include "ipc.h"
#include "io.h"

static pipe_fd* get_struct(IOStruct *handle, local_id from, local_id to) {
	if (handle == NULL) return NULL;
	if((from < 0 && from > handle->proc_num) || (to < 0 && to > handle->proc_num)) return NULL;
	return &handle->fd_table[from * handle->proc_num + to];
}

int send(void * self, local_id dst, const Message * msg) {
	IOStruct *h = (IOStruct*)self;
	if(dst == h->src_pid) return 0;
	pipe_fd *c = get_struct(h, h->src_pid, dst);

	if(write(c->write_fd, msg, sizeof(msg->s_header) + msg->s_header.s_payload_len) < 0) {
		return -1;
	}
	return 0;
}

int send_multicast(void * self, const Message * msg) {
	IOStruct *h = (IOStruct*)self;
	for(local_id i = 0; i < h->proc_num; i++) {
		if(send(self, i, msg) != 0) {
			return -1;
		}
	}
	return 0;
}

int receive(void * self, local_id from, Message * msg) {
	IOStruct *h = (IOStruct*)self;
	if(from == h->src_pid) return 0;
	pipe_fd *c = get_struct(h, from, h->src_pid);

	while(true) {
		int n = read(c->read_fd, msg, sizeof msg->s_header);
		if(n != -1) {
			n = read(c->read_fd, msg->s_payload, msg->s_header.s_payload_len);
			return 0;
		}
	}
}

int receive_any(void * self, Message * msg) {
	IOStruct *h = (IOStruct*)self;
	while(true) {
		for(local_id i = 0; i < h->proc_num; i++) {
			if(i != h->src_pid) {
				pipe_fd *c = get_struct(h, i, h->src_pid);
				if(c == NULL) return -1;

				int bytes_count = read(c->read_fd, msg, sizeof(msg->s_header));
				if(bytes_count <= 0) continue;
				bytes_count = read(c->read_fd, msg->s_payload, msg->s_header.s_payload_len);
				if(bytes_count >= 0) return 0;
			}
		}	
	}
	return 0;
}
