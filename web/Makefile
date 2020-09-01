heroku-maven-deploy:
	mvn clean heroku:deploy-war -Pprod -Dmaven.test.skip=true
	chmod -R ugo+rw target/
heroku-logs:
	heroku logs --app=jb-auth-api
