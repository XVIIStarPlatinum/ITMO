FROM ubuntu
RUN apt update && apt install -y clang
WORKDIR /pa2
COPY ./src .
COPY ./vendor/libruntime.so .
ENV LD_LIBRARY_PATH="/pa2"
RUN clang -std=c99 -Wall -pedantic *.c -o pa2 -L. -lruntime && \
    chmod +x pa2
