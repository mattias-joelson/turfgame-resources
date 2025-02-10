@echo off

call mvn exec:java -Dexec.mainClass="org.joelson.turf.turfgame.apiv4.FeedsPrinter" -Dexec.args="%*"
