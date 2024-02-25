setlocal
set BASE=%USERPROFILE%\git\util
set T=\\minipc\d\git\mine\drive
set CP=%BASE%\target\util-1.0-jar-with-dependencies.jar
java -cp %CP% saka1029.util.main.Drive %T% %*
endlocal
