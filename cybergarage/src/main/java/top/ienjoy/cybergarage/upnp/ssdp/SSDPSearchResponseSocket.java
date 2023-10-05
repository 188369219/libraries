package top.ienjoy.cybergarage.upnp.ssdp;

import java.net.BindException;
import java.net.DatagramSocket;
import java.net.InetAddress;

import top.ienjoy.cybergarage.upnp.*;

public class SSDPSearchResponseSocket extends HTTPUSocket implements Runnable {
    ////////////////////////////////////////////////
    //	Constructor
    ////////////////////////////////////////////////

    public SSDPSearchResponseSocket() {
        setControlPoint(null);
    }

    public SSDPSearchResponseSocket(String bindAddr, int port) throws BindException {
        super(bindAddr, port);
        setControlPoint(null);
    }

    ////////////////////////////////////////////////
    //	ControlPoint
    ////////////////////////////////////////////////

    private ControlPoint controlPoint = null;

    public void setControlPoint(ControlPoint ctrlp) {
        this.controlPoint = ctrlp;
    }

    public ControlPoint getControlPoint() {
        return controlPoint;
    }

    ////////////////////////////////////////////////
    //	run
    ////////////////////////////////////////////////

    private Thread deviceSearchResponseThread = null;

    public void run() {
        Thread thisThread = Thread.currentThread();

        ControlPoint ctrlPoint = getControlPoint();

        while (deviceSearchResponseThread == thisThread) {
            Thread.yield();
            SSDPPacket packet = receive();
            if (packet == null)
                break;
            if (ctrlPoint != null)
                ctrlPoint.searchResponseReceived(packet);
        }
    }

    public void start() {

        StringBuilder name = new StringBuilder("Cyber.SSDPSearchResponseSocket/");
        DatagramSocket s = getDatagramSocket();
        // localAddr is null on Android m3-rc37a (01/30/08)
        InetAddress localAddr = s.getLocalAddress();
        if (localAddr != null) {
            name.append(s.getLocalAddress()).append(':');
            name.append(s.getLocalPort());
        }
        deviceSearchResponseThread = new Thread(this, name.toString());
        deviceSearchResponseThread.start();
    }

    public void stop() {
        deviceSearchResponseThread = null;
    }

    ////////////////////////////////////////////////
    //	post
    ////////////////////////////////////////////////

    public boolean post(String addr, int port, SSDPSearchResponse res) {
        return post(addr, port, res.getHeader());
    }

    ////////////////////////////////////////////////
    //	post
    ////////////////////////////////////////////////

    public boolean post(String addr, int port, SSDPSearchRequest req) {
        return post(addr, port, req.toString());
    }
}

