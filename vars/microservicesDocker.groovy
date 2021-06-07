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
        stage('Build Image') {
            when { changeset "${image}/*"}
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
            when { changeset "${image}/*"}
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

