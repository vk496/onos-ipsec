#!/bin/bash

install_apps() {
  for filename in /deploy/*.oar; do
    until ./bin/onos-app localhost install! $filename; do
      echo "ONOS not ready yet. Trying again to install $filename"
    done
  done
}

install_apps &

./bin/onos-service server debug
