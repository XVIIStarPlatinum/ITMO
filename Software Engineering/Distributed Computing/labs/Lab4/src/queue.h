#ifndef __IFMO_DISTRIBUTED_CLASS_QUEUE__H
#define __IFMO_DISTRIBUTED_CLASS_QUEUE__H

#include "ipc.h"

typedef struct Queue {
	local_id pid;
	timestamp_t time;
    struct Queue *front, *back, *next;
} Queue;

void queue_init(Queue **queue);
void queue_push(local_id pid, timestamp_t time, Queue *queue);
void queue_pop(Queue *queue);
void queue_free(Queue *queue);

#endif
