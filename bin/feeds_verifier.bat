@echo off

call mvn exec:java -Dexec.mainClass="org.joelson.turf.turfgame.util.FeedsVerifier" -Dexec.args="%*"
