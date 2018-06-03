#!/bin/bash
set -x
# sleep $ONOS_SUBMIT_WAIT

NETCONF_USER=${NETCONF_USER-"root"}
NETCONF_PASSWORD=${NETCONF_PASSWORD-"root"}

while ! curl -s --user $ONOS_USER:$ONOS_PASSWORD -X POST -H "Content-Type: application/json" http://${ONOS_IP}:8181/onos/v1/network/configuration | grep -q '{.*"code":400.*}'; do
	echo "Waiting..."
	sleep 0.5
done

sleep 6

cfg=$(cat <<EOF
{
  "devices": {
    "netconf:$(hostname --ip-address):830": {
      "netconf": {
        "ip": "$(hostname --ip-address)",
        "port": 830,
        "username": "$NETCONF_USER",
        "password": "$NETCONF_PASSWORD"
      },
      "basic": {
        "driver": "ovs-netconf"
      }
    }
  }
}
EOF
)

echo $cfg | curl -X POST -H "content-type:application/json" http://${ONOS_IP}:8181/onos/v1/network/configuration -d @- --user $ONOS_USER:$ONOS_PASSWORD
