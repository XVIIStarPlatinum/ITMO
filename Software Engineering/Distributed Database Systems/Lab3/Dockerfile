FROM ubuntu:24.04

RUN mkdir -p /etc/postgresql-common \
    && echo "create_main_cluster = false" > /etc/postgresql-common/createcluster.conf

RUN apt-get update && apt-get install -y --no-install-recommends \
    openssh-server sudo ca-certificates \
    postgresql postgresql-contrib \
    cron nano lsof rsync \
    && rm -rf /var/lib/apt/lists/*

RUN useradd -m -s /bin/bash postgres1 \
    && echo "postgres1 ALL=(ALL) NOPASSWD:ALL" > /etc/sudoers.d/postgres1 \
    && chmod 0440 /etc/sudoers.d/postgres1

RUN echo "postgres1:4hxuMvSO" | chpasswd

RUN mkdir -p /var/run/sshd /var/lib/postgresql /var/log/postgresql \
    && chown postgres1:postgres1 /var/lib/postgresql /var/log/postgresql

COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

EXPOSE 22 5432

COPY entrypoint.sh /entrypoint.sh
RUN sed -i 's/\r$//' /entrypoint.sh && chmod +x /entrypoint.sh

CMD ["/entrypoint.sh"]