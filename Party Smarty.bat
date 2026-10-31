@echo off
setlocal EnableExtensions
cd /d "%~dp0"

where git >nul 2>&1
if errorlevel 1 (
	echo Git is required. Install it from https://git-scm.com/download/win
	pause
	exit /b 1
)

call :findjava "%JAVA_HOME%"
if not defined JAVA_HOME (
	echo Java 11 is required. Install Temurin JDK 11 from:
	echo https://adoptium.net/temurin/releases/?version=11
	pause
	exit /b 1
)
set "PATH=%JAVA_HOME%\bin;%PATH%"

if exist "%~dp0gradlew.bat" (
	git remote get-url origin 2>nul | findstr /I /C:"DangGucci/party-smarty" >nul
	if not errorlevel 1 (
		git pull
		if errorlevel 1 echo Could not update from GitHub. Starting the copy already on this PC.
	)
	call gradlew.bat run
) else (
	if not exist "%~dp0party-smarty\gradlew.bat" (
		git clone https://github.com/DangGucci/party-smarty.git "%~dp0party-smarty"
		if errorlevel 1 (
			echo Could not download Party Smarty.
			pause
			exit /b 1
		)
	) else (
		git -C "%~dp0party-smarty" pull
		if errorlevel 1 echo Could not update from GitHub. Starting the copy already on this PC.
	)
	pushd "%~dp0party-smarty"
	call gradlew.bat run
	popd
)

if errorlevel 1 (
	echo.
	echo Party Smarty failed to start.
	pause
)
exit /b %ERRORLEVEL%

:findjava
set "JAVA_HOME=%~1"
if defined JAVA_HOME (
	"%JAVA_HOME%\bin\java.exe" -version 2>&1 | findstr /C:"11.0" >nul
	if not errorlevel 1 exit /b 0
	set "JAVA_HOME="
)
for /d %%D in ("%ProgramFiles%\Eclipse Adoptium\jdk-11*") do set "JAVA_HOME=%%~D"
if defined JAVA_HOME exit /b 0
for /d %%D in ("%ProgramFiles%\Java\jdk-11*") do set "JAVA_HOME=%%~D"
if defined JAVA_HOME exit /b 0
for /d %%D in ("%ProgramFiles%\Microsoft\jdk-11*") do set "JAVA_HOME=%%~D"
if defined JAVA_HOME exit /b 0
for /d %%D in ("%LocalAppData%\Programs\Eclipse Adoptium\jdk-11*") do set "JAVA_HOME=%%~D"
exit /b 0