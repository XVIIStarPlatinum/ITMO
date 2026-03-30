#define _GNU_SOURCE
#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/wait.h>
#include <unistd.h>
#include <time.h>
#include "banking.h"
#include "msg.h"
#include "pa2345.h"

typedef struct {
  int fd_write;
  int fd_read;
} pipe_fd;

int parse_balances(int argc, char* argv[], int* num_accounts_out, int** balances_out);
pipe_fd * get_struct(int from, int to);

void parent_work(int fd_events_log, int fd_pipes_log);
void pipe_work(local_id id, balance_t start_balance, int fd_events_log, int fd_pipes_log);
void wait_children(void);
int receive_multicast(local_id self, int16_t s_type, int N);

void handle_transfers(local_id id, int start_balance, int fd_events_log, int fd_pipes_log);

void check_history(AllHistory * all_history);
void get_all_history_messages(AllHistory * all_history);
void send_history_message(local_id id, BalanceHistory *bh);
