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
                sh 'echo "This is ${image}"'
                dir("${image}/"){
                   script {
                      dockerImage = docker.build("${image}")
                      sh 'echo "This is ${image}"'
                   }
                }
            }
        }
        stage('Push Image') {
            when { changeset "${image}/**"}
            steps {
                sh 'echo "This is ${image}"'
                dir("${image}/"){
                   sh 'echo "pushing"'
                }
            }
        }
    }
  }
}

