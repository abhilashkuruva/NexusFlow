@echo off
echo ================================================================
echo  Starting NexusFlow Spring Boot Backend (Port 8081)
echo ================================================================
cd /d "%~dp0..\backend"
if exist "target\nexusflow-backend-1.0.0.jar" (
    echo Launching pre-built JAR package...
    start "NexusFlow Backend (Port 8081)" cmd /k "java -jar target\nexusflow-backend-1.0.0.jar"
) else (
    echo Running via Maven Spring Boot plugin...
    start "NexusFlow Backend (Port 8081)" cmd /k "mvn spring-boot:run"
)
echo Backend starting in a dedicated window.
echo Health/Swagger URL: http://localhost:8081/api/swagger-ui.html
