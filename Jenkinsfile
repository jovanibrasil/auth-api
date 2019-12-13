pipeline {
    agent { label 'digital-ocean-agent' }
    
    environment {
        VAULT_TOKEN = credentials('VAULT_TOKEN')
    }

    parameters {
        string(name: 'TASK', defaultValue: 'BUILD')
    }
    
    stages {
 
        stage("Environment configuration") {
            steps {
                sh 'git --version'
                echo "Branch: ${env.BRANCH_NAME}"
                sh 'docker -v'
                sh 'printenv'
            }
        }

  		stage("BUILD"){
  		    when{
               expression { return params.TASK == 'BUILD' }
            }
            steps {
                echo 'Cloning git ...'
                git([url: 'https://github.com/jovanibrasil/auth-api.git', branch: 'master',
                	credentialsId: '9bae9c61-0a29-483c-a07f-47273c351555'])
            	echo 'Running tests ...'
                sh 'make run-tests'
                // Build
                //sh 'make build'
                // Registry Image TODO
                //sh "docker tag auth-api docker.pkg.github.com/jovanibrasil/auth-api/auth-api:$BUILD_NUMBER"
                //sh "docker push docker.pkg.github.com/jovanibrasil/auth-api/auth-api:$BUILD_NUMBER"
                echo 'Finished!'
            }
        }

        stage("Deploy"){
            when{
               expression { return params.TASK == 'DEPLOY' }
            }
            steps {
                git([url: 'https://github.com/jovanibrasil/auth-api.git', branch: 'master',
                    credentialsId: '9bae9c61-0a29-483c-a07f-47273c351555'])
                // TODO deploy docker image
                //sh 'docker pull docker.pkg.github.com/jovanibrasil/auth-api/auth-api:docker-base-layer'
                sh 'make deploy-production VAULT_TOKEN=${VAULT_TOKEN} PROFILE=prod'
            }
        }

        stage("Remove temporary files"){
            steps {
                echo 'cleaning ...'
                //echo 'rm ~/workspace/auth-app ~/workspace/auth-app@tmp -rf'
            }
        }

    }
    
}
