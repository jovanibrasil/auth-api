pipeline {
    agent { label 'jenkins-slave' }
    
    environment {
        USERS_MYSQL_URL = credentials('USERS_MYSQL_URL')
        USERS_MYSQL_CREDENTIALS = credentials('USERS_MYSQL_CREDENTIALS')
        USERS_MYSQL_USERNAME = "${env.USERS_MYSQL_CREDENTIALS_USR}"
        USERS_MYSQL_PASSWORD = "${env.USERS_MYSQL_CREDENTIALS_PSW}"
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

        stage("Build") {
            steps {
                echo 'Cloning git ...'
                git([url: 'https://github.com/jovanibrasil/auth-api.git', branch: 'master', credentialsId: '18a17f19-9870-4bcc-8c7b-75eec38a059a'])
                echo 'Installing dependencies ...'
                sh 'mvn package'
                echo 'Building ...'
                sh 'docker build --build-arg USERS_MYSQL_URL --build-arg USERS_MYSQL_USERNAME --build-arg USERS_MYSQL_PASSWORD -t auth-api ~/workspace/auth-api'
            }
        }

        stage("Test"){
            steps {
                echo 'Todo'
            }
        }

        stage("Registry image"){
            steps {
                echo 'TODO'
            }
        }

        stage("Deploy"){
            steps {
                // sh 'docker stop auth-api'
                // sh 'docker rm auth-api'                
                sh 'docker run -p 8083:8080 -e "SPRING_PROFILES_ACTIVE=prod" --network net --name=auth-api -d auth-api'
            }
        }

        stage("Remove temporary files"){
            steps {
                echo 'cleaning ...'
                echo 'rm ~/workspace/auth-app ~/workspace/auth-app@tmp -rf'
            }
        }

    }
    
}
