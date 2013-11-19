@echo off

rmdir classes /S /Q
mkdir classes
 
if DEFINED DLL_PATH_SET GOTO build
PATH=%PATH%;./lib
set DLL_PATH_SET=1

:build
javac -cp ./lib/smile.jar; -d ./classes -sourcepath ./src/*.java

java -cp ./classes;./lib/smile.jar; WordPrediction

echo DONE
