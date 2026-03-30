#include <fcntl.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/wait.h>
#include <time.h>
#include "ipc.h"
#include "ops.h"

int send(void* context, local_id dst, const Message* msg) {
    local_id l_id = *(local_id *)context;
    pipe_fd *desc = get_struct(l_id, dst);
    
    if (write(desc->fd_write, msg, sizeof(MessageHeader) + msg->s_header.s_payload_len) == -1) {
        return -1;
    }
    return 0;
}

int receive(void* context, local_id src, Message* msg) {
    local_id id = *(local_id *)context;
    pipe_fd *desc = get_struct(id, src);

    while(1) {
        int n = read(desc->fd_read, &(msg->s_header), sizeof(MessageHeader));
        if (n != -1) {
            n = read(desc->fd_read, msg->s_payload, msg->s_header.s_payload_len);
            return 0;    
        }
    }
}

int receive_multicast(local_id self, int16_t s_type, int N) {
  for (local_id i = 1; i < N; i++)
      if (i != self) {
        Message m;
        if (receive(&self, i, &m) ==  -1)
          return -2; 
        
        if (m.s_header.s_type != s_type)
          return -1;
      }

  return 0;
}
