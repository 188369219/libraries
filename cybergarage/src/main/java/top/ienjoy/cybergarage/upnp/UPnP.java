package top.ienjoy.cybergarage.upnp;

import top.ienjoy.cybergarage.net.HostInterface;
import top.ienjoy.cybergarage.soap.SOAP;
import top.ienjoy.cybergarage.upnp.ssdp.SSDP;
import top.ienjoy.cybergarage.xml.Parser;
import top.ienjoy.cybergarage.xml.parser.JaxpParser;

@SuppressWarnings("unused")
public class UPnP {
    ////////////////////////////////////////////////
    //	Constants
    ////////////////////////////////////////////////

    /**
     * Name of the system properties used to identifies the default XML Parser.<br>
     * The value of the properties MUST BE the fully qualified class name of<br>
     * XML Parser which CyberLink should use.
     */
    public final static String XML_CLASS_PROPERTTY = "cyberlink.upnp.xml.parser";

    public final static String NAME = "CyberLinkJava";
    public final static String VERSION = "3.0";

    public final static int SERVER_RETRY_COUNT = 100;
    public final static int DEFAULT_EXPIRED_DEVICE_EXTRA_TIME = 60;

    public static String getServerName() {
        String osName = System.getProperty("os.name");
        String osVer = System.getProperty("os.version");
        return osName + "/" + osVer + " UPnP/1.0 " + NAME + "/" + VERSION;
    }

    public final static String INMPR03 = "INMPR03";
    public final static String INMPR03_VERSION = "1.0";
    public final static int INMPR03_DISCOVERY_OVER_WIRELESS_COUNT = 4;

    public final static String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";

    public final static int CONFIGID_UPNP_ORG_MAX = 16777215;

    ////////////////////////////////////////////////
    //	Enable / Disable
    ////////////////////////////////////////////////

    public final static int USE_ONLY_IPV6_ADDR = 1;
    public final static int USE_LOOPBACK_ADDR = 2;
    public final static int USE_IPV6_LINK_LOCAL_SCOPE = 3;
    public final static int USE_IPV6_SUBNET_SCOPE = 4;
    public final static int USE_IPV6_ADMINISTRATIVE_SCOPE = 5;
    public final static int USE_IPV6_SITE_LOCAL_SCOPE = 6;
    public final static int USE_IPV6_GLOBAL_SCOPE = 7;
    public final static int USE_SSDP_SEARCHRESPONSE_MULTIPLE_INTERFACES = 8;
    public final static int USE_ONLY_IPV4_ADDR = 9;

    public static void setEnable(int value) {
        switch (value) {
            case USE_ONLY_IPV6_ADDR -> HostInterface.USE_ONLY_IPV6_ADDR = true;
            case USE_ONLY_IPV4_ADDR -> HostInterface.USE_ONLY_IPV4_ADDR = true;
            case USE_LOOPBACK_ADDR -> HostInterface.USE_LOOPBACK_ADDR = true;
            case USE_IPV6_LINK_LOCAL_SCOPE -> SSDP.setIPv6Address(SSDP.IPV6_LINK_LOCAL_ADDRESS);
            case USE_IPV6_SUBNET_SCOPE -> SSDP.setIPv6Address(SSDP.IPV6_SUBNET_ADDRESS);
            case USE_IPV6_ADMINISTRATIVE_SCOPE -> SSDP.setIPv6Address(SSDP.IPV6_ADMINISTRATIVE_ADDRESS);
            case USE_IPV6_SITE_LOCAL_SCOPE -> SSDP.setIPv6Address(SSDP.IPV6_SITE_LOCAL_ADDRESS);
            case USE_IPV6_GLOBAL_SCOPE -> SSDP.setIPv6Address(SSDP.IPV6_GLOBAL_ADDRESS);
        }
    }

    public static void setDisable(int value) {
        switch (value) {
            case USE_ONLY_IPV6_ADDR -> HostInterface.USE_ONLY_IPV6_ADDR = false;
            case USE_ONLY_IPV4_ADDR -> HostInterface.USE_ONLY_IPV4_ADDR = false;
            case USE_LOOPBACK_ADDR -> HostInterface.USE_LOOPBACK_ADDR = false;
        }
    }

    public static boolean isEnabled(int value) {
        switch (value) {
            case USE_ONLY_IPV6_ADDR -> {
                return HostInterface.USE_ONLY_IPV6_ADDR;
            }
            case USE_ONLY_IPV4_ADDR -> {
                return HostInterface.USE_ONLY_IPV4_ADDR;
            }
            case USE_LOOPBACK_ADDR -> {
                return HostInterface.USE_LOOPBACK_ADDR;
            }
        }
        return false;
    }

    ////////////////////////////////////////////////
    //	UUID
    ////////////////////////////////////////////////

    private static String toUUID(int seed) {
        String id = Integer.toString(seed & 0xFFFF, 16);
        int idLen = id.length();
        StringBuilder uuid = new StringBuilder();
        for (int n = 0; n < (4 - idLen); n++)
            uuid.append("0");
        uuid.append(id);
        return uuid.toString();
    }

    public static String createUUID() {
        long time1 = System.currentTimeMillis();
        long time2 = (long) ((double) System.currentTimeMillis() * Math.random());
        return toUUID((int) (time1 & 0xFFFF)) + "-" +
                toUUID((int) ((time1 >> 32) | 0xA000) & 0xFFFF) + "-" +
                toUUID((int) (time2 & 0xFFFF)) + "-" +
                toUUID((int) ((time2 >> 32) | 0xE000) & 0xFFFF);
    }

    ////////////////////////////////////////////////
    //	BootId
    ////////////////////////////////////////////////

    public static int createBootId() {
        return (int) (System.currentTimeMillis() / 1000L);
    }

    ////////////////////////////////////////////////
    //	ConfigId
    ////////////////////////////////////////////////

    public static int caluculateConfigId(String configXml) {
        if (configXml == null)
            return 0;
        int configId = 0;
        int configLen = configXml.length();
        for (int n = 0; n < configLen; n++) {
            configId += configXml.codePointAt(n);
            if (configId < CONFIGID_UPNP_ORG_MAX)
                continue;
            configId = configId % CONFIGID_UPNP_ORG_MAX;
        }
        return configId;
    }

    ////////////////////////////////////////////////
    // XML Parser
    ////////////////////////////////////////////////

    private static Parser xmlParser;

    public static void setXMLParser(Parser parser) {
        xmlParser = parser;
        SOAP.setXMLParser(parser);
    }

    public static Parser getXMLParser() {
        if (xmlParser == null) {
            xmlParser = new JaxpParser();
            SOAP.setXMLParser(xmlParser);
        }
        return xmlParser;
    }

    ////////////////////////////////////////////////
    //	TTL
    ////////////////////////////////////////////////

    public final static int DEFAULT_TTL = 4;

    private static int timeToLive = DEFAULT_TTL;

    public static void setTimeToLive(int value) {
        timeToLive = value;
    }

    public static int getTimeToLive() {
        return timeToLive;
    }

    ////////////////////////////////////////////////
    //	Initialize
    ////////////////////////////////////////////////

    static {
        ////////////////////////////
        // Interface Option
        ////////////////////////////

        //setXMLParser(new kXML2Parser());


        ////////////////////////////
        // TimeToLive
        ////////////////////////////

        setTimeToLive(DEFAULT_TTL);

        //Debug.on();
    }

    public static void initialize() {
        // Dummy function to call UPnP.static
    }

}
