#!/bin/bash

# exit when any command fails
set -e

# source .env file
source <(grep -v -e '^#\|^[[:space:]]*$' .env | sed -e 's/\r$//' -e 's/^/export /' -e 's/=/="/' -e 's/$/"/')
source <(grep -v -e '^#\|^[[:space:]]*$' .env-secrets | sed -e 's/\r$//' -e 's/^/export /' -e 's/=/="/' -e 's/$/"/')

# build frontend image
cd ../../frontend/
docker buildx build --platform=linux/amd64 -t p3m-frontend . \
  --build-arg frontend_production=true \
  --build-arg deployment_url="${DEPLOYMENT_URL}" \
  --build-arg backend_url="${DEPLOYMENT_URL}/api" \
  --build-arg tileserver_style_url="${TILESERVER_STYLE_URL}"
cd ../deployment/p3m-local-e2e/

# build backend image
cd ../../backend/
mvn clean package -DskipTests
docker buildx build --platform=linux/amd64 -t p3m-backend .
cd ../deployment/p3m-local-e2e/

# run compose
docker-compose up -d
