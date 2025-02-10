@echo off

call mvn exec:java -Dexec.mainClass="org.joelson.turf.turfgame.apiv5.FeedsPrinter" -Dexec.args="%*"
