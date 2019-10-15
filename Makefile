ifndef PROFILE
override PROFILE = dev
endif

run-tests:
	mvn clean test -Ptest

stop:
	- docker stop auth-api
clean: stop
	- docker rm auth-api
build: clean
	mvn clean package -P${PROFILE} -Dmaven.test.skip=true
	docker build --build-arg USERS_MYSQL_URL=${USERS_MYSQL_URL} \
		--build-arg USERS_MYSQL_USERNAME=${USERS_MYSQL_USERNAME} \
		--build-arg USERS_MYSQL_PASSWORD=${USERS_MYSQL_PASSWORD} \
		--build-arg RECAPTCHA_KEY_SITE=${RECAPTCHA_KEY_SITE} \
		--build-arg RECAPTCHA_KEY_SECRET=${RECAPTCHA_KEY_SECRET} \
		--build-arg ENVIRONMENT=${PROFILE} \
		--network net -t auth-api .
	chmod -R ugo+rw target/
run: clean
	docker run -d -p 8083:8080 -m 192m --memory-swap 256m \
		-e "SPRING_PROFILES_ACTIVE=${PROFILE}" --name=auth-api \
		--network net auth-api
start: stop
	docker start auth-api
logs:
	docker logs auth-api
bash:
	docker container exec -i -t --user root auth-api bash
compose-down:
	docker-compose down -v 
compose-up: compose-down
	docker-compose up --no-recreate -d
stats:
	docker stats auth-api

deploy-production:
	/bin/sh scripts/deploy-docker-tomcat.sh VAULT_TOKEN=${VAULT_TOKEN} SPRING_PROFILES_ACTIVE=${PROFILE}