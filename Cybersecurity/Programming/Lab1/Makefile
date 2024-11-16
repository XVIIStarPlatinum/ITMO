CFLAGS     = -g -O2 -Wall -Werror -std=c17 -Wdiscarded-qualifiers -Wincompatible-pointer-types -Wint-conversion
CC         = gcc
LD         = gcc

main: main.o
	$(LD) -o $@ $^

main.o: main.c
	$(CC) -c $(CFLAGS) -o $@ $<

clean:
	$(RM) main main.o

.PHONY: clean
