#!/bin/sh
find . -name "*.class" -type f -delete

if javac -classpath ".:src:src/core:src/view" src/AuthSystem.java ;
then
  java -classpath ".:src/sqlite-jdbc-3.8.11.2.jar:src" AuthSystem
fi
