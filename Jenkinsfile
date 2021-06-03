pipeline {
    agent any
    stages {
        stage('BuildPush example1') {
            when { changeset "example1/*"}
            steps {
                sh 'echo "This is example1"'
                app = docker.build("example2", "-f example1/dockerfile .")
            }
        }
        stage('BuildPush example2') {
            when { changeset "example2/*"}
            steps {
                sh 'echo "This is example2"'
                app = docker.build("example2", "-f example2/dockerfile .")
            }
        }
        stage('Build2') {
            steps {
                sh 'echo "This is the next stage"'
            }
        }
    }
}

