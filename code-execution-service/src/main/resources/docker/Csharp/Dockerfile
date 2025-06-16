FROM mcr.microsoft.com/dotnet/sdk:6.0
LABEL authors="Buiama"

WORKDIR /app
COPY . /app

COPY compile-and-run.sh /app/
RUN chmod +x /app/compile-and-run.sh

CMD ["/app/compile-and-run.sh"]
