#!/bin/bash
set -e

cd /app

if [ -f "main.txt" ]; then
    MAIN_FILE=$(cat main.txt)
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
fi

if [ -f "requirements.txt" ]; then
    pip install --no-cache-dir -r requirements.txt
fi

if [ -f "input.txt" ]; then
    python "$MAIN_FILE" < input.txt
else
    python "$MAIN_FILE"
fi
