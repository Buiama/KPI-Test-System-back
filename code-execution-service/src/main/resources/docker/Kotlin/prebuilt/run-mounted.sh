#!/bin/bash
set -e

cd /app

if [ -f "main.txt" ]; then
    MAIN_FILE=$(cat main.txt)
else
    MAIN_FILE=$(find . -name "*.kt" -type f -exec grep -l "fun main" {} \; | head -n 1)
    if [ -z "$MAIN_FILE" ]; then
        MAIN_FILE=$(find . -name "*.kt" -type f | head -n 1)
    fi

    if [ -z "$MAIN_FILE" ]; then
        echo "No Kotlin files found!"
        exit 1
    fi
fi

kotlinc $(find . -name "*.kt") -include-runtime -d app.jar

if [ -f "input.txt" ]; then
    java -jar app.jar < input.txt
else
    java -jar app.jar
fi
