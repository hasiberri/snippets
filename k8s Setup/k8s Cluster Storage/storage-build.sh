#!/bin/bash

# $1: the username, used as user by kubernetes
if [[ $# -eq 0 ]] ; then
    echo 'Error: No arguments'
    exit 0
fi

# Create manifests folder
if [ -d manifests ]; then rm -Rf manifests; fi
mkdir -p manifests

# delete storageclass if exist
kubectl delete storageClass lainotik-$1

# deploy storageclass
lainotikclasspath='manifests/storageclass.yaml'
lainotikclasstemppath='templates/storageclass.yaml'
cp $lainotikclasstemppath $lainotikclasspath
sed -i "s/{clasname}/$1/g" $lainotikclasspath

# deploy persistent volumes
lainotikpvpath='manifests/persistentvolume.yaml'
lainotikpvtemppath='templates/persistentvolume.yaml'
cp $lainotikpvtemppath $lainotikpvpath
sed -i "s/{clasname}/$1/g" $lainotikpvpath
sed -i "s/{vol}/$2/g" $lainotikpvpath
