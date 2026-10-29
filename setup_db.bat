@echo off
title CampusConnect — Database Setup
cd /d "%~dp0"

echo ===================================================
echo   CampusConnect — MySQL Database Setup
echo ===================================================
echo.

:: Detect MySQL executable
set "MYSQL_CMD=mysql"
where mysql >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    if exist "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" (
        set "MYSQL_CMD=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
    )
)

echo Importing sql\campus_connect.sql into MySQL...
echo Please enter your MySQL root password when prompted:
echo.

"%MYSQL_CMD%" -u root -p < sql\campus_connect.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ===================================================
    echo   Database imported successfully!
    echo ===================================================
) else (
    echo.
    echo [ERROR] Database setup failed. Please check your password.
)

echo.
pause
