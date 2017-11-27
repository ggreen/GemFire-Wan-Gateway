#!/bin/bash

if [ "$#" -ne 1 ]
then
  echo "Usage: $0 [1-2]"
  exit 1
fi
export DISTRIBUTED_ID=$1

export REMOTE_DISTRIBUTED_ID=2

# -----------------------------
# Process distributed ID
if [ "$DISTRIBUTED_ID" -eq "2" ]
then
  export REMOTE_DISTRIBUTED_ID=1
fi


export WORK_DIR=/Projects/Financial/Fidelity/dev/GemFire-Wan-Gateway/cluster-config/runtime
export PULSE_HTTP_PORT="$DISTRIBUTED_ID"7070
export JMX_MANAGER_PORT="$DISTRIBUTED_ID"1099
export LOC_MEMBERSHIP_PORT_RANGE="$DISTRIBUTED_ID"0901-"$DISTRIBUTED_ID"0999
export LOC_TCP_PORT="$DISTRIBUTED_ID"0001
export LOCATOR_HOST=localhost
export LOCATOR_PORT="$DISTRIBUTED_ID"0000
export LOCATOR_NM=locator_"$DISTRIBUTED_ID"_1

export CS_PORT="$DISTRIBUTED_ID"0100
export CS_TCP_PORT="$DISTRIBUTED_ID"0002
export CS_MEMBERSHIP_PORT_RANGE="$DISTRIBUTED_ID"0801-"$DISTRIBUTED_ID"0899
export CS_NM=server_"$DISTRIBUTED_ID"_1


export REMOTE_LOCATOR_HOST=localhost
export REMOTE_LOCATOR_PORT="$REMOTE_DISTRIBUTED_ID"0000


#---------------------------------
# Print out for debugging
echo DISTRIBUTED_ID=$DISTRIBUTED_ID
echo REMOTE_DISTRIBUTED_ID=$REMOTE_DISTRIBUTED_ID


# Start locator
gfsh -e "start locator --name=$LOCATOR_NM --port=$LOCATOR_PORT --dir=$WORK_DIR/$LOCATOR_NM --J=-Dgemfire.jmx-manager-port=$JMX_MANAGER_PORT --J=-Dgemfire.remote-locators=$REMOTE_LOCATOR_HOST[$REMOTE_LOCATOR_PORT] --J=-Dgemfire.distributed-system-id=$DISTRIBUTED_ID  --J=-Dgemfire.tcp-port=$LOC_TCP_PORT --J=-Dgemfire.membership-port-range=$LOC_MEMBERSHIP_PORT_RANGE   --enable-cluster-configuration=true --http-service-port=$PULSE_HTTP_PORT"

# Configure pdx cache server
gfsh -e ="connect --locator=$LOCATOR_HOST[$LOCATOR_PORT]" -e "configure pdx --auto-serializable-classes=.* --read-serialized" -e "shutdown --include-locators=true"

# Restart locator
gfsh -e "start locator --name=$LOCATOR_NM --port=$LOCATOR_PORT --dir=$WORK_DIR/$LOCATOR_NM --J=-Dgemfire.jmx-manager-port=$JMX_MANAGER_PORT --J=-Dgemfire.remote-locators=$REMOTE_LOCATOR_HOST[$REMOTE_LOCATOR_PORT] --J=-Dgemfire.distributed-system-id=$DISTRIBUTED_ID  --J=-Dgemfire.tcp-port=$LOC_TCP_PORT --J=-Dgemfire.membership-port-range=$LOC_MEMBERSHIP_PORT_RANGE   --enable-cluster-configuration=true --http-service-port=$PULSE_HTTP_PORT"

# Start Cache Server
gfsh -e  "start server --name=$CS_NM --locators=$LOCATOR_HOST[$LOCATOR_PORT] --dir=$WORK_DIR/$CS_NM --use-cluster-configuration=true --server-port=$CS_PORT --J=-Dgemfire.tcp-port=$CS_TCP_PORT --J=-Dgemfire.membership-port-range=$CS_MEMBERSHIP_PORT_RANGE "

# Create Gateway
gfsh -e ="connect --locator=$LOCATOR_HOST[$LOCATOR_PORT]" -e "create gateway-receiver" -e "create gateway-sender --id=sender_to_$REMOTE_DISTRIBUTED_ID --remote-distributed-system-id=$REMOTE_DISTRIBUTED_ID --parallel=true "

# Create Regions
gfsh -e ="connect --locator=$LOCATOR_HOST[$LOCATOR_PORT]" -e "create region --name=test --type=PARTITION_REDUNDANT_PERSISTENT --gateway-sender-id=sender_to_$REMOTE_DISTRIBUTED_ID"
