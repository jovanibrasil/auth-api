CURRENT_DIR=$(shell pwd)

stop:
	- docker stop mariadb
clean: stop
	- docker rm mariadb
clean-data:  clean
	- docker volume rm mariadb-data
create-data: clean-data
	docker volume create mariadb-data

run:
	docker run -p 3307:3306 -p 33061:33060 -e MYSQL_ROOT_PASSWORD=root -e MYSQL_USER=root \
		-e MYSQL_DATABASE='usersdb' --name=mariadb --mount source=mariadb-data,target=/var/lib/mysql -d mariadb:latest
fresh-run: clean clean-data build run

start:
	docker start mariadb 
bash:
	docker container exec -i -t mariadb bash

default-mariadb-bash:
	docker exec -it mariadb bash
