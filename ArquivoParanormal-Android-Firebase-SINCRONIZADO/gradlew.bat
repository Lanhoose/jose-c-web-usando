@echo off
setlocal
set "GRADLE_VERSION=8.9"
set "DIST=%USERPROFILE%\.gradle\wrapper\dists\ge-tech-gradle-%GRADLE_VERSION%\gradle-%GRADLE_VERSION%"
if not exist "%DIST%\bin\gradle.bat" (
  echo Gradle bootstrap is intended for Codemagic/Linux. Please run on a machine with Gradle or use Codemagic.
  exit /b 1
)
call "%DIST%\bin\gradle.bat" %*
