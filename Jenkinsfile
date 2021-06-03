pipeline {
    agent any
    stages {
        stage('BuildPush example1') {
            when { changeset "example1/*"}
            steps {
                sh 'echo "This is example1"'
                dir('example1/'){
                    script{
                        try{
                          dockerImage = docker.build("example1")
                        } catch(e) {
                          echo e.toString()  
                        }
                    }
                }
            }
        }

        stage('BuildPush example2') {
            when { changeset "example2/*"}
            steps {
                sh 'echo "This is example2"'
                dir('example2/'){
                    script{
                        try{
                          dockerImage = docker.build("example2")
                        } catch(e) {
                          echo e.toString()  
                        }
                    }
                }
            }
        }

        stage('Build2') {
            steps {
                sh 'echo "This is the next stage"'
            }
        }
    }
}
