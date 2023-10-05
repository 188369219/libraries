package top.ienjoy.cybergarage.upnp.device;

import top.ienjoy.cybergarage.http.HTTPRequest;

public interface PresentationListener {
    void httpRequestRecieved(HTTPRequest httpReq);
}
