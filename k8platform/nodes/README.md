# Lainotik nodes in k8 Cluster

The lainotik platform will deploy its functionality within K8 clusters.

In order to have the K8 cluster manager decide in what nodes the Lainotik platform deploys its functionality, the K8 cluster manager could set an specific label in the chosen nodes. Thanks to this approach, the k8 cluster manager has full control of in which nodes the lainotik functionality is deployed, in such a way that different functionality can play in the same k8 cluster, each in its set of node resources.

As the K8 cluster administrator, to check the labels of the nodes of the k8 cluster, run the following command:

    $ kubectl get nodes --show-labels

To check the labels of a particular node (in this example the node is called "mykube")

    $ kubect describe node mykube

This command provides much more info than the actual labels, actually, all the information about the node. If we focus on the labels we can see that there are already many defined providing information about the node. For example:

* kubernetes.io/arch=amd64
* kubernetes.io/hostname=mykube
* kubernetes.io/os=linux
* minikube.k8s.io/name (Name of the node)
* node-role.kubernetes.io

To assign a label specific label to a node the following command could be used:

    $ kubectl label nodes mykube specificlabel=standard

To get all the nodes with a particular label value:

    $ kubectl get nodes -l specificlabel=standard

## lainotik.io/node values
The lainotik platform deploys different types of pods depending on the data services and apps the user has selected. Each service/app, has different node resource requirenments, which also depends on the chosen deployment plans. This is why, the lainotik.io/node label for a node could take different values. 

TBD






