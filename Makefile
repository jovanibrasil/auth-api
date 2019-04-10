stop:
	- docker stop auth-api
clean: stop
	- docker rm auth-api
build: clean
	mvn clean package
	docker build --build-arg USERS_MYSQL_URL --build-arg USERS_MYSQL_USERNAME --build-arg USERS_MYSQL_PASSWORD --network net -t auth-api .
run: clean
	docker run -d -p 8083:8080 -e "SPRING_PROFILES_ACTIVE=dev" --name=auth-api --network net auth-api
start: stop
	docker start auth-api
bash:
	docker container exec -i -t --user root auth-api bash
compose-down:
	docker-compose down -v 
compose-up: compose-down
	docker-compose up --no-recreate -d