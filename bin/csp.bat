@echo off
setlocal
set BASE=%USERPROFILE%\git\util
set CP=%BASE%\target\util-1.0.jar;%BASE%\lib\*
java -cp %CP% util.language.csp.Compiler %*
endlocal
