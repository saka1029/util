setlocal
set BASE=%USERPROFILE%\git\util
set T=%USERPROFILE%\git\history\drives
set CP=%BASE%\target\util-1.0.jar;%BASE%\lib\*
java -cp %CP% saka1029.util.main.Drives -t %T% %*
endlocal
