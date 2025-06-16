#!/bin/bash

if ! command -v docker &> /dev/null; then
    echo "Docker не установлен. Пожалуйста, установите Docker для работы с сервисом."
    exit 1
fi

if ! docker info &> /dev/null; then
    echo "Docker демон не запущен. Пожалуйста, запустите Docker для работы с сервисом."
    exit 1
fi

echo "Docker доступен и готов к использованию."
exit 0
