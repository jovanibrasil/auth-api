pipeline {
    agent { label 'aws-agent' }
    
    environment {
        USERS_MYSQL_URL = credentials('USERS_MYSQL_URL')
        USERS_MYSQL_CREDENTIALS = credentials('USERS_MYSQL_CREDENTIALS')
        USERS_MYSQL_USERNAME = "${env.USERS_MYSQL_CREDENTIALS_USR}"
        USERS_MYSQL_PASSWORD = "${env.USERS_MYSQL_CREDENTIALS_PSW}"
        RECAPTCHA_KEY_SITE =  credentials('RECAPTCHA_KEY_SITE')
        RECAPTCHA_KEY_SECRET = credentials('RECAPTCHA_KEY_SECRET') 
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
                git([url: 'https://github.com/jovanibrasil/auth-api.git', branch: 'master', credentialsId: '9bae9c61-0a29-483c-a07f-47273c351555'])
            }
        }

        stage("Test"){
            steps {
            	echo 'Running unit tests ...'
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
            	echo 'Building ...'
                sh 'make build RECAPTCHA_KEY_SITE=${RECAPTCHA_KEY_SITE} RECAPTCHA_KEY_SECRET=${RECAPTCHA_KEY_SECRET} PROFILE=prod USERS_MYSQL_URL=${USERS_MYSQL_URL} USERS_MYSQL_USERNAME=${USERS_MYSQL_USERNAME} USERS_MYSQL_PASSWORD=${USERS_MYSQL_PASSWORD}'            
            	echo 'Deploying auth API'
				sh 'make clean'               
                sh 'make run PROFILE=prod'
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
