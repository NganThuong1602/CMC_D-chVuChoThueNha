@echo off
chcp 65001 >nul
where java >nul 2>nul || (echo [ERROR] Can cai Java 21. & pause & exit /b 1)
where mvn >nul 2>nul || (echo [ERROR] Can cai Apache Maven va them vao PATH. & pause & exit /b 1)
where npm >nul 2>nul || (echo [ERROR] Can cai Node.js va npm. & pause & exit /b 1)
echo === EasyHome - Start backend and frontend ===
start "EasyHome Spring Boot API" /D "%~dp0backend" cmd /k "mvn spring-boot:run"
start "EasyHome React" /D "%~dp0frontend" cmd /k "npm install && npm run dev"
echo Backend: http://localhost:8080/api/listings
echo Frontend: http://localhost:5173
echo Du lieu demo: owner@easyhome.vn / 123456
pause
