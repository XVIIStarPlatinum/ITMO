obj-m = ch_drv.o
PWD = $(shell pwd)
all:
	make -C /lib/modules/$(shell uname -r)/build M="$(PWD)" modules
clean:
	make -C /lib/modules/$(shell uname -r)/build M="$(PWD)" clean

load:
	sudo insmod ch_drv.ko

uload:
	sudo rmmod ch_drv

lcheck:
	@sudo lsmod | grep ch_drv || true