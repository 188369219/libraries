package top.ienjoy.cybergarage.upnp.event;

import top.ienjoy.cybergarage.upnp.*;
import top.ienjoy.cybergarage.http.*;

public class SubscriptionResponse extends HTTPResponse {
    ////////////////////////////////////////////////
    //	Constructor
    ////////////////////////////////////////////////

    public SubscriptionResponse() {
        setServer(UPnP.getServerName());
    }

    public SubscriptionResponse(HTTPResponse httpRes) {
        super(httpRes);
    }

    ////////////////////////////////////////////////
    //	Error
    ////////////////////////////////////////////////

    public void setResponse(int code) {
        setStatusCode(code);
        setContentLength(0);
    }

    ////////////////////////////////////////////////
    //	Error
    ////////////////////////////////////////////////

    public void setErrorResponse(int code) {
        setStatusCode(code);
        setContentLength(0);
    }

    ////////////////////////////////////////////////
    //	SID
    ////////////////////////////////////////////////

    public void setSID(String id) {
        setHeader(HTTP.SID, Subscription.toSIDHeaderString(id));
    }

    public String getSID() {
        return Subscription.getSID(getHeaderValue(HTTP.SID));
    }

    ////////////////////////////////////////////////
    //	Timeout
    ////////////////////////////////////////////////

    public void setTimeout(long value) {
        setHeader(HTTP.TIMEOUT, Subscription.toTimeoutHeaderString(value));
    }

    public long getTimeout() {
        return Subscription.getTimeout(getHeaderValue(HTTP.TIMEOUT));
    }
}
