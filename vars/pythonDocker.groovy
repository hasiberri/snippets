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
    image: python:3
    command:
    - cat
    tty: true
    volumeMounts:
    - mountPath: '/opt/app/shared'
      name: sharedvolume  
  - name: kaniko
    image: gcr.io/kaniko-project/executor:debug
    imagePullPolicy: Always
    command:
    - /busybox/cat
    tty: true
  volumes:
  - name: sharedvolume
    emptyDir: {}
"""
      }
    }

    stages {
        stage('Unit Test') {
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
        stage('Security Test') {
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
			sh '/kaniko/executor -f Dockerfile --no-push'
                  }
                }
            }
        }
        stage('Push Image') {
            when { changeset "${image}/**"}
            steps {
                container('kaniko') {
                  dir("${image}/"){
                   	sh 'echo "pushing"'
                  }
		}      
            }

        }
    }
  }
}

