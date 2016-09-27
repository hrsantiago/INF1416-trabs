#!/bin/sh
javac src/LogView.java
java -classpath ".:src/sqlite-jdbc-3.8.11.2.jar:src" LogView
