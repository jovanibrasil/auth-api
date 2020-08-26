ifndef PROFILE
override PROFILE = dev
endif

PKG_VERSION_PATH := "./web/src/main/resources/buildNumber.properties"
LAST_VERSION := $(shell (grep buildNumber= | cut -d= -f2) < $(PKG_VERSION_PATH))
$(eval VERSION=$(shell echo $$(($(LAST_VERSION)+1))))

run-tests:
	mvn clean test -Ptest

stop:
	- docker stop auth-api
clean: stop
	- docker rm auth-api
build: clean
	mvn clean package -pl web -am -P$(PROFILE) -Dmaven.test.skip=true
	docker build  --build-arg ENVIRONMENT=dev --build-arg VERSION=$(VERSION)  -t auth-api .
	chmod -R ugo+rw web/target/
run: clean
	docker run -m 396m --memory-swap 512m --env-file ./.env \
		-e "SPRING_PROFILES_ACTIVE=${PROFILE}" -e "VAULT_TOKEN=${VAULT_TOKEN}" \
		--name=auth-api --network net auth-api
start: stop
	docker start auth-api
logs:
	docker logs auth-api
bash:
	docker container exec -i -t --user root auth-api bash

compose-down:
	#docker network disconnect -f auth-api_net auth-api
	docker-compose down -v --remove-orphans

compose-up-dev: compose-down
	docker-compose -f docker-compose.yml --compatibility up -d --no-recreate

compose-up-stage: compose-down
	docker-compose -f docker-compose.yml -f docker-compose.stage.yml --compatibility up -d --no-recreate

stats:
	docker stats auth-api

deploy-production:
	/bin/sh scripts/deploy-docker-tomcat.sh VAULT_TOKEN=$(VAULT_TOKEN) SPRING_PROFILES_ACTIVE=$(PROFILE)	
