// microservicesDocker.groovy
def call(image) {

  pipeline {
    agent any

    stages {
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
    }
  }
}

