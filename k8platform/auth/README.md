# Lainotik Authentication and Authorization in k8 Cluster

In order for the Lainotik platform to access a particular k8 cluster it is requiered to set proper authentication and authorization for it. This is mainly the task of the owner of the k8 cluster, k8 cluster administrator and, without any doubt, security is the must have requirenment to consider.

Here how this authentication and authorization is setup is explained in detail, describing the steps that need to be followed by the K8 cluster administrator and the third party that requires the authentication/authorization (in our case the Lainotik Platform).

## Introduction to certifications management

Kubectl, the "the facto" tool to manage kubernetes platforms, uses a file called kubeconfig to access K8 clusters. By default, this kubeconfig is located under ~/.kube/config. The location of the Kubeconfig can be set through the KUBECONFIG environment variable. In this environment variable several kubeconfig paths can be set separated by ":", being able to switch between them through kubectl, so it is recommended to set the default one and then go adding new ones if needed.

The kubeconfig file requires the following elements to allow kubectl access to a k8 cluster (see the folder examples-kubeconfig):

* IP/Port of the K8 platform API server
* The k8 platform internal CA certificate
* User credentials in the form of a private key and client certificate, or a token

When a k8 cluster is setup by the k8 cluster administrator, this includes internally a cluster CA (Certificate Authority). The k8 cluster administrator kubectl is also setup with the admin kubeconfig that includes the admin credentials to access the k8 cluster. These admin credentials are own by the k8 cluster administrator responsible of the k8 cluster, and must be kept safe.

In order for a third party to get access, the three elements mentioned above are required, which will be used for the third party to create a kubeconfig. Once created, the access for the third party to the K8 cluster will be available with the proper authentication and authorization. Therefore, here two actors have their play, the k8 cluster administrator (responsible for the K8 cluster) and the third party that requires access to the k8 cluster (Lainotik platform for example).

This document will provide a full description of how to setup authentication and authorization based on private key/certificates for a third party by the k8 cluster administrator.  For this, it will be considered the user name as "lainoadmin", and the name of the k8 cluster as "mykube". To facilitate the description a set of yaml templates are provided:

* templates/lainotik-cr.yaml
* templates/lainotik-crb.yaml
* templates/lainotik-csr.yaml

Additionally serveral shell scripts have been created to faciliate the work:

* clientkeycsr-build.sh
* clientcacrt-build.sh
* clientkubeconfig-build.sh

The use of this templates and scripts will be explained accross the whole description. Note that the generated certification files will be stored under /certs folder, while the k8 manifests will be stored under /manifests.

## Create key and credential signing request (Third Party)

In all this process, the first step is taken by the Third Party, which creates a private key and a certificate signing request (CSR). This can be done using openssl or any other similar tool:

     $ openssl req -new -newkey rsa:4096 -nodes -keyout client.key -out client.csr -subj "/CN=lainoadmin /O=lainoadmin-mykube"

WARNING: Keeping the client.key (the private key) safe is really critical. If this is in the open, then there is a risk that other that the third party could access the k8 cluster, so be carefull. Note everytime this openssl command is run a different key and CSR is generated. 

In order to help in the testing, here a few more uses of openssl that allows getting the public key from a private key, from a CSR, and from a client certificate:

    $ openssl req -in certs/client.csr -noout -pubkey -out clientcsr.pub
    $ openssl rsa -in certs/client.key -pubout > clientkey.pub
    $ openssl x509 -pubkey -noout -in certs/client.crt  > clientcrt.pub

Note that, if everything is done correctly, the three of them must generate the same public key. As expected, applying this openssl commands you can verify that the public key obtained from the key and the CSR is the same.

The common Name (CN) and organization (O) parameters are very important. As will be shown later, Kubernetes uses the Common Name (CN) to match the client certificate with the user name in the RoleBinding and uses the Organization (O) to match the group in the RoleBinding. More about this later.

To facilitate these step, the following shell script can be applied to generate the client private hey and the CSR:

    $ ./client-csr-build.sh lainoadmin mykube

## Obtaining the signed certificates (K8 Cluster Administrator)

The CSR needs to be signed through the cluster CA in order to obtain the client certificate. For that, as a first step, a CertificateSigningRequest object must be created in the K8 cluster with the CSR. As the K8 cluster administrator is the only one that has access to the k8 cluster with Kubectl in the first place, this can only be done by him/her. Important: Overwriting this object does not work, therefore before creating the object make sure to delete it first if exists (the name of this object in this example is lainotik-lainoadmin-csr):

    $ kubectl get csr
    $ kubectl delete csr lainotik-lainoadmin-csr

The k8 cluster administrator should be applying the lainotik-csr.yaml template to create the CertificateSigningRequest object in the k8 cluster, in which the base64 output of the CSR must be included under the requests field. Therefore, as the first step, the third party must provide the CSR to the infra team (not the private key), who them will insert within the lainotik.yaml the CSR as follows:

    $ cat certs/client.csr | base64 | tr -d '\n'
    copy the output within lainotik-csr.yaml request field
    $ kubectl apply -f lainotik-csr.yaml
    $ kubectl get csr # To check

Once applied the yaml, the CertificateSigningRequest object will be in condition "Pending". This means that this object is pending to be approved by the k8 cluster administrator responsible for the k8 cluster before it is signed. To approve it, the following command must be applied:

    $ kubectl certificate approve lainotik-lainoadmin-csr

After this, the condition of the object will change to "Aproved/Issued". This is the "signal" to indicate that the client certificate signing request has been completed and that there is a client certificate available that can be used with the original client private key to access the K8 cluster. In order to get the newly created client certificate, the k8 cluster administrator can apply the following command:

    $ kubectl get csr lainotik-lainoadmin-csr -o jsonpath='{.status.certificate}' | base64 --decode > client.crt

Checking the obtained CRT file we can confirm that it is actually a certificate, and with openssl it can be confirmed that it carries the same public key as the original CSR. Once the client certificate is obtained, the only thing missing is the CA certificate of the k8 cluster. The k8 cluster administrator can also obtain this through the following command:

    $ cat $(kubectl config view -o jsonpath='{.clusters[0].cluster.certificate-authority}') > ca.crt

With the client.crt and the ca.crt available, it is just a matter of providing both to the third party, together with the IP address of the K8 platform.

## Assigment of roles (k8 Cluster Administrator)

Having the key and the certificates gives the third party authentication access to the k8 cluster, but still it is necessary to define the authorization to this third party within the k8 cluster. Authorization defines the objects the third party can play with within the K8 cluster.

Here K8s RBAC (Role-based access control) is what takes place. In RBAC, roles are scoped to either the entire cluster via a ClusterRole object or to a particular namespace via a Role object. Setting the RBAC access control for the Third party is again a task for the k8 cluster administrator.

The goal here is to provide to the new user, the third party, proper authorization to operate within the k8 cluster. The way it works is as follows: First, the k8 cluster administrator must create a role object in the k8 cluster (clusterRole or namespace Role) with the right authorization, and after, this role should be assigned to the third party user by means of a role binding object.

The following k8 templates have been defined to get a better idea of how to use the role and binding process. Here a clusterRole is considered for the third party:

* lainotik-cr.yaml  # to create a cluster role with the corresponding authorization
* lainotik-crb.yaml  # to create a cluster role binding with the user

To facilitate the understanding, all the process to create a CertificateSigningRequest object, obtain the certificates and set the RBAC has been defined within a shell script:

    $ ./client-crt-build.sh lainoadmin

## Setting Kubeconfig and accessing the platform (Third Party)

Once the third party gets the client.crt and the ca.crt from the k8 cluster administrator, it is a matter of creating a kubeconfig with them. Note that the third party has the client.key, which as mentioned already, must be kept safe.

A Kubeconfig has three sections, cluster, credentials and context. Here the steps to create it through kubectl commands:

Set the cluster:

    $ kubectl config set-cluster $(kubectl config view -o jsonpath='{.clusters[0].name}') --server=$(kubectl config view -o jsonpath='{.clusters[0].cluster.server}') --certificate-authority=certs/ca.crt --kubeconfig=manifests/lainotik-config.yaml --embed-certs

Set credentials (certificate and keys) for the new user:

    $ kubectl config set-credentials lainoadmin --client-certificate=certs/client.crt --client-key=certs/client.key --embed-certs --kubeconfig=manifests/lainotik-config.yaml

Add context:

    $ kubectl config set-context lainoadmin-mykube --cluster=$(kubectl config view -o jsonpath='{.clusters[0].name}') --user=lainoadmin --kubeconfig=manifests/lainotik-config.yaml

Set this newly created context as the one to be used:

    $ kubectl config use-context lainotik-mykube --kubeconfig=manifests/lainotik-config.yaml

All this can be done using a template yaml, in our example templates/lainotik-config.yaml. Note that in this commands, and in the yaml itself, a reference to the key/cert files is used. Instead it is posible to embed base64 coded data of then in the yaml intself. Depending on wheter the files or the base64 data is used, the parameter should be called "certificate-authority" or "certificate-authority-data" respectively.

Finally just test that the authorization/authentication has been set properly:

    $ kubectl version --kubeconfig=manifests/lainotik-config.yaml

Then any Kubectl command can be tried, like this ones:

    $ kubectl get namespaces --kubeconfig=manifests/lainotik-config.yaml
    $ kubectl get pods --kubeconfig=manifests/lainotik-config.yaml
    $ kubectl get services --kubeconfig=manifests/lainotik-config.yaml
    $ ...

It might happen that the commands do not work because the action is forbidden. Note that this is due to the RBAC authorization settings for the clusterRole. Modify the clusterRole options in lainotik-crb.yaml as needed to make the kubectl commands work. Note that the right authorization, as limited as posible, must be set for the specific clusterRole in order to enforce security.

To facilitate the understanding, all the process to create a kubeconfig as been defined in a shell script (the ip is a ficticious one representing the k8 cluster ip):

    $ ./client-kubeconfig-build.sh lainoadmin mykube 192.168.99.107


