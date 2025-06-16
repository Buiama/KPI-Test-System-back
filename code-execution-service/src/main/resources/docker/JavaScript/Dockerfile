FROM node:16-slim
LABEL authors="Buiama"

WORKDIR /app
COPY . /app

RUN if [ -f "package.json" ]; then npm install; fi

COPY run.sh /app/
RUN chmod +x /app/run.sh

CMD ["/app/run.sh"]
