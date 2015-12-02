#!/bin/sh

mvn -U -pl base/api-base,course/api-course,event/api-event,feed/api-feed,im/api-im,poi/api-poi,user/api-user clean install
mvn -U -pl base/service-base,course/service-course,event/service-event,feed/service-feed,im/service-im,poi/service-poi,user/service-user clean install
mvn -U -pl service clean package
