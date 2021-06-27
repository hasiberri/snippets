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
			          sh '/kaniko/executor -f ./Dockerfile --cache=true --context=./ --destination=${image} --no-push'
              }
	          }
          }
        }
        stage('Lint Image') {
          when { changeset "${image}/**"}
          steps {
            container('dockle') {
              dir("${image}/"){
			          sh '/dockle ${image}'
              }
	          }
          }
        }

    }
  }
}

