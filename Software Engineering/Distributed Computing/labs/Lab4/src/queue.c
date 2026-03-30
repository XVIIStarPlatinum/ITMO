#include <string.h>
#include <stdlib.h>

#include "queue.h"

void queue_init(Queue **queue) {
	*queue = malloc(sizeof(**queue));
}

void queue_push(local_id pid, timestamp_t time, Queue *queue) {
	Queue *node;
	node = malloc(sizeof(*node));
	node->pid = pid;
	node->time = time;
	node->next = NULL;

	if (queue->back == NULL && queue->front == NULL){
		queue->front = queue->back = node;
		return;
	}
	Queue *current = queue->front, *prev = NULL;
	while (current != NULL) {
		if (current->time > time || (pid < current->pid && current->time == time)) {
			node->next = current;
			if (current == queue->front) {
                queue->front = node;
            }
            if (prev) {
                prev->next = node;
            }
			node = NULL;
			break;
		} else {
			prev = current;
			current = current->next;
		}
	}

	if (!node) {
		return;
	}
    queue->back->next = node;
	queue->back = node;
	node = NULL;
}

void queue_pop(Queue *queue) {
	if (queue->front == NULL) {
        return;
    }
	Queue *del = queue->front;
	if (queue->front != queue->back) {
		queue->front = queue->front->next;
	} else {
		queue->front = NULL;
		queue->back = NULL;
	}
	free(del);
}

void queue_free(Queue *queue) {
	Queue *node = queue->front;
	while (node != NULL) {
		Queue *next = node->next;
		free(node);
		node = next;
	}
	queue->front = NULL;
	queue->back = NULL;
	free(queue);
}
