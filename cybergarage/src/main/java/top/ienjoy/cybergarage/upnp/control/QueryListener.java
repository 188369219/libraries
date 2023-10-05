package top.ienjoy.cybergarage.upnp.control;

import top.ienjoy.cybergarage.upnp.*;

public interface QueryListener {
    boolean queryControlReceived(StateVariable stateVar);
}
