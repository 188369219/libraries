package top.ienjoy.cybergarage.upnp.ssdp;

import java.net.InetAddress;
import java.util.*;

import top.ienjoy.cybergarage.net.*;

import top.ienjoy.cybergarage.upnp.*;

public class SSDPSearchResponseSocketList extends Vector<SSDPSearchResponseSocket> {
    ////////////////////////////////////////////////
    //	Constructor
    ////////////////////////////////////////////////

    private InetAddress[] binds = null;

    public SSDPSearchResponseSocketList() {
    }

    /**
     * @param binds The host to bind.Use <tt>null</tt> for the default behavior
     */
    public SSDPSearchResponseSocketList(InetAddress[] binds) {
        this.binds = binds;
    }


    ////////////////////////////////////////////////
    //	ControlPoint

    ////////////////////////////////////////////////
    //	ControlPoint
    ////////////////////////////////////////////////

    public void setControlPoint(ControlPoint ctrlPoint) {
        int nSockets = size();
        for (int n = 0; n < nSockets; n++) {
            SSDPSearchResponseSocket sock = getSSDPSearchResponseSocket(n);
            sock.setControlPoint(ctrlPoint);
        }
    }

    ////////////////////////////////////////////////
    //	get
    ////////////////////////////////////////////////

    public SSDPSearchResponseSocket getSSDPSearchResponseSocket(int n) {
        return get(n);
    }

    ////////////////////////////////////////////////
    //	Methods
    ////////////////////////////////////////////////

    public boolean open(int port) {
        InetAddress[] binds = this.binds;
        String[] bindAddresses;
        if (binds != null) {
            bindAddresses = new String[binds.length];
            for (int i = 0; i < binds.length; i++) {
                bindAddresses[i] = binds[i].getHostAddress();
            }
        } else {
            int nHostAddrs = HostInterface.getNHostAddresses();
            bindAddresses = new String[nHostAddrs];
            for (int n = 0; n < nHostAddrs; n++) {
                bindAddresses[n] = HostInterface.getHostAddress(n);
            }
        }
        try {
            for (String bindAddress : bindAddresses) {
                SSDPSearchResponseSocket socket = new SSDPSearchResponseSocket(bindAddress, port);
                add(socket);
            }
        } catch (Exception e) {
            stop();
            close();
            clear();
            return false;
        }
        return true;
    }

    public boolean open() {
        return open(SSDP.PORT);
    }

    public void close() {
        int nSockets = size();
        for (int n = 0; n < nSockets; n++) {
            SSDPSearchResponseSocket sock = getSSDPSearchResponseSocket(n);
            sock.close();
        }
        clear();
    }

    ////////////////////////////////////////////////
    //	Methods
    ////////////////////////////////////////////////

    public void start() {
        int nSockets = size();
        for (int n = 0; n < nSockets; n++) {
            SSDPSearchResponseSocket sock = getSSDPSearchResponseSocket(n);
            sock.start();
        }
    }

    public void stop() {
        int nSockets = size();
        for (int n = 0; n < nSockets; n++) {
            SSDPSearchResponseSocket sock = getSSDPSearchResponseSocket(n);
            sock.stop();
        }
    }

    ////////////////////////////////////////////////
    //	Methods
    ////////////////////////////////////////////////

    public boolean post(SSDPSearchRequest req) {
        boolean ret = true;
        int nSockets = size();
        for (int n = 0; n < nSockets; n++) {
            SSDPSearchResponseSocket sock = getSSDPSearchResponseSocket(n);
            String bindAddr = sock.getLocalAddress();
            req.setLocalAddress(bindAddr);
            String ssdpAddr = SSDP.ADDRESS;
            if (HostInterface.isIPv6Address(bindAddr))
                ssdpAddr = SSDP.getIPv6Address();
            //sock.joinGroup(ssdpAddr, SSDP.PORT, bindAddr);
            if (!sock.post(ssdpAddr, SSDP.PORT, req))
                ret = false;
            //sock.leaveGroup(ssdpAddr, SSDP.PORT, bindAddr);
        }
        return ret;
    }

}

