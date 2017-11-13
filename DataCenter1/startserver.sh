#!/bin/bash

LOCATOR_PORT=10334
SERVER2_PORT=40405
PROJECT_JARS=../../target/19_WAN-1.0-SNAPSHOT.jar
GEMFIRE=/Users/wwilliams/Downloads/Pivotal_GemFire_827_b18_Linux/

#. ../../setenvironment.sh

gfsh <<!

connect

start server --name=server1 --locators=localhost[$LOCATOR_PORT] --J=-Xms512m --J=-Xmx512m --classpath=$PROJECT_JARS --cache-xml-file=config/cache.xml --properties-file=config/gemfire.properties 


list members;

list regions;

exit;
!
