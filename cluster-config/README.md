# Overview

The following demonstrates example scripts to start active active GemFire cluster across two GemFire cluster using
GemFire's cluster configuration services. Each cluster consist of the locator and data node.

# Usage

Use the following to start the first the cluster

	./startcluster.sh 1

Use the following to start the first the cluster

	./startcluster.sh 2


Connect to cluster 1 using the following gfsh command

	connect --locator=[10000]

Connect to cluster 2 using the following gfsh command

	connect --locator=[20000]


Note there is a single example region named "test". 
Updating or remove region entries will be reflect in each cluster 
