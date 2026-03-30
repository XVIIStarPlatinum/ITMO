#ifndef __IFMO_DISTRIBUTED_CLASS_UTIL__H
#define __IFMO_DISTRIBUTED_CLASS_UTIL__H

#include "io.h"
#include "ipc.h"

void create_message(Message *msg, MessageType type, size_t length);

int log_event(char *msg);
int parse_args(int argc, char *const argv[], void *data);
int create_handle(int proc_num, IOStruct *handle);
int generate_childs(IOStruct *handle);

int parent(IOStruct *handle);
int child(IOStruct *handle, void *data);

extern int child_work(IOStruct *handle, void *data);

#endif /* __IFMO_DISTRIBUTED_CLASS_UTIL__H */
