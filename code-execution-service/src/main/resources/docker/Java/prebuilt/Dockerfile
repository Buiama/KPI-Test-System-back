FROM openjdk:17-slim
LABEL authors="Buiama"

ENV LANG C.UTF-8
ENV LC_ALL C.UTF-8

WORKDIR /app
COPY run.sh /app/
RUN chmod +x /app/run.sh

CMD ["/app/run.sh"]
