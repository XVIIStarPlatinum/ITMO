FROM ubuntu
RUN apt update && apt install -y clang
WORKDIR /pa1
COPY ./src .
COPY ./vendor/libruntime.so .
ENV LD_LIBRARY_PATH="/pa1"
RUN clang -std=c99 -Wall -pedantic *.c -o pa1 -L. && \
    chmod +x pa1
