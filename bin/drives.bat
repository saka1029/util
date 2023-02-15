setlocal
set BASE=%USERPROFILE%\git\util
set T=%USERPROFILE%\git\mine\drives
set CP=%BASE%\target\util-1.0-jar-with-dependencies.jar
java -cp %CP% saka1029.util.main.Drives -t %T% %*
endlocal
