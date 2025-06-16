FROM julia:1.8
LABEL authors="Buiama"

WORKDIR /app
COPY run.sh /app/
RUN chmod +x /app/run.sh

CMD ["/app/run.sh"]
