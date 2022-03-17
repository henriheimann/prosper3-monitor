# Build stage
FROM --platform=$BUILDPLATFORM node:lts-alpine as build
ARG BUILDPLATFORM
ARG TARGETPLATFORM
ARG deployment_url
ARG backend_url
ARG tileserver_style_url

ENV P3M_DEPLOYMENT_URL=$deployment_url
ENV P3M_BACKEND_URL=$backend_url
ENV P3M_TILESERVER_STYLE_URL=$tileserver_style_url

WORKDIR /usr/src/app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run config && npm run build

# Run stage
FROM --platform=$TARGETPLATFORM nginx:latest
COPY --from=build /usr/src/app/dist/p3m-frontend /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
