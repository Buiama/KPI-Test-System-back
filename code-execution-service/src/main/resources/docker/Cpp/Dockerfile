FROM gcc:latest
LABEL authors="Buiama"

WORKDIR /app
COPY . /app

RUN apt-get update && apt-get install -y cmake make

COPY compile-and-run.sh /app/
RUN chmod +x /app/compile-and-run.sh

CMD ["/app/compile-and-run.sh"]
