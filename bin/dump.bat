@echo off
setlocal
set BASE=%USERPROFILE%\git\util\target
set CP=%BASE%\classes;%BASE%\dependency\*
java -cp %CP% saka1029.util.main.Dump %*
endlocal
