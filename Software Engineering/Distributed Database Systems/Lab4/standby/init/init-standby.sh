#!/bin/bash
set -e

until pg_isready -h primary -p 5432 -U postgres; do
  echo "Waiting for primary to be ready..."
  sleep 2
done

pg_ctl stop -D "$PGDATA"

rm -rf "$PGDATA"/*
echo "Data directory cleaned up"

PGPASSWORD='replicator_password' pg_basebackup -h primary -D /var/lib/postgresql/data -U replicator -v -P --wal-method=stream
echo "Base backup completed"

touch "$PGDATA/standby.signal"
chown -R postgres:postgres "$PGDATA"

cp /etc/postgresql/postgresql.conf "$PGDATA/postgresql.conf"
cp /etc/postgresql/pg_hba.conf "$PGDATA/pg_hba.conf"
echo "Conf files copied"

pg_ctl -D "$PGDATA" start
