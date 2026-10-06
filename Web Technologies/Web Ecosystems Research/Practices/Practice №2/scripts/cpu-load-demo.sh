#!/usr/bin/bash

{
  js ./redos-test.js &
  PID=$!
  HZ=$(getconf CLK_TCK)
  prev=0
  while kill -0 $PID 2>/dev/null; do
    sleep 1
    # удалил "pid (comm) " из вывода чтобы поля начинались с состояния;
    # utime и stime являются [11] и [12] соответственно
    read -r -a f < <(sed 's/.*) //' /proc/$PID/stat 2>/dev/null) || break
    ticks=$(( f[11] + f[12] ))
    echo $(( (ticks - prev) * 100 / HZ ))
    prev=$ticks
  done
} | tee ./logs/cpu-log.txt
