#ifndef __IFMO_DISTRIBUTED_CLASS_LAMPORT_TIME__H
#define __IFMO_DISTRIBUTED_CLASS_LAMPORT_TIME__H

#include "pa2345.h"

timestamp_t get_lamport_time(void);
void move_time(void);
void set_lamport_time(timestamp_t new_time);

#endif
