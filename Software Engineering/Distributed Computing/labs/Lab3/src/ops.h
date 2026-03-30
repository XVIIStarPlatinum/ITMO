#ifndef _UTIL_H_
#define _UTIL_H_

#include "banking.h"
#include "io.h"
#include "ipc.h"

static const char* const pipe_opened_msg = "Process #%d -> #%d (pipe opened)\n";
static const char* const pipe_closed_msg = "Process #%d -> #%d (pipe closed)\n";

void create_message(Message *msg, MessageType type, size_t length);

int log_event(char *msg);
int parse_args(int argc, char *const argv[]);
int create_handle(int proc_num, IOStruct *handle);
int create_pipes(IOStruct *handle);
int spawn_childs(IOStruct *handle);

int parent(IOStruct *handle);
int child(IOStruct *handle, void *data);

extern int parent_work(IOStruct *handle);
extern int parent_at_exit(IOStruct *handle);
extern void handle_transfer(BalanceHistory *history, TransferOrder *order, timestamp_t send_time);
extern int child_work(IOStruct *handle, void *data);


extern void update_child_started_state(char *msg, IOStruct *handle, void *data);
extern void send_done_message_from_child(char *msg, IOStruct *handle);
extern void update_child_done_state(char *msg, IOStruct *handle, void *data);
extern void child_received_all_done_msg(char *msg, IOStruct *handle);

#endif
