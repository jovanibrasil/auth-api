pipeline {
    agent { label 'digital-ocean-agent' }
    
    environment {
        VAULT_TOKEN = credentials('VAULT_TOKEN')
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

        stage("Clone from git") {
            steps {
                echo 'Cloning git ...'
                git([url: 'https://github.com/jovanibrasil/auth-api.git', branch: 'master', 
                	credentialsId: '9bae9c61-0a29-483c-a07f-47273c351555'])
            }
        }

  		stage("Test"){
            steps {
            	echo 'Running tests ...'
                sh 'make run-tests'
            }
        }

        stage("Registry image"){
            steps {
                echo 'TODO'
            }
        }

        stage("Deploy"){
            steps {
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
