#!/bin/bash
set -e

cd /app

if [ -f "main.txt" ]; then
    MAIN_CLASS=$(cat main.txt)
else
    # Ищем класс с методом main
    MAIN_FILE=$(find . -name "*.java" -type f -exec grep -l "public static void main" {} \; | head -n 1)
    if [ -z "$MAIN_FILE" ]; then
        # Если класс с main не найден, берем первый .java файл
        MAIN_FILE=$(find . -name "*.java" -type f | head -n 1)
    fi

    if [ -z "$MAIN_FILE" ]; then
        echo "No Java files found!"
        exit 1
    fi

    MAIN_CLASS=$(basename "$MAIN_FILE" .java)

    PACKAGE=$(grep -o "package .*;" "$MAIN_FILE" | awk '{print $2}' | sed 's/;//')
    if [ ! -z "$PACKAGE" ]; then
        MAIN_CLASS="${PACKAGE}.${MAIN_CLASS}"
    fi
fi

mkdir -p classes
find . -name "*.java" | xargs javac -d classes

if [ -f "input.txt" ]; then
    java -Dfile.encoding=UTF-8 -cp classes:"." "$MAIN_CLASS" < input.txt
else
    java -Dfile.encoding=UTF-8 -cp classes:"." "$MAIN_CLASS"
fi
