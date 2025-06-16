#!/bin/bash
set -e

cd /app

# Проверка на CMake проект
if [ -f "CMakeLists.txt" ]; then
    mkdir -p build && cd build
    cmake ..
    make
    EXECUTABLE=$(find . -type f -executable | head -n 1)
    if [ -z "$EXECUTABLE" ]; then
        echo "No executable found after CMake build!"
        exit 1
    fi
    "$EXECUTABLE"
    exit 0
fi

# Проверка на Makefile
if [ -f "Makefile" ] || [ -f "makefile" ]; then
    make
    EXECUTABLE=$(find . -type f -executable | head -n 1)
    if [ -z "$EXECUTABLE" ]; then
        echo "No executable found after Make build!"
        exit 1
    fi
    "$EXECUTABLE"
    exit 0
fi

if [ -f "main.txt" ]; then
    MAIN_FILE=$(cat main.txt)
    EXECUTABLE="app"
else
    if [ -f "main.cpp" ]; then
        MAIN_FILE="main.cpp"
    elif [ -f "Main.cpp" ]; then
        MAIN_FILE="Main.cpp"
    else
        # Ищем любой .cpp файл
        MAIN_FILE=$(find . -name "*.cpp" -type f | head -n 1)
    fi

    if [ -z "$MAIN_FILE" ]; then
        echo "No C++ files found!"
        exit 1
    fi
    EXECUTABLE="app"
fi

# Собираем все .cpp файлы для компиляции
CPP_FILES=$(find . -name "*.cpp" -type f)

if [ -f "input.txt" ]; then
    sed -i 's/\r$//' input.txt
fi

g++ -std=c++17 -finput-charset=UTF-8 -fexec-charset=UTF-8 -o "$EXECUTABLE" $CPP_FILES

if [ -f "input.txt" ]; then
    ./"$EXECUTABLE" < input.txt
else
    ./"$EXECUTABLE"
fi
