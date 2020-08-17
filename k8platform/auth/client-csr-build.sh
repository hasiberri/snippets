#!/bin/bash

# $1: the username, used as user by kubernetes
# $2: The reference/id of the companykuster. Used as group in Kubernetes
if [[ $# -eq 0 ]] ; then
    echo 'Error: No arguments'
    exit 0
fi

if [ -d certs ]; then rm -Rf certs; fi
mkdir -p certs

openssl req -new -newkey rsa:4096 -nodes -keyout certs/client.key -out certs/client.csr -subj "/CN=$1 /O=$1-$2"