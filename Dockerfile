FROM tomcat
LABEL maintainer="jovanibrasil@gmail.com"
USER root
    #COPY dist/ /app
    
    ARG USER_MYSQL_URL 
    ARG USER_MYSQL_USERNAME 
    ARG USER_MYSQL_PASSWORD

	ENV USER_MYSQL_URL=$USER_MYSQL_URL
	ENV USER_MYSQL_USERNAME=$USER_MYSQL_USERNAME
	ENV USER_MYSQL_PASSWORD=$USER_MYSQL_PASSWORD
	
    COPY ./target/auth-api.war /usr/local/tomcat/webapps/auth-api.war
    #COPY ./target/auth-api /usr/local/tomcat/webapps/auth-api
    EXPOSE 8083

#USER jenkins
