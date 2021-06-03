pipeline {
    agent any
    stages {
        stage('Build') {
            when { changeset "example1/*"}
            steps {
                sh 'echo "This is example1"'
            }
            when { changeset "example2/*"}
            steps {
                sh 'echo "This is example2"'
            }
        }
        stage('Build2') {
            steps {
                sh 'echo "This is the next stage"'
            }
        }
    }
}

