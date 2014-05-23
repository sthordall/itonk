#! /bin/sh
export

REGISTRYHOST=localhost
REGISTRYPORT=1099 CLASSPATH=$CLASSPATH:/Users/shiphter/repositories/github/ITONK/prototype_rmi/build
java -classpath build -Djava.rmi.server.codebase=file:build/ dk.iha.itonk.rmi.Program $REGISTRYHOST $REGISTRYPORT
