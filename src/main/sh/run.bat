@echo off

setLocal EnableDelayedExpansion
set CP=".
for /r ..\lib %%g in (*.jar) do (
  set CP=!CP!;%%g
)
set CP=!CP!"

set JRI_LD_PATH=../lib
set PPATH=%PATH%
set path=%PATH%;..\lib
java -cp %CP% -Dpython.path=. -Djava.library.path=../lib com.metaos.engine.Engine init.py %1 %2 %3 %4 %5 %6 %7 %8 %9
set PATH=%PPATH%
