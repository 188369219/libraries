package top.ienjoy.cybergarage.upnp.device;

import top.ienjoy.cybergarage.upnp.ssdp.*;

public interface SearchResponseListener {
    void deviceSearchResponseReceived(SSDPPacket ssdpPacket);
}
