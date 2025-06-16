#!/bin/bash
set -e

PROJECT_FILE=$(find . -maxdepth 1 -name "*.csproj" | head -n 1)
SOLUTION_FILE=$(find . -maxdepth 1 -name "*.sln" | head -n 1)

if [ ! -z "$PROJECT_FILE" ]; then
#    echo "Project file found: $PROJECT_FILE"
    dotnet restore "$PROJECT_FILE"
    dotnet build "$PROJECT_FILE" -c Release -nowarn:*
    if [ -f "input.txt" ]; then
        dotnet run --project "$PROJECT_FILE" --no-build -c Release < input.txt
    else
        dotnet run --project "$PROJECT_FILE" --no-build -c Release
    fi
    exit 0
fi

if [ ! -z "$SOLUTION_FILE" ]; then
#    echo "Solution file found: $SOLUTION_FILE"
    dotnet restore "$PROJECT_FILE"
    dotnet build "$SOLUTION_FILE" -c Release -nowarn:*

    STARTUP_PROJECT=$(find . -name "*.csproj" -exec grep -l "<OutputType>Exe</OutputType>" {} \; | head -n 1)

    if [ ! -z "$STARTUP_PROJECT" ]; then
        if [ -f "input.txt" ]; then
            dotnet run --project "$STARTUP_PROJECT" --no-build -c Release < input.txt
        else
            dotnet run --project "$STARTUP_PROJECT" --no-build -c Release
        fi
    else
        echo "No executable project found in solution!"
        exit 1
    fi

    exit 0
fi

if [ -f "main.txt" ]; then
    MAIN_FILE=$(cat main.txt)
#    echo "Main file from main.txt: $MAIN_FILE"
else
    if [ -f "Program.cs" ]; then
        MAIN_FILE="Program.cs"
    elif [ -f "Main.cs" ]; then
        MAIN_FILE="Main.cs"
    else
        MAIN_FILE=$(find . -name "*.cs" -type f | head -n 1)
    fi

    if [ -z "$MAIN_FILE" ]; then
        echo "No C# files found!"
        exit 1
    fi
fi

dotnet new console -o TempProject --force --no-restore --name TempProjectApp > /dev/null 2>&1

sed -i 's|</PropertyGroup>|  <NoWarn>$(NoWarn);*</NoWarn>\n  <WarningsAsErrors></WarningsAsErrors>\n  <WarningLevel>0</WarningLevel>\n  <Nullable>disable</Nullable>\n</PropertyGroup>|' TempProject/TempProjectApp.csproj
find . -name "*.cs" ! -path "./TempProject/*" -exec cp {} TempProject/ \;

#echo "Building and running temporary project..."
cd TempProject
dotnet build -c Release -nowarn:* > /dev/null 2>&1
if [ -f "../input.txt" ]; then
    dotnet run -c Release < ../input.txt
else
    dotnet run -c Release
fi
