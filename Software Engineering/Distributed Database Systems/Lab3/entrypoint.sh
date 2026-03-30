#!/bin/bash
set -e

ssh-keygen -A

mkdir -p /home/postgres1/.ssh
chown postgres1:postgres1 /home/postgres1/.ssh || true
chmod 700 /home/postgres1/.ssh || true

if [ -f /home/postgres1/.ssh/authorized_keys ]; then
  chown postgres1:postgres1 /home/postgres1/.ssh/authorized_keys || true
  chmod 600 /home/postgres1/.ssh/authorized_keys || true
fi

if [ -f /home/postgres1/.ssh/id_rsa ]; then
  chown postgres1:postgres1 /home/postgres1/.ssh/id_rsa || true
  chmod 600 /home/postgres1/.ssh/id_rsa || true
fi

chown -R postgres1:postgres1 /var/lib/postgresql || true
chmod 700 /var/lib/postgresql || true

exec /usr/sbin/sshd -D