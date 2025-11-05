@echo off
setlocal
set M=%~1
if "%M%"=="" set M=..
git add .
git commit -m "%M%"
git push
endlocal
