package top.ienjoy.cybergarage.upnp.device;

import top.ienjoy.cybergarage.upnp.ssdp.*;

public interface SearchListener {
    void deviceSearchReceived(SSDPPacket ssdpPacket);
}
