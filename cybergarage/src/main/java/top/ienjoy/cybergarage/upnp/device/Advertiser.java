package top.ienjoy.cybergarage.upnp.device;

import top.ienjoy.cybergarage.util.*;
import top.ienjoy.cybergarage.upnp.*;

public class Advertiser extends ThreadCore {
    ////////////////////////////////////////////////
    //	Constructor
    ////////////////////////////////////////////////

    public Advertiser(Device dev) {
        setDevice(dev);
    }

    ////////////////////////////////////////////////
    //	Member
    ////////////////////////////////////////////////

    private Device device;

    public void setDevice(Device dev) {
        device = dev;
    }

    public Device getDevice() {
        return device;
    }

    ////////////////////////////////////////////////
    //	Thread
    ////////////////////////////////////////////////

    public void run() {
        Device dev = getDevice();
        long leaseTime = dev.getLeaseTime();
        long notifyInterval;
        while (isRunnable()) {
            notifyInterval = (leaseTime / 4) + (long) ((float) leaseTime * (Math.random() * 0.25f));
            notifyInterval *= 1000;
            try {
                Thread.sleep(notifyInterval);
            } catch (InterruptedException ignored) {
            }
            dev.announce();
        }
    }
}
