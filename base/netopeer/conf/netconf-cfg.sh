#!/bin/bash
set -x

NETCONF_USER=${NETCONF_USER-"root"}
NETCONF_PASSWORD=${NETCONF_PASSWORD-"root"}

# while ! ( curl -f -s --user $ONOS_USER:$ONOS_PASSWORD -X GET http://onos:8181/onos/v1/applications/org.foo.app|grep ^ || echo "__" ) | jq -e 'select(.state=="ACTIVE")'; do
# 	echo "Waiting..."
# 	# sleep 0.5
# done

#Listen Broadcast
tcpdump -Avnn -l udp port 3000 |
	while read p; do
		 if echo $p | grep -q "onos-ipsec";then
			 break;
		 fi;
 	done

netopeerIP=$(ip route get $SDN_NET | awk '{print $8}')

# Still not working. We must to wait a little bit before continue
# sleep 10

cfg=$(cat <<EOF
{
  "devices": {
    "netconf:$netopeerIP": {
      "netconf": {
        "ip": "$netopeerIP",
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
