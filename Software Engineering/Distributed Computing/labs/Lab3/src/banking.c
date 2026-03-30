#include <stdlib.h>
#include <string.h>
#include "banking.h"
#include "lamport_time.h"
#include "ops.h"

void transfer(void* parent_data, local_id src, local_id dst, balance_t amount) {
	move_time();
    Message msg;
	create_message(&msg, TRANSFER, 0);
    TransferOrder order = {
		.s_src = src,
    	.s_dst = dst,
    	.s_amount = amount
	};
    msg.s_header.s_payload_len = sizeof(order);
    memcpy(msg.s_payload, &order, msg.s_header.s_payload_len);
    int rc = send(parent_data, src, &msg);
    if(rc) exit(1);
    rc = receive(parent_data, dst, &msg);
    if(rc) exit(1);
}
