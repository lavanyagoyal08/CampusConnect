@echo off
title CampusConnect — Smart Campus Activities Management System
cd /d "%~dp0"

echo ===================================================
echo   CampusConnect — Starting Application...
echo ===================================================
echo.

:: Detect Java executable
set "JAVA_CMD=java"
where java >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    if exist "C:\Users\kaush\.antigravity\extensions\redhat.java-1.54.0-win32-x64\jre\21.0.10-win32-x86_64\bin\java.exe" (
        set "JAVA_CMD=C:\Users\kaush\.antigravity\extensions\redhat.java-1.54.0-win32-x64\jre\21.0.10-win32-x86_64\bin\java.exe"
    ) else if exist "C:\Program Files\Java" (
        for /d %%i in ("C:\Program Files\Java\jdk*") do (
            if exist "%%i\bin\java.exe" set "JAVA_CMD=%%i\bin\java.exe"
        )
    )
)

echo Using Java: "%JAVA_CMD%"
echo.

:: Run application
"%JAVA_CMD%" -cp "sql\bin;lib\mysql-connector-j-26.10.0.jar;src" app.CampusConnectApp

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Application failed to launch.
    pause
)
