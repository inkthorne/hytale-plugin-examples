@echo off
echo Building example-inventory plugin...
call ./gradlew.bat build
if %ERRORLEVEL% EQU 0 (
    echo Build successful! JAR located at: build\libs\example-inventory.jar
) else (
    echo Build failed!
)
