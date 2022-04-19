call mvn clean package
del /F/S/Q lib\*
call mvn dependency:copy-dependencies -DoutputDirectory=lib
