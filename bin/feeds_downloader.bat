@echo off

SETLOCAL

set jar_file="c:\Users\eljol\.m2\repository\org\joelson\turf\resources\1.2-SNAPSHOT\resources-1.2-SNAPSHOT-jar-with-dependencies.jar"
set feed_v4_dir="c:\Users\eljol\src\turfgame_feedsv4"
set feed_v5_dir="c:\Users\eljol\src\turfgame_feedsv5"
set log_file="c:\Users\eljol\AppData\Local\Temp\feeds_startup_log.txt"

echo jar file:     %jar_file%
echo feed v4 dir:  %feed_v4_dir%
echo feed v5 dir:  %feed_v5_dir%
echo log file:     %log_file%

@echo on
:loop
echo "%date% %time% [feeds_startup] starting FeedsDownloader" >> %log_file%
call java -cp %jar_file% org.joelson.turf.turfgame.util.FeedsDownloader %feed_v4_dir% %feed_v5_dir%
echo "%date% %time% [feeds_startup]   errorlevel: %errorlevel%" >> %log_file%
goto loop

ENDLOCAL
