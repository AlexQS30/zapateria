@echo off
SET "JAVA_HOME=C:\Program Files\Java\jdk-21.0.10"
SET "PATH=%JAVA_HOME%\bin;%PATH%"
cd /d "D:\Reibin\Trabajos\Iniciativas\zapateria\zapateria\zapateria-back\zapateria-back"
mvn clean test jacoco:report
