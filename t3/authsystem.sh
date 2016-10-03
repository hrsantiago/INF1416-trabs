#!/bin/sh
if javac -classpath ".:src:src/core" src/AuthSystem.java ;
then
  java -classpath ".:src/sqlite-jdbc-3.8.11.2.jar:src" AuthSystem
fi
