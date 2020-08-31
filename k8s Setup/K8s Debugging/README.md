## Debuging Tools
The following are basic tools that can help debugging an inplementation. They just create symples pods with basic specific functionality that can be used to check the core dns, URL access.

### dnsutils
Allows debuging the core dns within the k8 cluster. Usage:

    $ kubectl apply -f dnsutils.yaml
    $ kubectl exec -ti dnsutils -- nslookup  xxxaddress
    $ kubectl delete pod dnsutils

### curlutils
Allows debuging web access within the k8 cluster. Usage:

    $ kubectl apply -f curlutils.yaml
    $ kubectl exec -ti curlutils -- curl http://admin:password@dataflow1-lainocmak-cs:9000/cmak/clusters
    $ kubectl delete pod curlutils