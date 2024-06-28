@echo off

call mvn exec:java -Dexec.mainClass="org.joelson.turf.turfgame.apiv5util.FeedsReader" -Dexec.args="%*"
