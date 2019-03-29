@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  Application startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and APPLICATION_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\Application.jar;%APP_HOME%\lib\JDA-3.8.1_450.jar;%APP_HOME%\lib\shadow-4.0.3.jar;%APP_HOME%\lib\log4j-core-2.11.1.jar;%APP_HOME%\lib\mysql-connector-java-8.0.13.jar;%APP_HOME%\lib\slf4j-simple-1.7.25.jar;%APP_HOME%\lib\spark-core_2.12-2.4.0.jar;%APP_HOME%\lib\spark-core-2.7.2.jar;%APP_HOME%\lib\simple-java-mail-5.1.3.jar;%APP_HOME%\lib\jda-utilities-examples-2.1.5.jar;%APP_HOME%\lib\jda-utilities-command-2.1.5.jar;%APP_HOME%\lib\avro-mapred-1.8.2-hadoop2.jar;%APP_HOME%\lib\jul-to-slf4j-1.7.16.jar;%APP_HOME%\lib\jcl-over-slf4j-1.7.16.jar;%APP_HOME%\lib\spark-network-shuffle_2.12-2.4.0.jar;%APP_HOME%\lib\spark-network-common_2.12-2.4.0.jar;%APP_HOME%\lib\metrics-jvm-3.1.5.jar;%APP_HOME%\lib\metrics-json-3.1.5.jar;%APP_HOME%\lib\metrics-graphite-3.1.5.jar;%APP_HOME%\lib\metrics-core-3.1.5.jar;%APP_HOME%\lib\avro-ipc-1.8.2.jar;%APP_HOME%\lib\hadoop-client-2.6.5.jar;%APP_HOME%\lib\hadoop-common-2.6.5.jar;%APP_HOME%\lib\hadoop-mapreduce-client-app-2.6.5.jar;%APP_HOME%\lib\hadoop-mapreduce-client-jobclient-2.6.5.jar;%APP_HOME%\lib\avro-1.8.2.jar;%APP_HOME%\lib\hadoop-auth-2.6.5.jar;%APP_HOME%\lib\curator-recipes-2.6.0.jar;%APP_HOME%\lib\curator-framework-2.6.0.jar;%APP_HOME%\lib\curator-client-2.6.0.jar;%APP_HOME%\lib\hadoop-mapreduce-client-shuffle-2.6.5.jar;%APP_HOME%\lib\hadoop-mapreduce-client-common-2.6.5.jar;%APP_HOME%\lib\hadoop-yarn-server-nodemanager-2.6.5.jar;%APP_HOME%\lib\hadoop-yarn-server-common-2.6.5.jar;%APP_HOME%\lib\zookeeper-3.4.6.jar;%APP_HOME%\lib\hadoop-mapreduce-client-core-2.6.5.jar;%APP_HOME%\lib\slf4j-log4j12-1.7.16.jar;%APP_HOME%\lib\apacheds-kerberos-codec-2.0.0-M15.jar;%APP_HOME%\lib\apacheds-i18n-2.0.0-M15.jar;%APP_HOME%\lib\api-asn1-api-1.0.0-M20.jar;%APP_HOME%\lib\api-util-1.0.0-M20.jar;%APP_HOME%\lib\hadoop-yarn-client-2.6.5.jar;%APP_HOME%\lib\hadoop-yarn-common-2.6.5.jar;%APP_HOME%\lib\slf4j-api-1.7.25.jar;%APP_HOME%\lib\commons-lang3-3.8.1.jar;%APP_HOME%\lib\stripe-java-7.17.0.jar;%APP_HOME%\lib\commons-dbcp2-2.5.0.jar;%APP_HOME%\lib\jda-utilities-menu-2.1.5.jar;%APP_HOME%\lib\spark-unsafe_2.12-2.4.0.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\annotations-16.0.1.jar;%APP_HOME%\lib\nv-websocket-client-2.5.jar;%APP_HOME%\lib\okhttp-3.12.0.jar;%APP_HOME%\lib\opus-java-1.0.4.pom;%APP_HOME%\lib\commons-collections4-4.1.jar;%APP_HOME%\lib\json-20160810.jar;%APP_HOME%\lib\trove4j-3.0.3.jar;%APP_HOME%\lib\jda-utilities-doc-2.1.5.jar;%APP_HOME%\lib\jda-utilities-commons-2.1.5.jar;%APP_HOME%\lib\groovy-backports-compat23-2.4.15.jar;%APP_HOME%\lib\log4j-api-2.11.1.jar;%APP_HOME%\lib\hadoop-hdfs-2.6.5.jar;%APP_HOME%\lib\hadoop-yarn-api-2.6.5.jar;%APP_HOME%\lib\protobuf-java-3.6.1.jar;%APP_HOME%\lib\gson-2.8.5.jar;%APP_HOME%\lib\commons-pool2-2.6.0.jar;%APP_HOME%\lib\commons-logging-1.2.jar;%APP_HOME%\lib\chill_2.12-0.9.3.jar;%APP_HOME%\lib\chill-java-0.9.3.jar;%APP_HOME%\lib\xbean-asm6-shaded-4.8.jar;%APP_HOME%\lib\spark-launcher_2.12-2.4.0.jar;%APP_HOME%\lib\spark-kvstore_2.12-2.4.0.jar;%APP_HOME%\lib\emailaddress-rfc2822-1.1.2.jar;%APP_HOME%\lib\javax.mail-1.6.0.jar;%APP_HOME%\lib\jaxb-api-2.2.2.jar;%APP_HOME%\lib\activation-1.1.1.jar;%APP_HOME%\lib\jetty-webapp-9.4.8.v20171121.jar;%APP_HOME%\lib\websocket-server-9.4.8.v20171121.jar;%APP_HOME%\lib\jetty-servlet-9.4.8.v20171121.jar;%APP_HOME%\lib\jetty-security-9.4.8.v20171121.jar;%APP_HOME%\lib\jetty-server-9.4.8.v20171121.jar;%APP_HOME%\lib\websocket-servlet-9.4.8.v20171121.jar;%APP_HOME%\lib\javax.servlet-api-3.1.0.jar;%APP_HOME%\lib\commons-math3-3.4.1.jar;%APP_HOME%\lib\log4j-1.2.17.jar;%APP_HOME%\lib\compress-lzf-1.0.3.jar;%APP_HOME%\lib\snappy-java-1.1.7.1.jar;%APP_HOME%\lib\lz4-java-1.4.0.jar;%APP_HOME%\lib\zstd-jni-1.3.2-2.jar;%APP_HOME%\lib\RoaringBitmap-0.5.11.jar;%APP_HOME%\lib\commons-net-3.1.jar;%APP_HOME%\lib\json4s-jackson_2.12-3.5.3.jar;%APP_HOME%\lib\jackson-module-scala_2.12-2.6.7.1.jar;%APP_HOME%\lib\spark-tags_2.12-2.4.0.jar;%APP_HOME%\lib\json4s-core_2.12-3.5.3.jar;%APP_HOME%\lib\scala-reflect-2.12.1.jar;%APP_HOME%\lib\json4s-ast_2.12-3.5.3.jar;%APP_HOME%\lib\json4s-scalap_2.12-3.5.3.jar;%APP_HOME%\lib\scala-xml_2.12-1.0.6.jar;%APP_HOME%\lib\scala-library-2.12.7.jar;%APP_HOME%\lib\jersey-container-servlet-2.22.2.jar;%APP_HOME%\lib\jersey-container-servlet-core-2.22.2.jar;%APP_HOME%\lib\jersey-server-2.22.2.jar;%APP_HOME%\lib\jersey-client-2.22.2.jar;%APP_HOME%\lib\jersey-media-jaxb-2.22.2.jar;%APP_HOME%\lib\jersey-common-2.22.2.jar;%APP_HOME%\lib\netty-all-4.1.17.Final.jar;%APP_HOME%\lib\netty-3.9.9.Final.jar;%APP_HOME%\lib\stream-2.7.0.jar;%APP_HOME%\lib\jackson-databind-2.6.7.1.jar;%APP_HOME%\lib\ivy-2.4.0.jar;%APP_HOME%\lib\oro-2.0.8.jar;%APP_HOME%\lib\pyrolite-4.13.jar;%APP_HOME%\lib\py4j-0.10.7.jar;%APP_HOME%\lib\commons-crypto-1.0.0.jar;%APP_HOME%\lib\unused-1.0.0.jar;%APP_HOME%\lib\okio-1.15.0.jar;%APP_HOME%\lib\opus-java-api-1.0.4.jar;%APP_HOME%\lib\opus-java-natives-1.0.4.jar;%APP_HOME%\lib\jackson-mapper-asl-1.9.13.jar;%APP_HOME%\lib\jackson-xc-1.9.13.jar;%APP_HOME%\lib\jackson-jaxrs-1.9.13.jar;%APP_HOME%\lib\jackson-core-asl-1.9.13.jar;%APP_HOME%\lib\jackson-module-paranamer-2.7.9.jar;%APP_HOME%\lib\paranamer-2.8.jar;%APP_HOME%\lib\commons-compress-1.8.1.jar;%APP_HOME%\lib\xz-1.5.jar;%APP_HOME%\lib\commons-httpclient-3.1.jar;%APP_HOME%\lib\httpclient-4.2.5.jar;%APP_HOME%\lib\commons-codec-1.9.jar;%APP_HOME%\lib\kryo-shaded-4.0.2.jar;%APP_HOME%\lib\hadoop-annotations-2.6.5.jar;%APP_HOME%\lib\leveldbjni-all-1.8.jar;%APP_HOME%\lib\jackson-core-2.6.7.jar;%APP_HOME%\lib\jackson-annotations-2.6.7.jar;%APP_HOME%\lib\htrace-core-3.0.4.jar;%APP_HOME%\lib\guava-16.0.1.jar;%APP_HOME%\lib\javax.ws.rs-api-2.0.1.jar;%APP_HOME%\lib\hk2-locator-2.4.0-b34.jar;%APP_HOME%\lib\hk2-api-2.4.0-b34.jar;%APP_HOME%\lib\javax.inject-2.4.0-b34.jar;%APP_HOME%\lib\javax.annotation-api-1.2.jar;%APP_HOME%\lib\jersey-guava-2.22.2.jar;%APP_HOME%\lib\osgi-resource-locator-1.0.1.jar;%APP_HOME%\lib\validation-api-1.1.0.Final.jar;%APP_HOME%\lib\websocket-client-9.4.8.v20171121.jar;%APP_HOME%\lib\jetty-client-9.4.8.v20171121.jar;%APP_HOME%\lib\jetty-http-9.4.8.v20171121.jar;%APP_HOME%\lib\websocket-common-9.4.8.v20171121.jar;%APP_HOME%\lib\jetty-io-9.4.8.v20171121.jar;%APP_HOME%\lib\jetty-xml-9.4.8.v20171121.jar;%APP_HOME%\lib\websocket-api-9.4.8.v20171121.jar;%APP_HOME%\lib\jna-4.4.0.jar;%APP_HOME%\lib\minlog-1.3.0.jar;%APP_HOME%\lib\objenesis-2.5.1.jar;%APP_HOME%\lib\commons-cli-1.2.jar;%APP_HOME%\lib\xmlenc-0.52.jar;%APP_HOME%\lib\commons-io-2.4.jar;%APP_HOME%\lib\commons-configuration-1.6.jar;%APP_HOME%\lib\commons-collections-3.2.2.jar;%APP_HOME%\lib\commons-lang-2.6.jar;%APP_HOME%\lib\jetty-util-6.1.26.jar;%APP_HOME%\lib\xercesImpl-2.9.1.jar;%APP_HOME%\lib\hk2-utils-2.4.0-b34.jar;%APP_HOME%\lib\aopalliance-repackaged-2.4.0-b34.jar;%APP_HOME%\lib\javassist-3.18.1-GA.jar;%APP_HOME%\lib\jetty-util-9.4.8.v20171121.jar;%APP_HOME%\lib\commons-digester-1.8.jar;%APP_HOME%\lib\commons-beanutils-core-1.8.0.jar;%APP_HOME%\lib\jline-0.9.94.jar;%APP_HOME%\lib\guice-3.0.jar;%APP_HOME%\lib\javax.inject-1.jar;%APP_HOME%\lib\commons-beanutils-1.7.0.jar;%APP_HOME%\lib\httpcore-4.2.4.jar;%APP_HOME%\lib\junit-3.8.1.jar;%APP_HOME%\lib\jettison-1.1.jar;%APP_HOME%\lib\stax-api-1.0-2.jar;%APP_HOME%\lib\aopalliance-1.0.jar;%APP_HOME%\lib\cglib-2.2.1-v20090111.jar

@rem Execute Application
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %APPLICATION_OPTS%  -classpath "%CLASSPATH%" us.verif.bot.Bot %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable APPLICATION_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%APPLICATION_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
