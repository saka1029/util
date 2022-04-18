setlocal
set HOME=%USERPROFILE%\git\util
set T=%USERPROFILE%\git\history\backup\drives
set CP=%HOME%\target\util-1.0.jar;%HOME%\lib\*
java -cp %CP% util.main.Drives -t %T% %*
endlocal