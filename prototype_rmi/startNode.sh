#! /bin/sh
export REGISTRYHOST=127.0.1.1
export REGISTRYPORT=1099
export CLASSPATH=$CLASSPATH:/home/shiphter/Desktop/ITONK/prototype_rmi/build
java -classpath build -Djava.rmi.server.codebase=file:/home/shiphter/Desktop/ITONK/prototype_rmi/build/ dk.iha.itonk.rmi.Program $REGISTRYHOST $REGISTRYPORT
