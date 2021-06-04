// microservicesDocker.groovy
def call(image) {

  pipeline {
    agent any
    environment {
        image1 = 'example1'
    }

    stages {
        stage('Build Image') {
            when { changeset "${image}/*"}
            steps {
                sh 'echo "This is ${image}"'
                dir("${image}/"){
                   script {
                      dockerImage = docker.build("${image}")
                   }
                }
            }
        }
    }
  }
}

