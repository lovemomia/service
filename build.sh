#!/bin/sh

mvn -U -pl base/api-base,course/api-course,event/api-event,poi/api-poi,user/api-user clean install
mvn -U -pl base/service-base,course/service-course,event/service-event,poi/service-poi,user/service-user clean install
mvn -U -pl service clean package
