#!/bin/bash

# exit when any command fails
set -e

# source .env files
export $(grep -v '^#' .env | xargs)
export $(grep -v '^#' .env-secrets | xargs)

# default variable values
SKIP_FRONTEND_BUILD=1
SKIP_BACKEND_BUILD=1

# parse opts
while getopts fb option; do
  case "${option}" in
  f) SKIP_FRONTEND_BUILD=0 ;;
  b) SKIP_BACKEND_BUILD=0 ;;
  *) ;;
  esac
done

# make sure directory exists on deployment server
ssh "${DEPLOYMENT_USER}@${DEPLOYMENT_IP}" 'mkdir -p ~/p3m'

# build and upload frontend if not skipped
if [ "${SKIP_FRONTEND_BUILD}" = "0" ]; then
  cd ../../frontend/
  docker buildx build --platform=linux/amd64 -t p3m-frontend . \
    --build-arg frontend_production=true \
    --build-arg deployment_url="${DEPLOYMENT_URL}" \
    --build-arg backend_url="${DEPLOYMENT_URL}/api" \
    --build-arg tileserver_style_url="${TILESERVER_STYLE_URL}"
  docker save p3m-frontend > p3m-frontend.tar
  scp p3m-frontend.tar "${DEPLOYMENT_USER}@${DEPLOYMENT_IP}:~/p3m"
  rm p3m-frontend.tar
  cd ../deployment/p3m-production/
else
  echo "Skipping frontend build..."
fi

# build and upload backend if not skipped
if [ "${SKIP_BACKEND_BUILD}" = "0" ]; then
  cd ../../backend/
  mvn clean package -DskipTests
  docker buildx build --platform=linux/amd64 -t p3m-backend .
  docker save p3m-backend > p3m-backend.tar
  scp p3m-backend.tar "${DEPLOYMENT_USER}@${DEPLOYMENT_IP}:~/p3m"
  rm p3m-backend.tar
  cd ../deployment/p3m-production/
else
  echo "Skipping backend build..."
fi

# upload compose and configuration
rsync -aP docker-compose.yml .env .env-secrets config scripts "${DEPLOYMENT_USER}@${DEPLOYMENT_IP}:~/p3m"

# upload tileserver configuration
rsync -aP ../tileserver "${DEPLOYMENT_USER}@${DEPLOYMENT_IP}:~/p3m"

# load frontend
if [ "${SKIP_FRONTEND_BUILD}" = "0" ]; then
  ssh "${DEPLOYMENT_USER}@${DEPLOYMENT_IP}" 'cd ~/p3m && docker load < p3m-frontend.tar && rm p3m-frontend.tar'
fi

# load backend
if [ "${SKIP_BACKEND_BUILD}" = "0" ]; then
  ssh "${DEPLOYMENT_USER}@${DEPLOYMENT_IP}" 'cd ~/p3m && docker load < p3m-backend.tar && rm p3m-backend.tar'
fi

# run compose
ssh "${DEPLOYMENT_USER}@${DEPLOYMENT_IP}" 'cd ~/p3m && ./scripts/start.sh && docker image prune -f'

# add crontab if it doesn't exist
grep_result=$(ssh "${DEPLOYMENT_USER}@${DEPLOYMENT_IP}" "crontab -l | grep '~/p3m/scripts/ssl-renew.sh'" 2>&1) || true
if [ -z "${grep_result}" ]; then
    echo "Creating crontab entry..."
    ssh "${DEPLOYMENT_USER}@${DEPLOYMENT_IP}" '(crontab -l ; echo "0 12 * * * ~/p3m/scripts/ssl-renew.sh >> ~/p3m/scripts/ssl-renew.log 2>&1") | crontab -'
fi
