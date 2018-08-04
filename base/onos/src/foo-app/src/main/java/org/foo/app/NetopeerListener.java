/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foo.app;

import java.util.logging.Level;
import org.onosproject.net.DeviceId;
import org.onosproject.net.device.DeviceEvent;
import org.onosproject.net.device.DeviceListener;
import org.onosproject.netconf.NetconfController;
import org.onosproject.netconf.NetconfDevice;
import org.onosproject.netconf.NetconfDeviceListener;
import org.onosproject.netconf.NetconfException;
import org.onosproject.netconf.NetconfSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author vk496
 */
public class NetopeerListener implements NetconfDeviceListener {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private NetconfController controller = null;

    public NetopeerListener(NetconfController controller) {
        this.controller = controller;
    }

    @Override
    public void deviceAdded(DeviceId di) {
        log.info("vk496 - NEW DEVICE " + di);
        NetconfDevice d = controller.getNetconfDevice(di);
        NetconfSession s = d.getSession();
        log.info("vk496 - Sent message to " + di);

        String ip = d.getDeviceInfo().ip().toInetAddress().getHostAddress();

        try {
            switch (ip) {
                case "10.0.3.2":
                    s.editConfig(getIPsecConfig("192.169.0.2", "192.169.0.3", 10, 11));
                    break;
                case "10.0.3.3":
                    s.editConfig(getIPsecConfig("192.169.0.3", "192.169.0.2", 11, 10));
                    break;
                default:
                    log.info("vk496 - Unknown IP for xml: " + ip);
                    break;
            }
            log.info("vk496 - Set xml to " + ip);

        } catch (NetconfException ex) {
            log.info("vk496 - Edit config error. " + ex);
        }

    }

    @Override
    public void deviceRemoved(DeviceId di) {
        log.info("vk496 - REMOVED DEVICE " + di);
    }

    private String getIPsecConfig(String local_ip, String remote_ip, Integer local_spi, Integer remote_spi) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><rpc message-id=\"6\"  "
                + "xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n"
                + "<edit-config>\n"
                + "<target><running/></target>\n"
                + "<config xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n"
                + "<ietf-ipsec xmlns=\"http://example.net/ietf-ipsec\" xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n"
                + "<ipsec nc:operation=\"merge\">\n"
                + "  <spd>\n"
                + "	<spd-entry>\n"
                + "		<rule-number>10</rule-number>\n"
                + "		<priority>0</priority>\n"
                + "		<names>\n"
                + "			<name>in/" + remote_ip + "/" + local_ip + "</name>\n"
                + "		</names>\n"
                + "		<condition>\n"
                + "			<traffic-selector-list>\n"
                + "				<ts-number>102</ts-number>\n"
                + "				<direction>INBOUND</direction>\n"
                + "				<local-addresses>\n"
                + "					<start>" + remote_ip + "</start>\n"
                + " 					<end>" + remote_ip + "</end>\n"
                + "				</local-addresses>\n"
                + "				<remote-addresses>\n"
                + "					<start>" + local_ip + "</start>\n"
                + "					<end>" + local_ip + "</end>\n"
                + "				</remote-addresses>\n"
                + "				<next-layer-protocol>TCP</next-layer-protocol>\n"
                + "				<local-ports>\n"
                + "					<start>0</start>\n"
                + "					<end>0</end>\n"
                + "				</local-ports>\n"
                + "				<remote-ports>\n"
                + "					<start>0</start>\n"
                + "					<end>0</end>\n"
                + "				</remote-ports>\n"
                + "			</traffic-selector-list>\n"
                + "		</condition>\n"
                + "		<processing-info>\n"
                + "			<action>PROTECT</action>\n"
                + "			<ipsec-sa-cfg>\n"
                + "				<security-protocol>esp</security-protocol>\n"
                + "				<mode>TUNNEL</mode>\n"
                + "        		<tunnel>\n"
                + "          			<local>" + remote_ip + "</local>\n"
                + "          			<remote>" + local_ip + "</remote>\n"
                + "        		</tunnel>\n"
                + "			</ipsec-sa-cfg>\n"
                + "		</processing-info>\n"
                + "	</spd-entry>\n"
                + "	<spd-entry>\n"
                + "		<rule-number>11</rule-number>\n"
                + "		<priority>0</priority>\n"
                + "		<names>\n"
                + "			<name>out/" + local_ip + "/" + remote_ip + "</name>\n"
                + "		</names>\n"
                + "		<condition>\n"
                + "			<traffic-selector-list>\n"
                + "				<ts-number>2</ts-number>\n"
                + "				<direction>OUTBOUND</direction>\n"
                + "				<local-addresses>\n"
                + "					<start>" + local_ip + "</start>\n"
                + " 					<end>" + local_ip + "</end>\n"
                + "				</local-addresses>\n"
                + "				<remote-addresses>\n"
                + "					<start>" + remote_ip + "</start>\n"
                + "					<end>" + remote_ip + "</end>\n"
                + "				</remote-addresses>\n"
                + "				<next-layer-protocol>TCP</next-layer-protocol>\n"
                + "				<local-ports>\n"
                + "					<start>0</start>\n"
                + "					<end>0</end>\n"
                + "				</local-ports>\n"
                + "				<remote-ports>\n"
                + "					<start>0</start>\n"
                + "					<end>0</end>\n"
                + "				</remote-ports>\n"
                + "			</traffic-selector-list>\n"
                + "		</condition>\n"
                + "		<processing-info>\n"
                + "			<action>PROTECT</action>\n"
                + "			<ipsec-sa-cfg>\n"
                + "				<security-protocol>esp</security-protocol>\n"
                + "				<mode>TUNNEL</mode>\n"
                + "        		<tunnel>\n"
                + "          			<local>" + local_ip + "</local>\n"
                + "          			<remote>" + remote_ip + "</remote>\n"
                + "        		</tunnel>\n"
                + "			</ipsec-sa-cfg>\n"
                + "		</processing-info>\n"
                + "	</spd-entry>\n"
                + "  </spd>\n"
                + "<sad>\n"
                + "	<sad-entry>\n"
                + "		<spi>" + local_spi + "</spi>\n"
                + "		<rule-number>101</rule-number>\n"
                + "		<local-addresses>\n"
                + "			<start>" + local_ip + "</start>\n"
                + " 			<end>" + local_ip + "</end>\n"
                + "		</local-addresses>\n"
                + "		<remote-addresses>\n"
                + "			<start>" + remote_ip + "</start>\n"
                + "			<end>" + remote_ip + "</end>\n"
                + "		</remote-addresses>\n"
                + "		<next-layer-protocol>TCP</next-layer-protocol>\n"
                + "		<local-ports>\n"
                + "			<start>0</start>\n"
                + "			<end>0</end>\n"
                + "		</local-ports>\n"
                + "		<remote-ports>\n"
                + "			<start>0</start>\n"
                + "			<end>0</end>\n"
                + "		</remote-ports>\n"
                + "		<security-protocol>esp</security-protocol>\n"
                + "		<esp-sa>\n"
                + "			<encryption>\n"
                + "				<encryption-algorithm>3des</encryption-algorithm>\n"
                + "				<key>ecr_secret</key>\n"
                + "				<iv>vector</iv>\n"
                + "			</encryption>\n"
                + "		</esp-sa>\n"
                + "		<mode>TUNNEL</mode>\n"
                + "	</sad-entry>\n"
                + "	<sad-entry>\n"
                + "		<spi>" + remote_spi + "</spi>\n"
                + "		<rule-number>1</rule-number>\n"
                + "		<local-addresses>\n"
                + "			<start>" + remote_ip + "</start>\n"
                + " 			<end>" + remote_ip + "</end>\n"
                + "		</local-addresses>\n"
                + "		<remote-addresses>\n"
                + "			<start>" + local_ip + "</start>\n"
                + "			<end>" + local_ip + "</end>\n"
                + "		</remote-addresses>\n"
                + "		<next-layer-protocol>TCP</next-layer-protocol>\n"
                + "		<local-ports>\n"
                + "			<start>0</start>\n"
                + "			<end>0</end>\n"
                + "		</local-ports>\n"
                + "		<remote-ports>\n"
                + "			<start>0</start>\n"
                + "			<end>0</end>\n"
                + "		</remote-ports>\n"
                + "		<security-protocol>esp</security-protocol>\n"
                + "		<esp-sa>\n"
                + "			<encryption>\n"
                + "				<encryption-algorithm>3des</encryption-algorithm>\n"
                + "				<key>ecr_secret</key>\n"
                + "				<iv>vector</iv>\n"
                + "			</encryption>\n"
                + "		</esp-sa>\n"
                + "		<mode>TUNNEL</mode>\n"
                + "	</sad-entry>\n"
                + "</sad>\n"
                + "</ipsec>\n"
                + "</ietf-ipsec>\n"
                + "</config>\n"
                + "</edit-config>\n"
                + "</rpc>]]>]]>";
    }

}
