### Сборка и запуск

#### Docker

```bash
./scripts/enter.sh

# внутри контейнера
./pa[N] -p 3 [--mutexl]

# чтобы пересобрать
clang -std=c99 -Wall -pedantic *.c -o pa[N] -L. [-lruntime] && chmod +x pa[N]
```

#### Локально

```bash
cd src
export LD_LIBRARY_PATH="../vendor:$LD_LIBRARY_PATH"
clang -std=c99 -Wall -pedantic *.c -L../vendor [-lruntime] -o pa[N]
./pa[N] -p 3 [--mutexl]
```

### Архивация

Создаёт пригодный для отправки на проверку файл `pa[N].tar.gz` со всеми `.c` и `.h` файлами:

```bash
./scripts/archive.sh
```

* *[N] --- номер лабораторной работы
* *[--mutexl] --- опциональный аргумент для лабораторных кроме №1
