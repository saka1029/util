@echo off
setlocal
set BASE=%USERPROFILE%\git\util\target
set CP=%BASE%\classes;%BASE%\dependency\*
set T=%BASE%\..\drive
java -cp %CP% saka1029.util.main.Drive -d %T% %*
endlocal
