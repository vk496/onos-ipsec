/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foo.app;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.onosproject.drivers.utilities.XmlConfigParser;
import org.onosproject.net.DeviceId;
import org.onosproject.netconf.NetconfController;
import org.onosproject.netconf.NetconfException;
import org.onosproject.netconf.NetconfSession;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

/**
 *
 * @author vk496
 */
public class IPsec {

    private static IPsec INSTANCE = null;
    private static int counter = 1;

    private final Logger log = getLogger(getClass());

    private final Map<DeviceId, String> devicesDataLayerIP = new HashMap<>();
    private final Map<String, Integer> devicesDataLayerIPcounter = new HashMap<>();

    private NetconfController controller = null;

    private IPsec(NetconfController c) {
        controller = c;
    }

    public static void setupIPsec(NetconfController c) {
        INSTANCE = new IPsec(c);
    }

    public static IPsec getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("IPsec class was not initialized. Please, use setupIPsec before");
        }

        return INSTANCE;
    }

    /**
     * Check if 1 or more devices are already registered
     *
     * @return true if or more devices available, false in any other case
     */
    public synchronized boolean canEstablishTunnels() {

        return !devicesDataLayerIP.isEmpty();
    }

    /**
     * Add device to IPsec structures
     *
     * @param device
     * @return true if new device was added, false if already exists
     * @throws NetconfException
     */
    public synchronized boolean addDevice(DeviceId device) throws NetconfException {

        if (devicesDataLayerIP.containsKey(device)) {
            return false;
        }

        String request = "<rpc message-id=\"101\"\n"
                + "          xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">"
                + "<get>\n"
                + "         <filter type=\"subtree\">\n"
                + "           <interfaces-state xmlns=\"urn:ietf:params:xml:ns:yang:ietf-interfaces\">\n"
                + "               <interface>\n"
                + "                 <name>eth1</name>\n"
                + "               </interface>\n"
                + "           </interfaces-state>\n"
                + "         </filter>\n"
                + "       </get>"
                + "</rpc>";

        NetconfSession s = controller.getNetconfDevice(device).getSession();

        String reply = s.requestSync(request);

        HierarchicalConfiguration cfg = XmlConfigParser.
                loadXml(new ByteArrayInputStream(reply.getBytes()));

        List<HierarchicalConfiguration> subtrees
                = cfg.configurationsAt("data.interfaces-state.interface");
        for (HierarchicalConfiguration netopeerInterface : subtrees) {
            String nameIface = netopeerInterface.getString("name");
            HierarchicalConfiguration ipV4info = netopeerInterface.configurationAt("ipv4");
            String myIP = ipV4info.configurationAt("address").getString("ip");
            log.info("Iface: " + nameIface + " have IP: " + myIP);
        }

        String deviceDataLayerIP = reply.split("<ip>")[1].split("</ip>")[0];

        devicesDataLayerIP.put(device, deviceDataLayerIP);
//        devicesDataLayerIPcounter.put(device, counter);

//        counter += 2;
        return true;
    }

    public String getSAD(DeviceId device, DeviceId newDevice, TRAFFIC_TYPE type) {
        return getGenericEntry(IPsecStructure.SAD, device, newDevice, type);
    }

    public String getSPD(DeviceId device, DeviceId newDevice, TRAFFIC_TYPE type) {
        return getGenericEntry(IPsecStructure.SPD, device, newDevice, type);
    }

    private String getGenericEntry(IPsecStructure structure, DeviceId device, DeviceId newDevice, TRAFFIC_TYPE type) {

        if (!devicesDataLayerIP.containsKey(device)) {
            throw new IllegalArgumentException("Device " + device + " don't have asociated IP of Data Layer");
        }

        String local_ip, remote_ip, ipsec_name;
        Integer local_counter;

        if (devicesDataLayerIPcounter.containsKey(device.toString() + newDevice.toString())) {
        } else if (devicesDataLayerIPcounter.containsKey(newDevice.toString() + device.toString())) {
        } else {
            devicesDataLayerIPcounter.put(device.toString() + newDevice.toString(), counter);
            counter += 2;
        }

        switch (type) {
            case INBOUND:
                remote_ip = devicesDataLayerIP.get(newDevice);
                local_ip = devicesDataLayerIP.get(device);
                ipsec_name = "in/" + newDevice + "/" + devicesDataLayerIP.get(device);

                if (devicesDataLayerIPcounter.containsKey(device.toString() + newDevice.toString())) {
                    local_counter = devicesDataLayerIPcounter.get(device.toString() + newDevice.toString());
                } else {
                    local_counter = devicesDataLayerIPcounter.get(newDevice.toString() + device.toString()) + 1;
                }

                break;
            case OUTBOUND:
                remote_ip = devicesDataLayerIP.get(device);
                local_ip = devicesDataLayerIP.get(newDevice);
                ipsec_name = "out/" + devicesDataLayerIP.get(device) + "/" + newDevice;

                if (devicesDataLayerIPcounter.containsKey(device.toString() + newDevice.toString())) {
                    local_counter = devicesDataLayerIPcounter.get(device.toString() + newDevice.toString()) + 1;
                } else {
                    local_counter = devicesDataLayerIPcounter.get(newDevice.toString() + device.toString());
                }

                break;
            case BOTH:
                return getGenericEntry(structure, device, newDevice, TRAFFIC_TYPE.INBOUND) + getGenericEntry(structure, device, newDevice, TRAFFIC_TYPE.OUTBOUND);
            default:
                throw new IllegalStateException("Wrong IPsec traffic type");
        }

        return getXML(structure, local_counter, ipsec_name, type, remote_ip, local_ip);

    }

    private String getXML(IPsecStructure s, int local_counter, String ipsec_name, TRAFFIC_TYPE type, String remote_ip, String local_ip) {

        switch (s) {
            case SPD:
                return "	<spd-entry>\n"
                        + "		<rule-number>" + local_counter + "</rule-number>\n"
                        + "		<priority>0</priority>\n"
                        + "		<names>\n"
                        + "			<name>" + ipsec_name + "</name>\n"
                        + "		</names>\n"
                        + "		<condition>\n"
                        + "			<traffic-selector-list>\n"
                        + "				<ts-number>102</ts-number>\n"
                        + "				<direction>" + type + "</direction>\n"
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
                        + "				<mode>TRANSPORT</mode>\n"
                        // + "				<mode>TUNNEL</mode>\n"
                        // + "        		<tunnel>\n"
                        // + "          			<local>" + remote_ip + "</local>\n"
                        // + "          			<remote>" + local_ip + "</remote>\n"
                        // + "        		</tunnel>\n"
                        + "			</ipsec-sa-cfg>\n"
                        + "		</processing-info>\n"
                        + "	</spd-entry>\n";
            case SAD:
                return "	<sad-entry>\n"
                        + "		<spi>" + local_counter + "</spi>\n"
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
                        + "			<integrity>\n"
                        + "				<integrity-algorithm>hmac-md5-128</integrity-algorithm>\n"
                        + "				<key>auth</key>\n"
                        + "			</integrity>\n"
                        + "		</esp-sa>\n"
                        // + "		<mode>TUNNEL</mode>\n"
                        + "		<mode>TRANSPORT</mode>\n"
                        + "	</sad-entry>\n";
            default:
                throw new IllegalStateException("Wrong XML request");
        }

    }

    public enum TRAFFIC_TYPE {
        INBOUND, OUTBOUND, BOTH
    }

    private enum IPsecStructure {
        SPD, SAD
    }

}
