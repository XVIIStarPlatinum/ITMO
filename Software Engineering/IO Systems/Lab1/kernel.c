#include "kernel.h"

extern char __bss[], __bss_end[], __stack_top[];

struct sbiret
sbi_call(long arg0, long arg1, long arg2, long arg3, long arg4, long arg5, long fid, long eid)
{
    register long a0 __asm__("a0") = arg0;
    register long a1 __asm__("a1") = arg1;
    register long a2 __asm__("a2") = arg2;
    register long a3 __asm__("a3") = arg3;
    register long a4 __asm__("a4") = arg4;
    register long a5 __asm__("a5") = arg5;
    register long a6 __asm__("a6") = fid;
    register long a7 __asm__("a7") = eid;

    __asm__ __volatile__("ecall"
                    : "=r"(a0), "=r"(a1)
                    : "r"(a0), "r"(a1), "r"(a2), "r"(a3), "r"(a4), "r"(a5), "r"(a6), "r"(a7)
                    : "memory");
    return (struct sbiret){.error = a0, .value = a1};
}

void
putchar(char ch)
{
    sbi_call(ch, 0, 0, 0, 0, 0, 0, SBI_ECALL_0_1_PUTCHAR);
}

int
getchar(void)
{
    struct sbiret ret;
    do {

        ret = sbi_call(0, 0, 0, 0, 0, 0, 0, SBI_ECALL_0_1_GETCHAR);

    } while(ret.error == -1);

    char ch = (char) ret.error;
    return (int)ch;
}

void
printf(const char *fmt, ...) {
    va_list vargs;
    va_start(vargs, fmt);

    while (*fmt) {
        if (*fmt == '%') {
            fmt++;
            switch (*fmt) {
                case '\0':
                    putchar('%');
                goto end;
                case '%':
                    putchar('%');
                break;
                case 's': {
                    const char *s = va_arg(vargs, const char *);
                    while (*s) {
                        putchar(*s);
                        s++;
                    }
                    break;
                }
                case 'd': {
                    int value = va_arg(vargs, int);
                    if (value < 0) {
                        putchar('-');
                        value = -value;
                    }

                    int divisor = 1;
                    while (value / divisor > 9)
                        divisor *= 10;

                    while (divisor > 0) {
                        putchar('0' + value / divisor);
                        value %= divisor;
                        divisor /= 10;
                    }

                    break;
                }
                case 'x': {
                    int value = va_arg(vargs, int);
                    for (int i = 7; i >= 0; i--) {
                        int nibble = (value >> (i * 4)) & 0xf;
                        putchar("0123456789abcdef"[nibble]);
                    }
                }
            }
        } else {
            putchar(*fmt);
        }

        fmt++;
    }

    end:
        va_end(vargs);
}

void
readline(char *buf, int max_len)
{
    int i = 0;
    char c;
    while (i < max_len - 1)
    {
        c = getchar();
        putchar(c);
        if (c == '\r' || c == '\n')
        {
            putchar('\n');
            break;
        }
        buf[i++] = c;
    }
    buf[i] = '\0';
}

int
atoi(const char* str)
{
    int res = 0;
    while (*str)
    {
        res = res * 10 + (*str - '0');
        str++;
    }
    return res;
}

void
get_sbi_version()
{
    struct sbiret ret = sbi_call(0, 0, 0, 0, 0, 0, SBI_EXT_VER, SBI_EXT_BASE);
    long major = ret.value >> 24;
    // do NOT show the next line of code to Drake
    long minor = ret.value & 0xFFFFFF;
    printf("\nВерсия SBI: %d.", major);
    if(minor < 10)
    {
        putchar('0');
    }
    if(minor < 100)
    {
        putchar('0');
    }
    printf("%d\n", minor);
}

void
get_num_counters()
{
    struct sbiret ret = sbi_call(0, 0, 0, 0, 0, 0, SBI_EXT_CTR_NUM, SBI_EXT_PMU);
    printf("Число счётчиков: %d\n", ret.value);
}

void
get_counter_details()
{
    printf("\nВведите число счётчика: ");
    char input[32];
    readline(input, sizeof(input));
    long counter_num = atoi(input);

    struct sbiret ret = sbi_call(counter_num, 0, 0, 0, 0, 0, SBI_EXT_CTR_DTLS, SBI_EXT_PMU);
    printf("\nСчётчик: %d\n", counter_num);
    printf("Детали: \n");
    if(ret.error)
    {
        printf("Ошибка: %d\n", ret.error);
        return;
    }
    unsigned long counter_info = ret.value;
    int type = (counter_info >> (__riscv_xlen - 1)) & 0x1;
    int width = (counter_info >> 12) & 0x3F;
    int csr = counter_info & 0xFFF;

    printf("Тип: ");
    printf(type ? "Прошивка\n" : "Аппаратура\n");

    if(!type)
    {
        printf("CSR: %d\n", csr);
        printf("Ширина: %d биты\n", width + 1);
    }
    else
    {
        printf("CSR и ширина не применимы для прошивки.\n");
    }
}

void
system_shutdown()
{
    printf("Завершение работы системы...\n");
    sbi_call(0, 0, 0, 0, 0, 0, SBI_EXT_SHUTDOWN, SBI_EXT_SRST);
    while(1)
    {}
}

void
menu()
{
    while(1)
    {
        printf("\nМеню OpenSBI:\n");
        printf("1. Get SBI specification version\n");
        printf("2. Get number of counters\n");
        printf("3. Get details of a counter\n");
        printf("4. System shutdown\n");
        printf("Введите опцию:\n");

        char input[32];
        readline(input, sizeof(input));
        int choice = atoi(input);
        switch (choice)
        {
            case 1:
                get_sbi_version();
            break;
            case 2:
                get_num_counters();
            break;
            case 3:
                get_counter_details();
            break;
            case 4:
                system_shutdown();
            break;
            default:
                printf("\nТакой опции нет. Попробуйте снова.\n");
        }
    }
}

void
kernel_main(void)
{
    for(char *p = __bss; p < __bss_end; p++)
    {
        *p = 0;
    }

    menu();

    for(;;)
    {
        __asm__ __volatile__("wfi");
    }
}

__attribute__((section(".text.boot")))
__attribute__((naked))
void
boot(void)
{
    __asm__ __volatile__(
            "mv sp, %[stack_top]\n"
            "j kernel_main\n"
            :
            : [stack_top] "r" (__stack_top)
    );
}