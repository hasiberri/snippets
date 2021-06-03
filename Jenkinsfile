pipeline {
    agent any

    def build_ok = true

    stages {
        stage('BuildPush example1') {
            def container = "example1"
            when { changeset "$container/*"}
            steps {
                sh 'echo "This is $container"'
                dir("$container/"){
                    script{
                        try{
                          dockerImage = docker.build("$container")
                        } catch(e) {
                            build_ok = false
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
                            build_ok = false
                            echo e.toString()  
                        }
                    }
                }
            }
        }

        if(build_ok) {
            sh 'echo "ALL container builds correct"'
        } else {
            sh 'echo "Container builds FAILURES"'
            sh 'exit 1'   // failure
        }
    }
}
