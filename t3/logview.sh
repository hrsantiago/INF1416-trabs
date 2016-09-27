#!/bin/sh
if javac src/LogView.java ;
then
  java -classpath ".:src/sqlite-jdbc-3.8.11.2.jar:src" LogView
fi
