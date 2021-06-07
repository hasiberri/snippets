// microservicesDocker.groovy
def call(image) {

  pipeline {
    agent any

    stages {
        stage('Unit Test') {
            when { changeset "${image}/**"}
            steps {
		dir("${image}/"){
        		sh 'pip install pytest'
        		sh 'pytest'
		}
            }
        }
        stage('Security Test') {
            when { changeset "${image}/**"}
            steps {
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
        stage('Build Image') {
            when { changeset "${image}/**"}
            steps {
                dir("${image}/"){
                   script {
                      dockerImage = docker.build("${image}")
                   }
                }
            }
        }
        stage('Push Image') {
            when { changeset "${image}/**"}
	    container('python'){
            	steps {
                	dir("${image}/"){
                   	sh 'echo "pushing"'
			}
                }
            }
        }
        stage('Delete Image') {
            when { changeset "${image}/**"}
            steps {
                sh 'docker system prune -af --volumes '
            }
        }
    }
  }
}

