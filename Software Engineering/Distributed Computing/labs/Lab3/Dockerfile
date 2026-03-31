FROM ubuntu
RUN apt update && apt install -y clang
WORKDIR /pa3
COPY ./src .
COPY ./vendor/libruntime.so .
ENV LD_LIBRARY_PATH="/pa3"
RUN clang -std=c99 -Wall -pedantic *.c -o pa3 -L. -lruntime && \
    chmod +x pa3
