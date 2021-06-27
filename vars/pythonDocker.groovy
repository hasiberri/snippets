// pythonDocker.groovy
def call(image) {

  pipeline {
    agent {
     kubernetes {
      inheritFrom 'mypython'
      defaultContainer 'jnlp'
      yamlFile 'pythonDocker.yaml'
      }
    }

    stages {
        stage('python Unit Test') {
          when { changeset "${image}/**"}
          steps {
            container('python') {
		          dir("${image}/"){
        		    sh 'pip install pytest'
        		    sh 'pytest'
		          }
		        }
          }
        }
        stage('python Security Test') {
          when { changeset "${image}/**"}
          steps {
            container('python') {
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
        }
        stage('Build Image') {
          when { changeset "${image}/**"}
          steps {
            container('kaniko') {
              dir("${image}/"){
			          sh '/kaniko/executor -f Dockerfile --context=./ --tarPath=./image.tar --destination=image.tar --no-push'
              }
	          }
          }
        }
        stage('Lint Check Image') {
          when { changeset "${image}/**"}
          steps {
            container('dockle') {
              dir("${image}/"){
			          sh 'dockle --input ./image.tar -f json --exit-code 1'
              }
	          }
          }
        }
        stage('Vulnerability Check Image') {
          when { changeset "${image}/**"}
          steps {
            container('trivy') {
              dir("${image}/"){
			          sh 'trivy image ./image.tar --severity=CRITICAL,HIGH,MEDIUM --exit-code=1 -f json'
              }
	          }
          }
        }
    }
  }
}

