package top.ienjoy.cybergarage.upnp.ssdp;

import java.net.InetAddress;
import java.util.Vector;

import top.ienjoy.cybergarage.net.HostInterface;
import top.ienjoy.cybergarage.upnp.device.SearchListener;

public class SSDPSearchSocketList extends Vector<SSDPSearchSocket> {
    ////////////////////////////////////////////////
    //	Constructor
    ////////////////////////////////////////////////

    private InetAddress[] binds = null;
    private String multicastIPv4 = SSDP.ADDRESS;
    private String multicastIPv6 = SSDP.getIPv6Address();
    private int port = SSDP.PORT;

    public SSDPSearchSocketList() {
    }

    /**
     * @param binds The IP address that we will used for bindind the service
     */
    public SSDPSearchSocketList(InetAddress[] binds) {
        this.binds = binds;
    }

    /**
     * @param binds         The IP address that we will used for bindind the service
     * @param port          The port that we will used for bindind the service
     * @param multicastIPv4 The IPv4 address that we will used for multicast comunication
     * @param multicastIPv6 The IPv6 address that we will used for multicast comunication
     * @since 1.8
     */
    public SSDPSearchSocketList(InetAddress[] binds, int port, String multicastIPv4, String multicastIPv6) {
        this.binds = binds;
        this.port = port;
        this.multicastIPv4 = multicastIPv4;
        this.multicastIPv6 = multicastIPv6;
    }

    ////////////////////////////////////////////////
    //	Methods
    ////////////////////////////////////////////////

    public SSDPSearchSocket getSSDPSearchSocket(int n) {
        return get(n);
    }

    public void addSearchListener(SearchListener listener) {
        int nServers = size();
        for (int n = 0; n < nServers; n++) {
            SSDPSearchSocket sock = getSSDPSearchSocket(n);
            sock.addSearchListener(listener);
        }
    }

    ////////////////////////////////////////////////
    //	Methods
    ////////////////////////////////////////////////

    public boolean open() {
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

        for (String bindAddress : bindAddresses) {
            if (bindAddress != null) {
                SSDPSearchSocket ssdpSearchSocket;
                if (HostInterface.isIPv6Address(bindAddress))
                    ssdpSearchSocket = new SSDPSearchSocket(bindAddress, port, multicastIPv6);
                else
                    ssdpSearchSocket = new SSDPSearchSocket(bindAddress, port, multicastIPv4);
                add(ssdpSearchSocket);
            }
        }
        return true;
    }

    public void close() {
        int nSockets = size();
        for (int n = 0; n < nSockets; n++) {
            SSDPSearchSocket sock = getSSDPSearchSocket(n);
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
            SSDPSearchSocket sock = getSSDPSearchSocket(n);
            sock.start();
        }
    }

    public void stop() {
        int nSockets = size();
        for (int n = 0; n < nSockets; n++) {
            SSDPSearchSocket sock = getSSDPSearchSocket(n);
            sock.stop();
        }
    }

}

