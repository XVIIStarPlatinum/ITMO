#!/usr/bin/bash

Ladybird ladybird-redos-demo-4.html 2>&1 | tee logs/redos-raw.txt &&
bash ./cpu-load-demo.sh &&
bash ./time-demo.sh

python -m time_chart.py &&
scp -P 2222 ../ladybird-redos-demo-4.html s"$ISU_ID"@se.ifmo.ru:~/public_html/masters/web_eco_re
