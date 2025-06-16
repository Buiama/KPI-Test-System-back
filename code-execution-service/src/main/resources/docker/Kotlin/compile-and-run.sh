#!/bin/bash
set -e

if [ -f "main.txt" ]; then
    MAIN_FILE=$(cat main.txt)
#    echo "Main file from main.txt: $MAIN_FILE"
else
    MAIN_FILE=$(find . -name "*.kt" -type f -exec grep -l "fun main" {} \; | head -n 1)
    if [ -z "$MAIN_FILE" ]; then
        MAIN_FILE=$(find . -name "*.kt" -type f | head -n 1)
    fi

    if [ -z "$MAIN_FILE" ]; then
        echo "No Kotlin files found!"
        exit 1
    fi
#    echo "Detected main file: $MAIN_FILE"
fi

#echo "Compiling all Kotlin files..."
kotlinc $(find . -name "*.kt") -include-runtime -d app.jar

#echo "Compilation completed, running the application..."

if [ -f "input.txt" ]; then
#    echo "Found input.txt, will use it as input"
#    echo "Running with input from input.txt..."
    java -jar app.jar < input.txt
else
#    echo "Running without input..."
    java -jar app.jar
fi
