#include <stdio.h>
#include <unistd.h>

#include "lamport_time.h"
#include "banking.h"
#include "ops.h"

void update_child_started_state(char *msg, IOStruct *handle, void *data) {
	BalanceHistory *balances = (BalanceHistory*)data;
	BalanceState state = balances[handle->src_pid].s_history[get_lamport_time()];
	snprintf(msg, MAX_PAYLOAD_LEN, log_started_fmt, get_lamport_time(),
			 handle->src_pid, getpid(), handle->parent_pid, state.s_balance);
}

void update_child_done_state(char *msg, IOStruct *handle, void *data) {
	BalanceHistory *balances = (BalanceHistory*)data;
	BalanceState state = balances[handle->src_pid].s_history[get_lamport_time()];
	snprintf(msg, MAX_PAYLOAD_LEN, log_done_fmt,
			 get_lamport_time(), handle->src_pid, state.s_balance);
}

void send_done_message_from_child(char *msg, IOStruct *handle) {
	snprintf(msg, MAX_PAYLOAD_LEN, log_received_all_started_fmt,
			 get_lamport_time(), handle->src_pid);
}

void child_received_all_done_msg(char *msg, IOStruct *handle) {
	snprintf(msg, MAX_PAYLOAD_LEN, log_received_all_done_fmt,
			 get_lamport_time(), handle->src_pid);
}
