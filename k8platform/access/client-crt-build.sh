#!/bin/bash

# Create manifests folder
if [ -d manifests ]; then rm -Rf manifests; fi
mkdir -p manifests

# get CN (username) and O (organization) from csr in certs
subject=$(openssl req -in certs/client.csr -noout -subject)
subjetParams=${subject:8}
subjectParamCN=${subjetParams%,*}
subjectParamO=${subjetParams##*,}
subjectCN=`echo ${subjectParamCN##*=} | sed -e 's/^[[:space:]]*//'`
subjectO=`echo ${subjectParamO##*=} | sed -e 's/^[[:space:]]*//'`
echo "CSR with CN = $subjectCN and O = $subjectO"

# delete the csr object in kubernetes if exists
kubectl delete csr lainotik-$subjectCN-csr

# Sign and approve certificate request
lainotikcsrpath='manifests/lainotik-csr.yaml'
lainotikcsrtemppath='templates/lainotik-csr.yaml'
cp $lainotikcsrtemppath $lainotikcsrpath
crt=$(cat certs/client.csr | base64 | tr -d '\n')
sed -i "s/{user}/$subjectCN/g" $lainotikcsrpath
sed -i "s/{lainocsr}/$crt/g" $lainotikcsrpath
kubectl apply -f $lainotikcsrpath
kubectl certificate approve lainotik-$subjectCN-csr

# Create lainotik cluster role
lainotikcrpath='manifests/lainotik-cr.yaml'
lainotikcrtemppath='templates/lainotik-cr.yaml'
cp $lainotikcrtemppath $lainotikcrpath
sed -i "s/{user}/$subjectCN/g" $lainotikcrpath
kubectl delete clusterRole lainotik-$subjectCN-clusterRole
kubectl apply -f $lainotikcrpath

# create lainotik cluster role binding with the user and cluster role input parameter
lainotikcrbpath='manifests/lainotik-crb.yaml'
lainotikcrbtemppath='templates/lainotik-crb.yaml'
cp $lainotikcrbtemppath $lainotikcrbpath
sed -i "s/{user}/$subjectCN/g" $lainotikcrbpath
kubectl delete clusterRoleBinding lainotik-$subjectCN-clusterRoleBinding
kubectl apply -f $lainotikcrbpath

# Get certificates
kubectl get csr lainotik-$subjectCN-csr -o jsonpath='{.status.certificate}' | base64 --decode > certs/client.crt
cat $(kubectl config view -o jsonpath='{.clusters[0].cluster.certificate-authority}') >  certs/ca.crt
echo "Certificates obtained"
