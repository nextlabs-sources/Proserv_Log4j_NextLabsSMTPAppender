#!/bin/sh
# ------------------------------------------------------------
# -- SAP JCo EDRM Install Script
# -- OS: Linux; PC: CE-Policy Controller
# ------------------------------------------------------------

# ---- Default values
IN_FOLDER=/opt/nextlabs/SAP/IN
OUT_FOLDER=/opt/nextlabs/SAP/OUT
SERVER_PREFIX=SERVRMI_
CLIENT_HOST=ED6.demo20.nextlabs.com
CLIENT_SYSNR=00
CLIENT_ID=100
CLIENT_USER=ndeveloper
CLIENT_PASSWD=Imagine1234
CLIENT_PASSWD_ENC=sa2583b7a0a979225ea145484cef1f497
GATEWAY_HOST=ED6.demo20.nextlabs.com
GATEWAY_SERV=sapgw00
GATEWAY_PRGID=NXL_EDRM_VER3
KEY_STORE_NAME=/opt/nextlabs/KeyManagement/rmskmc-keystore.jks
KEY_STORE_PASSWORD=sa1f78f49e437288039751654ece96ede
TRUST_STORE_NAME=/opt/nextlabs/KeyManagement/rmskmc-truststore.jks
TRUST_STORE_PASSWORD=sa1f78f49e437288039751654ece96ede
PC_HOST_NAME=localhost
RMI_PORT_NUM=1499

clear
echo --------- SAP JCo EDRM Deployment Manager -----------
echo    [0] Exit
echo    [1] Install
echo    [2] Uninstall
echo -----------------------------------------------------

echo Choose an option :
read MO

while [ $MO -ne 1 ] && [ $MO -ne 2 ] && [ $MO -ne 0 ] 
do
	echo Choose a valid option :
	read MO
done

install() {
	clear
		echo --------- SAP JCo EDRM Deployment Manager -----------
		echo Installation Menu
		echo    [0] Exit
		echo    [1] Proceed with INSTALLATION. Make sure all above values are correctly set.
		echo    [2] Set IN FOLDER [$IN_FOLDER]
		echo	[3] Set OUT FOLDER [$OUT_FOLDER]
		echo    [4] Set Client Host [$CLIENT_HOST]
		echo    [5] Set Client System No. [$CLIENT_SYSNR] 
		echo    [6] Set Client ID [$CLIENT_ID]
		echo    [7] Set Client User [$CLIENT_USER]
		echo    [8] Set Client Password [$CLIENT_PASSWD]
		echo    [9] Set Gateway Host [$GATEWAY_HOST]
		echo    [10] Set Gateway Service [$GATEWAY_SERV]
		echo    [11] Set Gateway Program ID [$GATEWAY_PRGID]
		echo    [12] Set Key Store Name [$KEY_STORE_NAME]
		echo    [13] Set Key Store Password [$KEY_STORE_PASSWORD]
		echo    [14] Set Trust Store Name [$TRUST_STORE_NAME]
		echo    [15] Set Trust Store Password [$TRUST_STORE_PASSWORD]
		echo    [16] Set RMI Port Number [$RMI_PORT_NUM]
		echo	[17] Set PC Host Name [$PC_HOST_NAME]
		echo ------------------------------------------------------		

		echo Choose an option:
		read CO
		while  [ "$CO" -ne 0 ] && [ "$CO" -ne 1 ] && [ "$CO" -ne 2 ] && [ "$CO" -ne 3 ] && [ "$CO" -ne 4 ] && [ "$CO" -ne 5 ] && [ "$CO" -ne 6 ] && [ "$CO" -ne 7 ] && [ "$CO" -ne 8 ] && [ "$CO" -ne 9 ] && [ "$CO" -ne 10 ] && [ "$CO" -ne 11 ] && [ "$CO" -ne 12 ] && [ "$CO" -ne 13 ] && [ "$CO" -ne 14 ] && [ "$CO" -ne 15 ] && [ "$CO" -ne 16 ] && [ "$CO" -ne 17 ]
		do
			echo Choose a valid option:
			read CO 
		done
		
		case "$CO" in
		
		0) echo Exiting
			exit
			;;
		1) echo Installing SAP JCo EDRM
			file="./conf/SAPJCo-EDRM.properties"
			
			if [ -f "$file" ];
			then
				echo "Loading properties file"
				while IFS="=" read -r key value || [ -n "$key" ]
				do
					case "$value" in
						"[IN_FOLDER]") newValue=$IN_FOLDER
									;;
						"[OUT_FOLDER]") newValue=$OUT_FOLDER
									;;
						"[CLIENT_HOST]") newValue=$CLIENT_HOST
									;;
						"[CLIENT_SYSNR]") newValue=$CLIENT_SYSNR
									;;
						"[CLIENT_ID]") newValue=$CLIENT_ID
									;;
						"[CLIENT_USER]") newValue=$CLIENT_USER
									;;
						"[CLIENT_PASSWD]") newValue=$CLIENT_PASSWD_ENC
									;;
						"[GATEWAY_HOST]") newValue=$GATEWAY_HOST
									;;
						"[GATEWAY_SERV]") newValue=$GATEWAY_SERV
									;;
						"[GATEWAY_PRGID]") newValue=$GATEWAY_PRGID
									;;
						"[KEY_STORE_NAME]") newValue=$KEY_STORE_NAME
									;;
						"[KEY_STORE_PASSWORD]") newValue=$KEY_STORE_PASSWORD
									;;
						"[TRUST_STORE_NAME]") newValue=$TRUST_STORE_NAME
									;;
						"[TRUST_STORE_PASSWORD]") newValue=$TRUST_STORE_PASSWORD
									;;
						"[RMI_PORT_NUM]") newValue=$RMI_PORT_NUM
									;;
						"[PC_HOST_NAME]") newValue=$PC_HOST_NAME
									;;
						*) newValue=$value
									;;
					esac
					
					if [ -n "$key" ]; then
						sed -i -E "s~($key=).*~\1$newValue~" $file
					fi
					
					echo $key=$newValue
					
				done < "$file"
			
				#Create necessary folders
				if [ ! -d "$IN_FOLDER" ]; then
					mkdir -p -- "$IN_FOLDER"
				fi
				if [ ! -d "$OUT_FOLDER" ]; then 
					mkdir -p -- "$OUT_FOLDER"
				fi

				#Install Service
				./installService.sh "start"
				
				CURRENT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
				echo "/bin/sh $CURRENT_DIR/installService.sh start" >> /etc/rc.d/rc.local
				chmod +x /etc/rc.d/rc.local
				echo "Finished setting up SAPJCo-EDRM to automatically start during boot time" 
				echo DONE.
			else
				echo "Cannot load properties file at $file"
			fi
			
			exit
			;;
			
			2) echo Enter IN FOLDER: 
			read INPUT
			IN_FOLDER="${INPUT:-$IN_FOLDER}"
			install
			;;
			
			3)
			echo Enter OUT FOLDER:
			read INPUT
			OUT_FOLDER="${INPUT:-$OUT_FOLDER}"
			install
			;;
			
			4) echo Enter Client Host:
			read INPUT
			CLIENT_HOST="${INPUT:-$CLIENT_HOST}"
			install
			;;
			
			5) echo Enter Client System No:
			read INPUT
			CLIENT_SYSNR="${INPUT:-$CLIENT_SYSNR}"
			install
			;;
			
			6) echo Enter Client ID:
			read INPUT
			CLIENT_ID="${INPUT:-$CLIENT_ID}"
			install
			;;
			
			7) echo Enter Client User:
			read INPUT
			CLIENT_USER="${INPUT:-$CLIENT_USER}"
			install
			;;
			
			8) echo "Enter Client Password":
			read INPUT
			CLIENT_PASSWD="${INPUT:-$CLIENT_PASSWD}"
			OUTPUT=$(java -classpath "./encryptor/crypt.jar:./encryptor/common-framework.jar" com.bluejungle.framework.crypt.Encryptor -password $INPUT)
			CLIENT_PASSWD_ENC="${OUTPUT:-$CLIENT_PASSWD_ENC}"
			install
			;;
			
			9) echo Enter Gateway Host:
			read INPUT
			GATEWAY_HOST="${INPUT:-$GATEWAY_HOST}"
			install
			;;
			
			10) echo Enter Gateway Service:
			read INPUT
			GATEWAY_SERV="${INPUT:-$GATEWAY_SERV}"
			install
			;;
			
			11) echo Enter Gateway Program ID:
			read INPUT
			GATEWAY_PRGID="${INPUT:-$GATEWAY_PRGID}"
			install
			;;
			
			12) echo Enter Key Store Name:
			read INPUT
					
			while [ ! -r $INPUT ] 
			do
				echo File does not exist. Please try again
				echo Enter Key Store Name:
				read INPUT
			done
			
			KEY_STORE_NAME="${INPUT:-$KEY_STORE_NAME}"
			install
			;;
			
			13) echo "Enter Key Store Password (encrypted)":
			read INPUT
			KEY_STORE_PASSWORD="${INPUT:-$KEY_STORE_PASSWORD}"
			install
			;;
			
			14) echo Enter Trust Store Name:
			read INPUT
			
			while [ ! -r $INPUT ] 
			do
				echo File does not exist. Please try again
				echo Enter Trust Store Name:
				read INPUT
			done
			
			TRUST_STORE_NAME="${INPUT:-$TRUST_STORE_NAME}"
			install
			;;
			
			15) echo "Enter Trust Store Password (Encrypted)":
			read INPUT
			TRUST_STORE_PASSWORD="${INPUT:-$TRUST_STORE_PASSWORD}"
			install
			;;
			
			16) echo Enter RMI Port Number:
			read INPUT
			RMI_PORT_NUM="${INPUT:-$RMI_PORT_NUM}"
			install
			;;
			
			17) echo Enter Policy Controller Hostname:
			read INPUT
			PC_HOST_NAME="${INPUT:-$PC_HOST_NAME}"
			install
			;;
			
		esac
}
case "$MO" in
	1) 	install
		;;
	2) clear
		echo --------- SAP JCo EDRM Deployment Manager -----------
		echo Uninstallation Menu
		echo    [9] Proceed with UNINSTALLATION.
		echo    [0] Exit
		echo -----------------------------------------------------
		
		echo Choose an option:
		read CO
		
		while [ "$CO" -ne 0 ] && [ "$CO" -ne 9 ]
		do
			echo Choose a valid option:
			read CO
		done
		
		case "$CO" in
			0) echo Exiting
				exit
				;;
			9) echo Uninstalling SAP JCo ...
				./installService.sh "stop"
				CURRENT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
				/bin/sed -i "/installService.sh/d" /etc/rc.d/rc.local
				echo "Finished removing service from startup"
				echo DONE.	
				;;
		esac
		exit
		;;
	0) echo Exiting
		exit
		;;
esac
}