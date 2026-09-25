@echo off
setlocal
set OPT=-Xss8192K --enable-native-access=ALL-UNNAMED
set BASE=%USERPROFILE%\git\util\target
set CP=%BASE%\classes;%BASE%\dependency\*
java %OPT% -cp %CP% saka1029.util.declisp.Main %*
endlocal
