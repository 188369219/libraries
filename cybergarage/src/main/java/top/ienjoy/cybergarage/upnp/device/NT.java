package top.ienjoy.cybergarage.upnp.device;

public class NT {
    public final static String ROOTDEVICE = "upnp:rootdevice";
    public final static String EVENT = "upnp:event";

    public static boolean isRootDevice(String ntValue) {
        if (ntValue == null)
            return false;
        return ntValue.startsWith(ROOTDEVICE);
    }
}

