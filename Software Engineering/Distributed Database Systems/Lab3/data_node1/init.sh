PGDATA=$HOME/wyd81
PGLOCALE=C
PGENCODE=KOI8R
PGUSERNAME=postgres1
PGHOST=pg104
PGWAL=$PGDATA/pg_wal

export PGDATA PGLOCALE PGENCODE PGUSERNAME PGHOST PGWAL

mkdir -p $PGDATA

chown postgres1 $PGDATA

chmod 750 $PGDATA

initdb --locale=$PGLOCALE --encoding=$PGENCODE --username=$PGUSERNAME --lc-messages=$PGLOCALE --lc-numeric=$PGLOCALE --lc-monetary=$PGLOCALE --lc-time=$PGLOCALE --no-locale

pg_ctl -D $PGDATA -l $PGDATA.server.log start
