// pythonDocker.groovy
def call(image) {

  pipeline {
    agent {
     kubernetes {
      label 'mypython'
      defaultContainer 'jnlp'
      yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: python
    image: docker.io/python:3.7-slim-buster
    imagePullPolicy: IfNotPresent
    command:
    - /cat
    tty: true
    volumeMounts:
    - mountPath: '/opt/app/shared'
      name: sharedvolume

  - name: dockle
    image: docker.io/goodwithtech/dockle:v0.3.15
    imagePullPolicy: IfNotPresent
    command:
    - /cat
    tty: true 

  - name: kaniko
    image: gcr.io/kaniko-project/executor:v1.6.0
    imagePullPolicy: IfNotPresent
    command:
    - /busybox/cat
    tty: true

  - name: trivy
    image: docker.io/aquasec/trivy:0.18.3
    imagePullPolicy: IfNotPresent
    command:
    - /cat
    tty: true

  volumes:
  - name: sharedvolume
    emptyDir: {}
"""
      }
    }

    stages {
        stage('python Unit Test') {
            when { changeset "${image}/**"}
            steps {
                container('python') {
		  dir("${image}/"){
        		sh 'pip install pytest'
        		sh 'pytest'
		  }
		}
            }
        }
        stage('python Security Test') {
            when { changeset "${image}/**"}
            steps {
                container('python') {
		  dir("${image}/"){
        		sh 'pip install bandit'
        		sh '''bandit -r ./src 2>&1 | tee ./out.log
				num=`grep "Severity: High" ./out.log | wc -l`
				if [ $num -eq 0 ]; then
     					exit 0
				else
     					exit 1
				fi'''
                  }
	        }
            }
        }
        stage('Build Image') {
            when { changeset "${image}/**"}
            steps {
                container('kaniko') {
                  dir("${image}/"){
			sh '/kaniko/executor -f ./Dockerfile --cache=true --context=./ --destination=${image} --no-push'
                  }
                }
            }
        }
        stage('Lint Image') {
            when { changeset "${image}/**"}
            steps {
                container('dockle') {
                  dir("${image}/"){
			sh '/dockle ${image}'
                  }
                }
            }
        }

    }
  }
}

