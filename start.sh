#!/usr/bin/env bash
if [  -d ./out/ ]; then
rm -rf out/
fi
mkdir out
javac  -cp "./src/:$(pwd)/libs/*:$(pwd)/libs/jssc.jar:$(pwd)/libs/v4l4j.jar:/lib/jvm/java-1.8.0-sun-1.8.0.91/jre/lib/rt.jar:$(pwd)/libs/opencv-2410.jar:$(pwd)/libs/" src/strategy/Strategy.java -d out
export LD_LIBRARY_PATH=$(pwd)/libs/
java  -cp "./out/:./src/:$(pwd)/libs/*:$(pwd)/libs/jssc.jar:$(pwd)/libs/v4l4j.jar:/lib/jvm/java-1.8.0-sun-1.8.0.91/jre/lib/rt.jar:$(pwd)/libs/opencv-2410.jar:$(pwd)/libs/" strategy.Strategy
