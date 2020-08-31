# Lainotik storage in K8 cluster

Container storage is ephemeral, but often in many use cases persistent storage is required. There are two Kubernetes storage paradigms: Dynamic Storage and CSI.

* Dynamic Storage: A pod can reference a PersistentVolumeClaim (PVC) that will be created but still unbounded to any volume. The PVC should have a binding mode. If the binding mode of that claim is to wait for the first consumer, nothing is done. Otherwise, 0r after the first client, the StorageClass of the claim is checked by the Kubernetes volume controller, in such a way that if known, the controller will make the volume, bound it with the PVC, and allowing the pod to mount it. Therefore, StorageClasses enables dynamic provisioning, as this defines using the right storage for each app.

* Container Storage Interface (CSI): It is an interface for implementing persistent volumes. Until CSI, there was no way to force the kubelet to mount storage using custom code that could be bundled, shipped, and lifecycled outside of a Kubernetes distribution. Combining CSI with Dynamic Provisioning enables declarative storage for pods, on any volume, without any dependencies on Kubernetes.

# Storage in Minikube
Minikube ships its own Dynamic Provisioner as a default activated addon, which uses *gasp*, hostPath. First lets check the available storage clases in minikube:

     $ kubectl get storageclass

Minikube has another addon activated by default that starts a storage class on its own called "standard", managed by a provisioner called "k8s.io/minikube-hostpath". This one follows the same model that a CSI PV fulfillment will. If no storageclass has been defined in a pvc this is the one used within minikube.

Each StorageClass contains the fields provisioner, parameters, and reclaimPolicy (and additionally allowVolumeExpansion, mountOptions, volumeBindingMode):

* provisioner: It could be a cloud service, NFS or even local.
* parameters: These are specific to each provisioner.
* reclaimPolicy: PersistentVolumes that are dynamically created by a StorageClass can be Delete and retained. Recycle is also suported in NFS and local providers.
* allowVolumeExpansion: Some providers allow pv expanding, which will apply is this iss et to yes. (They edit the pvc object)
* mountOptions: 
* volumeBindingMode: Could be Immediate or WaitForFirstConsumer.

## Persistent Volume storage in Minikube
TBD

* To start over again remove all pvc (persistent volume claims) and pv (persistent volumes) objects through this commands:

    kubectl delete pvc --all
    kubectl delete pv --all

* Go to minikube with the following command:

    minikube ssh -p mykube

* Within minikube run the following script:

```shell
    #!/bin/bash
    sudo rm -r /data/vol*
    sudo mkdir /data/vol{1..50}
    sudo chmod -R 0777 /data/vol*
```

* Create persistent volumes with the following:

    kubectl apply -f persistentvolume.yaml


