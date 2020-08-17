FROM tomcat:9.0.37-jdk11-openjdk
LABEL maintainer="jovanibrasil@gmail.com"
USER root

RUN apt-get -y update && apt-get -y install netcat

ARG USERS_MYSQL_URL 
ARG USERS_MYSQL_USERNAME 
ARG USERS_MYSQL_PASSWORD
ARG RECAPTCHA_KEY_SECRET
ARG RECAPTCHA_KEY_SITE
ARG ENVIRONMENT
ARG VERSION
ARG DBSERVICEPORT
ARG DBSERVICENAME

ENV USERS_MYSQL_URL=$USERS_MYSQL_URL
ENV USERS_MYSQL_USERNAME=$USERS_MYSQL_USERNAME
ENV USERS_MYSQL_PASSWORD=$USERS_MYSQL_PASSWORD
ENV ENVIRONMENT=$ENVIRONMENT
ENV RECAPTCHA_KEY_SECRET=$RECAPTCHA_KEY_SECRET
ENV RECAPTCHA_KEY_SITE=$RECAPTCHA_KEY_SITE
ENV VAULT_HOST="vault-server"

ENV DBSERVICEPORT=$DBSERVICEPORT
ENV DBSERVICENAME=$DBSERVICENAME

COPY ./web/target/security-web##${VERSION}.war /usr/local/tomcat/webapps/ROOT##${VERSION}.war
COPY ./web/target/security-web##${VERSION} /usr/local/tomcat/webapps/ROOT##${VERSION}
COPY ./scripts ./scripts

RUN if [ "$ENVIRONMENT" = "stage" ]; \
	then cp ./scripts/startup-dev.sh /startup.sh; \
	else cp ./scripts/startup-prod.sh /startup.sh;\
	fi

RUN rm ./scripts -rf
EXPOSE 8080

RUN ["chmod", "+x", "/startup.sh"]
CMD ["sh", "-c", "/startup.sh $DBSERVICENAME $DBSERVICEPORT"]
