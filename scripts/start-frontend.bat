@echo off
echo ================================================================
echo  Starting NexusFlow React/Vite Frontend (Port 3001)
echo ================================================================
cd /d "%~dp0..\frontend"
start "NexusFlow Frontend (Port 3001)" cmd /k "npm run dev"
echo Frontend starting in a dedicated window.
echo Dashboard URL: http://localhost:3001
