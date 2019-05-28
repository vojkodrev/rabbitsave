#!/bin/bash

set -e

sleep $WAIT_FOR_RABBIT

rabbitmqctl stop_app 

rabbitmqctl reset 

rabbitmqctl join_cluster $CLUSTER_NODENAME

rabbitmqctl start_app

sleep `expr $WAIT_FOR_RABBIT / 3`

rabbitmqctl cluster_status


