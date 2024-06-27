@echo off

SETLOCAL

set scripts_dir="c:\Users\eljol\src\turfgame-resources\bin\"
set jar_file="c:\Users\eljol\.m2\repository\org\joelson\turf\resources\1.1.0\resources-1.1.0-jar-with-dependencies.jar"
set feed_drive=c:
set feed_dir="c:\Users\eljol\src\turfgame_feedsv5"
set log_file="c:\Users\eljol\AppData\Local\Temp\feeds_startup_log.txt"

echo scripts:   %scripts_dir%
echo jar file:  %jar_file%
echo drive:     %feed_drive%
echo feed dir:  %feed_dir%
echo log file:  %log_file%

@echo on
:loop
%feed_drive%
cd %feed_dir%
echo "%date% %time% [feedsv5_startup] starting feedsv5.bat" >> %log_file%
call %scripts_dir%feedsv5.bat %jar_file%
echo "%date% %time% [feedsv5_startup]   errorlevel: %errorlevel%" >> %log_file%
goto loop

ENDLOCAL
