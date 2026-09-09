@echo off
echo ================================================================
echo  Starting NexusFlow: Autonomous AI Supply Chain Risk Platform
echo ================================================================

echo [1/2] Launching Backend on port 8081 (Spring Boot 3.2.5 / Java 21)...
cd /d "%~dp0..\backend"
start "NexusFlow Backend (Port 8081)" cmd /k "mvn spring-boot:run"

echo [2/2] Launching Frontend on port 3001 (React 18 / Vite 7)...
cd /d "%~dp0..\frontend"
start "NexusFlow Frontend (Port 3001)" cmd /k "npm run dev"

echo ================================================================
echo  Application Services Started:
echo  - Frontend Control Tower:  http://localhost:3001
echo  - Backend API Base:        http://localhost:8081/api
echo  - Swagger OpenAPI UI:      http://localhost:8081/api/swagger-ui.html
echo  - H2 Database Console:     http://localhost:8081/api/h2-console
echo  Demo Credentials: admin@nexusflow.com / admin123
echo ================================================================
