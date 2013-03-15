#!/bin/bash
cd "$(dirname "$0")"
cd src
javac -cp ".;../lib/guava-11.0.2.jar;../lib/javassist-3.12.1.GA.jar;../lib/reflections-0.9.8.jar" com/cjwatts/fractalexplorer/main/FractalExplorer.java
cd ../bin
java -cp ".;../lib/guava-11.0.2.jar;../lib/javassist-3.12.1.GA.jar;../lib/reflections-0.9.8.jar" com/cjwatts/fractalexplorer/main/FractalExplorer