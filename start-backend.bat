@echo off
cd /d "%~dp0backend"
if exist "target\BOOT-INF\lib" (
    start "NexusFlow Backend" cmd /c "java -cp ""target\classes;target\BOOT-INF\lib\*"" com.nexusflow.NexusFlowApplication"
) else if exist "target\nexusflow-backend-1.0.0.jar" (
    start "NexusFlow Backend" cmd /c "java -jar target\nexusflow-backend-1.0.0.jar"
) else (
    start "NexusFlow Backend" cmd /c "mvn spring-boot:run"
)
echo Backend application starting in a new window...
echo Please wait for the application to fully start (about 30-60 seconds)
