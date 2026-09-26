@echo off
REM Port Availability Checker for Microservices
REM Checks if all required ports are free before starting services

echo ============================================
echo   Port Availability Checker
echo ============================================
echo.

SET PORTS_AVAILABLE=1

echo Checking required ports...
echo.

REM Function to check a port
:CHECK_PORT
SET PORT=%1
SET SERVICE=%2

netstat -ano | findstr ":%PORT% " | findstr "LISTENING" >nul
IF %ERRORLEVEL% EQU 0 (
    echo [X] Port %PORT% - IN USE     - %SERVICE%
    SET PORTS_AVAILABLE=0
    
    REM Find process using the port
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%PORT% " ^| findstr "LISTENING"') do (
        echo     Process ID: %%a
        for /f "tokens=1" %%b in ('tasklist /FI "PID eq %%a" /NH') do (
            echo     Process Name: %%b
        )
    )
    echo.
) ELSE (
    echo [OK] Port %PORT% - AVAILABLE - %SERVICE%
)
GOTO :EOF

REM Check all ports
CALL :CHECK_PORT 8761 "Service Registry (Eureka)"
CALL :CHECK_PORT 7100 "Config Server"
CALL :CHECK_PORT 8099 "API Gateway"
CALL :CHECK_PORT 8300 "Cart Service"
CALL :CHECK_PORT 8100 "Order Service"
CALL :CHECK_PORT 8900 "Inventory Service"
CALL :CHECK_PORT 16686 "Jaeger UI"
CALL :CHECK_PORT 4318 "Jaeger OTLP"

echo.
echo ============================================
echo   Summary
echo ============================================
echo.

IF %PORTS_AVAILABLE% EQU 1 (
    echo [SUCCESS] All ports are available!
    echo You can start the services now.
    echo.
    echo To start services:
    echo   - Docker:  docker-compose up -d
    echo   - Manual:  See README.md for startup sequence
) ELSE (
    echo [WARNING] Some ports are in use!
    echo.
    echo To free up ports, you can:
    echo   1. Stop the processes shown above
    echo   2. Change ports in application.properties files
    echo   3. Use Docker (it will stop conflicting containers)
    echo.
    echo To stop a process:
    echo   taskkill /PID [Process_ID] /F
)

echo.
pause
