@echo off
REM ############################################################
REM ## SAP JCo EDRM Install Script
REM ## OS: Windows; PC: CE-Policy Controller
REM ############################################################
SETLOCAL ENABLEDELAYEDEXPANSION

REM #### Identify 64/32bit ###
IF "%~1"=="" (SET Computer=%ComputerName%) ELSE (SET Computer=%~1)
IF /I NOT "%Computer%"=="%ComputerName%" (
	PING %Computer% -n 2 2>NUL | FIND "TTL=" >NUL
)
FOR /F "tokens=2 delims==" %%A IN ('WMIC /Node:"%Computer%" Path Win32_Processor Get AddressWidth /Format:list') DO SET OSB=%%A

REM #### Default values
SET IN_FOLDER=C:/NextLabs/SAP/IN
SET OUT_FOLDER=C:/NextLabs/SAP/OUT
SET SERVER_PREFIX=SERVRMI_
SET CLIENT_HOST=ED6.demo20.nextlabs.com
SET CLIENT_SYSNR=00
SET CLIENT_ID=100
SET CLIENT_USER=ndeveloper
SET CLIENT_PASSWD=Imagine1234
SET CLIENT_PASSWD_ENC=sa2583b7a0a979225ea145484cef1f497
SET GATEWAY_HOST=ED6.demo20.nextlabs.com
SET GATEWAY_SERV=sapgw00
SET GATEWAY_PRGID=NXL_EDRM_VER2
SET KEY_STORE_NAME=C:/NextLabs/KeyManagement/rmskmc-keystore.jks
SET KEY_STORE_PASSWORD=sa1f78f49e437288039751654ece96ede
SET TRUST_STORE_NAME=C:/NextLabs/KeyManagement/rmskmc-truststore.jks
SET TRUST_STORE_PASSWORD=sa1f78f49e437288039751654ece96ede
SET PC_HOST_NAME=localhost
SET RMI_PORT_NUM=1499

:ShowMainOptions
cls
echo ######### SAP JCo EDRM Deployment Manager ###########
echo.
echo    [0] Exit
echo    [1] Install
echo    [2] Uninstall
echo.
GOTO ChooseMainOption

:ChooseMainOption
set CO=0
set /P CO=Choose an option [default is !CO!] :
IF %CO% NEQ 1 IF %CO% NEQ 2 IF %CO% NEQ 0 GOTO ShowMainOptions
set MO=MainOption!CO!
GOTO %MO%

:MainOption1
cls
echo ######### SAP JCo EDRM Deployment Manager ###########
echo.
echo Installation Menu
echo.
echo    [0] Back
echo    [1] Proceed with INSTALLATION. Make sure all above values are correctly set.
echo    [2] Set IN FOLDER [!IN_FOLDER!]
echo	[3] Set OUT FOLDER [!OUT_FOLDER!]
echo    [4] Set Client Host [!CLIENT_HOST!]
echo    [5] Set Client System No. [!CLIENT_SYSNR!] 
echo    [6] Set Client ID [!CLIENT_ID!]
echo    [7] Set Client User [!CLIENT_USER!]
echo    [8] Set Client Password [!CLIENT_PASSWD!]
echo    [9] Set Gateway Host [!GATEWAY_HOST!]
echo    [10] Set Gateway Service [!GATEWAY_SERV!]
echo    [11] Set Gateway Program ID [!GATEWAY_PRGID!]
echo    [12] Set Key Store Name [!KEY_STORE_NAME!]
echo    [13] Set Key Store Password [!KEY_STORE_PASSWORD!]
echo    [14] Set Trust Store Name [!TRUST_STORE_NAME!]
echo    [15] Set Trust Store Password [!TRUST_STORE_PASSWORD!]
echo    [16] Set RMI Port Number [!RMI_PORT_NUM!]
echo	[17] Set PC Host Name [!PC_HOST_NAME!]
echo.
GOTO ChooseInsOption

:MainOption2
cls
echo ######### SAP JCo EDRM Deployment Manager ###########
echo.
echo Uninstallation Menu
echo.
echo    [9] Proceed with UNINSTALLATION.
echo    [0] Back
echo.
GOTO ChooseUninsOption

:MainOption0
GOTO FinalExit

:FinalExit
set /p DUMMY=Hit ENTER to complete.
EXIT /B

:ChooseInsOption
set CO=0
set /P CO=Choose an option [default is !CO!] :
IF  %CO% NEQ 0 IF %CO% NEQ 1 IF %CO% NEQ 2 IF %CO% NEQ 3 IF %CO% NEQ 4 IF %CO% NEQ 5 IF %CO% NEQ 6 IF %CO% NEQ 7 IF %CO% NEQ 8 IF %CO% NEQ 9 IF %CO% NEQ 10 IF %CO% NEQ 11 IF %CO% NEQ 12 IF %CO% NEQ 13 IF %CO% NEQ 14 IF %CO% NEQ 15 IF %CO% NEQ 16 IF %CO% NEQ 17 GOTO MainOption8
set IO=InsOption!CO!
GOTO %IO%

:InsOption0
GOTO ShowMainOptions

:InsOption2
set /P IN_FOLDER=Enter IN FOLDER [default is !IN_FOLDER!] :
GOTO MainOption1

:InsOption3
set /P OUT_FOLDER=Enter OUT FOLDER [default is !OUT_FOLDER!] :
GOTO MainOption1

:InsOption4
set /P CLIENT_HOST=Enter Client Host [default is !CLIENT_HOST!] :
GOTO MainOption1

:InsOption5
set /P CLIENT_SYSNR=Enter Client System No. [default is !CLIENT_SYSNR!] :
GOTO MainOption1

:InsOption6
set /P CLIENT_ID=Enter Client ID [default is !CLIENT_ID!] :
GOTO MainOption1

:InsOption7
set /P CLIENT_USER=Enter Client User [default is !CLIENT_USER!] :
GOTO MainOption1

:InsOption8
set /P CLIENT_PASSWD=Enter Client Password[default is !CLIENT_PASSWD!] :
for /f "usebackq delims=" %%a in (`java.exe -classpath "./encryptor/crypt.jar;./encryptor/common-framework.jar" com.bluejungle.framework.crypt.Encryptor -password !CLIENT_PASSWD!`) DO SET CLIENT_PASSWD_ENC=%%a
echo Encrypted Password is %CLIENT_PASSWD_ENC%
GOTO MainOption1

:InsOption9
set /P GATEWAY_HOST=Enter Gateway Host [default is !GATEWAY_HOST!] :
GOTO MainOption1

:InsOption10
set /P GATEWAY_SERV=Enter Gateway Service [default is !GATEWAY_SERV!] :
GOTO MainOption1

:InsOption11
set /P GATEWAY_PRGID=Enter Gateway Program ID [default is !GATEWAY_PRGID!] :
GOTO MainOption1

:InsOption12
set /P KEY_STORE_NAME=Enter Key Store Name [default is !KEY_STORE_NAME!] :
if not exist %KEY_STORE_NAME% (
	echo File does not exist. Please try again.
	goto InsOption12
)
GOTO MainOption1

:InsOption13
set /P KEY_STORE_PASSWORD=Enter Key Store Password [default is !KEY_STORE_PASSWORD!] :
GOTO MainOption1

:InsOption14
set /P TRUST_STORE_NAME=Enter Trust Store Name [default is !TRUST_STORE_NAME!] :
if not exist %TRUST_STORE_NAME% (
	echo File does not exist. Please try again.
	goto InsOption12
)
GOTO MainOption1

:InsOption15
set /P TRUST_STORE_PASSWORD=Enter Trust Store Password [default is !TRUST_STORE_PASSWORD!] :
GOTO MainOption1

:InsOption16
set /P RMI_PORT_NUM=Enter RMI Port Number [default is !RMI_PORT_NUM!] :
GOTO MainOption1

:InsOption17
set /P PC_HOST_NAME=Enter Policy Controller Host Name [default is !PC_HOST_NAME!] :
GOTO MainOption1

:InsOption1
echo Installing SAP JCo EDRM ...
echo #Auto Generated>.\conf\temp.properties
FOR /F "tokens=* delims=" %%x in (.\conf\SAPJCo-EDRM.properties) DO (
	set FILE_CONTENT=%%x
	set FILE_CONTENT=!FILE_CONTENT:[IN_FOLDER]=%IN_FOLDER%!
	set FILE_CONTENT=!FILE_CONTENT:[OUT_FOLDER]=%OUT_FOLDER%!
	set FILE_CONTENT=!FILE_CONTENT:[CLIENT_HOST]=%CLIENT_HOST%!
	set FILE_CONTENT=!FILE_CONTENT:[CLIENT_SYSNR]=%CLIENT_SYSNR%!
	set FILE_CONTENT=!FILE_CONTENT:[CLIENT_ID]=%CLIENT_ID%!
	set FILE_CONTENT=!FILE_CONTENT:[CLIENT_USER]=%CLIENT_USER%!
	set FILE_CONTENT=!FILE_CONTENT:[CLIENT_PASSWD]=%CLIENT_PASSWD_ENC%!
	set FILE_CONTENT=!FILE_CONTENT:[GATEWAY_HOST]=%GATEWAY_HOST%!
	set FILE_CONTENT=!FILE_CONTENT:[GATEWAY_SERV]=%GATEWAY_SERV%!
	set FILE_CONTENT=!FILE_CONTENT:[GATEWAY_PRGID]=%GATEWAY_PRGID%!
	set FILE_CONTENT=!FILE_CONTENT:[KEY_STORE_NAME]=%KEY_STORE_NAME%!
	set FILE_CONTENT=!FILE_CONTENT:[KEY_STORE_PASSWORD]=%KEY_STORE_PASSWORD%!
	set FILE_CONTENT=!FILE_CONTENT:[TRUST_STORE_NAME]=%TRUST_STORE_NAME%!
	set FILE_CONTENT=!FILE_CONTENT:[TRUST_STORE_PASSWORD]=%TRUST_STORE_PASSWORD%!
	set FILE_CONTENT=!FILE_CONTENT:[RMI_PORT_NUM]=%RMI_PORT_NUM%!
	set FILE_CONTENT=!FILE_CONTENT:[PC_HOST_NAME]=%PC_HOST_NAME%!
	echo !FILE_CONTENT!>>.\conf\temp.properties
)
REM #### Create necessary folders ###
IF NOT EXIST "%IN_FOLDER%" MKDIR "%IN_FOLDER%"
IF NOT EXIST "%OUT_FOLDER%" MKDIR "%OUT_FOLDER%"

REM #### Copy properties ###
COPY .\conf\temp.properties .\conf\SAPJCo-EDRM.properties >NUL
DEL  .\conf\temp.properties >NUL

REM #### Install Service ###
CALL installService.bat
echo DONE.
GOTO FinalExit

:ChooseUninsOption
set CO=0
set /P CO=Choose an option [default is !CO!] :
IF  %CO% NEQ 0 IF %CO% NEQ 9 GOTO MainOption9
set UO=UninsOption!CO!
GOTO %UO%

:UninsOption0
GOTO ShowMainOptions

:UninsOption9
echo Uninstalling SAP JCo ...
REM #### Delete files ###
CALL uninstallService.bat
echo DONE.
GOTO FinalExit