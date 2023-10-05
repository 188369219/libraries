package top.ienjoy.cybergarage.upnp.device;

import top.ienjoy.cybergarage.upnp.ssdp.*;

public interface NotifyListener {
    void deviceNotifyReceived(SSDPPacket ssdpPacket);
}
