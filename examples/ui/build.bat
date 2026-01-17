@echo off
echo Building example-ui plugin...
call ./gradlew.bat build
if %ERRORLEVEL% EQU 0 (
    echo Build successful! JAR located at: build\libs\example-ui.jar
) else (
    echo Build failed!
)
