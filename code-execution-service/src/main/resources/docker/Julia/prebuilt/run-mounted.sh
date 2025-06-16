#!/bin/bash
set -e

cd /app

if [ -f "Project.toml" ]; then
    julia --project=. -e 'using Pkg; Pkg.instantiate(); Pkg.precompile()'

    if [ -f "src/main.jl" ]; then
        if [ -f "input.txt" ]; then
            julia --project=. src/main.jl < input.txt
        else
            julia --project=. src/main.jl
        fi
        exit 0
    fi
fi

if [ -f "main.txt" ]; then
    MAIN_FILE=$(cat main.txt)
else
    if [ -f "main.jl" ]; then
        MAIN_FILE="main.jl"
    elif [ -f "Main.jl" ]; then
        MAIN_FILE="Main.jl"
    else
        MAIN_FILE=$(find . -name "*.jl" -type f | head -n 1)
    fi

    if [ -z "$MAIN_FILE" ]; then
        echo "No Julia files found!"
        exit 1
    fi
fi

if [ -f "input.txt" ]; then
    julia "$MAIN_FILE" < input.txt
else
    julia "$MAIN_FILE"
fi
