/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foo.app;

import org.onosproject.net.device.DeviceEvent;
import org.onosproject.net.device.DeviceListener;

/**
 *
 * @author vk496
 */
public class NetopeerListener implements DeviceListener {

//    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void event(DeviceEvent e) {
        System.out.println("vk496," + " NEW Event: " + e.type());
    }

}
