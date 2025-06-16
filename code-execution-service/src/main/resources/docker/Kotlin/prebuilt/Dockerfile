FROM openjdk:17-slim
LABEL authors="Buiama"

ENV LANG C.UTF-8
ENV LC_ALL C.UTF-8

RUN apt-get update && \
    apt-get install -y --no-install-recommends wget unzip ca-certificates && \
    wget -q https://github.com/JetBrains/kotlin/releases/download/v1.9.0/kotlin-compiler-1.9.0.zip && \
    unzip kotlin-compiler-1.9.0.zip -d /opt && \
    rm kotlin-compiler-1.9.0.zip && \
    apt-get remove -y wget unzip && \
    apt-get autoremove -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

ENV PATH=$PATH:/opt/kotlinc/bin

WORKDIR /app
COPY run.sh /app/
RUN chmod +x /app/run.sh

CMD ["/app/run.sh"]
