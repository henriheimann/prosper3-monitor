#!/bin/bash

# exit when any command fails
set -e

# source .env file
source <(grep -v -e '^#\|^[[:space:]]*$' .env | sed -e 's/\r$//' -e 's/^/export /' -e 's/=/="/' -e 's/$/"/')

# build frontend image
cd ../../frontend/
docker build -t uem-frontend . \
  --build-arg frontend_production=true \
  --build-arg deployment_url="${DEPLOYMENT_URL}" \
  --build-arg backend_url="${DEPLOYMENT_URL}/api" \
  --build-arg backend_oauth_client_id="${UEM_BACKEND_OAUTH_CLIENT_ID}" \
  --build-arg backend_oauth_client_secret="${UEM_BACKEND_OAUTH_CLIENT_SECRET}"
cd ../deployment/uem-local-production/

# build backend image
cd ../../backend/
mvn clean package -DskipTests
docker build -t uem-backend .
cd ../deployment/uem-local-production/

# run compose
docker-compose up -d
