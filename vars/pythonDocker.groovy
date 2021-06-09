// microservicesDocker.groovy
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
  - name: mypython
    image: python:3
    command:
    - cat
    tty: true
    volumeMounts:
    - mountPath: '/opt/app/shared'
      name: sharedvolume  
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
                container('mypython') {
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
                container('mypython') {
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
                container('mypython') {
                  dir("${image}/"){
                   		script {
                      			dockerImage = docker.build("${image}")
                   		}
                  }
                }
            }
        }
        stage('Push Image') {
            when { changeset "${image}/**"}
            steps {
                container('mypython') {
                  dir("${image}/"){
                   	sh 'echo "pushing"'
                  }
		}      
            }

        }
        stage('Delete Image') {
            when { changeset "${image}/**"}
            steps {
                container('mypython') {
                  sh 'docker system prune -af --volumes '
		} 
            }
        }
    }
  }
}

