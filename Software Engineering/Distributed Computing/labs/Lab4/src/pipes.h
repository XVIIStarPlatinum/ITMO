#ifndef __IFMO_DISTRIBUTED_CLASS_PIPES__H
#define __IFMO_DISTRIBUTED_CLASS_PIPES__H

#include "io.h"

static const char* const pipe_opened_msg = "Process #%d -> #%d (pipe opened)\n";
static const char* const pipe_closed_msg = "Process #%d -> #%d (pipe closed)\n";

int create_pipes(IOStruct *handle);
void close_pipes(IOStruct *handle);

#endif
