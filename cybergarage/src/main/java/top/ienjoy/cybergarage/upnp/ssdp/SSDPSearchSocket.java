package top.ienjoy.cybergarage.upnp.ssdp;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.io.IOException;

import top.ienjoy.cybergarage.net.*;
import top.ienjoy.cybergarage.util.*;

import top.ienjoy.cybergarage.upnp.device.*;

@SuppressWarnings("unused")
public class SSDPSearchSocket extends HTTPMUSocket implements Runnable {
    private boolean useIPv6Address;

    ////////////////////////////////////////////////
    //	Constructor
    ////////////////////////////////////////////////


    /**
     * @param bindAddr  The address to bind the service
     * @param port      The port used for accepting message
     * @param multicast The multicast address to use as destination
     * @since 1.8
     */
    public SSDPSearchSocket(String bindAddr, int port, String multicast) {
        open(bindAddr, multicast);
    }

    /**
     * @param bindAddr the binding address for senging multicast packet
     * @since 1.8
     */
    public SSDPSearchSocket(InetAddress bindAddr) {
        if (bindAddr.getAddress().length != 4) {
            this.open((Inet6Address) bindAddr);
        } else {
            this.open((Inet4Address) bindAddr);
        }
    }

    ////////////////////////////////////////////////
    //	Constructor
    ////////////////////////////////////////////////
    public boolean open(Inet4Address bindAddr) {
        useIPv6Address = false;
        return open(SSDP.ADDRESS, SSDP.PORT, bindAddr);
    }

    public boolean open(Inet6Address bindAddr) {
        useIPv6Address = true;
        return open(SSDP.getIPv6Address(), SSDP.PORT, bindAddr);
    }

    public boolean open(String bind, String multicast) {
        if ((HostInterface.isIPv6Address(bind)) && (HostInterface.isIPv6Address(multicast))) {
            useIPv6Address = true;
        } else if (HostInterface.isIPv4Address(bind) && (HostInterface.isIPv4Address(multicast))) {
            useIPv6Address = false;
        } else {
            throw new IllegalArgumentException("Cannot open a UDP Socket for IPv6 address on IPv4 interface or viceversa");
        }
        return open(multicast, SSDP.PORT, bind);
    }

    /**
     * @param bindAddr the hostname of the interface to use for senfing multicast packet
     * @return true if and only if it open the socket
     */
    public boolean open(String bindAddr) {
        String addr = SSDP.ADDRESS;
        useIPv6Address = false;
        if (HostInterface.isIPv6Address(bindAddr)) {
            addr = SSDP.getIPv6Address();
            useIPv6Address = true;
        }
        return open(addr, SSDP.PORT, bindAddr);
    }

    ////////////////////////////////////////////////
    //	deviceSearch
    ////////////////////////////////////////////////

    private ListenerList deviceSearchListenerList = new ListenerList();

    public void addSearchListener(SearchListener listener) {
        deviceSearchListenerList.add(listener);
    }

    public void removeSearchListener(SearchListener listener) {
        deviceSearchListenerList.remove(listener);
    }

    public void performSearchListener(SSDPPacket ssdpPacket) {
        int listenerSize = deviceSearchListenerList.size();
        for (int n = 0; n < listenerSize; n++) {
            SearchListener listener = (SearchListener) deviceSearchListenerList.get(n);
            listener.deviceSearchReceived(ssdpPacket);
        }
    }

    ////////////////////////////////////////////////
    //	run
    ////////////////////////////////////////////////

    private Thread deviceSearchThread = null;

    public void run() {
        Thread thisThread = Thread.currentThread();

        while (deviceSearchThread == thisThread) {
            Thread.yield();

            // Thanks for Kazuyuki Shudo (08/23/07)
            SSDPPacket packet;
            try {
                packet = receive();
            } catch (IOException e) {
                break;
            }

            // Thanks for Mikael Hakman (04/20/05)
            if (packet == null)
                continue;

            if (packet.isDiscover())
                performSearchListener(packet);
        }
    }

    public void start() {
        StringBuilder name = new StringBuilder("Cyber.SSDPSearchSocket/");
        String localAddr = this.getLocalAddress();
        // localAddr is null on Android m3-rc37a (01/30/08)
        if (localAddr != null && 0 < localAddr.length()) {
            name.append(this.getLocalAddress()).append(':');
            name.append(this.getLocalPort()).append(" -> ");
            name.append(this.getMulticastAddress()).append(':');
            name.append(this.getMulticastPort());
        }
        deviceSearchThread = new Thread(this, name.toString());
        deviceSearchThread.start();
    }

    public void stop() {
        // Thanks for Mikael Hakman (04/20/05)
        close();

        deviceSearchThread = null;
    }
}

