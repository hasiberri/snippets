// pythonDocker.groovy
def call(image) {

  pipeline {
    agent {
     kubernetes {
      inheritFrom 'mypython'
      defaultContainer 'jnlp'
      yamlFile 'vars/pythonDocker.yaml'
      }
    }

    stages {
        stage('python Unit Test') {
          when { changeset "${image}/**"}
          steps {
            container('python') {
		          dir("${image}/"){
        		    sh 'pip install pytest'
        		    sh 'pytest --with-xunit --xunit-file=pyunit.xml --cover-xml --cover-xml-file=cov.xml tests/*.py || true'
                step([$class: 'CoberturaPublisher', 
                        coberturaReportFile: "cov.xml",
                        onlyStable: false,
                        failNoReports: true,
                        failUnhealthy: false,
                        failUnstable: false,
                        autoUpdateHealth: true,
                        autoUpdateStability: true,
                        zoomCoverageChart: true,
                        maxNumberOfBuilds: 10,
                        lineCoverageTargets: '80, 80, 80',
                        conditionalCoverageTargets: '80, 80, 80',
                        classCoverageTargets: '80, 80, 80',
                        fileCoverageTargets: '80, 80, 80',
                    ])
                junit "pyunit.xml"
		          }
		        }
          }
        }
        stage('python Static code check') {
          when { changeset "${image}/**"}
          steps {
            container('python') {
		          dir("${image}/"){
        	      sh 'pip install pylint'
                sh 'find . -name \\*.py | xargs pylint -f parseable | tee pylint.log'
                recordIssues(
                        tool: pyLint(pattern: 'pylint.log'),
                        unstableTotalHigh: 100,
                )
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
        stage('Image Build') {
          when { changeset "${image}/**"}
          steps {
            container('kaniko') {
              dir("${image}/"){
			          sh '/kaniko/executor -f Dockerfile --context=./ --tarPath=./image.tar --destination=image.tar --no-push'
              }
	          }
          }
        }
        stage('Image Lint Check') {
          when { changeset "${image}/**"}
          steps {
            container('dockle') {
              dir("${image}/"){
			          sh 'dockle --input ./image.tar -f json --exit-code 1'
              }
	          }
          }
        }
        stage('Image Vulnerability Check') {
          when { changeset "${image}/**"}
          steps {
            container('trivy') {
              dir("${image}/"){
			          sh 'trivy image --no-progress --severity=CRITICAL,HIGH,MEDIUM --exit-code=1 -f json --input ./image.tar'
              }
	          }
          }
        }
    }
  }
}

