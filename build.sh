#!/bin/sh

mvn -U -pl course/api-course,discuss/api-discuss,feed/api-feed,im/api-im,poi/api-poi,user/api-user clean install
mvn -U -pl course/service-course,discuss/service-discuss,feed/service-feed,im/service-im,poi/service-poi,user/service-user clean install
mvn -U -pl service clean package
