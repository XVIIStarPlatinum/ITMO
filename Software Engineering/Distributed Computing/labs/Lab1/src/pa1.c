#define _POSIX_C_SOURCE 200809L

#include <unistd.h>
#include <sys/wait.h>
#include <fcntl.h>
#include <stdarg.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "ipc.h"
#include "pa1.h"
#include "common.h"

typedef struct {
    local_id current_pid;
    int total_processes;
    int comm_matrix[MAX_PROCESS_ID + 1][MAX_PROCESS_ID + 1][2];
} ProcessContext;

static ProcessContext proc_ctx;
static FILE* events_log_file = NULL;
static FILE* pipes_log_file = NULL;

void write_log_entry(const char* template, ...) {
    va_list arg_list;

    va_start(arg_list, template);
    vprintf(template, arg_list);
    va_end(arg_list);

    if (events_log_file) {
        va_start(arg_list, template);
        vfprintf(events_log_file, template, arg_list);
        va_end(arg_list);
        fflush(events_log_file);
    }
}

void setup_pipes(void) {
    pipes_log_file = fopen(pipes_log, "w");

    for (int src = 0; src < proc_ctx.total_processes; src++) {
        for (int dest = 0; dest < proc_ctx.total_processes; dest++) {
            if (src != dest) {
                if (pipe(proc_ctx.comm_matrix[src][dest]) == -1) {
                    perror("pipe creation failed");
                    exit(EXIT_FAILURE);
                }

                if (pipes_log_file) {
                    fprintf(pipes_log_file, "Pipe from %d to %d: read=%d, write=%d\n",
                            src, dest,
                            proc_ctx.comm_matrix[src][dest][0],
                            proc_ctx.comm_matrix[src][dest][1]);
                }
            }
        }
    }

    if (pipes_log_file) fclose(pipes_log_file);
}

void cleanup_pipes(void) {
    for (int i = 0; i < proc_ctx.total_processes; i++) {
        for (int j = 0; j < proc_ctx.total_processes; j++) {
            if (i != j) {
                if (i != proc_ctx.current_pid)
                    close(proc_ctx.comm_matrix[i][j][0]);
                if (j != proc_ctx.current_pid)
                    close(proc_ctx.comm_matrix[i][j][1]);
            }
        }
    }
}

int send(void* context, local_id destination, const Message* data) {
    ProcessContext* ctx = (ProcessContext*)context;
    int write_fd = ctx->comm_matrix[ctx->current_pid][destination][1];
    size_t message_size = sizeof(MessageHeader) + data->s_header.s_payload_len;
    ssize_t bytes_written = write(write_fd, data, message_size);
    return (bytes_written == (ssize_t)message_size) ? 0 : -1;
}

int send_multicast(void* context, const Message* data) {
    ProcessContext* ctx = (ProcessContext*)context;
    for (int i = 0; i < ctx->total_processes; i++) {
        if (i != ctx->current_pid) {
            if (send(context, (local_id) i, data) != 0)
                return -1;
        }
    }
    return 0;
}

int receive(void* context, local_id source, Message* data) {
    ProcessContext* ctx = (ProcessContext*)context;
    int read_fd = ctx->comm_matrix[source][ctx->current_pid][0];
    if (read(read_fd, &data->s_header, sizeof(MessageHeader)) == sizeof(MessageHeader)) {
        if (data->s_header.s_payload_len > 0) {
            read(read_fd, data->s_payload, data->s_header.s_payload_len);
        }
        return 0;
    }
    return -1;
}

int receive_any(void* context, Message* data) {
    ProcessContext* ctx = (ProcessContext*)context;
    for (int i = 0; i < ctx->total_processes; i++) {
        if (i != ctx->current_pid) {
            int read_fd = ctx->comm_matrix[i][ctx->current_pid][0];
            if (read(read_fd, &data->s_header, sizeof(MessageHeader)) == sizeof(MessageHeader)) {
                if (data->s_header.s_payload_len > 0) {
                    read(read_fd, data->s_payload, data->s_header.s_payload_len);
                }
                return 0;
            }
        }
    }
    return -1;
}

void execute_child_process(void) {
    Message packet;
    snprintf(packet.s_payload, MAX_PAYLOAD_LEN, log_started_fmt,
             proc_ctx.current_pid, getpid(), getppid());
    packet.s_header.s_magic = MESSAGE_MAGIC;
    packet.s_header.s_payload_len = strlen(packet.s_payload);
    packet.s_header.s_type = STARTED;
    packet.s_header.s_local_time = 0;

    write_log_entry(packet.s_payload);
    send_multicast(&proc_ctx, &packet);

    for (int i = 1; i < proc_ctx.total_processes; i++) {
        if (i != proc_ctx.current_pid) {
            receive(&proc_ctx, (local_id) i, &packet);
        }
    }

    write_log_entry(log_received_all_started_fmt, proc_ctx.current_pid);

    snprintf(packet.s_payload, MAX_PAYLOAD_LEN, log_done_fmt, proc_ctx.current_pid);

    packet.s_header.s_magic = MESSAGE_MAGIC;
    packet.s_header.s_payload_len = strlen(packet.s_payload);
    packet.s_header.s_type = DONE;
    packet.s_header.s_local_time = 0;

    write_log_entry(packet.s_payload);
    send_multicast(&proc_ctx, &packet);

    for (int i = 1; i < proc_ctx.total_processes; i++) {
        if (i != proc_ctx.current_pid) {
            receive(&proc_ctx, (local_id) i, &packet);
        }
    }

    write_log_entry(log_received_all_done_fmt, proc_ctx.current_pid);
}

void execute_parent_process(void) {
    Message packet;

    for (int i = 1; i < proc_ctx.total_processes; i++) {
        receive(&proc_ctx, (local_id) i, &packet);
    }

    write_log_entry(log_received_all_started_fmt, PARENT_ID);

    for (int i = 1; i < proc_ctx.total_processes; i++) {
        receive(&proc_ctx, (local_id) i, &packet);
    }

    write_log_entry(log_received_all_done_fmt, PARENT_ID);

    for (int i = 1; i < proc_ctx.total_processes; i++) {
        wait(NULL);
    }
}

int main(int argc, char* argv[]) {
    int child_count = 0;
    if (argc != 3 || strcmp(argv[1], "-p") != 0) {
        fprintf(stderr, "Usage: %s -p X (0 < X < 11)\n", argv[0]);
        return EXIT_FAILURE;
    }

    child_count = strtol(argv[2], NULL, 10);

    if (child_count < 1 || child_count > MAX_PROCESS_ID - 1) {
        fprintf(stderr, "Process count is invalid: %d\n", child_count);
        return EXIT_FAILURE;
    }

    proc_ctx.total_processes = child_count + 1;
    events_log_file = fopen(events_log, "w");

    setup_pipes();

    for (int i = 1; i <= child_count; i++) {
        pid_t child_pid = fork();

        if (child_pid == -1) {
            perror("process creation failed");
            return EXIT_FAILURE;
        }

        if (child_pid == 0) {
            proc_ctx.current_pid = (local_id) i;
            cleanup_pipes();
            execute_child_process();

            if (events_log_file) fclose(events_log_file);
            return EXIT_SUCCESS;
        }
    }

    proc_ctx.current_pid = PARENT_ID;
    cleanup_pipes();
    execute_parent_process();

    if (events_log_file) fclose(events_log_file);

    return EXIT_SUCCESS;
}
