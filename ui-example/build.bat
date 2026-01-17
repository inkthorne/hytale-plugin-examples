@echo off
echo Building ui-example plugin...
call gradlew build
if %ERRORLEVEL% EQU 0 (
    echo Build successful! JAR located at: build\libs\ui-example-1.0.0.jar
) else (
    echo Build failed!
)
