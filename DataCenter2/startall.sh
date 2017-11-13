#!/bin/bash

# assign different ports than those of data center 2
LOCATOR_PORT=10335
SERVER1_PORT=40406
SERVER2_PORT=40407
PROJECT_JARS=../../target/19_WAN-1.0-SNAPSHOT.jar
LOCAL_IP=
IP=$(ifconfig | awk '/inet /{print substr($2,1)}' | tail -n1)

echo IP=$IP

GEMFIRE=/Users/wwilliams/Downloads/Pivotal_GemFire_827_b18_Linux/

#. ../../setenvironment.sh

#
#  Note that the two servers here point to the locator on port 10335 and not 10334
#

gfsh <<!

start locator --name=locator2 --properties-file=config/locator.properties --port=$LOCATOR_PORT --J=-Xms256m --J=-Xmx256m

start server --name=server3 --locators=localhost[$LOCATOR_PORT] --J=-Xms512m --J=-Xmx512m --cache-xml-file=config/cache.xml --properties-file=config/gemfire.properties  --server-port=$SERVER1_PORT --J=-Dgemfire.start-dev-rest-api=true --J=-Dgemfire.http-service-bind-address=$IP --J=-Dgemfire.http-service-port=7075 --classpath=$PROJECT_JARS

#start server --name=server4 --locators=localhost[$LOCATOR_PORT] --J=-Xms512m --J=-Xmx512m --classpath=$GEMFIRE/lib/server-dependencies.jar:$PROJECT_JARS --cache-xml-file=config/cache.xml  --properties-file=config/gemfire.properties --server-port=$SERVER2_PORT

list members;

list regions;

exit;
!
