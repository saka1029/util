set GIT=%USERPROFILE%\git

: call pullall
: git clone https://github.com/saka1029/csp.git %GIT%\csp
: git clone https://github.com/saka1029/declisp.git %GIT%\declisp
call mvn -f %GIT%\csp install
call mvn -f %GIT%\declisp install
call mvn -f %GIT%\util compile
