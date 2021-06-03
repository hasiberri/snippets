pipeline {
    agent any

    stages {
        stage('BuildPush example1') {
            environment {
                container = "example1"
            }
            when { changeset "${container}/*"}
            steps {
                sh 'echo "This is ${container}"'
                dir("${container}/"){
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
    }
}

