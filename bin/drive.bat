setlocal
set BASE=%USERPROFILE%\git
set T=%BASE%\mine\drive
set CP=%BASE%\util\target\util-1.0-jar-with-dependencies.jar
java -cp %CP% saka1029.util.main.Drive %T% %*
endlocal
