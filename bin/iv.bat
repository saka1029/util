@echo off
setlocal
set BASE=%USERPROFILE%\git\util
set CP=%BASE%\target\util-1.0.jar;%BASE%\lib\*
start javaw -cp %CP% saka1029.util.main.ImageViewer %*
endlocal
