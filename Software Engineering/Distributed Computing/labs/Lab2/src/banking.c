#include <string.h>
#include <unistd.h>
#include <time.h>
#include <stdio.h>
#include "banking.h"
#include "ipc.h"

void transfer(void * parent_data, local_id src, local_id dst, balance_t amount) {
    TransferOrder to = {
        .s_src = src,
        .s_dst = dst,
        .s_amount = amount
    };

    Message m = {
        .s_header = {
            .s_magic = MESSAGE_MAGIC,
            .s_type = TRANSFER,
            .s_payload_len = sizeof(TransferOrder),
            .s_local_time = get_physical_time()
        }
    };
    Message m_received;
    memcpy(m.s_payload, &to, sizeof(TransferOrder));

    local_id id = 0;
    send(&id, src, &m);
    receive(&id, dst, &m_received);
}
