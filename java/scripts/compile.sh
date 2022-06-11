#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

export JAVA_HOME=/usr/csshare/pkgs/jdk1.7.0_17
export PATH=$JAVA_HOME/bin:$PATH

# compile the java program
javac /extra/amuno034/needed_files/166-final/java/src/Cafe.java

#run the java program
#Use your database name, port number and login
java -cp /extra/amuno034/needed_files/166-final/java/lib/pg73jdbc3.jar Cafe $USER"_DB" $PGPORT $USER

