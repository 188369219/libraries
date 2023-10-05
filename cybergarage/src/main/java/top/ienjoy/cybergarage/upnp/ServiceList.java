package top.ienjoy.cybergarage.upnp;

import java.util.Vector;

public class ServiceList extends Vector<Service> {
    ////////////////////////////////////////////////
    //	Constants
    ////////////////////////////////////////////////

    public final static String ELEM_NAME = "serviceList";

    ////////////////////////////////////////////////
    //	Constructor
    ////////////////////////////////////////////////

    public ServiceList() {
    }

    ////////////////////////////////////////////////
    //	Methods
    ////////////////////////////////////////////////

    public Service getService(int n) {
        Service obj = null;
        try {
            obj = get(n);
        } catch (Exception ignored) {
        }
        return obj;
    }
}

