#!/bin/bash

# $1: the username, used as user by kubernetes
if [[ $# -eq 0 ]] ; then
    echo 'Error: No arguments'
    exit 0
fi


# Create manifests folder
if [ -d manifests ]; then rm -Rf manifests; fi
mkdir -p manifests

# delete the csr in kubernetes if exists
kubectl delete csr lainotik-$1-csr

# Sign and approve certificate request
lainotikcsrpath='manifests/lainotik-csr.yaml'
lainotikcsrtemppath='templates/lainotik-csr.yaml'
cp $lainotikcsrtemppath $lainotikcsrpath
crt=$(cat certs/client.csr | base64 | tr -d '\n')
sed -i "s/{user}/$1/g" $lainotikcsrpath
sed -i "s/{lainocsr}/$crt/g" $lainotikcsrpath
kubectl apply -f $lainotikcsrpath
kubectl certificate approve lainotik-$1-csr

# Create lainotik cluster role
lainotikcrpath='manifests/lainotik-cr.yaml'
lainotikcrtemppath='templates/lainotik-cr.yaml'
cp $lainotikcrtemppath $lainotikcrpath
sed -i "s/{user}/$1/g" $lainotikcrpath
kubectl delete clusterRole lainotik-$1-clusterRole
kubectl apply -f $lainotikcrpath

# create lainotik cluster role binding with the user and cluster role input parameter
lainotikcrbpath='manifests/lainotik-crb.yaml'
lainotikcrbtemppath='templates/lainotik-crb.yaml'
cp $lainotikcrbtemppath $lainotikcrbpath
sed -i "s/{user}/$1/g" $lainotikcrbpath
kubectl delete clusterRoleBinding lainotik-$1-clusterRoleBinding
kubectl apply -f $lainotikcrbpath

# Get certificates
kubectl get csr lainotik-$1-csr -o jsonpath='{.status.certificate}' | base64 --decode > certs/client.crt
cat $(kubectl config view -o jsonpath='{.clusters[0].cluster.certificate-authority}') >  certs/ca.crt
echo "Certificates obtained"
