pipeline {
    agent any
    stages {
        stage('Build') {
            when { changeset "example1/*"}
            steps {
                sh 'echo "This is example1"'
            }
        }
        stage('Build2') {
            steps {
                sh 'echo "This is the next stage"'
            }
        }
    }
}

