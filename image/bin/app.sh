#!/bin/bash

source "/etc/profile"

if [ $# -lt 1 ]; then
    echo "usage: $0 start|stop [port]" >&2
    exit 1
fi

if [ $# -eq 2 ]; then
    PORT=$2
fi

cd `dirname $0`/..

APPNAME="duola-image"

LOGSDIR="logs"
SYSLOGDIR="/data/applogs/$APPNAME"
PIDFILE="bin/server.pid"
CLASSPATH=".:lib/*"
GCLOGPATH="logs/gc.log"

source "bin/env.sh"

if [ ! -d $LOGSDIR ]; then
    if [ -d $SYSLOGDIR ]; then
        ln -s $SYSLOGDIR $LOGSDIR
    else
        mkdir $LOGSDIR
    fi
fi

case $1 in
start)
    echo "starting $APPNAME ..."
    if [ -f $PIDFILE ]; then
      if kill -0 `cat $PIDFILE` > /dev/null 2>&1; then
         echo "$APPNAME already running as process `cat $PIDFILE`"
         exit 0
      fi
    fi

    nohup java $JAVA_OPTS -cp $CLASSPATH cn.momia.image.web.ImageWeb $PORT >> $GCLOGPATH 2>&1 &
    pid=$!
    sleep 3
    kill -0 $pid
    if [ $? -eq 0 ]
    then
        echo $pid > $PIDFILE
        echo "$APPNAME is started"
    else
        echo "$APPNAME start error"
        exit 1
    fi
    ;;

stop)
    SLEEP=120
    echo "stopping $APPNAME..."
    if [ ! -f "$PIDFILE" ]
    then
      echo "no $APPNAME to stop (could not find file $PIDFILE)"
    else
      kill $(cat "$PIDFILE")
      while [ $SLEEP -ge 0 ]; do
        kill -0 $(cat "$PIDFILE") >/dev/null 2>&1
        if [ $? -eq 1 ]; then
          rm "$PIDFILE"
          break
        fi
        if [ $SLEEP -gt 0 ]; then
          sleep 1
        fi
        if [ $SLEEP -eq 0 ]; then
          echo "fail to stop $APPNAME"
          exit 1
        fi
        SLEEP=`expr $SLEEP - 1 `
      done
      echo "$APPNAME is stopped"
    fi
    exit 0
    ;;

restart)
    shift
    "bin/`basename $0`" stop ${@}
    sleep 3
    "bin/`basename $0`" start ${@}
    ;;

*)
    echo "usage: $0 start|stop [port]" >&2
esac
