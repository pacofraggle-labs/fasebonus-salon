#!/bin/bash

mvn clean compile exec:java -Dexec.mainClass=es.pacofraggle.fasebonus.salon.App

if [ $# -gt 0 ] && [ -d $HOME/Dropbox/Public/fb/rank/$1 ]; then
  mv tmp/*ranking.png $HOME/Dropbox/Public/fb/rank/$1
fi
