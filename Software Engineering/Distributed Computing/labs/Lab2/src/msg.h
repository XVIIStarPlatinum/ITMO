#ifndef __MSG_H
#define __MSG_H

static const char* const invalid_args_msg = 
    "Usage: %s -p N s1 s2 ... sN\n";
static const char* const open_file_log_error_msg =
    "Error while creating or opening log files\n";
static const char* const fork_error_msg = 
    "Error while forking processes\n";
static const char* const message_type_error_msg = 
	"Error while parsing message (invalid type)\n";
static const char* const send_multicast_error_msg =
	"Error while using send_multicast (non-zero result)\n";
static const char* const receive_multicast_type_error_msg = 
	"Error while receiving messages (received message with different value)\n";
static const char* const unexpected_message_type_error_msg = 
	"Error while receiving messages (unexpected type)\n";
static const char* const transfer_parameters_error_msg = 
	"Error while initializing TransferOrder (struct with invalid parameters)\n";
static const char* const duplicated_stop_message_error_msg = 
	"Error while receiving messages (stop message was received several times)\n";
static const char* const early_balance_history_request_error_msg = 
	"Error while receiving BalanceHistory (message was received before process done his work)\n";

static const char * const pipe_opened_msg = "Process #%d -> #%d (pipe opened)\n";
static const char * const pipe_closed_msg = "Process #%d -> #%d (pipe closed)\n";

#endif
