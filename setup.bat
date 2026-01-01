@echo off
echo =========================================
echo Shared Academic Calendar - Project Setup
echo =========================================

echo.
echo [1/2] Setting up Backend (Spring Boot - Java 17)
cd backend
call mvnw clean install
if errorlevel 1 (
    echo Backend setup failed
    pause
    exit /b 1
)

echo.
echo [2/2] Setting up Frontend (Flutter)
cd ../my_app
flutter pub get
if errorlevel 1 (
    echo Frontend setup failed
    pause
    exit /b 1
)

echo.
echo All dependencies installed successfully!
echo You can now run the backend and frontend.
pause
