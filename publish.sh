#!/bin/bash

if [ $# -ne 1 ]; then
  echo "ERROR: Event number missing"
  exit -1
fi

for i in tmp/*ranking.png; do
  mkdir -p ~/Dropbox/Public/fb/rank/$1
  echo mv $i ~/Dropbox/Public/fb/rank/$1
  mv $i ~/Dropbox/Public/fb/rank/$1
done
