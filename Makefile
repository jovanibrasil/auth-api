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
	FILE_NAME=blog-api\#\#$(shell find target/*.war -type f | grep -Eo '[0-9]+)
	docker build  --build-arg ENVIRONMENT=dev --build-arg FILE_NAME -t auth-api .
	chmod -R ugo+rw target/
run: clean
	docker run -d -p 8083:8080 -m 192m --memory-swap 256m \
		-e "SPRING_PROFILES_ACTIVE=${PROFILE}" -e "VAULT_TOKEN=${VAULT_TOKEN}" \
		--name=auth-api --network net auth-api
start: stop
	docker start auth-api
logs:
	docker logs auth-api
bash:
	docker container exec -i -t --user root auth-api bash
compose-down:
	docker-compose down -v 
compose-up: compose-down
	docker-compose --compatibility up --no-recreate -d
stats:
	docker stats auth-api

deploy-production:
	/bin/sh scripts/deploy-docker-tomcat.sh VAULT_TOKEN=${VAULT_TOKEN} SPRING_PROFILES_ACTIVE=${PROFILE}