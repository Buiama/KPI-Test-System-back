#!/bin/bash

IMAGES=(
    "openjdk:17-slim"
    "python:3.9-slim"
    "gcc:latest"
    "node:14-slim"
    "golang:1.17-alpine"
    "mcr.microsoft.com/dotnet/sdk:6.0"
    "julia:1.7"
)

echo "Начинаем загрузку базовых Docker-образов..."

for IMAGE in "${IMAGES[@]}"; do
    echo "Загрузка образа $IMAGE..."
    docker pull "$IMAGE"
    if [ $? -eq 0 ]; then
        echo "Образ $IMAGE успешно загружен"
    else
        echo "Ошибка при загрузке образа $IMAGE"
    fi
done

echo "Загрузка всех образов завершена."
