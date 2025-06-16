FROM node:16-slim
LABEL authors="Buiama"

ENV LANG C.UTF-8
ENV LC_ALL C.UTF-8

RUN npm install -g typescript@latest

WORKDIR /app
COPY run.sh /app/
RUN chmod +x /app/run.sh

RUN echo '{"name":"typescript-execution-env","version":"1.0.0","private":true,"devDependencies":{"@types/node":"^16.0.0"}}' > /app/package.json
RUN echo '{"compilerOptions":{"target":"es2018","module":"commonjs","strict":true,"esModuleInterop":true,"skipLibCheck":true,"forceConsistentCasingInFileNames":true,"outDir":"./dist","rootDir":".","types":["node"]},"include":["**/*.ts"],"exclude":["node_modules","dist"]}' > /app/tsconfig.json
RUN npm install --only=development

CMD ["/app/run.sh"]
