@echo off
SET callingDir=%cd%
cd %~dp0
java -Xmx1g -Xms1g -Xmn768m -XX:+UseConcMarkSweepGC -XX:+HeapDumpOnOutOfMemoryError -classpath lib;lib/* com.infovista.statsRetriever.StatsPoller %*
cd %callingDir%