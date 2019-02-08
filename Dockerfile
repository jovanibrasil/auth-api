FROM tomcat
LABEL maintainer="jovanibrasil@gmail.com"
USER root
    #COPY dist/ /app

    COPY ./target/auth-api.war /usr/local/tomcat/webapps/auth-api.war
    #COPY ./target/auth-api /usr/local/tomcat/webapps/auth-api
    EXPOSE 8080

#USER jenkins
