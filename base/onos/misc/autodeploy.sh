#!/bin/bash

install_apps() {
  set -x
  for filename in /deploy/*.oar; do
    until ./bin/onos-app localhost install! $filename; do
      echo "ONOS not ready yet. Trying again to install $filename"
    done
  done

iface=$(ip route get $SDN_NET | awk '{print $4}')
b_IP=$(ip a s dev $iface | awk '/inet / {print $4}')

  #Wake up all waiting netopeer nodes. Magic packet
  echo "onos-ipsec" | socat - udp-datagram:${b_IP}:3000,broadcast #Break pipe
  #Trick. Twice for continue flow execution
  echo "onos-ipsec" | socat - udp-datagram:${b_IP}:3000,broadcast
  set +x
}

install_apps &

./bin/onos-service server debug
