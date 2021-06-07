// microservicesDocker.groovy
def call(image) {

  pipeline {
    agent any

    stages {
        stage('Unit Test') {
            when { changeset "${image}/**"}
            steps {
		dir("${image}/"){
        		sh 'pip install pytest'
			sh 'mkdir reports'
        		sh 'pytest --cov=highlevel --junitxml reports/junit.xml --cov-report xml:reports/coverage.xml highlevel'
		}
            }
            post {
                always {
                    junit 'reports/*.xml'
                    step([$class: 'CoberturaPublisher',
                                   autoUpdateHealth: false,
                                   autoUpdateStability: false,
                                   coberturaReportFile: 'reports/coverage.xml',
                                   failNoReports: false,
                                   failUnhealthy: false,
                                   failUnstable: false,
                                   maxNumberOfBuilds: 10,
                                   onlyStable: false,
                                   sourceEncoding: 'ASCII',
                                   zoomCoverageChart: false])
                }
            }
        }
        stage('Security Test') {
            when { changeset "${image}/**"}
            steps {
		dir("${image}/"){
        		sh 'pip install bandit'
        		sh '''bandit -r ./src 2>&1 | tee ./out.log
				num=`grep "Severity: High" ./out.log | wc -l`
				if [ $num -eq 0 ]; then
     					exit 0
				else
     					exit 1
				fi'''
                }
            }
        }
        stage('Build Image') {
            when { changeset "${image}/**"}
            steps {
                dir("${image}/"){
                   script {
                      dockerImage = docker.build("${image}")
                   }
                }
            }
        }
        stage('Push Image') {
            when { changeset "${image}/**"}
            steps {
                dir("${image}/"){
                   sh 'echo "pushing"'
                }
            }
        }
        stage('Delete Image') {
            when { changeset "${image}/**"}
            steps {
                sh 'docker system prune -af --volumes '
            }
        }
    }
  }
}

