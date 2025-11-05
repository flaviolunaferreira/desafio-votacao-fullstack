@echo off
chcp 65001 >nul
echo ========================================
echo  Frontend Angular - Sistema de Votacao
echo ========================================
echo.

echo [1/3] Verificando Docker Desktop...
docker --version >nul 2>&1
if errorlevel 1 (
    echo ERRO: Docker nao encontrado.
    echo Instale o Docker Desktop para Windows
    pause
    exit /b 1
)

echo [2/3] Verificando se Docker esta rodando...
docker ps >nul 2>&1
if errorlevel 1 (
    echo ERRO: Docker Desktop nao esta rodando
    echo Inicie o Docker Desktop e tente novamente
    pause
    exit /b 1
)

echo [3/3] Parando containers existentes do frontend...
docker-compose down -v

echo.
echo Verificando se o backend esta rodando na porta 8080...
curl -f http://localhost:8080/actuator/health >nul 2>&1
if errorlevel 1 (
    echo AVISO: Backend nao encontrado na porta 8080
    echo Certifique-se de que o backend esta rodando primeiro
    echo.
)

echo Construindo e iniciando Frontend Angular...
echo (O build pode demorar alguns minutos na primeira vez)
docker-compose up -d --build

if errorlevel 1 (
    echo ERRO: Falha ao iniciar frontend
    echo Verifique os logs com: docker-compose logs
    pause
    exit /b 1
)

echo.
echo ========================================
echo  Frontend iniciado com sucesso!
echo ========================================
echo.
echo Frontend: http://localhost:4200
echo Backend API: http://localhost:8080
echo.
echo Para ver logs: docker-compose logs -f
echo Para parar: docker-compose down
echo.
pause
