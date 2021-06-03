pipeline {
    agent any
    stages {
        stage('BuildPush example1') {
            when { changeset "example1/*"}
            steps {
                sh 'echo "This is example1 in $GIT_BRANCH"'
            }
        }
        stage('BuildPush example2 in $GIT_BRANCH') {
            when { changeset "example2/*"}
            steps {
                sh 'echo "This is example2 in $GIT_BRANCH"'
            }
        }
        stage('Build2') {
            steps {
                sh 'echo "This is the next stage"'
            }
        }
    }
}

