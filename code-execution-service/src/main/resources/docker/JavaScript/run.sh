#!/bin/bash
set -e

if [ -f "package.json" ]; then
#    echo "Package.json found"
    if grep -q "\"start\"" package.json; then
#        echo "Running npm start script..."
        npm start
        exit 0
    fi
fi

if [ -f "main.txt" ]; then
    MAIN_FILE=$(cat main.txt)
#    echo "Main file from main.txt: $MAIN_FILE"
else
    if [ -f "index.js" ]; then
        MAIN_FILE="index.js"
    elif [ -f "main.js" ]; then
        MAIN_FILE="main.js"
    elif [ -f "Main.js" ]; then
        MAIN_FILE="Main.js"
    else
        MAIN_FILE=$(find . -name "*.js" -type f | head -n 1)
    fi

    if [ -z "$MAIN_FILE" ]; then
        echo "No JavaScript files found!"
        exit 1
    fi
#    echo "Detected main file: $MAIN_FILE"
fi

#echo "Running $MAIN_FILE..."
if [ -f "input.txt" ]; then
    node "$MAIN_FILE" < input.txt
else
    node "$MAIN_FILE"
fi
