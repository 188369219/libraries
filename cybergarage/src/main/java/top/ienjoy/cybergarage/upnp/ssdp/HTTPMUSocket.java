package top.ienjoy.cybergarage.upnp.ssdp;

import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.io.IOException;

import top.ienjoy.cybergarage.http.HTTPRequest;
import top.ienjoy.cybergarage.upnp.UPnP;
import top.ienjoy.cybergarage.util.Debug;

// Dummy Class for Android m3-rc37a
// import top.ienjoy.cybergarage.android.MulticastSocket;

@SuppressWarnings("unused")
public class HTTPMUSocket {
    ////////////////////////////////////////////////
    //	Member
    ////////////////////////////////////////////////

    private InetSocketAddress ssdpMultiGroup = null;
    private MulticastSocket ssdpMultiSock = null;
    private NetworkInterface ssdpMultiIf = null;

    ////////////////////////////////////////////////
    //	Constructor
    ////////////////////////////////////////////////

    public HTTPMUSocket() {
    }

    public HTTPMUSocket(String addr, int port, String bindAddr) {
        open(addr, port, bindAddr);
    }

    protected void finalize() {
        close();
    }

    ////////////////////////////////////////////////
    //	bindAddr
    ////////////////////////////////////////////////

    public String getLocalAddress() {
        if (ssdpMultiGroup == null || ssdpMultiIf == null)
            return "";
        InetAddress mcastAddr = ssdpMultiGroup.getAddress();
        Enumeration<InetAddress> addrs = ssdpMultiIf.getInetAddresses();
        while (addrs.hasMoreElements()) {
            InetAddress addr = addrs.nextElement();
            if (mcastAddr instanceof Inet6Address && addr instanceof Inet6Address)
                return addr.getHostAddress();
            if (mcastAddr instanceof Inet4Address && addr instanceof Inet4Address)
                return addr.getHostAddress();
        }
        return "";
    }

    /**
     * @return the destination port for multicast packet
     * @since 1.8
     */
    public int getMulticastPort() {
        return ssdpMultiGroup.getPort();
    }

    /**
     * @return the source port for multicast packet
     * @since 1.8
     */
    public int getLocalPort() {
        return ssdpMultiSock.getLocalPort();
    }

    /**
     * @return the opened {@link MulticastSocket}
     * @since 1.8
     */
    public MulticastSocket getSocket() {
        return ssdpMultiSock;
    }


    ////////////////////////////////////////////////
    //	MulticastAddr
    ////////////////////////////////////////////////

    public InetAddress getMulticastInetAddress() {
        return ssdpMultiGroup.getAddress();
    }

    public String getMulticastAddress() {
        return getMulticastInetAddress().getHostAddress();
    }

    /**
     * @param addr     {@link String} rappresenting the multicast hostname to join into.
     * @param port     int rappresenting the port to be use poth as source and destination
     * @param bindAddr {@link InetAddress} which identify the hostname of the interface
     *                 to use for sending and recieving multicast packet
     */
    public boolean open(String addr, int port, InetAddress bindAddr) {
        try {
            ssdpMultiSock = new MulticastSocket(null);
            ssdpMultiSock.setReuseAddress(true);
            InetSocketAddress bindSockAddr = new InetSocketAddress(port);
            ssdpMultiSock.bind(bindSockAddr);
            ssdpMultiGroup = new InetSocketAddress(InetAddress.getByName(addr), port);
            ssdpMultiIf = NetworkInterface.getByInetAddress(bindAddr);
            ssdpMultiSock.joinGroup(ssdpMultiGroup, ssdpMultiIf);
        } catch (Exception e) {
            Debug.warning(e);
            return false;
        }

        return true;
    }

    public boolean open(String addr, int port, String bindAddr) {
        try {
            return open(addr, port, InetAddress.getByName(bindAddr));
        } catch (Exception e) {
            Debug.warning(e);
            return false;
        }
    }

    public boolean close() {
        if (ssdpMultiSock == null)
            return true;

        try {
            ssdpMultiSock.leaveGroup(ssdpMultiGroup, ssdpMultiIf);
            ssdpMultiSock.close();
            ssdpMultiSock = null;
        } catch (Exception e) {
            //Debug.warning(e);
            return false;
        }

        return true;
    }

    ////////////////////////////////////////////////
    //	send
    ////////////////////////////////////////////////

    public boolean send(String msg, String bindAddr, int bindPort) {
        try {
            MulticastSocket msock;
            if ((bindAddr) != null && (0 < bindPort)) {
                msock = new MulticastSocket(null);
                msock.bind(new InetSocketAddress(bindAddr, bindPort));
            } else {
                msock = new MulticastSocket();
            }
            DatagramPacket dgmPacket = new DatagramPacket(msg.getBytes(), msg.length(), ssdpMultiGroup);
            // Thnaks for Theo Beisch (11/09/04)
            msock.setTimeToLive(UPnP.getTimeToLive());
            msock.send(dgmPacket);
            msock.close();
        } catch (Exception e) {
            Debug.warning(e);
            return false;
        }
        return true;
    }

    public boolean send(String msg) {
        return send(msg, null, -1);
    }

    ////////////////////////////////////////////////
    //	post (HTTPRequest)
    ////////////////////////////////////////////////

    public boolean post(HTTPRequest req, String bindAddr, int bindPort) {
        return send(req.toString(), bindAddr, bindPort);
    }

    public boolean post(HTTPRequest req) {
        return send(req.toString(), null, -1);
    }

    ////////////////////////////////////////////////
    //	reveive
    ////////////////////////////////////////////////

    public SSDPPacket receive() throws IOException {
        byte[] ssdvRecvBuf = new byte[SSDP.RECV_MESSAGE_BUFSIZE];
        SSDPPacket recvPacket = new SSDPPacket(ssdvRecvBuf, ssdvRecvBuf.length);
        recvPacket.setLocalAddress(getLocalAddress());

        // Thanks for Kazuyuki Shudo (08/23/07)
        // Thanks for Stephan Mehlhase (2010-10-26)
        if (ssdpMultiSock != null)
            ssdpMultiSock.receive(recvPacket.getDatagramPacket()); // throws IOException
        else
            throw new IOException("Multicast socket has already been closed.");

        recvPacket.setTimeStamp(System.currentTimeMillis());

        return recvPacket;
    }
}
