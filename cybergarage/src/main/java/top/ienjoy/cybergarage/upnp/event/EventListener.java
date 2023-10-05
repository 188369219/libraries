package top.ienjoy.cybergarage.upnp.event;

public interface EventListener {
    void eventNotifyReceived(String uuid, long seq, String varName, String value);
}
