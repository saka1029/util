@echo off
setlocal
set BASE=%USERPROFILE%\git\util
set CP=%BASE%\target\util-1.0-jar-with-dependencies.jar
java -cp %CP% saka1029.util.dentaku.Dentaku %*
endlocal
