#!/bin/zsh

# exit when any command fails
set -e

# source .env file
source <(grep -v -e '^#\|^[[:space:]]*$' .env | sed -e 's/\r$//' -e 's/^/export /' -e 's/=/="/' -e 's/$/"/')
source <(grep -v -e '^#\|^[[:space:]]*$' .env-secrets | sed -e 's/\r$//' -e 's/^/export /' -e 's/=/="/' -e 's/$/"/')

# build frontend image
cd ../../frontend/
docker build -t p3m-frontend . \
  --build-arg frontend_production=true \
  --build-arg deployment_url="${DEPLOYMENT_URL}" \
  --build-arg backend_url="${DEPLOYMENT_URL}/api" \
  --build-arg backend_oauth_client_id="${P3M_BACKEND_OAUTH_CLIENT_ID}" \
  --build-arg backend_oauth_client_secret="${P3M_BACKEND_OAUTH_CLIENT_SECRET}"
cd ../deployment/p3m-local-production/

# build backend image
cd ../../backend/
mvn clean package -DskipTests
docker build -t p3m-backend .
cd ../deployment/p3m-local-production/

# run compose
docker-compose up -d
