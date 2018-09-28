/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foo.app;

import java.util.LinkedHashMap;
import java.util.Map;
import org.onosproject.net.DeviceId;
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
//    private int id_couter = 1;
//    private DeviceId rootDevice = null; //our root device ID
//    private String rootDeviceDataLayerIP = null;
//    private final Set<DeviceId> listDevices = new LinkedHashSet<>(); //List parsed devices

    public NetopeerListener(NetconfController controller) {
        this.controller = controller;
        IPsec.setupIPsec(controller);
    }

    @Override
    public void deviceAdded(DeviceId di) {

        try {
            if (!IPsec.getInstance().addDevice(di)) { //already parsed
                return;
            }
        } catch (NetconfException ex) {
            log.info("vk496 - Edit config error. " + ex);
        }

        log.info("vk496 - NEW DEVICE " + di);

        try {

            if (IPsec.getInstance().canEstablishTunnels()) {
                createTunnels(di); //Let's register
            }

        } catch (NetconfException ex) {
            log.info("vk496 - error Bad bad bad " + di + ". " + ex);
        }

        log.info("onos-ipsec new device " + di + ": " + System.currentTimeMillis());

    }

    @Override
    public void deviceRemoved(DeviceId di) {

        log.info("vk496 - REMOVED DEVICE " + di);
    }

    // private String getIPsecConfig(String local_ip, String remote_ip, Integer local_spi, Integer remote_spi) {
    //     return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><rpc message-id=\"6\"  "
    //             + "xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n"
    //             + "<edit-config>\n"
    //             + "<target><running/></target>\n"
    //             + "<config xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n"
    //             + "<ietf-ipsec xmlns=\"http://example.net/ietf-ipsec\" xmlns:nc=\"urn:ietf:params:xml:ns:netconf:base:1.0\">\n"
    //             + "<ipsec nc:operation=\"merge\">\n"
    //             + "  <spd>\n"
    //             + "	<spd-entry>\n"
    //             + "		<rule-number>10</rule-number>\n"
    //             + "		<priority>0</priority>\n"
    //             + "		<names>\n"
    //             + "			<name>in/" + remote_ip + "/" + local_ip + "</name>\n"
    //             + "		</names>\n"
    //             + "		<condition>\n"
    //             + "			<traffic-selector-list>\n"
    //             + "				<ts-number>102</ts-number>\n"
    //             + "				<direction>INBOUND</direction>\n"
    //             + "				<local-addresses>\n"
    //             + "					<start>" + remote_ip + "</start>\n"
    //             + " 					<end>" + remote_ip + "</end>\n"
    //             + "				</local-addresses>\n"
    //             + "				<remote-addresses>\n"
    //             + "					<start>" + local_ip + "</start>\n"
    //             + "					<end>" + local_ip + "</end>\n"
    //             + "				</remote-addresses>\n"
    //             + "				<next-layer-protocol>TCP</next-layer-protocol>\n"
    //             + "				<local-ports>\n"
    //             + "					<start>0</start>\n"
    //             + "					<end>0</end>\n"
    //             + "				</local-ports>\n"
    //             + "				<remote-ports>\n"
    //             + "					<start>0</start>\n"
    //             + "					<end>0</end>\n"
    //             + "				</remote-ports>\n"
    //             + "			</traffic-selector-list>\n"
    //             + "		</condition>\n"
    //             + "		<processing-info>\n"
    //             + "			<action>PROTECT</action>\n"
    //             + "			<ipsec-sa-cfg>\n"
    //             + "				<security-protocol>esp</security-protocol>\n"
    //             + "				<mode>TUNNEL</mode>\n"
    //             + "        		<tunnel>\n"
    //             + "          			<local>" + remote_ip + "</local>\n"
    //             + "          			<remote>" + local_ip + "</remote>\n"
    //             + "        		</tunnel>\n"
    //             + "			</ipsec-sa-cfg>\n"
    //             + "		</processing-info>\n"
    //             + "	</spd-entry>\n"
    //             + "	<spd-entry>\n"
    //             + "		<rule-number>11</rule-number>\n"
    //             + "		<priority>0</priority>\n"
    //             + "		<names>\n"
    //             + "			<name>out/" + local_ip + "/" + remote_ip + "</name>\n"
    //             + "		</names>\n"
    //             + "		<condition>\n"
    //             + "			<traffic-selector-list>\n"
    //             + "				<ts-number>2</ts-number>\n"
    //             + "				<direction>OUTBOUND</direction>\n"
    //             + "				<local-addresses>\n"
    //             + "					<start>" + local_ip + "</start>\n"
    //             + " 					<end>" + local_ip + "</end>\n"
    //             + "				</local-addresses>\n"
    //             + "				<remote-addresses>\n"
    //             + "					<start>" + remote_ip + "</start>\n"
    //             + "					<end>" + remote_ip + "</end>\n"
    //             + "				</remote-addresses>\n"
    //             + "				<next-layer-protocol>TCP</next-layer-protocol>\n"
    //             + "				<local-ports>\n"
    //             + "					<start>0</start>\n"
    //             + "					<end>0</end>\n"
    //             + "				</local-ports>\n"
    //             + "				<remote-ports>\n"
    //             + "					<start>0</start>\n"
    //             + "					<end>0</end>\n"
    //             + "				</remote-ports>\n"
    //             + "			</traffic-selector-list>\n"
    //             + "		</condition>\n"
    //             + "		<processing-info>\n"
    //             + "			<action>PROTECT</action>\n"
    //             + "			<ipsec-sa-cfg>\n"
    //             + "				<security-protocol>esp</security-protocol>\n"
    //             + "				<mode>TUNNEL</mode>\n"
    //             + "        		<tunnel>\n"
    //             + "          			<local>" + local_ip + "</local>\n"
    //             + "          			<remote>" + remote_ip + "</remote>\n"
    //             + "        		</tunnel>\n"
    //             + "			</ipsec-sa-cfg>\n"
    //             + "		</processing-info>\n"
    //             + "	</spd-entry>\n"
    //             + "  </spd>\n"
    //             + "<sad>\n"
    //             + "	<sad-entry>\n"
    //             + "		<spi>" + local_spi + "</spi>\n"
    //             + "		<rule-number>101</rule-number>\n"
    //             + "		<local-addresses>\n"
    //             + "			<start>" + local_ip + "</start>\n"
    //             + " 			<end>" + local_ip + "</end>\n"
    //             + "		</local-addresses>\n"
    //             + "		<remote-addresses>\n"
    //             + "			<start>" + remote_ip + "</start>\n"
    //             + "			<end>" + remote_ip + "</end>\n"
    //             + "		</remote-addresses>\n"
    //             + "		<next-layer-protocol>TCP</next-layer-protocol>\n"
    //             + "		<local-ports>\n"
    //             + "			<start>0</start>\n"
    //             + "			<end>0</end>\n"
    //             + "		</local-ports>\n"
    //             + "		<remote-ports>\n"
    //             + "			<start>0</start>\n"
    //             + "			<end>0</end>\n"
    //             + "		</remote-ports>\n"
    //             + "		<security-protocol>esp</security-protocol>\n"
    //             + "		<esp-sa>\n"
    //             + "			<encryption>\n"
    //             + "				<encryption-algorithm>3des</encryption-algorithm>\n"
    //             + "				<key>ecr_secret</key>\n"
    //             + "				<iv>vector</iv>\n"
    //             + "			</encryption>\n"
    //             + "		</esp-sa>\n"
    //             + "		<mode>TUNNEL</mode>\n"
    //             + "	</sad-entry>\n"
    //             + "	<sad-entry>\n"
    //             + "		<spi>" + remote_spi + "</spi>\n"
    //             + "		<rule-number>1</rule-number>\n"
    //             + "		<local-addresses>\n"
    //             + "			<start>" + remote_ip + "</start>\n"
    //             + " 			<end>" + remote_ip + "</end>\n"
    //             + "		</local-addresses>\n"
    //             + "		<remote-addresses>\n"
    //             + "			<start>" + local_ip + "</start>\n"
    //             + "			<end>" + local_ip + "</end>\n"
    //             + "		</remote-addresses>\n"
    //             + "		<next-layer-protocol>TCP</next-layer-protocol>\n"
    //             + "		<local-ports>\n"
    //             + "			<start>0</start>\n"
    //             + "			<end>0</end>\n"
    //             + "		</local-ports>\n"
    //             + "		<remote-ports>\n"
    //             + "			<start>0</start>\n"
    //             + "			<end>0</end>\n"
    //             + "		</remote-ports>\n"
    //             + "		<security-protocol>esp</security-protocol>\n"
    //             + "		<esp-sa>\n"
    //             + "			<encryption>\n"
    //             + "				<encryption-algorithm>3des</encryption-algorithm>\n"
    //             + "				<key>ecr_secret</key>\n"
    //             + "				<iv>vector</iv>\n"
    //             + "			</encryption>\n"
    //             + "		</esp-sa>\n"
    //             + "		<mode>TUNNEL</mode>\n"
    //             + "	</sad-entry>\n"
    //             + "</sad>\n"
    //             + "</ipsec>\n"
    //             + "</ietf-ipsec>\n"
    //             + "</config>\n"
    //             + "</edit-config>\n"
    //             + "</rpc>]]>]]>";
    // }
    private synchronized void createTunnels(DeviceId new_device) throws NetconfException {

        //        YangXmlUtils.getInstance().loadXml(null).set;
        String all_SPD = "";
        String all_SAD = "";

        Map<DeviceId, NetconfDevice> test = new LinkedHashMap<>(controller.getDevicesMap());
        test.remove(new_device);

        for (Map.Entry<DeviceId, NetconfDevice> pair : test.entrySet()) {
            String spdForNEWDevice = IPsec.getInstance().getSPD(new_device, pair.getKey(), IPsec.TRAFFIC_TYPE.BOTH);
            String sadForNEWDevice = IPsec.getInstance().getSAD(new_device, pair.getKey(), IPsec.TRAFFIC_TYPE.BOTH);

            //rules for the new device
            all_SPD = all_SPD + spdForNEWDevice;
            all_SAD = all_SAD + sadForNEWDevice;

            controller
                    .getNetconfDevice(pair.getKey())
                    .getSession()
                    .requestSync(
                            IPsec.encapsulateSPDandSADinXML(
                                    IPsec.getInstance().getSPD(pair.getKey(), new_device, IPsec.TRAFFIC_TYPE.BOTH),
                                    IPsec.getInstance().getSAD(pair.getKey(), new_device, IPsec.TRAFFIC_TYPE.BOTH)
                            )
                    );
            log.info("vk496 - Update tunnel between: " + pair.getKey() + " and " + new_device);

        }

        NetconfSession newDeviceSession = controller.getNetconfDevice(new_device).getSession();
        newDeviceSession.requestSync(IPsec.encapsulateSPDandSADinXML(all_SPD, all_SAD));

        log.info("vk496 - Remote device: " + new_device);

    }

}
