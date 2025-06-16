#!/bin/bash
set -e

cd /app

if [ -f "main.txt" ]; then
    MAIN_FILE=$(cat main.txt)
else
    MAIN_FILE=$(find . -name "*.ts" -type f | grep -i "main\|index" | head -n 1)
    if [ -z "$MAIN_FILE" ]; then
        MAIN_FILE=$(find . -name "*.ts" -type f | head -n 1)
    fi

    if [ -z "$MAIN_FILE" ]; then
        echo "No TypeScript files found!"
        exit 1
    fi
fi

if [ -f "package.json" ]; then
    npm install
fi

if [ ! -f "tsconfig.json" ]; then
    cat > tsconfig.json << EOF
{
  "compilerOptions": {
    "target": "es2018",
    "module": "commonjs",
    "strict": true,
    "esModuleInterop": true,
    "skipLibCheck": true,
    "forceConsistentCasingInFileNames": true,
    "outDir": "./dist",
    "rootDir": "."
  },
  "include": ["**/*.ts"],
  "exclude": ["node_modules"]
}
EOF
fi

tsc -p tsconfig.json

MAIN_FILE_BASE=$(basename "$MAIN_FILE")
MAIN_FILE_JS="${MAIN_FILE_BASE%.ts}.js"
JS_MAIN_FILE="dist/$MAIN_FILE_JS"

if [ -f "input.txt" ]; then
    node "$JS_MAIN_FILE" < input.txt
else
    node "$JS_MAIN_FILE"
fi
