#!/bin/bash

JAVA_OPTS="-Xms2G -Xmx2G -Xmn512M \
	-XX:PermSize=128M -XX:MaxPermSize=128M \
	-XX:+UseConcMarkSweepGC -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=80 \
	-XX:+PrintGCDetails -XX:+PrintGCDateStamps -verbose:gc -XX:+DisableExplicitGC \
	-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8888"
