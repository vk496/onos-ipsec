#!/bin/sh


if [[ -z $1 ]] || ( ! [[ $1 =~ ^[0-9]+$ ]] && [[ $1 -gt 0 ]] ); then
	docker-compose up -d --build
else
	echo Deploy with $1 instances
	sleep 0.3
	docker-compose up -d --build --scale netopeer=$1
fi
