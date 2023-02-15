@echo on
call mvn clean package
:::@echo on
:::del /F/S/Q lib\*
:::@echo on
:::call mvn dependency:copy-dependencies -DoutputDirectory=lib
