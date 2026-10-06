#!/usr/bin/bash

INPUT_FILE="../logs/redos-raw.txt"
OUTPUT_FILE="../logs/time-log.txt"

if [ ! -f "$INPUT_FILE" ]; then
    echo "[ERROR] Файл $INPUT_FILE не найден!"
    exit 1
fi

COUNT=$(grep -c 'REDOS_RESULT' "$INPUT_FILE")

if [ "$COUNT" -ge 25 ] ; then
    mkdir -p logs

    sed -n '/REDOS_RESULT/{s/.*REDOS_RESULT,//; s/,false//g; s/"//g; p;}' "$INPUT_FILE" > "$OUTPUT_FILE"
    echo "[OK] Обработано строк: $COUNT. Результат в $OUTPUT_FILE"
else
    echo "[SKIP] Строк с результатом: $COUNT (ожидалось ~26). Пропускаю."
fi
