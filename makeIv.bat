::: ImageViewerを実行するためのJarファイルを作成し、
::: exewrapを使用してExeファイルに変換します。
::: このバッチファイルはmvn package実行後に実行する必要があります。
setlocal
set JAR=iv.jar
set EXE=bin\IV.exe
set CD=%CD%
set JARPATH=%CD%\%JAR
set DIR=target\classes
set CLASSES=saka1029\util\main\ImageViewer*
set MAIN=saka1029.util.main.ImageViewer
pushd %DIR%
jar cvfe %JARPATH% %MAIN% %CLASSES%
popd
exewrap -g -o %EXE% %JAR%
endlocal
