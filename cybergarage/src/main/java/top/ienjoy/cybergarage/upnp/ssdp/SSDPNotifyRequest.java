package top.ienjoy.cybergarage.upnp.ssdp;

import top.ienjoy.cybergarage.http.HTTP;

public class SSDPNotifyRequest extends SSDPRequest {
    ////////////////////////////////////////////////
    //	Constructor
    ////////////////////////////////////////////////

    public SSDPNotifyRequest() {
        setMethod(HTTP.NOTIFY);
        setURI("*");
    }
}
