@echo off
set "MODS_DIR=%APPDATA%\Hytale\UserData\Mods"
set "JAR_FILE=build\libs\ui-example-1.0.0.jar"

if not exist "%JAR_FILE%" (
    echo JAR not found. Building first...
    call gradlew build
    if %ERRORLEVEL% NEQ 0 (
        echo Build failed!
                exit /b 1
    )
)

if not exist "%MODS_DIR%" (
    echo Creating mods directory...
    mkdir "%MODS_DIR%"
)

echo Deploying to %MODS_DIR%...
copy /Y "%JAR_FILE%" "%MODS_DIR%\"

if %ERRORLEVEL% EQU 0 (
    echo Deployed successfully!
) else (
    echo Deploy failed!
)
