package top.ienjoy.cybergarage.upnp.ssdp;

import top.ienjoy.cybergarage.net.*;
import top.ienjoy.cybergarage.http.*;

import top.ienjoy.cybergarage.upnp.device.*;

public class SSDPSearchRequest extends SSDPRequest {
    ////////////////////////////////////////////////
    //	Constructor
    ////////////////////////////////////////////////

    public SSDPSearchRequest(String serachTarget, int mx) {
        setMethod(HTTP.M_SEARCH);
        setURI("*");

        setHeader(HTTP.ST, serachTarget);
        setHeader(HTTP.MX, Integer.toString(mx));
        setHeader(HTTP.MAN, "\"" + MAN.DISCOVER + "\"");
    }

    public SSDPSearchRequest(String serachTarget) {
        this(serachTarget, SSDP.DEFAULT_MSEARCH_MX);
    }

    public SSDPSearchRequest() {
        this(ST.ROOT_DEVICE);
    }

    ////////////////////////////////////////////////
    //	HOST
    ////////////////////////////////////////////////

    public void setLocalAddress(String bindAddr) {
        String ssdpAddr = SSDP.ADDRESS;
        if (HostInterface.isIPv6Address(bindAddr))
            ssdpAddr = SSDP.getIPv6Address();
        setHost(ssdpAddr, SSDP.PORT);
    }

}
