#ifndef __UTILS__H
#define __UTILS__H

#include "pa2345.h"
#include "banking.h"

typedef enum {
	LOG_STARTED,
	LOG_STARTED_RECEIVED_ALL,
	LOG_DONE,
	LOG_TRANSFER_OUT,
	LOG_TRANSFER_IN,
	LOG_DONE_RECEIVED_ALL
} EventLogType;

typedef enum {
    ARGUMENTS_FORMAT,
    OPEN_LOG_FILES,
    FORK,
    UNKNOWN_MESSAGE_TYPE,
    SEND_MULTICAST,
    RECEIVE_MULTICAST_TYPE,
    UNEXPECTED_MESSAGE_TYPE,
    TRANSFER_PARAMETERS,
    DUPLICATED_STOP_MESSAGE,
    EARLY_BALANCE_HISTORY_REQUEST
} ErrorType;

void log_error(int err_num);
void log_event(int num_event, local_id l_id, local_id to_id, balance_t s_balance, int fd_events_log);
void log_pipe(int num_event, int from, int to, int fd_pipes_log);

#endif
