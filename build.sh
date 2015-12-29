#!/bin/sh

mvn -U -pl course/api-course,feed/api-feed,im/api-im,operate/api-operate,poi/api-poi,teacher/api-teacher,user/api-user clean install
mvn -U -pl course/service-course,feed/service-feed,im/service-im,operate/service-operate,poi/service-poi,teacher/service-teacher,user/service-user clean install
mvn -U -pl service clean package
