@echo off
cd nexusflow-backend
start "NexusFlow Backend" cmd /k "mvn spring-boot:run"
echo Backend application starting in a new window...
echo Please wait for the application to fully start (about 30-60 seconds)