@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup script (Windows), version 3.3.2
@REM ----------------------------------------------------------------------------

@echo off
setlocal

set ERROR_CODE=0

@REM Resolve the script location
set MAVEN_WRAPPER_SCRIPT_DIR=%~dp0

@REM Check for Java and JAVA_HOME
if defined JAVA_HOME goto findJavaFromJavaHome

set "JAVACMD=java"
%JAVACMD% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto init

echo.
echo The JAVA_HOME environment variable is not defined correctly, so mvnw cannot run.
echo Either set the JAVA_HOME variable in your environment to match the
echo location of your Java installation, or add "java" to your PATH.
set ERROR_CODE=1
goto end

:findJavaFromJavaHome
set "JAVACMD=%JAVA_HOME%\bin\java.exe"
if exist "%JAVACMD%" goto init

echo.
echo The JAVA_HOME environment variable is not defined correctly, so mvnw cannot run.
echo JAVA_HOME is set to "%JAVA_HOME%", but "%JAVACMD%" does not exist.
set ERROR_CODE=1
goto end

:init
@REM Determine project base dir
set "MVNW_BASEDIR=%MAVEN_WRAPPER_SCRIPT_DIR%"
set "MVNW_VERBOSE=%MVNW_VERBOSE%"

if not "%MVNW_VERBOSE%"=="" (
  echo MVNW: Base dir: %MVNW_BASEDIR%
)

@REM Locate .mvn directory
set WRAPPER_JAR=.mvn\wrapper\maven-wrapper.jar
set WRAPPER_PROPERTIES=.mvn\wrapper\maven-wrapper.properties

if not exist "%WRAPPER_PROPERTIES%" (
  echo.
  echo ERROR: "%WRAPPER_PROPERTIES%" not found.
  echo Please make sure you run this script in the root directory of your project.
  set ERROR_CODE=1
  goto end
)

@REM Read distributionUrl from properties (fallback to default handled by Java helper)
for /f "usebackq tokens=1,* delims==" %%A in ("%WRAPPER_PROPERTIES%") do (
  if /I "%%A"=="distributionUrl" set "MVNW_DIST_URL=%%B"
)

@REM Ensure wrapper JAR is present, download if missing
if exist "%WRAPPER_JAR%" goto runmaven

if not "%MVNW_VERBOSE%"=="" echo MVNW: Wrapper jar not found, downloading...

@REM Try PowerShell/webclient, then curl, then bitsadmin as last resort
set DOWNLOAD_SUCCESS=

powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "try { $ProgressPreference='SilentlyContinue'; $u='%MVNW_DIST_URL%'.Replace('\','/'); $p='.mvn/wrapper/maven-wrapper.jar';" ^
  " $src='https://repo1.maven.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar';" ^
  " [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.SecurityProtocolType]::Tls12; " ^
  " (New-Object Net.WebClient).DownloadFile($src, $p); exit 0 } catch { exit 1 }" >NUL 2>&1

if %ERRORLEVEL% EQU 0 set DOWNLOAD_SUCCESS=1

if not defined DOWNLOAD_SUCCESS (
  where curl >NUL 2>&1
  if %ERRORLEVEL% EQU 0 (
    curl -fSL -o ".mvn/wrapper/maven-wrapper.jar" ^
      "https://repo1.maven.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"
    if %ERRORLEVEL% EQU 0 set DOWNLOAD_SUCCESS=1
  )
)

if not defined DOWNLOAD_SUCCESS (
  where bitsadmin >NUL 2>&1
  if %ERRORLEVEL% EQU 0 (
    bitsadmin /transfer mvnw /download /priority normal ^
      "https://repo1.maven.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar" ^
      "%CD%\.mvn\wrapper\maven-wrapper.jar" >NUL 2>&1
    if %ERRORLEVEL% EQU 0 set DOWNLOAD_SUCCESS=1
  )
)

if not defined DOWNLOAD_SUCCESS (
  echo.
  echo ERROR: Failed to download maven-wrapper-3.3.2.jar
  set ERROR_CODE=1
  goto end
)

:runmaven
if not "%MVNW_VERBOSE%"=="" echo MVNW: Running Maven via Wrapper
set MAVEN_JAVA_EXE="%JAVACMD%"

@REM Pass through JVM options and CLI args
set MAVEN_OPTS=%MAVEN_OPTS%

@REM Enable ANSI if supported (Win10+)
set "MVNW_ANSI=true"

%MAVEN_JAVA_EXE% ^
  %MAVEN_OPTS% ^
  -classpath "%WRAPPER_JAR%" ^
  -Dmaven.multiModuleProjectDirectory="%MVNW_BASEDIR%" ^
  -Dmaven.wrapper.multiModuleProjectDirectory="%MVNW_BASEDIR%" ^
  org.apache.maven.wrapper.MavenWrapperMain %*

set ERROR_CODE=%ERRORLEVEL%

:end
endlocal & exit /b %ERROR_CODE%
