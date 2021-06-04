// microservicesDocker.groovy
def call(image) {

  pipeline {
    agent any
    environment {
        image1 = 'example1'
    }

    stages {
        stage('Build Image') {
            when { changeset "example1/*"}
            steps {
                sh 'echo "This is ${image}"'
                dir("example1/"){
                    script{
                        try{
                          dockerImage = docker.build("${image1}")
                        } catch(e) {
                            echo e.toString()  
                        }
                    }
                }
            }
        }
    }
  }
}

