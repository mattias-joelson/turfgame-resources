@echo off

SETLOCAL

set jar_file="c:\Users\mattias\.m2\repository\org\joelson\turf\resources\1.4.0\resources-1.4.0-jar-with-dependencies.jar"
set feeds_dir="c:\Users\mattias\src\turfgame_feeds"
set log_file="feeds_startup.log"

echo jar file:   %jar_file%
echo feeds dir:  %feeds_dir%
echo log file:   %log_file%

@echo on
:loop
cd %feeds_dir%
echo %date% %time% [feeds_startup] starting FeedsDownloader >> %feeds_dir%\%log_file%
call java -cp %jar_file% org.joelson.turf.turfgame.util.FeedsDownloader %feeds_dir%
echo %date% %time% [feeds_startup]   errorlevel: %errorlevel% >> %feeds_dir%\%log_file%
goto loop

ENDLOCAL
