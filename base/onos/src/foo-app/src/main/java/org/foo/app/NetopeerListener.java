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
        try {
            s.get("<vk496>TEST</vk496>");
            log.info("vk496 - Sent message to " + di);

        } catch (NetconfException ex) {
            log.error(ex.toString());
        }

    }

    @Override
    public void deviceRemoved(DeviceId di) {
        log.info("vk496 - REMOVED DEVICE " + di);
    }

}
