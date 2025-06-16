FROM node:16-slim
LABEL authors="Buiama"

ENV LANG C.UTF-8
ENV LC_ALL C.UTF-8

RUN npm install -g typescript@latest

WORKDIR /app

COPY package.json ./
COPY tsconfig.json ./

RUN npm install --only=development

COPY . .

RUN chmod +x /app/compile-and-run.sh

CMD ["/app/compile-and-run.sh"]
