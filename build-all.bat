@echo off
REM Build All Microservices Script
REM Requires Java 21

echo ============================================
echo   Building All Microservices
echo ============================================
echo.

REM Check Java version (Skip check - Gradle wrapper will handle Java 21 via toolchain)
echo [1/7] Checking build environment...
echo [INFO] Using Gradle wrapper with Java toolchain
echo [INFO] Gradle will automatically use Java 21 for compilation
echo.

REM Service Registry
echo [2/7] Building Service Registry...
cd service-registry
call gradlew.bat clean build -x test
if errorlevel 1 (
    echo [FAILED] Service Registry build failed!
    cd ..
    pause
    exit /b 1
)
echo [OK] Service Registry built successfully
cd ..
echo.

REM Config Server
echo [3/7] Building Config Server...
cd configserver
call gradlew.bat clean build -x test
if errorlevel 1 (
    echo [FAILED] Config Server build failed!
    cd ..
    pause
    exit /b 1
)
echo [OK] Config Server built successfully
cd ..
echo.

REM API Gateway
echo [4/7] Building API Gateway...
cd api-gateway
call gradlew.bat clean build -x test
if errorlevel 1 (
    echo [FAILED] API Gateway build failed!
    cd ..
    pause
    exit /b 1
)
echo [OK] API Gateway built successfully
cd ..
echo.

REM Cart Service
echo [5/7] Building Cart Service...
cd cart-service
call gradlew.bat clean build -x test
if errorlevel 1 (
    echo [FAILED] Cart Service build failed!
    cd ..
    pause
    exit /b 1
)
echo [OK] Cart Service built successfully
cd ..
echo.

REM Order Service
echo [6/7] Building Order Service...
cd order-service
call gradlew.bat clean build -x test
if errorlevel 1 (
    echo [FAILED] Order Service build failed!
    cd ..
    pause
    exit /b 1
)
echo [OK] Order Service built successfully
cd ..
echo.

REM Inventory Service
echo [7/7] Building Inventory Service...
cd inventory-service
call gradlew.bat clean build -x test
if errorlevel 1 (
    echo [FAILED] Inventory Service build failed!
    cd ..
    pause
    exit /b 1
)
echo [OK] Inventory Service built successfully
cd ..
echo.

echo ============================================
echo   BUILD SUMMARY
echo ============================================
echo.
echo All services built successfully!
echo.
echo JAR files location:
echo   service-registry/build/libs/service-registry-0.0.1-SNAPSHOT.jar
echo   configserver/build/libs/configserver-0.0.1-SNAPSHOT.jar
echo   api-gateway/build/libs/api-gateway-0.0.1-SNAPSHOT.jar
echo   cart-service/build/libs/cart-service-0.0.1-SNAPSHOT.jar
echo   order-service/build/libs/order-service-0.0.1-SNAPSHOT.jar
echo   inventory-service/build/libs/inventory-service-0.0.1-SNAPSHOT.jar
echo.
echo ============================================
echo   NEXT STEPS
echo ============================================
echo.
echo To run services:
echo   1. With Docker: docker-compose up
echo   2. Manually: Run each JAR file
echo.
pause
