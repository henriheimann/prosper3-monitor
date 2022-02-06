#!/bin/bash

# source .env files
source <(grep -v -e '^#\|^[[:space:]]*$' .env | sed -e 's/\r$//' -e 's/^/export /' -e 's/=/="/' -e 's/$/"/')
source <(grep -v -e '^#\|^[[:space:]]*$' .env-secrets | sed -e 's/\r$//' -e 's/^/export /' -e 's/=/="/' -e 's/$/"/')

# run docker-compose
docker-compose up -d
