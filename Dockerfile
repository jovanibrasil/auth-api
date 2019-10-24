FROM tomcat
LABEL maintainer="jovanibrasil@gmail.com"
USER root

RUN apt-get -y update && apt-get -y install netcat

ARG USERS_MYSQL_URL 
ARG USERS_MYSQL_USERNAME 
ARG USERS_MYSQL_PASSWORD
ARG RECAPTCHA_KEY_SECRET
ARG RECAPTCHA_KEY_SITE
ARG ENVIRONMENT
ARG FILE_NAME

ENV USERS_MYSQL_URL=$USERS_MYSQL_URL
ENV USERS_MYSQL_USERNAME=$USERS_MYSQL_USERNAME
ENV USERS_MYSQL_PASSWORD=$USERS_MYSQL_PASSWORD
ENV ENVIRONMENT=$ENVIRONMENT
ENV RECAPTCHA_KEY_SECRET=$RECAPTCHA_KEY_SECRET
ENV RECAPTCHA_KEY_SITE=$RECAPTCHA_KEY_SITE
ENV FILE_NAME=${FILE_NAME}

COPY ./target/${FILE_NAME} /usr/local/tomcat/webapps/${FILE_NAME}
COPY ./scripts ./scripts
RUN if [ "$ENVIRONMENT" = "dev" ]; \
	then cp ./scripts/startup-dev.sh /startup.sh; \
	else cp ./scripts/startup-prod.sh /startup.sh;\
	fi
RUN rm ./scripts -rf
EXPOSE 8080

CMD ["/bin/bash", "/startup.sh"]
