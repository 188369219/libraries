package top.ienjoy.cybergarage.upnp.device;

public class MAN {
    public final static String DISCOVER = "ssdp:discover";

    public static boolean isDiscover(String value) {
        if (value == null)
            return false;
        if (value.equals(MAN.DISCOVER))
            return true;
        return value.equals("\"" + MAN.DISCOVER + "\"");
    }
}

