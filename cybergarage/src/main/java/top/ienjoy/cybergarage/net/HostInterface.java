package top.ienjoy.cybergarage.net;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import top.ienjoy.cybergarage.util.Debug;

@SuppressWarnings("unused")
public class HostInterface {
    ////////////////////////////////////////////////
    //	Constants
    ////////////////////////////////////////////////

    public static boolean USE_LOOPBACK_ADDR = false;
    public static boolean USE_ONLY_IPV4_ADDR = false;
    public static boolean USE_ONLY_IPV6_ADDR = false;

    ////////////////////////////////////////////////
    //	Network Interfaces
    ////////////////////////////////////////////////

    private static String ifAddress = "";
    public final static int IPV4_BITMASK = 0x0001;
    public final static int IPV6_BITMASK = 0x0010;
    public final static int LOCAL_BITMASK = 0x0100;

    public static void setInterface(String ifaddr) {
        ifAddress = ifaddr;
    }

    public static String getInterface() {
        return ifAddress;
    }

    private static boolean hasAssignedInterface() {
        return 0 < ifAddress.length();
    }

    ////////////////////////////////////////////////
    //	Network Interfaces
    ////////////////////////////////////////////////

    // Thanks for Theo Beisch (10/27/04)

    private static boolean isUsableAddress(InetAddress addr) {
        if (!USE_LOOPBACK_ADDR) {
            if (addr.isLoopbackAddress())
                return false;
        }
        if (USE_ONLY_IPV4_ADDR) {
            if (addr instanceof Inet6Address)
                return false;
        }
        if (USE_ONLY_IPV6_ADDR) {
            return !(addr instanceof Inet4Address);
        }
        return true;
    }

    public static int getNHostAddresses() {
        if (hasAssignedInterface())
            return 1;

        int nHostAddrs = 0;
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                Enumeration<InetAddress> addrs = ni.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress addr = addrs.nextElement();
                    if (!isUsableAddress(addr))
                        continue;
                    nHostAddrs++;
                }
            }
        } catch (Exception e) {
            Debug.warning(e);
        }
        return nHostAddrs;
    }

    /**
     * @author Stefano "Kismet" Lenzi &lt;kismet.sl@gmail.com&gt;
     * @since 1.8.0
     */
    public static InetAddress[] getInetAddress(int ipfilter, String[] interfaces) {
        Enumeration<NetworkInterface> nis;
        if (interfaces != null) {
            Vector<NetworkInterface> iflist = new Vector<>();
            for (String anInterface : interfaces) {
                NetworkInterface ni;
                try {
                    ni = NetworkInterface.getByName(anInterface);
                } catch (SocketException e) {
                    continue;
                }
                if (ni != null) iflist.add(ni);

            }
            nis = iflist.elements();
        } else {
            try {
                nis = NetworkInterface.getNetworkInterfaces();
            } catch (SocketException e) {
                return null;
            }
        }
        ArrayList<InetAddress> addresses = new ArrayList<>();
        while (nis.hasMoreElements()) {
            NetworkInterface ni = nis.nextElement();
            Enumeration<InetAddress> addrs = ni.getInetAddresses();
            while (addrs.hasMoreElements()) {
                InetAddress addr = addrs.nextElement();
                if (((ipfilter & LOCAL_BITMASK) == 0) && addr.isLoopbackAddress())
                    continue;

                if (((ipfilter & IPV4_BITMASK) != 0) && addr instanceof Inet4Address) {
                    addresses.add(addr);
                } else if (((ipfilter & IPV6_BITMASK) != 0) && addr != null) {
                    addresses.add(addr);
                }
            }
        }
        return addresses.toArray(new InetAddress[]{});
    }


    public static String getHostAddress(int n) {
        if (hasAssignedInterface())
            return getInterface();

        int hostAddrCnt = 0;
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                Enumeration<InetAddress> addrs = ni.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress addr = addrs.nextElement();
                    if (!isUsableAddress(addr))
                        continue;
                    if (hostAddrCnt < n) {
                        hostAddrCnt++;
                        continue;
                    }

                    return addr.getHostAddress();
                }
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    ////////////////////////////////////////////////
    //	isIPv?Address
    ////////////////////////////////////////////////

    public static boolean isIPv6Address(String host) {
        try {
            InetAddress addr = InetAddress.getByName(host);
            return addr instanceof Inet6Address;
        } catch (Exception ignored) {
        }
        return false;
    }

    public static boolean isIPv4Address(String host) {
        try {
            InetAddress addr = InetAddress.getByName(host);
            return addr instanceof Inet4Address;
        } catch (Exception ignored) {
        }
        return false;
    }

    ////////////////////////////////////////////////
    //	hasIPv?Interfaces
    ////////////////////////////////////////////////

    public static boolean hasIPv4Addresses() {
        int addrCnt = getNHostAddresses();
        for (int n = 0; n < addrCnt; n++) {
            String addr = getHostAddress(n);
            if (isIPv4Address(addr))
                return true;
        }
        return false;
    }

    public static boolean hasIPv6Addresses() {
        int addrCnt = getNHostAddresses();
        for (int n = 0; n < addrCnt; n++) {
            String addr = getHostAddress(n);
            if (isIPv6Address(addr))
                return true;
        }
        return false;
    }

    ////////////////////////////////////////////////
    //	hasIPv?Interfaces
    ////////////////////////////////////////////////

    public static String getIPv4Address() {
        int addrCnt = getNHostAddresses();
        for (int n = 0; n < addrCnt; n++) {
            String addr = getHostAddress(n);
            if (isIPv4Address(addr))
                return addr;
        }
        return "";
    }

    public static String getIPv6Address() {
        int addrCnt = getNHostAddresses();
        for (int n = 0; n < addrCnt; n++) {
            String addr = getHostAddress(n);
            if (isIPv6Address(addr))
                return addr;
        }
        return "";
    }

    ////////////////////////////////////////////////
    //	getHostURL
    ////////////////////////////////////////////////

    public static String getHostURL(String host, int port, String uri) {
        String hostAddr = host;
        if (isIPv6Address(host))
            hostAddr = "[" + host + "]";
        return "http://" + hostAddr + ":" + port + uri;
    }

}
