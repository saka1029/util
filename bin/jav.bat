setlocal
set BASE=%USERPROFILE%\git\util
set CP=%BASE%\target\util-1.0.jar;%BASE%\lib\*
java -cp %CP% saka1029.util.main.Jav %*
endlocal
