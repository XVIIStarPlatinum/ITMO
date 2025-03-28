#!/bin/bash
set -xue

QEMU=qemu-system-riscv32

# Путь к clang и его флагам
CC=clang  # Для Ubuntu (и WSL): используйте CC=clang
CFLAGS="-std=c11 -O2 -g3 -Wall -Wextra --target=riscv32 -ffreestanding -nostdlib"

# Сборка ядра
$CC $CFLAGS -Wl,-Tkernel.ld -Wl,-Map=kernel.map -o kernel.elf \
    kernel.c

# Запуск QEMU
$QEMU -machine virt -bios default -nographic -serial mon:stdio --no-reboot \    -kernel kernel.elf