FROM tomcat
LABEL maintainer="jovanibrasil@gmail.com"
USER root
    RUN apt-get -y update && apt-get -y install netcat

	ARG USERS_MYSQL_URL 
    ARG USERS_MYSQL_USERNAME 
    ARG USERS_MYSQL_PASSWORD

	ENV USERS_MYSQL_URL=$USERS_MYSQL_URL
	ENV USERS_MYSQL_USERNAME=$USERS_MYSQL_USERNAME
	ENV USERS_MYSQL_PASSWORD=$USERS_MYSQL_PASSWORD

    COPY ./target/auth-api.war /usr/local/tomcat/webapps/auth-api.war
    COPY ./scripts/startup.sh /
    EXPOSE 8080

	CMD ["/bin/bash", "/startup.sh"]