#!/bin/bash

# exit when any command fails
set -e

# source .env file
export $(grep -v '^#' .env | xargs)
export $(grep -v '^#' .env-secrets | xargs)

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
