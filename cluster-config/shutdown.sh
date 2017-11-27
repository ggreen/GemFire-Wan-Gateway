#!/bin/bash

if [ "$#" -ne 1 ]
then
  echo "Usage: $0 [1-2]"
  exit 1
fi

export LOCATOR_HOST=localhost 
export DISTRIBUTED_ID=$1
export REMOTE_DISTRIBUTED_ID=2


if [ "$DISTRIBUTED_ID" -eq "2" ]
then
  export REMOTE_DISTRIBUTED_ID=1
fi

export LOCATOR_PORT="$DISTRIBUTED_ID"0000
echo  LOCATOR_PORT $LOCATOR_PORT


gfsh -e ="connect --locator=$LOCATOR_HOST[$LOCATOR_PORT]" -e "configure pdx --auto-serializable-classes=.* --read-serialized" -e "shutdown --include-locators=true"



