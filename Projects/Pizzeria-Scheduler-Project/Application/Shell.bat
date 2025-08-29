@echo off 
REM Checking if a filename is provided
if "%~1"=="" (
    call :PrintHelp
    exit /b 1 
) else if "%~1"=="setup" (
    call :Setup
) else if "%~1"=="compile" (
    call :Compile
) else if "%~1"=="test" (
    call :UnitTest
) else if "%~1"=="one" (
    call :RunExample_One
) else if "%~1"=="two" (
    call :RunExample_Two
) else if "%~1"=="three" (
    call :RunExample_Three
) else if "%~1"=="four" (
    call :RunExample_Four 
) else if "%~1"=="five" (
    call :RunExample_Five
) else if "%~1"=="six" (
    call :RunExample_Six
) else if "%~1"=="seven" (
    call :RunExample_Seven
) else if "%~1"=="classRR" (
    call :RunExample_ClassRoundRobin
) else if "%~1"=="classFoc" (
    call :RunExample_ClassFocused
) else (
    echo Invalid command: %~1
    call :PrintHelp
    exit /b 1
)

exit /b 0

:: Setup Routine 
:Setup
echo Setting up development environment...
if not exist "lib" mkdir lib 
if not exist "bin" mkdir bin 
if not exist "lib\junit-4.13.2.jar" (
    echo Downloading JUnit...
    powershell -Command "Invoke-WebRequest -Uri 'https://search.maven.org/remotecontent?filepath=junit/junit/4.13.2/junit-4.13.2.jar' -OutFile 'lib\junit-4.13.2.jar'"
)
if not exist "lib\hamcrest-core-1.3.jar" (
    echo Downloading Hamcrest...
    powershell -Command "Invoke-WebRequest -Uri 'https://search.maven.org/remotecontent?filepath=org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar' -OutFile 'lib\hamcrest-core-1.3.jar'"
)
echo Setup complete!
exit /b 0

:: Compile Routine 
:Compile 
echo Compiling Java files...
javac -d bin src/lib/*.java src/App.java src/ThreadedApp.java src/components/*.java
if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed!
    exit /b 1
)
echo Compilation successful!
exit /b 0

:: Test Routine 
:UnitTest
echo Compiling Java Test files...
javac -d bin -cp "lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" src/*.java src/lib/*.java src/components/*.java src/test/*.java
if errorlevel 1 (
    echo Compilation failed!
    exit /b 1
)
echo Running tests... 
java -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" org.junit.runner.JUnitCore ^
    test.ChefTest ^
    test.DriverTest ^
    test.FileParserTest ^
    test.OrderTest ^
    test.OvenTest ^
    test.SchedulerFocusedTest ^
    test.SchedulerRoundRobinTest ^
    test.SchedulerTest
if errorlevel 1 (
    echo Tests failed!
    exit /b 1
)
echo All tests passed!
exit /b 0

:: Run Examples: BasicFO on Focused Scheduler 
:RunExample_One
call :Compile 
if errorlevel 1 exit /b 1
java -cp bin App --input-file BasicFocused.txt --available-ovens 2 --available-chefs 2 --available-drivers 2 --bake-time 6 --chef-time 4 --chef-strategy FOCUSED
exit /b 0

:: Run Examples: BasicRR on Focused Scheduler
:RunExample_Two
call :Compile 
if errorlevel 1 exit /b 1 
java -cp bin App --input-file BasicRoundRobin.txt --available-ovens 2 --available-chefs 2 --available-drivers 2 --bake-time 6 --chef-time 4 --chef-strategy FOCUSED
exit /b 0

:: Run Examples: THREADED 
:RunExample_Three 
call :Compile 
if errorlevel 1 exit /b 1
java -cp bin ThreadedApp --input-files BasicFocused.txt,BasicRoundRobin.txt --available-ovens 2 --available-chefs 2 --available-drivers 2 --bake-time 6 --chef-time 4 --chef-strategy FOCUSED
exit /b 0

:: Run Examples: BasicRR on RR Scheduler
:RunExample_Four
call :Compile 
if errorlevel 1 exit /b 1
java -cp bin App --input-file BasicRoundRobin.txt --available-ovens 2 --available-chefs 2 --available-drivers 2 --bake-time 6 --chef-time 4 --chef-strategy RR --chef-quantum 2
exit /b 0

:: Run Examples: BasicFO on RR Scheduler 
:RunExample_Five 
call :Compile 
if errorlevel 1 exit /b 1
java -cp bin App --input-file BasicFocused.txt --available-ovens 2 --available-chefs 2 --available-drivers 2 --bake-time 6 --chef-time 4 --chef-strategy RR --chef-quantum 2
exit /b 0

:: Run Examples: JenTest on RR Scheduler
:RunExample_Six
call :Compile 
if errorlevel 1 exit /b 1
java -cp bin App --input-file JenTest.txt --available-ovens 2 --available-chefs 3 --available-drivers 2 --bake-time 6 --chef-time 4 --chef-strategy RR --chef-quantum 2
exit /b 0

:: Run Examples: JenTest on Focused Scheduler
:RunExample_Seven
call :Compile 
if errorlevel 1 exit /b 1
java -cp bin App --input-file JenTest.txt --available-ovens 2 --available-chefs 3 --available-drivers 2 --bake-time 6 --chef-time 4 --chef-strategy FOCUSED
exit /b 0

:RunExample_ClassRoundRobin
call :Compile 
if errorlevel 1 exit /b 1
java -cp bin App --input-file ClassTest.txt --available-ovens 2 --available-chefs 2 --available-drivers 2 --bake-time 3 --chef-time 3 --chef-strategy RR --chef-quantum 1
exit /b 0

:RunExample_ClassFocused
call :Compile 
if errorlevel 1 exit /b 1
java -cp bin App --input-file ClassTest.txt --available-ovens 2 --available-chefs 2 --available-drivers 2 --bake-time 3 --chef-time 3 --chef-strategy FOCUSED
exit /b 0




:PrintHelp 
echo Pizza Scheduler Usage:
echo Shell.bat [command]
echo.
echo Commands:
echo    setup       Download required dependencies 
echo    compile     Compile the project
echo    test        Run unit tests
echo    one         Run simulation 1
echo    two         Run simulation 2 
echo    three       Run simulation 3
echo    four        Run simulation 4
echo    five        Run simulation 5
echo    size        Run simulation 6
echo    seven       Run simulation 7
echo    classRR     Run simulation class - RoundRobin
echo    classFoc    Run simulation class - Focused
echo. 
echo Examples:
echo    Shell.bat setup 
echo    Shell.bat test 
echo    Shell.bat compile 
echo    Shell.bat one
exit /b 0