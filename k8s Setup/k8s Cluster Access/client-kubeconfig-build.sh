#!/bin/bash

# $1: The reference/id of the companykuster. Used as group in Kubernetes
# $2: IP address of the k8 platform
if [[ $# -eq 0 ]] ; then
    echo 'Error: No arguments'
    exit 0
fi

# get CN (username) and O (organization) from csr in certs
subject=$(openssl req -in certs/client.csr -noout -subject)
subjetParams=${subject:8}
subjectParamCN=${subjetParams%,*}
subjectParamO=${subjetParams##*,}
subjectCN=`echo ${subjectParamCN##*=} | sed -e 's/^[[:space:]]*//'`
subjectO=`echo ${subjectParamO##*=} | sed -e 's/^[[:space:]]*//'`
echo "CSR with CN = $subjectCN and O = $subjectO"

lainotikconfigpath='manifests/lainotik-config.yaml'
lainotikconfigtemppath='templates/lainotik-config.yaml'
cp $lainotikconfigtemppath $lainotikconfigpath
sed -i "s|{currentpath}|$PWD|g" $lainotikconfigpath
sed -i "s/{user}/$subjectCN/g" $lainotikconfigpath
sed -i "s/{platform}/$1/g" $lainotikconfigpath
sed -i "s/{ip}/$2/g" $lainotikconfigpath

kubectl config use-context $1-$2 --kubeconfig=$lainotikconfigpath