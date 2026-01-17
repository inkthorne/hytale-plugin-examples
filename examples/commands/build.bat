@echo off
echo Building example-commands plugin...
call ./gradlew.bat build
if %ERRORLEVEL% EQU 0 (
    echo Build successful! JAR located at: build\libs\example-commands.jar
) else (
    echo Build failed!
)
