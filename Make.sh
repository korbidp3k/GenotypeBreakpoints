#!/bin/bash

javac -cp lib/htsjdk-1.122.jar:lib/sam-1.113.jar:build/classes/ -d build/classes/ src/*.java
