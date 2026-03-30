#include <stdio.h>
#include <getopt.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/wait.h>
#include <fcntl.h>

#include "io.h"
#include "ops.h"
#include "pipes.h"
#include "common.h"

#include "lamport_time.h"

static int events_log_fd = -1;

static const char* invalid_args_msg = "Usage: %s -p N s1 s2 ... sN\n";
static const char* invalid_proc_num_msg = "Error while parsing process number (should be between 2 and 10)\n";

static const char* open_file_log_error_msg = "Error while creating or opening log files\n";
static const char* send_multicast_error_msg = "Error while using send_multicast (non-zero result)\n";
static const char* send_multicast_error_msg_read = "Error while receiving messages with receive_multicast\n";

void create_message(Message *msg, MessageType type, size_t length) {
	memset(msg, 0, sizeof(*msg));
	msg->s_header = (MessageHeader) {
    	.s_magic = MESSAGE_MAGIC,
    	.s_type = type,
    	.s_local_time = get_lamport_time(),
    	.s_payload_len = length
	};
}

int log_event(char *msg) {
	if(write(events_log_fd, msg, strlen(msg)) >= 0) return 0;
	(void)write(STDERR_FILENO, open_file_log_error_msg, sizeof(open_file_log_error_msg));
	return 1;
}

static int receive_all(IOStruct *handle) {
	Message msg;
	create_message(&msg, 0, 0);
	for(local_id i = 1; i < handle->proc_num; i++) {
		if(receive(handle, i, &msg)) {
			(void)write(STDERR_FILENO, send_multicast_error_msg_read, sizeof(send_multicast_error_msg));
			return 1;
		}
		set_max_time(msg.s_header.s_local_time);
		move_time();
	}
	return 0;
}

int parse_args(int argc, char *const argv[]) {
	if (argc < 2) {
		(void)write(STDERR_FILENO, invalid_args_msg, sizeof(invalid_args_msg));
        return -1;
    }

	int proc_num = 0;
	char *endp = NULL;

	int opt = getopt(argc, argv, "p:");
	if (opt == 'p') {
		proc_num = strtoul(optarg, &endp, 10);
		if (*endp != '\0' || proc_num == 0 || proc_num > 10) {
			(void)write(STDERR_FILENO, invalid_proc_num_msg, sizeof(invalid_proc_num_msg));
			return -1;
		}
	} else if (opt == -1) {
		return -1;
	}
	return ++proc_num;
}

int create_handle(int proc_num, IOStruct *handle) {
	pipe_fd *fd_table = calloc(1, proc_num * proc_num * sizeof(*fd_table));
	if(fd_table == NULL) return 1;

	memset(handle, 0, sizeof *handle);
	handle->proc_num = proc_num;
	handle->fd_table = fd_table;
	handle->parent_pid = getpid();
	return 0;
}


int spawn_childs(IOStruct *handle) {
	events_log_fd = open(events_log, O_CREAT | O_WRONLY | O_TRUNC | O_APPEND | O_NONBLOCK, 0644);
	if(events_log_fd < 0) return 1;

	int is_parent = 1;
	for(local_id pid = 1; pid < handle->proc_num; pid++) {
		int sys_pid = fork();
		if(sys_pid < 0) return -1;
		if(sys_pid == 0) {
			is_parent = 0;
			handle->src_pid = pid;
			break;
		}
	}

	return is_parent;
}

int parent(IOStruct *handle) {
	close_pipes(handle);
	int rc = 0;
	rc = receive_all(handle);
	if(rc) return 1;

	rc = parent_work(handle);
	if(rc) return rc;

	rc = receive_all(handle);
	if(rc) return 1;

	rc = parent_at_exit(handle);
	if(rc) return 1;

	for(local_id pid = 1; pid < handle->proc_num; pid++) {
		wait(NULL);
	}
	return 0;
}

int child(IOStruct *handle, void *data) {
	close_pipes(handle);
	char log_buff[MAX_PAYLOAD_LEN];
	int rc = 0;
	move_time();

	Message msg;
	create_message(&msg, STARTED, 0);
	update_child_started_state(msg.s_payload, handle, data);
	msg.s_header.s_payload_len = strlen(msg.s_payload);

	rc = log_event(msg.s_payload);
	if(rc) return 1;

	rc = send_multicast(handle, &msg);
	if(rc) {
		(void)write(STDERR_FILENO, send_multicast_error_msg, sizeof(send_multicast_error_msg));
		return 1;
	}

	rc = receive_all(handle);
	if(rc) return 1;

	send_done_message_from_child(log_buff, handle);

	rc = log_event(log_buff);
	if(rc) return 1;

	rc = child_work(handle, data);
	if(rc) return rc;

	
update_child_done_state(msg.s_payload, handle, data);
	move_time();
	msg.s_header.s_local_time = get_lamport_time();

	msg.s_header.s_payload_len = strlen(msg.s_payload);
	msg.s_header.s_type = DONE;

	rc = log_event(msg.s_payload);
	if(rc) return 1;

	rc = send_multicast(handle, &msg);
	if(rc < 0) {
		(void)write(STDERR_FILENO, send_multicast_error_msg, sizeof(send_multicast_error_msg));
		return 1;
	}

	rc = receive_all(handle);
	if(rc) return 1;

	child_received_all_done_msg(log_buff, handle);
	rc = log_event(log_buff);
	if(rc) return 1;

	BalanceHistory *balances = (BalanceHistory*)data;
	BalanceHistory *history = &balances[handle->src_pid];
	move_time();
	TransferOrder order = {
		.s_src = 0,
		.s_dst = handle->src_pid,
		.s_amount = 0
	};

	handle_transfer(history, &order, get_lamport_time());

	create_message(&msg, BALANCE_HISTORY,
		sizeof *history - (MAX_T + 1 - history->s_history_len) * sizeof(*history->s_history)
	);

	memcpy(msg.s_payload, history, msg.s_header.s_payload_len);
	return send(handle, 0, &msg);
}
