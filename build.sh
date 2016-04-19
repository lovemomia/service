#!/bin/sh

mvn -U -pl course/api-course,discuss/api-discuss,im/api-im,poi/api-poi,user/api-user clean install
mvn -U -pl course/service-course,discuss/service-discuss,im/service-im,poi/service-poi,user/service-user clean install
mvn -U -pl service clean package
