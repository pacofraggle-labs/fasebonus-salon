#!/bin/bash

if [ $# -gt 0 ] && [ $1 == "--rebuild" ]; then
  opts="clean compile "
else
  opts=""
fi
mvn $opts exec:java -Dexec.mainClass=es.pacofraggle.fasebonus.salon.App
