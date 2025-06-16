FROM golang:1.18-alpine
LABEL authors="Buiama"

WORKDIR /app
RUN apk add --no-cache git

COPY run.sh /app/
RUN chmod +x /app/run.sh

CMD ["/app/run.sh"]
