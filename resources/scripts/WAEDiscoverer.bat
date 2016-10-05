@echo off
SET callingDir=%cd%
cd %~dp0
java -classpath lib;lib/* com.infovista.client.WAEDiscoverer %*
cd %callingDir%