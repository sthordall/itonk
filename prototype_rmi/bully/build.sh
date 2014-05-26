#! /bin/sh

RMI_JAVA="src/dk/iha/itonk/rmi"

rm -rf build
mkdir build

javac -d build $RMI_JAVA/Node.java $RMI_JAVA/NodeRemoteInterface.java $RMI_JAVA/Program.java
