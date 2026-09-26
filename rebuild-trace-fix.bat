@echo off
REM ============================================================
REM Rebuild Services After Trace Propagation Fix
REM This script rebuilds all services with updated tracing config
REM ============================================================

setlocal enabledelayedexpansion
set "START_TIME=%TIME%"
set "FAILED_BUILDS="
set "SUCCESS_COUNT=0"
set "FAIL_COUNT=0"

echo.
echo ============================================================
echo Rebuilding Services with Trace Propagation Fix
echo ============================================================
echo.
echo IMPORTANT: This rebuild includes:
echo  - W3C trace propagation configuration
echo  - Feign client trace context propagation
echo  - Updated config server properties
echo.

REM Build config server FIRST (has new properties)
echo [1/6] Building configserver (contains updated trace config)...
cd configserver
call gradlew.bat build -x test > nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [configserver] BUILD SUCCESS
    set /a SUCCESS_COUNT+=1
) else (
    echo [configserver] BUILD FAILED
    set "FAILED_BUILDS=!FAILED_BUILDS! configserver"
    set /a FAIL_COUNT+=1
)
cd ..
echo.

REM Build remaining services
set "SERVICES=service-registry api-gateway cart-service order-service inventory-service"
set COUNT=2

for %%S in (%SERVICES%) do (
    echo [!COUNT!/6] Building %%S...
    cd "%%S"
    call gradlew.bat build -x test > nul 2>&1
    if !ERRORLEVEL! EQU 0 (
        echo [%%S] BUILD SUCCESS
        set /a SUCCESS_COUNT+=1
    ) else (
        echo [%%S] BUILD FAILED
        set "FAILED_BUILDS=!FAILED_BUILDS! %%S"
        set /a FAIL_COUNT+=1
    )
    cd ..
    echo.
    set /a COUNT+=1
)

echo.
echo ============================================================
echo Build Complete
echo ============================================================
echo Total Services: 6
echo Successful: %SUCCESS_COUNT%
echo Failed: %FAIL_COUNT%
echo.

if %FAIL_COUNT% GTR 0 (
    echo [ERROR] Some builds failed:%FAILED_BUILDS%
    echo Please check the errors above.
    echo.
    exit /b 1
) else (
    echo [SUCCESS] All services rebuilt successfully!
    echo.
    echo ============================================================
    echo Next Steps - RESTART SERVICES IN THIS ORDER:
    echo ============================================================
    echo.
    echo 1. Config Server (MUST BE FIRST - has new trace config)
    echo    cd configserver
    echo    java -jar target\configserver-0.0.1-SNAPSHOT.jar
    echo.
    echo 2. Service Registry
    echo    cd service-registry
    echo    java -jar target\service-registry-0.0.1-SNAPSHOT.jar
    echo.
    echo 3. API Gateway
    echo    cd api-gateway
    echo    java -jar target\api-gateway-0.0.1-SNAPSHOT.jar
    echo.
    echo 4. Cart Service
    echo    cd cart-service
    echo    java -jar target\cart-service-0.0.1-SNAPSHOT.jar
    echo.
    echo 5. Order Service
    echo    cd order-service
    echo    java -jar target\order-service-0.0.1-SNAPSHOT.jar
    echo.
    echo 6. Inventory Service
    echo    cd inventory-service
    echo    java -jar target\inventory-service-0.0.1-SNAPSHOT.jar
    echo.
    echo ============================================================
    echo After restarting, verify traces in Jaeger:
    echo http://localhost:16686
    echo.
    echo You should now see ALL 4 services in the trace:
    echo  - api-gateway
    echo  - cart-service
    echo  - order-service
    echo  - inventory-service
    echo.
)

echo Start Time: %START_TIME%
echo End Time:   %TIME%
echo.
exit /b 0
