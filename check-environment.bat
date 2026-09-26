@echo off
REM Environment Check Script

echo ============================================
echo   Environment Check
echo ============================================
echo.

echo [1] Checking Java...
java -version 2>&1
if errorlevel 1 (
    echo [FAILED] Java not found!
) else (
    echo.
    java -version 2>&1 | findstr /C:"21" >nul
    if errorlevel 1 (
        echo [WARNING] Java 21 NOT detected
        echo [INFO] Project requires Java 21
    ) else (
        echo [OK] Java 21 detected
    )
)
echo.

echo [2] Checking Gradle...
gradle --version 2>&1 | findstr /C:"Gradle"
if errorlevel 1 (
    echo [INFO] Gradle not installed globally (not required - using wrapper)
) else (
    echo [OK] Gradle installed
)
echo.

echo [3] Checking Docker...
docker --version 2>&1
if errorlevel 1 (
    echo [WARNING] Docker not found
    echo [INFO] Docker is optional but recommended
) else (
    echo [OK] Docker installed
)
echo.

echo [4] Checking Docker Compose...
docker-compose --version 2>&1
if errorlevel 1 (
    echo [WARNING] Docker Compose not found
    echo [INFO] Docker Compose is optional but recommended
) else (
    echo [OK] Docker Compose installed
)
echo.

echo ============================================
echo   Build Status Check
echo ============================================
echo.

set BUILD_COUNT=0

if exist "service-registry\build\libs\*.jar" (
    echo [OK] Service Registry: Built
    set /a BUILD_COUNT+=1
) else (
    echo [PENDING] Service Registry: Not built
)

if exist "configserver\build\libs\*.jar" (
    echo [OK] Config Server: Built
    set /a BUILD_COUNT+=1
) else (
    echo [PENDING] Config Server: Not built
)

if exist "api-gateway\build\libs\*.jar" (
    echo [OK] API Gateway: Built
    set /a BUILD_COUNT+=1
) else (
    echo [PENDING] API Gateway: Not built
)

if exist "cart-service\build\libs\*.jar" (
    echo [OK] Cart Service: Built
    set /a BUILD_COUNT+=1
) else (
    echo [PENDING] Cart Service: Not built
)

if exist "order-service\build\libs\*.jar" (
    echo [OK] Order Service: Built
    set /a BUILD_COUNT+=1
) else (
    echo [PENDING] Order Service: Not built
)

if exist "inventory-service\build\libs\*.jar" (
    echo [OK] Inventory Service: Built
    set /a BUILD_COUNT+=1
) else (
    echo [PENDING] Inventory Service: Not built
)

echo.
echo Services Built: %BUILD_COUNT%/6
echo.

echo ============================================
echo   Recommendations
echo ============================================
echo.

java -version 2>&1 | findstr /C:"21" >nul
if errorlevel 1 (
    echo [ACTION REQUIRED] Install Java 21
    echo   - Chocolatey: choco install openjdk21
    echo   - Manual: https://adoptium.net/temurin/releases/?version=21
    echo.
)

if %BUILD_COUNT%==0 (
    echo [NEXT STEP] Build all services:
    echo   Option 1: Run build-all.bat
    echo   Option 2: docker-compose up --build
    echo.
)

if %BUILD_COUNT% GTR 0 if %BUILD_COUNT% LSS 6 (
    echo [NEXT STEP] Complete remaining builds:
    echo   Run: build-all.bat
    echo.
)

if %BUILD_COUNT%==6 (
    echo [READY] All services are built!
    echo [NEXT STEP] Start services:
    echo   - With Docker: docker-compose up
    echo   - Manually: Run JAR files individually
    echo.
)

pause
