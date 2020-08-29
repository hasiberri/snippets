# Lainotik Deployment Plans

The lainotik platform will deploy bigdata services within any K8 clusters.

In order to have the K8 cluster manager decide in what nodes the Lainotik platform deploys the bigdata services, the K8 cluster manager could set an specific label in the chosen nodes. Thanks to this approach, the k8 cluster manager has full control of in which nodes the lainotik bigdata services are deployed, in such a way that different functionality can play in the same k8 cluster, each in its set of node resources.

The node labels to consider depend on the selected deployment plan and the specific app/service being deployed.

## Lainotik deployment plans
The lainotik platform deploys different types of pods depending on the data services and apps the user has selected. Each service/app has different node resource requirenments depending on the selected deployment plan. Three deployment plans are defined by the lainotik Platform:

* Playgroud: Requires minimum resources. Used to play with the platform, not used for production.
* Standard: Production ready, but focused in the minimum cost of resources
* Advanced: Production ready, focused on performance

Get in touch with Lainotik to know about the deployment plans and its specific node labels. 

## Node label management

As the K8 cluster administrator, to check the labels of the nodes of the k8 cluster, run the following command:

```shell
$ kubectl get nodes --show-labels
```

To check the labels of a particular node (in this example the node is called "mykube")

```shell
$ kubect describe node mykube
```

This command provides much more info than the actual labels, actually, all the information about the node. If we focus on the labels we can see that there are already many defined providing information about the node. For example:

* kubernetes.io/arch=amd64
* kubernetes.io/hostname=mykube
* kubernetes.io/os=linux
* minikube.k8s.io/name (Name of the node)
* node-role.kubernetes.io

To assign a specific label to a node the following command could be used:

```shell
$ kubectl label nodes mykube specificlabel=standard
```

To get all the nodes with a particular label value:

```shell
$ kubectl get nodes -l specificlabel=standard
```

Here a summary of the different commands that can be used to manage node labels:

```shell
$ kubectl get nodes                                      # Get all nodes
$ kubectl describe nodes mykube                          # Describe a particular node
$ kubectl get nodes mykube --show-labels                 # show all labels of a particular node
$ kubectl label nodes mykube specificlabel=standard      # add a label to a particular node
$ kubectl get nodes -l specificlabel=standard            # Get all nodes with a particular label
$ kubectl label node mykube specificlabel-               # delete a label form a particular node
```







