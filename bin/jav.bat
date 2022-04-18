setlocal
set HOME=%USERPROFILE%\git\util
set CP=%HOME%\target\util-1.0.jar;%HOME%\lib\*
java -cp %CP% util.main.Jav %*
endlocal