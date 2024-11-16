#include <stdio.h>
#include <stdint.h>
#include <inttypes.h>
#include <time.h>
#include <stdlib.h>
#include <string.h>

int main(int argc, char** argv) {
    srand(time(NULL));
    uint64_t num, num1, mask = 0xF;
    int n = 0;
    char* endPointer;
    if (argc == 1) {
        num = rand();
    } else if (argc == 2) {
        num = strtoull(argv[1], &endPointer, 16);
    } else {
        printf("Неправильное количество аргументов.");
        return 0;
    }
    num1 = num;

    while(num1 != 0) {
        num1 >>= 4;
        n++;
    }
    unsigned short digits[n], result[n];
    for (int i = 0; i < n; i++) {
        digits[n - i - 1] = num & mask;
        num >>= 4;
    }
    for (int i = 0; i < n; i++) {
        int d = digits[i], ones = 0, zeros = 0;
        while (d != 0) {
            (d & 1) ? ones++ : zeros++;
            d >>= 1;
        }
        int diff = abs(ones - zeros);


    }
    /**
     * 0 = 4
     * 1 = 2
     * 2 = 2
     * 3 = 0
     * 4 = 2
     * 5 = 0
     * 6 = 0
     * 7 = 2
     * 8 = 2
     * 9 = 0
     * A = 0
     * B = 2
     * C = 0
     * D = 2
     * E = 2
     * F = 4
     *
     * 0, F - ?
     * 1, 2, 4, 7, 8, B, D, E - ?
     * 3, 5, 6, 9, A, C - ?
     */

    return 0;
}
