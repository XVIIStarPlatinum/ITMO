#include <stdbool.h>
#include <stdlib.h>
#include <unistd.h>
#include "pipes.h"
#include "utils.h"

pipe_fd *pipes;
int N;

int main(int argc, char* argv[]) {
    int fd_events_log, fd_pipes_log;
    int num_accounts;
    int* balances = NULL;
    
    if (parse_balances(argc, argv, &num_accounts, &balances) != 0) {
        fprintf(stderr, "Usage: %s -p N s1 s2 ... sN\n", argv[0]);
        return 1;
    }
    
    N = num_accounts + 1;

    fd_events_log = open(events_log, O_WRONLY | O_APPEND | O_CREAT | O_TRUNC | O_NONBLOCK, 0777);
    if (fd_events_log < 0) log_error(OPEN_LOG_FILES);
    
    fd_pipes_log = open(pipes_log, O_WRONLY | O_APPEND | O_CREAT | O_TRUNC | O_NONBLOCK, 0777);
    if (fd_pipes_log < 0) log_error(OPEN_LOG_FILES);

    open_pipes(&pipes, fd_pipes_log, N);
    pid_t process_ids[N];

    process_ids[0] = getpid();
    
    for (local_id i = 1; i < N; i++) {
        process_ids[i] = fork();
    
        if (process_ids[i] == 0) {
            pipe_work(i, balances[i - 1], fd_events_log, fd_pipes_log);
            return 0;
        } else if (process_ids[i] == -1) {
            log_error(FORK);
        }
    }

    parent_work(fd_events_log, fd_pipes_log);

    close(fd_events_log);
    close(fd_pipes_log);
    free(pipes);

    return 0;
}

pipe_fd* get_struct(int from, int to) {
    if (from == to) return NULL;
    pipe_fd *result = pipes + (size_t)(from * (N - 1) + ((to >= from) ? (to - 1) : to));
    return result;
}

void parent_work(int fd_events_log, int fd_pipes_log) {  
    local_id id = 0;
    pid_t pid;
    int status;
    close_other_pipes(id, fd_pipes_log, N);
    
    if (receive_multicast(id, STARTED, N) < 0) log_error(RECEIVE_MULTICAST_TYPE);

    bank_robbery(NULL, N - 1);

    Message m = {
        .s_header = {
            .s_type = STOP, 
            .s_magic = MESSAGE_MAGIC, 
            .s_payload_len = 0, 
            .s_local_time = get_physical_time()
        }
    };

    if (send_multicast(&id, &m) == -1) log_error(SEND_MULTICAST);
    if (receive_multicast(id, DONE, N) < 0) log_error(RECEIVE_MULTICAST_TYPE);

    AllHistory all_history;
    get_all_history_messages(&all_history);
    
    while((pid = wait(&status)) > 0) {}
    close_own_pipes(id, fd_pipes_log, N);
    print_history(&all_history);
}

void get_all_history_messages(AllHistory* all_history) {
    
    Message response_msg;
    int longest_history = 0;
    local_id parent_id = 0;
	Message request_msg = {
		.s_header = {
			.s_magic = MESSAGE_MAGIC,
			.s_type = BALANCE_HISTORY,
			.s_payload_len = 0
		}
	};

    all_history->s_history_len = N - 1;

    for (local_id idx = 0; idx < N - 1; idx++) {
        request_msg.s_header.s_local_time = get_physical_time();

        send(&parent_id, idx + 1, &request_msg);
        receive(&parent_id, idx + 1, &response_msg);

        char* payload = response_msg.s_payload;
        local_id proc_identifier;
        uint8_t hist_length;

        memcpy(&proc_identifier, payload, sizeof(local_id));
        all_history->s_history[idx].s_id = proc_identifier;
        payload += sizeof(local_id);
        memcpy(&hist_length, payload, sizeof(uint8_t));
        all_history->s_history[idx].s_history_len = hist_length;
        payload += sizeof(uint8_t);

        if (hist_length > longest_history) longest_history = hist_length;

        for (int entry = 0; entry < all_history->s_history[idx].s_history_len; entry++) {
            memcpy(&all_history->s_history[idx].s_history[entry], payload, sizeof(BalanceState));
            payload += sizeof(BalanceState);
        }
    }

    for (local_id idx = 0; idx < N - 1; idx++) {
        int current_len = all_history->s_history[idx].s_history_len;
        if (current_len >= longest_history) continue;
        
        BalanceState last_entry = all_history->s_history[idx].s_history[current_len - 1];
        for (int time_point = current_len; time_point < longest_history; time_point++) {
            last_entry.s_time = time_point;
            all_history->s_history[idx].s_history[time_point] = last_entry;
        }
        all_history->s_history[idx].s_history_len = longest_history;
    }
}

void pipe_work(local_id id, balance_t start_balance, int fd_events_log, int fd_pipes_log) {
    close_other_pipes(id, fd_pipes_log, N);
    log_event(LOG_STARTED, id, 0, start_balance, fd_events_log);
    
    Message m = {
        .s_header = {
            .s_magic = MESSAGE_MAGIC,
            .s_payload_len = sprintf(m.s_payload, log_started_fmt, 
                  get_physical_time(), id, getpid(), getppid(), start_balance),
            .s_type = STARTED,
            .s_local_time = get_physical_time()
        }
    };

    if (send_multicast(&id, &m) != 0) log_error(SEND_MULTICAST);
 
    receive_multicast(id, m.s_header.s_type, N);
    log_event(LOG_STARTED_RECEIVED_ALL, id, 0, start_balance, fd_events_log);
    handle_transfers(id, start_balance, fd_events_log, fd_pipes_log);
    close_own_pipes(id, fd_pipes_log, N);
}

void handle_transfers(local_id id, int start_balance, int fd_events_log, int fd_pipes_log) {
	Message invoice;
	int done_count = 0, balance = start_balance;
	BalanceState bs = {
        .s_balance = start_balance,
        .s_time = 0,
        .s_balance_pending_in = 0,
		
	};
    BalanceHistory bh = {
		.s_id = id,
		.s_history[0] = bs
	};
    bool isInStopState = false, isHistoryRequired = false;
	timestamp_t last_time = 0;
    while (true) {
        receive_any(&id, &invoice);
        if (invoice.s_header.s_type == TRANSFER) {
            TransferOrder to;
            timestamp_t new_time = get_physical_time();
            memcpy(&to, invoice.s_payload, sizeof(TransferOrder));
			if (to.s_src != id && to.s_dst != id) {
				log_error(TRANSFER_PARAMETERS);
			}
			if (to.s_src == id) {
				balance -= to.s_amount;
            	send(&id, to.s_dst, &invoice);
            	log_event(LOG_TRANSFER_OUT, id, to.s_dst, to.s_amount, fd_events_log);
			}
			if (to.s_dst == id) {
                balance += to.s_amount;
                Message response = {
                    .s_header = {
                        .s_type = ACK,
                        .s_magic = MESSAGE_MAGIC,
                        .s_payload_len = 0,
                        .s_local_time = get_physical_time()
                    }
                };
                send(&id, PARENT_ID, &response);
                log_event(LOG_TRANSFER_IN, id, to.s_src, to.s_amount, fd_events_log);
            }
            bs.s_time = new_time;
            bs.s_balance = balance;
            bh.s_history[new_time] = bs;
            bs = bh.s_history[last_time++];

            while (last_time < new_time) {
                bs.s_time = last_time;
                bh.s_history[last_time] = bs;
                last_time++;
            }

        } else if (invoice.s_header.s_type == STOP) {
            if (isInStopState) {
                log_error(DUPLICATED_STOP_MESSAGE);
            }
            Message response = {
              .s_header = {
                .s_type = DONE,
                .s_magic = MESSAGE_MAGIC,
                .s_local_time = get_physical_time(),
                .s_payload_len = sprintf(
                  response.s_payload, log_done_fmt,
                  get_physical_time(), id, balance
                )
              }
            };
            if (send_multicast(&id, &response) == -1) {
                log_error(SEND_MULTICAST);
            }
            isInStopState = true;
        } else if (invoice.s_header.s_type == DONE) {
            done_count++;
            if (done_count > N - 2) {
                printf("Process with ID=%d received an inappropriate amount of messages: %d\n", id, done_count);
                exit(1);
            }
			if (done_count == N - 2) {
				log_event(LOG_DONE_RECEIVED_ALL, id, 0, 0, fd_events_log);
            	if (isInStopState && isHistoryRequired) {
                	bh.s_history_len = last_time + 1;
                	send_history_message(id, &bh);
                	return;
            	}
			}
            
		} else if (invoice.s_header.s_type == BALANCE_HISTORY) {
			if (!isInStopState) {
				log_error(EARLY_BALANCE_HISTORY_REQUEST);
			}
			if (done_count != N - 2) {
				isHistoryRequired = true;
			}
            bh.s_history_len = last_time + 1;
            send_history_message(id, &bh);
            return;
        } else if (invoice.s_header.s_type == STARTED || invoice.s_header.s_type == ACK) {
            log_error(UNEXPECTED_MESSAGE_TYPE);
        } else {
            log_error(UNKNOWN_MESSAGE_TYPE);
        }
    }
}

int receive_any(void* self, Message* msg) {
	local_id self_id = *(local_id*)self;

  	while (1) {
	    for (local_id i = 0; i < N; i++) {
    	  	if (i == self_id) {
				continue;
	  	}
      	pipe_fd* desc = get_struct(self_id, i);
      	int bytes_count = read(desc->fd_read, &(msg->s_header), sizeof(MessageHeader));

		if (bytes_count <= 0) continue;

    	bytes_count = read(desc->fd_read, msg->s_payload, msg->s_header.s_payload_len);

    	return (int)i;
    	}
  	}
}

int send_multicast(void * self, const Message * msg) {
  	local_id l_id = *(local_id *)self;

  	for (local_id i = 0; i < N; i++) {
    	if (i != l_id) {
			if (send(self, i, msg) != 0) {
	        	return -1; 
    	  	}
		}      
  	}
  	return 0;
}

void send_history_message(local_id id, BalanceHistory *bh) {
	uint16_t pl = sizeof(balance_t) + sizeof(uint8_t) + (bh->s_history_len + 1) * sizeof(BalanceState);
	Message invoice = {
		.s_header = {
		.s_type = BALANCE_HISTORY, 
		.s_magic = MESSAGE_MAGIC, 
		.s_payload_len = pl, 
		.s_local_time = get_physical_time()
		}
	};
	memcpy(&invoice.s_payload, bh, pl);
	send(&id, PARENT_ID, &invoice);
}

int parse_balances(int argc, char* argv[], int* num_accounts_out, int** balances_out) {
    if (argc < 4 || strcmp(argv[1], "-p") != 0) {
        return -1;
    }
    
    int accounts = atoi(argv[2]);
    if (accounts < 2 || accounts > 10 || argc != 3 + accounts) {
        return -1;
    }
    
    int* balances = calloc(accounts, sizeof(int));
    if (!balances) {
        return -1;
    }
    
    for (int i = 0; i < accounts; i++) {
        int val = atoi(argv[3 + i]);
        if (val < 1 || val > 99) {
            free(balances);
            return -1;
        }
        balances[i] = val;
    }
    
    *num_accounts_out = accounts;
    *balances_out = balances;
    return 0;
}
