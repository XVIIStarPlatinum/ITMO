#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <time.h>
#include <errno.h>
#include "ops.h"
#include "utils.h"

void log_error(int err_num) {
    if (err_num == ARGUMENTS_FORMAT) {
        printf("%s", invalid_args_msg);
    } else if (err_num == OPEN_LOG_FILES) {
        printf("%s", open_file_log_error_msg);
    } else if (err_num == FORK) {
        printf("%s", fork_error_msg);
    } else if (err_num == UNKNOWN_MESSAGE_TYPE) {
        printf("%s", message_type_error_msg);
    } else if (err_num == SEND_MULTICAST) {
        printf("%s", send_multicast_error_msg);
    } else if (err_num == RECEIVE_MULTICAST_TYPE) {
        printf("%s", receive_multicast_type_error_msg);
    } else if (err_num == UNEXPECTED_MESSAGE_TYPE) {
        printf("%s", unexpected_message_type_error_msg);
    } else if (err_num == TRANSFER_PARAMETERS) {
        printf("%s", transfer_parameters_error_msg);
    } else if (err_num == DUPLICATED_STOP_MESSAGE) {
        printf("%s", duplicated_stop_message_error_msg);
    } else if (err_num == EARLY_BALANCE_HISTORY_REQUEST) {
        printf("%s", early_balance_history_request_error_msg);
    } else {
        log_error(UNKNOWN_MESSAGE_TYPE);
    }
    exit(1);
}

void log_event(int num_event, local_id l_id, local_id to_id, balance_t s_balance, int fd_events_log) {
    char buf[MAX_MESSAGE_LEN];

    if (num_event == LOG_STARTED) {
        sprintf(buf, log_started_fmt, get_physical_time(), l_id, getpid(), getppid(), s_balance);
    } else if (num_event == LOG_STARTED_RECEIVED_ALL) {
        sprintf(buf, log_received_all_started_fmt, get_physical_time(), l_id);
    } else if (num_event == LOG_DONE) {
        sprintf(buf, log_done_fmt, get_physical_time(), l_id, s_balance);
    } else if (num_event == LOG_TRANSFER_OUT) {
        sprintf(buf, log_transfer_out_fmt, get_physical_time(), l_id, s_balance, to_id);
    } else if (num_event == LOG_TRANSFER_IN) {
        sprintf(buf, log_transfer_in_fmt, get_physical_time(), l_id, s_balance, to_id);
    } else if (num_event == LOG_DONE_RECEIVED_ALL) {
        sprintf(buf, log_received_all_done_fmt, get_physical_time(), l_id);
    } else {
        log_error(UNKNOWN_MESSAGE_TYPE);
    }

    printf("%s", buf);
    write(fd_events_log, buf, strlen(buf));
}

void log_pipe(int num_event, int from, int to, int fd_pipes_log) {
	char buf[100];
	if (num_event == 0) {
		sprintf(buf, pipe_opened_msg, from, to);
		write(fd_pipes_log, buf, strlen(buf));
	} else if (num_event == 1) {
		sprintf(buf, pipe_closed_msg, from, to);
		write(fd_pipes_log, buf, strlen(buf));
	}
}
