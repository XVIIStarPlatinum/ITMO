#ifndef __PIPES__H
#define __PIPES__H
#include "io.h"

int create_pipes(IOStruct *handle);
void close_pipes(IOStruct *handle);

#endif
