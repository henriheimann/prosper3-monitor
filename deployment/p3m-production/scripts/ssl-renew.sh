#!/bin/bash

cd ~/p3m
docker-compose run p3m-certbot renew && docker-compose kill -s SIGHUP p3m-reverse-proxy
docker system prune -af
