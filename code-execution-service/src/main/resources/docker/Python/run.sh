#!/bin/bash
set -e

if [ -f "main.txt" ]; then
    MAIN_FILE=$(cat main.txt)
#    echo "Main file from main.txt: $MAIN_FILE"
else
    if [ -f "Main.py" ]; then
        MAIN_FILE="Main.py"
    elif [ -f "main.py" ]; then
        MAIN_FILE="main.py"
    elif [ -f "app.py" ]; then
        MAIN_FILE="app.py"
    else
        MAIN_FILE=$(find . -name "*.py" -type f | head -n 1)
    fi

    if [ -z "$MAIN_FILE" ]; then
        echo "No Python files found!"
        exit 1
    fi
#    echo "Detected main file: $MAIN_FILE"
fi

if [ -f "input.txt" ]; then
#    echo "Found input.txt, will use it as input"
    python "$MAIN_FILE" < input.txt
else
    python "$MAIN_FILE"
fi
