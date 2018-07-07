#!/bin/bash
set -x
# sleep $ONOS_SUBMIT_WAIT

NETCONF_USER=${NETCONF_USER-"root"}
NETCONF_PASSWORD=${NETCONF_PASSWORD-"root"}

while ! ( curl -f -s --user $ONOS_USER:$ONOS_PASSWORD -X GET http://onos:8181/onos/v1/applications/org.foo.app|grep ^ || echo "__" ) | jq -e 'select(.state=="ACTIVE")'; do
	echo "Waiting..."
	sleep $ONOS_SUBMIT_WAIT
done

sleep 10

cfg=$(cat <<EOF
{
  "devices": {
    "netconf:$(hostname --ip-address)": {
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

echo $cfg | curl -X POST -H "content-type:application/json" http://onos:8181/onos/v1/network/configuration -d @- --user $ONOS_USER:$ONOS_PASSWORD
