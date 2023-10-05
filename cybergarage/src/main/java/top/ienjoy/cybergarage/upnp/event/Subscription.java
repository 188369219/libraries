package top.ienjoy.cybergarage.upnp.event;

import top.ienjoy.cybergarage.upnp.*;

public class Subscription {
    public final static String XMLNS = "urn:schemas-upnp-org:event-1-0";
    public final static String TIMEOUT_HEADER = "Second-";
    public final static String INFINITE_STRING = "infinite";
    public final static int INFINITE_VALUE = -1;
    public final static String UUID = "uuid:";
    public final static String SUBSCRIBE_METHOD = "SUBSCRIBE";
    public final static String UNSUBSCRIBE_METHOD = "UNSUBSCRIBE";

    ////////////////////////////////////////////////
    //	Timeout
    ////////////////////////////////////////////////

    public static String toTimeoutHeaderString(long time) {
        if (time == Subscription.INFINITE_VALUE)
            return Subscription.INFINITE_STRING;
        return Subscription.TIMEOUT_HEADER + time;
    }

    public static long getTimeout(String headerValue) {
        int minusIdx = headerValue.indexOf('-');
        long timeout = Subscription.INFINITE_VALUE;
        try {
            String timeoutStr = headerValue.substring(minusIdx + 1);
            timeout = Long.parseLong(timeoutStr);
        } catch (Exception ignored) {
        }
        return timeout;
    }

    ////////////////////////////////////////////////
    //	SID
    ////////////////////////////////////////////////

    public static String createSID() {
        return UPnP.createUUID();
    }

    public static String toSIDHeaderString(String id) {
        return Subscription.UUID + id;
    }

    public static String getSID(String headerValue) {
        if (headerValue == null)
            return "";
        if (!headerValue.startsWith(Subscription.UUID))
            return headerValue;
        return headerValue.substring(Subscription.UUID.length());
    }

}

