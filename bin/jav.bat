@echo off
setlocal
set BASE=%USERPROFILE%\git\util\target
set CP=%BASE%\util-1.0.jar;%BASE%\dependency\*
java -cp %CP% saka1029.util.main.Jav %*
endlocal
