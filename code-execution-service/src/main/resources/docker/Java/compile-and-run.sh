#!/bin/bash
set -e

if [ -f "main.txt" ]; then
    MAIN_CLASS=$(cat main.txt)
#    echo "Main class from main.txt: $MAIN_CLASS"
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
#    echo "Detected main class: $MAIN_CLASS from $MAIN_FILE"

    PACKAGE=$(grep -o "package .*;" "$MAIN_FILE" | awk '{print $2}' | sed 's/;//')
    if [ ! -z "$PACKAGE" ]; then
        MAIN_CLASS="${PACKAGE}.${MAIN_CLASS}"
#        echo "Class is in package: $MAIN_CLASS"
    fi
fi

#echo "Compiling all Java files..."
mkdir -p classes
find . -name "*.java" | xargs javac -d classes

if [ -f "input.txt" ]; then
#    echo "Found input.txt, will use it as input"
#    echo "Running $MAIN_CLASS with input from input.txt..."
    java -Dfile.encoding=UTF-8 -cp classes:"." "$MAIN_CLASS" < input.txt
else
#    echo "Running $MAIN_CLASS without input..."
    java -Dfile.encoding=UTF-8 -cp classes:"." "$MAIN_CLASS"
fi
