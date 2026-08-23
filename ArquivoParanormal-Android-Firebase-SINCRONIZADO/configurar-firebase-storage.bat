@echo off
setlocal
cd /d "%~dp0"

echo ================================================
echo  ARQUIVO PARANORMAL - CONFIGURAR FIREBASE STORAGE
echo ================================================
echo.

where firebase >nul 2>nul
if errorlevel 1 (
  echo Firebase CLI nao encontrado.
  echo Instalando Firebase CLI globalmente...
  npm install -g firebase-tools
  if errorlevel 1 (
    echo.
    echo ERRO ao instalar o Firebase CLI.
    pause
    exit /b 1
  )
)

echo.
echo Projeto Firebase: arquivo-paranormal
echo.
firebase use arquivo-paranormal
if errorlevel 1 (
  echo ERRO: nao foi possivel selecionar o projeto.
  pause
  exit /b 1
)

echo.
echo Publicando regras do Storage e Firestore...
firebase deploy --only storage,firestore:rules
if errorlevel 1 (
  echo.
  echo ERRO no deploy.
  echo Se o Storage ainda nao foi criado, abra o Firebase Console,
echo entre em Storage > Comecar e crie o bucket primeiro.
  pause
  exit /b 1
)

echo.
echo ================================================
echo  REGRAS PUBLICADAS COM SUCESSO
echo ================================================
echo.
echo IMPORTANTE: o bucket precisa existir no Firebase.
echo O projeto usa o bucket configurado no google-services.json.
echo.
pause
endlocal
