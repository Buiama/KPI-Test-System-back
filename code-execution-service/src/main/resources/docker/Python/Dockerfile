FROM python:3.9-slim
LABEL authors="Buiama"

WORKDIR /app
COPY . /app

RUN if [ -f "requirements.txt" ]; then pip install --no-cache-dir -r requirements.txt; fi

COPY run.sh /app/
RUN chmod +x /app/run.sh

CMD ["/app/run.sh"]
