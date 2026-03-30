#include <getopt.h>
#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>
#include <string.h>

#include "banking.h"
#include "lamport_time.h"
#include "ops.h"

timestamp_t proc_time = 0;

timestamp_t get_lamport_time(void) {
	return proc_time;
}

void set_max_time(timestamp_t new_time) {
	proc_time = new_time > proc_time ? new_time : proc_time;
}

void move_time(void) {
	proc_time++;
}

int main(int argc, char * argv[]) {
	int rc = 0;

	int proc_num = parse_args(argc, argv);
	if(proc_num < 0) return 1;

	if(argc - optind < proc_num - 1) return 1;

	BalanceHistory *balances = calloc(1, proc_num * sizeof(*balances));
	if(balances == NULL) return 2;

	TransferOrder order;
	memset(&order, 0, sizeof(order));
	for(int i = 1; i < proc_num; i++) {
		balances[i].s_id = i;
		order.s_dst = i;
		order.s_amount = atoi(argv[optind + i - 1]);
		balances[i].s_history_len = 0;
		handle_transfer(&balances[i], &order, 0);
	}

	IOStruct handle;
	rc = create_handle(proc_num, &handle);
	if(rc) return rc;
	rc = create_pipes(&handle);
	if(rc) return rc;
	int is_parent = spawn_childs(&handle);
	if(is_parent < 0) return 2;

	rc = (!is_parent) ? child(&handle, balances) : parent(&handle);

	free(balances);
	free(handle.fd_table);

	return rc;
}

int parent_work(IOStruct *handle) {
    bank_robbery(handle, handle->proc_num - 1);
    move_time();
	Message stop;
	create_message(&stop, STOP, 0);
	return send_multicast(handle, &stop);
}

int parent_at_exit(IOStruct *handle) {
	AllHistory all = {
		.s_history_len = handle->proc_num - 1
	};
	Message msg;
	create_message(&msg, 0, 0);
	for(local_id i = 1; i < handle->proc_num; i++) {
		int rc = receive(handle, i, &msg);
		set_max_time(msg.s_header.s_local_time);
		move_time();
		if(rc) return 1;
		memcpy(&all.s_history[i - 1], msg.s_payload, msg.s_header.s_payload_len);
	}
	print_history(&all);
	return 0;
}

void handle_transfer(BalanceHistory *history, TransferOrder *order, timestamp_t send_time) {
	timestamp_t current_time = get_lamport_time();
	balance_t prev = history->s_history_len == 0
			? 0 : history->s_history[history->s_history_len - 1].s_balance;
	timestamp_t new_len = current_time + 1;
	if(new_len > 1) {
		for(timestamp_t t = history->s_history_len; t < new_len; t++) {
			history->s_history[t] = history->s_history[t - 1];
			history->s_history[t].s_time++;
		}
	}

	for (timestamp_t t = send_time; t < current_time; t++) {
		history->s_history[t].s_balance_pending_in = order->s_amount;
	}

	history->s_history[current_time].s_time = current_time;
	history->s_history[current_time].s_balance =
		order->s_src == history->s_id ? prev - order->s_amount
									  : prev + order->s_amount;

	history->s_history_len = current_time + 1;
}

int child_work(IOStruct *handle, void *data) {
	BalanceHistory *balances = (BalanceHistory*)data;
	BalanceHistory *history = &balances[handle->src_pid];
	char buff[MAX_PAYLOAD_LEN];
	Message msg;
	TransferOrder order;
	create_message(&msg, 0, 0);
	memset(&order, 0, sizeof(order));
	while(true) {
		int rc = receive_any(handle, &msg);
		if(rc) return 1;
		set_max_time(msg.s_header.s_local_time);
		move_time();
		if (msg.s_header.s_type == TRANSFER) {
			memcpy(&order, msg.s_payload, msg.s_header.s_payload_len);
			move_time();
			if (order.s_src == handle->src_pid) {
				msg.s_header.s_local_time = get_lamport_time();
				snprintf(buff, MAX_PAYLOAD_LEN, log_transfer_out_fmt,
						get_lamport_time(), handle->src_pid,
						order.s_amount, order.s_dst);
				log_event(buff);

				handle_transfer(history, &order, msg.s_header.s_local_time);
				rc = send(handle, order.s_dst, &msg);
				if (rc) return 1;
			} else {
				handle_transfer(history, &order, msg.s_header.s_local_time);
				create_message(&msg, ACK, 0);
				snprintf(buff, MAX_PAYLOAD_LEN, log_transfer_in_fmt,
						get_lamport_time(), handle->src_pid,
						order.s_amount, order.s_src);
				log_event(buff);
				rc = send(handle, 0, &msg);
				if (rc) return 1;
			}
		} else if (msg.s_header.s_type == STOP) {
			order.s_src = 0;
			order.s_dst = handle->src_pid;
			order.s_amount = 0;
			handle_transfer(history, &order, get_lamport_time());
			return 0;
		}
	}
	return 0;
}
