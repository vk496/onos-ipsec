#!/bin/bash

install_apps() {
  while ! curl -s --user $ONOS_USER:$ONOS_PASSWORD -X POST -H "Content-Type: application/json" http://localhost:8181/onos/v1/network/configuration | grep -q '{.*"code":400.*}'; do
  	sleep 0.5
  done

  sleep 2

  for filename in /deploy/*.oar; do
    ./bin/onos-app localhost install! $filename
  done
}

install_apps &

./bin/onos-service server
