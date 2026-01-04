@echo off
setlocal

set "MSYS2_ROOT=C:\msys64"
set "REPO_DIR=C:\Users\int\GolandProjects\anixartgtk"
set "EXE_PATH=%REPO_DIR%\bin\anixartgtk.exe"

"%MSYS2_ROOT%\usr\bin\bash.exe" -lc "export MSYSTEM=MINGW64; export PATH=/mingw64/bin:/usr/bin; export GOROOT=/mingw64/lib/go; cd /c/Users/int/GolandProjects/anixartgtk && make windows"
if errorlevel 1 exit /b 1

if exist "%EXE_PATH%" (
  "%EXE_PATH%"
) else (
  echo Error: %EXE_PATH% not found.
  exit /b 1
)
