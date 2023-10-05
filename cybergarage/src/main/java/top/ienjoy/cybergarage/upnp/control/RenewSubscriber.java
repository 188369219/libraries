package top.ienjoy.cybergarage.upnp.control;

import top.ienjoy.cybergarage.util.*;
import top.ienjoy.cybergarage.upnp.*;

public class RenewSubscriber extends ThreadCore {
    public final static long INTERVAL = 120;

    ////////////////////////////////////////////////
    //	Constructor
    ////////////////////////////////////////////////

    public RenewSubscriber(ControlPoint ctrlp) {
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
        long renewInterval = INTERVAL * 1000;
        while (isRunnable()) {
            try {
                Thread.sleep(renewInterval);
            } catch (InterruptedException ignored) {
            }
            ctrlp.renewSubscriberService();
        }
    }
}
