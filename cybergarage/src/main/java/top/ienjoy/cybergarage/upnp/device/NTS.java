package top.ienjoy.cybergarage.upnp.device;

public class NTS {
    public final static String ALIVE = "ssdp:alive";
    public final static String BYEBYE = "ssdp:byebye";
    public final static String PROPCHANGE = "upnp:propchange";

    public static boolean isAlive(String ntsValue) {
        if (ntsValue == null)
            return false;
        return ntsValue.startsWith(NTS.ALIVE);
    }

    public static boolean isByeBye(String ntsValue) {
        if (ntsValue == null)
            return false;
        return ntsValue.startsWith(NTS.BYEBYE);
    }
}

