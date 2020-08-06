#REM ************************************************************************************
#REM Description: run 
#REM Author: Rui S. Moreira
#REM Date: 10/04/2018
#REM ************************************************************************************
#REM Script usage: runclient <role> (where role should be: producer / consumer)
source ./setenv.sh chatgui

echo ${ABSPATH2CLASSES}
echo ${REGISTRY_HOST}
echo ${REGISTRY_PORT}
echo ${SERVICE_NAME_ON_REGISTRY}
echo ${BROKER_QUEUE}

cd ${ABSPATH2CLASSES}
#clear
#pwd 
java -cp ${CLASSPATH} \
      -Djava.security.policy=${OBSERVER_SECURITY_POLICY} \
      -Djava.rmi.server.codebase=${SERVER_CODEBASE} \
      -D${JAVAPACKAGEROLE}.codebase=${CLIENT_CODEBASE} \
      -D${JAVAPACKAGE}.servicename=${SERVICE_NAME_ON_REGISTRY} \
      ${JAVAPACKAGEROLEPATH}.${OBSERVER_CLASS_PREFIX} ${REGISTRY_HOST} ${REGISTRY_PORT} ${SERVICE_NAME_ON_REGISTRY} ${BROKER_QUEUE}
echo ${ABSPATH2SRC}/${JAVASCRIPTSPATH}
cd ${ABSPATH2SRC}/${JAVASCRIPTSPATH}
#pwd