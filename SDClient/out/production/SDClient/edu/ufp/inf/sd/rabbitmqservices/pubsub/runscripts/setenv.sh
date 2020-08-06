#!/usr/bin/env bash
#@REM ************************************************************************************
#@REM Description: run previously to all batch files
#@REM Author: Rui S. Moreira
#@REM Date: 10/04/2018
#@REM pwd: /Users/rui/Documents/NetBeansProjects/SD/src/edu/ufp/sd/rabbitmqservices
#@REM ************************************************************************************

#@REM ======================== Use Shell Parameters ========================
#@REM Script usage: setenv <role> (where role should be: server / client)
export SCRIPT_ROLE=$1

#@REM ======================== CHANGE BELOW ACCORDING YOUR PROJECT and PC SETTINGS ========================
#@REM ==== PC STUFF ====
export JDK=/System/Library/Java/JavaVirtualMachines/1.8.0_171.jdk/Contents/Home
export NETBEANS=NetBeans
export INTELLIJ=IntelliJ
export CURRENT_IDE=${INTELLIJ}
#export CURRENT_IDE=¢{NETBEANS}
export USERNAME=faculdade

#@REM ==== JAVA NAMING STUFF ====
export JAVAPROJ_NAME=SDClient
export JAVAPROJ=/Users/${USERNAME}/Documents/SistemasDistribuidos/projeto/SDClient
export RABBITMQ_SERVICES_FOLDER=edu/ufp/inf/sd/rabbitmqservices
export RABBITMQ_SERVICES_PACKAGE=edu.ufp.inf.sd.rabbitmqservices
export PACKAGE=pubsub
export SERVICE_NAME_ON_REGISTRY=ObserverService
export QUEUE_NAME_PREFIX=hello
export OBSERVER_CLASS_PREFIX=LoginGui
export PRODUCER_CLASS_PREFIX=EmitLog
export CONSUMER_CLASS_PREFIX=ReceiveLogs

#@REM ==== NETWORK STUFF ====
export BROKER_HOST=localhost
export BROKER_PORT=15672
export REGISTRY_HOST=localhost
export REGISTRY_PORT=1099
export SERVER_RMI_HOST=${REGISTRY_HOST}
export SERVER_RMI_PORT=1098

export SERVER_CODEBASE_HOST=${SERVER_RMI_HOST}
export SERVER_CODEBASE_PORT=8000
export CLIENT_RMI_HOST=${REGISTRY_HOST}
export CLIENT_RMI_PORT=1097
export CLIENT_CODEBASE_HOST=${CLIENT_RMI_HOST}
export CLIENT_CODEBASE_PORT=8000


#@REM ======================== DO NOT CHANGE AFTER THIS POINT ========================
export JAVAPACKAGE=${RABBITMQ_SERVICES_PACKAGE}.${PACKAGE}
export JAVAPACKAGEROLE=${JAVAPACKAGE}.${SCRIPT_ROLE}
export JAVAPACKAGEROLEPATH=${RABBITMQ_SERVICES_FOLDER}/${PACKAGE}/${SCRIPT_ROLE}
export JAVASCRIPTSPATH=${RABBITMQ_SERVICES_FOLDER}/${PACKAGE}/runscripts
export JAVASECURITYPATH=${RABBITMQ_SERVICES_FOLDER}/${PACKAGE}/securitypolicies
export BROKER_QUEUE=${QUEUE_NAME_PREFIX}_queue
#export SERVICE_URL=http://${BROKER_HOST}:${BROKER_PORT}
export SERVICE_URL=http://${BROKER_HOST}:${BROKER_PORT}


export PATH=${PATH}:${JDK}/bin

if [[ "${CURRENT_IDE}" == "${NETBEANS}" ]]; then
    export JAVAPROJ_SRC=src
    export JAVAPROJ_CLASSES=build/classes/
    export JAVAPROJ_DIST=dist
    export JAVAPROJ_DIST_LIB=lib
elif [[ "${CURRENT_IDE}" == "${INTELLIJ}" ]]; then
    export JAVAPROJ_SRC=src
    export JAVAPROJ_CLASSES=out/production/${JAVAPROJ_NAME}/
    export JAVAPROJ_DIST=out/artifacts/${JAVAPROJ_NAME}
    export JAVAPROJ_DIST_LIB=lib
fi

export JAVAPROJ_CLASSES_FOLDER=${JAVAPROJ}/${JAVAPROJ_CLASSES}
export JAVAPROJ_JAR_FILE=${JAVAPROJ}/${JAVAPROJ_DIST}/${JAVAPROJ_NAME}.jar
export JAVA_LIB_FOLDER=${JAVAPROJ}/${JAVAPROJ_DIST_LIB}
export JAVA_RABBITMQ_TOOLS=${JAVA_LIB_FOLDER}/amqp-client-5.9.0.jar:${JAVA_LIB_FOLDER}/slf4j-api-1.7.30.jar:${JAVA_LIB_FOLDER}/slf4j-simple-1.7.30.jar:${JAVA_LIB_FOLDER}/jackson-annotations-2.9.0.jar:${JAVA_LIB_FOLDER}/jackson-core-2.9.6.jar:${JAVA_LIB_FOLDER}/jackson-databind-2.9.6.jar:${JAVA_LIB_FOLDER}/jjwt-0.9.1.jar

export CLASSPATH=${JAVAPROJ_CLASSES_FOLDER}:${JAVA_RABBITMQ_TOOLS}
#export CLASSPATH=${JAVAPROJ_JAR_FILE}
#export CLASSPATH=${JAVAPROJ_JAR_FILE}:${JAVA_RABBITMQ_TOOLS}

export ABSPATH2CLASSES=${JAVAPROJ_CLASSES_FOLDER}
export ABSPATH2SRC=${JAVAPROJ}/${JAVAPROJ_SRC}

export SERVER_CODEBASE=http://${SERVER_CODEBASE_HOST}:${SERVER_CODEBASE_PORT}/${JAVAPROJ_JAR_FILE}
export CLIENT_CODEBASE=http://${CLIENT_CODEBASE_HOST}:${CLIENT_CODEBASE_PORT}/${JAVAPROJ_JAR_FILE}

export OBSERVER_SECURITY_POLICY=file:///${JAVAPROJ}/${JAVAPROJ_SRC}/${JAVASECURITYPATH}/clientAllPermition.policy
export SETUP_SECURITY_POLICY=file:///${JAVAPROJ}/${JAVAPROJ_SRC}/${JAVASECURITYPATH}/setup.policy
export RMID_SECURITY_POLICY=file:///${JAVAPROJ}/${JAVAPROJ_SRC}/${JAVASECURITYPATH}/rmid.policy
export GROUP_SECURITY_POLICY=file:///${JAVAPROJ}/${JAVAPROJ_SRC}/${JAVASECURITYPATH}/group.policy