#!/bin/bash

# $1: the username, used as user by kubernetes
# $2: The reference/id of the companykuster. Used as group in Kubernetes
# $3: IP address of the k8 platform
if [[ $# -eq 0 ]] ; then
    echo 'Error: No arguments'
    exit 0
fi

lainotikconfigpath='manifests/lainotik-config.yaml'
lainotikconfigtemppath='templates/lainotik-config.yaml'
cp $lainotikconfigtemppath $lainotikconfigpath
sed -i "s|{currentpath}|$PWD|g" $lainotikconfigpath
sed -i "s/{user}/$1/g" $lainotikconfigpath
sed -i "s/{platform}/$2/g" $lainotikconfigpath
sed -i "s/{ip}/$3/g" $lainotikconfigpath

kubectl config use-context $1-$2 --kubeconfig=$lainotikconfigpath