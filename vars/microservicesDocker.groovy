// microservicesDocker.groovy
def call(Map pipelineParams) {

  pipeline {
    agent any
    environment {
        image1 = 'example1'
    }

    stages {
        stage('Build Image') {
            when { changeset "example1/*"}
            steps {
                sh 'echo "This is ${pipelineParams.image}"'
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

