FROM gcc:latest
LABEL authors="Buiama"

WORKDIR /app
RUN apt-get update && apt-get install -y cmake make

COPY run.sh /app/
RUN chmod +x /app/run.sh

CMD ["/app/run.sh"]
