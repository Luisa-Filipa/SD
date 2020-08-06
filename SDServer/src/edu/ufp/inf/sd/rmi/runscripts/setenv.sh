#!/usr/bin/env bash
#@REM ************************************************************************************
#@REM Description: run previously all batch files
#@REM Author: Rui S. Moreira
#@REM Date: 20/02/2019
#@REM pwd: /Users/rui/Documents/NetBeansProjects/SD/src/edu/ufp/inf/sd/rmi/helloworld
#@REM http://docs.oracle.com/javase/tutorial/rmi/running.html
#@REM ************************************************************************************

#@REM ======================== Use Shell Parameters ========================
#@REM Script usage: setenv <role> (where role should be: server / client)
export SCRIPT_ROLE=$1

#@REM =====================================================================================================
#@REM ======================== CHANGE BELOW ACCORDING YOUR PROJECT and PC SETTINGS ========================
#@REM =====================================================================================================
# @REM ==== PC STUFF ====
export JDK=/System/Library/Java/JavaVirtualMachines/1.8.0_171.jdk/Contents/Home
#@REM These vars will be used to check the output folder (whereto classes are generated)
export NETBEANS=NetBeans
export INTELLIJ=IntelliJ
export CURRENT_IDE=${INTELLIJ}
#export CURRENT_IDE=¢{NETBEANS}
export USERNAME=faculdade

#@REM ==== JAVA NAMING STUFF ====
export JAVAPROJ_NAME=SDServer
export JAVAPROJ=/Users/${USERNAME}/Documents/SistemasDistribuidos/projeto/SDServer

export PACKAGE=rmi
export PACKAGE_PREFIX=edu.ufp.inf.sd
export PACKAGE_PREFIX_FOLDERS=edu/ufp/inf/sd
export SERVICE_NAME_ON_REGISTRY=ObserverService
export CLIENT_CLASS_PREFIX=Login
export SERVER_CLASS_PREFIX=Hash
export CLIENT_CLASS_POSTFIX=Gui
export SERVER_CLASS_POSTFIX=Server

#@REM ==== NETWORK STUFF ====
#@REM Must run http server on codebase host:
#@REM Python 2: python -m SimpleHTTPServer 8000
#@REM Python 3: python -m http.server 8000
export MYLOCALIP=localhost
#export MYLOCALIP=192.168.1.80
export REGISTRY_HOST=${MYLOCALIP}
export REGISTRY_PORT=1099
export SERVER_RMI_HOST=${REGISTRY_HOST}
export SERVER_RMI_PORT=1098
export SERVER_CODEBASE_HOST=${SERVER_RMI_HOST}
export SERVER_CODEBASE_PORT=8000
export CLIENT_RMI_HOST=${REGISTRY_HOST}
export CLIENT_RMI_PORT=1097
export CLIENT_CODEBASE_HOST=${CLIENT_RMI_HOST}
export CLIENT_CODEBASE_PORT=8000



#@REM =====================================================================================================
#@REM ======================== DO NOT CHANGE AFTER THIS POINT =============================================
#@REM =====================================================================================================
export JAVAPACKAGE=${PACKAGE_PREFIX}.${PACKAGE}
export JAVAPACKAGEROLE=${PACKAGE_PREFIX}.${PACKAGE}.${SCRIPT_ROLE}
export JAVAPACKAGEPATH=${PACKAGE_PREFIX_FOLDERS}/${PACKAGE}/${SCRIPT_ROLE}
export JAVASCRIPTSPATH=${PACKAGE_PREFIX_FOLDERS}/${PACKAGE}/runscripts
export JAVASECURITYPATH=${PACKAGE_PREFIX_FOLDERS}/${PACKAGE}/securitypolicies
export SERVICE_NAME=${SERVICE_PREFIX}Service
export SERVICE_URL=rmi://${REGISTRY_HOST}:${REGISTRY_PORT}/${SERVICE_NAME}

export SERVANT_ACTIVATABLE_IMPL_CLASS=${JAVAPACKAGEROLE}.${SERVER_CLASS_PREFIX}${SERVANT_ACTIVATABLE_IMPL_CLASS_POSTFIX}
export SERVANT_PERSISTENT_STATE_FILENAME=${SERVICE_PREFIX}Persistent.State

export PATH=${PATH}:${JDK}/bin

#@REM export JAVAPROJ_CLASSES=build/classes/
if [ "${CURRENT_IDE}" == "${NETBEANS}" ]; then
    export JAVAPROJ_CLASSES=build/classes/
    export JAVAPROJ_DIST=dist
    export JAVAPROJ_SRC=src
    export JAVAPROJ_DIST_LIB=lib
elif [ "${CURRENT_IDE}" == "${INTELLIJ}" ]; then
    export JAVAPROJ_CLASSES=out/production/${JAVAPROJ_NAME}/
    export JAVAPROJ_DIST=out/artifacts/${JAVAPROJ_NAME}
    export JAVAPROJ_SRC=src
    export JAVAPROJ_DIST_LIB=lib 
fi

export JAVAPROJ_CLASSES_FOLDER=${JAVAPROJ}/${JAVAPROJ_CLASSES}
export JAVAPROJ_DIST_FOLDER=${JAVAPROJ}/${JAVAPROJ_DIST}
export JAVAPROJ_DIST_LIB_FOLDER=${JAVAPROJ}/${JAVAPROJ_DIST_LIB}
export JAVAPROJ_JAR_FILE=${JAVAPROJ_NAME}.jar
export JAVA_LIB_FOLDER=${JAVAPROJ}/${JAVAPROJ_DIST_LIB}
export JAVA_RABBITMQ_TOOLS=${JAVA_LIB_FOLDER}/amqp-client-5.9.0.jar:${JAVA_LIB_FOLDER}/slf4j-api-1.7.30.jar:${JAVA_LIB_FOLDER}/slf4j-simple-1.7.30.jar:${JAVA_LIB_FOLDER}/jackson-annotations-2.9.0.jar:${JAVA_LIB_FOLDER}/jackson-core-2.9.6.jar:${JAVA_LIB_FOLDER}/jackson-databind-2.9.6.jar:${JAVA_LIB_FOLDER}/jjwt-0.9.1.jar

#export CLASSPATH=.:${JAVAPROJ_CLASSES_FOLDER}
export CLASSPATH=.:${JAVAPROJ_DIST_FOLDER}/${JAVAPROJ_JAR_FILE}:${JAVA_RABBITMQ_TOOLS}

export ABSPATH2CLASSES=${JAVAPROJ}/${JAVAPROJ_CLASSES}
export ABSPATH2SRC=${JAVAPROJ}/${JAVAPROJ_SRC}
export ABSPATH2DIST=${JAVAPROJ}/${JAVAPROJ_DIST}

#java.rmi.server.codebase property defines the location where the client/server provides its classes.
#export CODEBASE=file:///${JAVAPROJ}/${NETBEANS_CLASSES}
#export SERVER_CODEBASE=http://${SERVER_CODEBASE_HOST}:${SERVER_CODEBASE_PORT}/${JAVAPROJ_CLASSES}
#export CLIENT_CODEBASE=http://${CLIENT_CODEBASE_HOST}:${CLIENT_CODEBASE_PORT}/${JAVAPROJ_CLASSES}
#With several JARS: http://${SERVER_CODEBASE_HOST}:${SERVER_CODEBASE_PORT}/${MYSQL_CON_JAR}
export SERVER_CODEBASE=http://${SERVER_CODEBASE_HOST}:${SERVER_CODEBASE_PORT}/${JAVAPROJ_JAR_FILE}
export CLIENT_CODEBASE=http://${CLIENT_CODEBASE_HOST}:${CLIENT_CODEBASE_PORT}/${JAVAPROJ_JAR_FILE}

#Policy tool editor: /Library/Java/JavaVirtualMachines/jdk1.8.0_25.jdk/Contents/Home/bin/policytool
export SERVER_SECURITY_POLICY=file:///${JAVAPROJ}/${JAVAPROJ_SRC}/${JAVASECURITYPATH}/serverAllPermition.policy
export CLIENT_SECURITY_POLICY=file:///${JAVAPROJ}/${JAVAPROJ_SRC}/${JAVASECURITYPATH}/clientAllPermition.policy
export SETUP_SECURITY_POLICY=file:///${JAVAPROJ}/${JAVAPROJ_SRC}/${JAVASECURITYPATH}/setup.policy
export RMID_SECURITY_POLICY=file:///${JAVAPROJ}/${JAVAPROJ_SRC}/${JAVASECURITYPATH}/rmid.policy
export GROUP_SECURITY_POLICY=file:///${JAVAPROJ}/${JAVAPROJ_SRC}/${JAVASECURITYPATH}/group.policy
