# Dockerfile for Vue CSR App
FROM node:23 AS build
WORKDIR /usr/src/app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Nginx 단계로 정적 파일 복사
FROM nginx:alpine
COPY --from=build /usr/src/app/dist /usr/share/nginx/html/app
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
