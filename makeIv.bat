setlocal
set JAR=iv.jar
set CD=%CD%
set JARPATH=%CD%\%JAR
set DIR=target\classes
set FILES=saka1029/util/main/ImageViewer*
set MAIN=saka1029.util.main.ImageViewer
pushd %DIR%
jar cvfe %JARPATH% %MAIN% %FILES%
popd
exewrap -g %JAR%
endlocal
