pipeline {
    agent any
    stages {
      try{
        stage('BuildPush example1') {
            when { changeset "example1/*"}
            steps {
                sh 'echo "This is example1"'
                dir('example1/'){
                    script{
                        dockerImage = docker.build("example1")
                    }
                }
            }
        }
      } catch(e) {
        echo e.toString()  
      }

      try{
        stage('BuildPush example2') {
            when { changeset "example2/*"}
            steps {
                sh 'echo "This is example2"'
                dir('example2/'){
                    script{
                        dockerImage = docker.build("example2")
                    }
                }
            }
        }
      } catch(e) {
        echo e.toString()  
      }

      stage('Build2') {
          steps {
              sh 'echo "This is the next stage"'
          }
      }

    }
}

