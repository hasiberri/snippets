// microservicesDocker.groovy
def call(Map pipelineParams) {

  pipeline {
    agent any

    stages {
        stage('Build Image') {
            when { changeset "example1/*"}
            steps {
                sh 'echo "This is ${pipelineParams.image}"'
                dir("pipelineParams.image/"){
                    script{
                        try{
                          dockerImage = docker.build("${pipelineParams.image}")
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

