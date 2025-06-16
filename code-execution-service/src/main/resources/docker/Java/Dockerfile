FROM openjdk:17-slim
LABEL authors="Buiama"

ENV LANG C.UTF-8
ENV LC_ALL C.UTF-8

WORKDIR /app
COPY . /app

COPY compile-and-run.sh /app/
RUN chmod +x /app/compile-and-run.sh

CMD ["/app/compile-and-run.sh"]