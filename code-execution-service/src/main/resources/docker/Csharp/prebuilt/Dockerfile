FROM mcr.microsoft.com/dotnet/sdk:6.0
LABEL authors="Buiama"

WORKDIR /app
COPY run.sh /app/
RUN chmod +x /app/run.sh

CMD ["/app/run.sh"]
