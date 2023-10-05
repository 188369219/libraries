package top.ienjoy.cybergarage.upnp.device;

public class ST {
    public final static String ALL_DEVICE = "ssdp:all";
    public final static String ROOT_DEVICE = "upnp:rootdevice";
    public final static String UUID_DEVICE = "uuid";
    public final static String URN_DEVICE = "urn:schemas-upnp-org:device:";
    public final static String URN_SERVICE = "urn:schemas-upnp-org:service:";

    public static boolean isAllDevice(String value) {
        if (value == null)
            return false;
        if (value.equals(ALL_DEVICE))
            return true;
        return value.equals("\"" + ALL_DEVICE + "\"");
    }

    public static boolean isRootDevice(String value) {
        if (value == null)
            return false;
        if (value.equals(ROOT_DEVICE))
            return true;
        return value.equals("\"" + ROOT_DEVICE + "\"");
    }

    public static boolean isUUIDDevice(String value) {
        if (value == null)
            return false;
        if (value.startsWith(UUID_DEVICE))
            return true;
        return value.startsWith("\"" + UUID_DEVICE);
    }

    public static boolean isURNDevice(String value) {
        if (value == null)
            return false;
        if (value.startsWith(URN_DEVICE))
            return true;
        return value.startsWith("\"" + URN_DEVICE);
    }

    public static boolean isURNService(String value) {
        if (value == null)
            return false;
        if (value.startsWith(URN_SERVICE))
            return true;
        return value.startsWith("\"" + URN_SERVICE);
    }
}

