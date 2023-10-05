package top.ienjoy.cybergarage.upnp.device;

import top.ienjoy.cybergarage.upnp.*;
import top.ienjoy.cybergarage.util.*;

public class Disposer extends ThreadCore {
    ////////////////////////////////////////////////
    //	Constructor
    ////////////////////////////////////////////////

    public Disposer(ControlPoint ctrlp) {
        setControlPoint(ctrlp);
    }

    ////////////////////////////////////////////////
    //	Member
    ////////////////////////////////////////////////

    private ControlPoint ctrlPoint;

    public void setControlPoint(ControlPoint ctrlp) {
        ctrlPoint = ctrlp;
    }

    public ControlPoint getControlPoint() {
        return ctrlPoint;
    }

    ////////////////////////////////////////////////
    //	Thread
    ////////////////////////////////////////////////

    public void run() {
        ControlPoint ctrlp = getControlPoint();
        long monitorInterval = ctrlp.getExpiredDeviceMonitoringInterval() * 1000;

        while (isRunnable()) {
            try {
                Thread.sleep(monitorInterval);
            } catch (InterruptedException ignored) {
            }
            ctrlp.removeExpiredDevices();
            //ctrlp.print();
        }
    }
}
