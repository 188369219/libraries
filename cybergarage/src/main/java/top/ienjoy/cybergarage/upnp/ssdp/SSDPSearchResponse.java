package top.ienjoy.cybergarage.upnp.ssdp;

import top.ienjoy.cybergarage.http.*;

import top.ienjoy.cybergarage.upnp.*;

public class SSDPSearchResponse extends SSDPResponse {
    ////////////////////////////////////////////////
    //	Constructor
    ////////////////////////////////////////////////

    public SSDPSearchResponse() {
        setStatusCode(HTTPStatus.OK);
        setCacheControl(Device.DEFAULT_LEASE_TIME);
        setHeader(HTTP.SERVER, UPnP.getServerName());
        setHeader(HTTP.EXT, "");
    }
}
