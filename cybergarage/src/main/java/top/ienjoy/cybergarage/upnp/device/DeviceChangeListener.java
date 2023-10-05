package top.ienjoy.cybergarage.upnp.device;

import top.ienjoy.cybergarage.upnp.Device;

public interface DeviceChangeListener {
    void deviceAdded(Device dev);

    void deviceRemoved(Device dev);
}
