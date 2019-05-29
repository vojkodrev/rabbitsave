#!/bin/bash

set -e

wait_for_rabbit () {
  while ! rabbitmqctl cluster_status > /dev/null 2>&1; do
    echo "$CLUSTER_NODENAME not online, retrying ..."
    sleep 0.25  
  done 
}

wait_for_rabbit

rabbitmqctl stop_app 

rabbitmqctl reset 

rabbitmqctl join_cluster $CLUSTER_NODENAME

rabbitmqctl start_app

wait_for_rabbit

rabbitmqctl cluster_status


