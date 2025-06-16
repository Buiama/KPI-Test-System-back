#!/bin/sh
set -e

if [ -f "go.mod" ]; then
#    echo "Go module detected (go.mod found)"
    go mod download

    if grep -q "package main" *.go 2>/dev/null; then
#        echo "Found main package in current directory, running..."
        if [ -f "input.txt" ]; then
#            echo "Found input.txt, will use it as input"
            go run . < input.txt
        else
            go run .
        fi
        exit 0
    fi

    MAIN_PKG=$(find . -path "*/cmd/*" -name "*.go" -exec grep -l "package main" {} \; -exec dirname {} \; | sort | uniq | head -n 1)
    if [ -z "$MAIN_PKG" ]; then
        MAIN_PKG=$(find . -type f -name "*.go" -exec grep -l "package main" {} \; -exec dirname {} \; | sort | uniq | head -n 1)
    fi

    if [ ! -z "$MAIN_PKG" ]; then
#        echo "Found main package in $MAIN_PKG, running..."
        if [ -f "input.txt" ]; then
#            echo "Found input.txt, will use it as input"
            go run "$MAIN_PKG" < input.txt
        else
            go run "$MAIN_PKG"
        fi
        exit 0
    fi

    echo "No main package found in Go module"
    exit 1
fi

if [ -f "main.txt" ]; then
    MAIN_FILE=$(cat main.txt)
#    echo "Main file from main.txt: $MAIN_FILE"
else
    if [ -f "main.go" ]; then
        MAIN_FILE="main.go"
    elif [ -f "Main.go" ]; then
        MAIN_FILE="Main.go"
    else
        MAIN_FILE=$(grep -l "package main" *.go 2>/dev/null | head -n 1)

        if [ -z "$MAIN_FILE" ]; then
            MAIN_FILE=$(find . -name "*.go" -type f | head -n 1)
        fi
    fi

    if [ -z "$MAIN_FILE" ]; then
        echo "No Go files found!"
        exit 1
    fi
#    echo "Detected main file: $MAIN_FILE"
fi

if [ -f "input.txt" ]; then
#    echo "Found input.txt, will use it as input"
    if [ -f "go.mod" ]; then
        go run . < input.txt
    else
        go run *.go < input.txt
    fi
else
    if [ -f "go.mod" ]; then
        go run .
    else
        go run *.go
    fi
fi
