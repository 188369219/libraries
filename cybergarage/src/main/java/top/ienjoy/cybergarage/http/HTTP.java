package top.ienjoy.cybergarage.http;

import java.net.URL;

@SuppressWarnings("unused")
public class HTTP {
    ////////////////////////////////////////////////
    // Constants
    ////////////////////////////////////////////////

    public static final String HOST = "HOST";

    public static final String VERSION = "1.1";
    public static final String VERSION_10 = "1.0";
    public static final String VERSION_11 = "1.1";

    public static final String CRLF = "\r\n";
    public static final byte CR = '\r';
    public static final byte LF = '\n';
    public static final String TAB = "\t";

    public static final String SOAP_ACTION = "SOAPACTION";

    public static final String M_SEARCH = "M-SEARCH";
    public static final String NOTIFY = "NOTIFY";
    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String HEAD = "HEAD";
    public static final String SUBSCRIBE = "SUBSCRIBE";
    public static final String UNSUBSCRIBE = "UNSUBSCRIBE";

    public static final String DATE = "Date";
    public static final String CACHE_CONTROL = "Cache-Control";
    public static final String NO_CACHE = "no-cache";
    public static final String MAX_AGE = "max-age";
    public static final String CONNECTION = "Connection";
    public static final String CLOSE = "close";
    public static final String KEEP_ALIVE = "Keep-Alive";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CHARSET = "charset";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_LANGUAGE = "Content-Language";
    public static final String CONTENT_RANGE = "Content-Range";
    public static final String CONTENT_RANGE_BYTES = "bytes";
    public static final String RANGE = "Range";
    public static final String TRANSFER_ENCODING = "Transfer-Encoding";
    public static final String CHUNKED = "Chunked";
    public static final String LOCATION = "Location";
    public static final String SERVER = "Server";

    public static final String ST = "ST";
    public static final String MX = "MX";
    public static final String MAN = "MAN";
    public static final String NT = "NT";
    public static final String NTS = "NTS";
    public static final String USN = "USN";
    public static final String EXT = "EXT";
    public static final String SID = "SID";
    public static final String SEQ = "SEQ";
    public final static String CALLBACK = "CALLBACK";
    public final static String TIMEOUT = "TIMEOUT";

    public final static String BOOTID_UPNP_ORG = "BOOTID.UPNP.ORG";

    // Thanks for Brent Hills (10/20/04)
    public final static String MYNAME = "MYNAME";

    public static final String REQEST_LINE_DELIM = " ";
    public static final String HEADER_LINE_DELIM = " :";
    public static final String STATUS_LINE_DELIM = " ";

    public static final int DEFAULT_PORT = 80;
    public static final int DEFAULT_CHUNK_SIZE = 512 * 1024;
    public static final int DEFAULT_TIMEOUT = 30;

    ////////////////////////////////////////////////
    // URL
    ////////////////////////////////////////////////

    public static boolean isAbsoluteURL(String urlStr) {
        try {
            new URL(urlStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getHost(String urlStr) {
        try {
            URL url = new URL(urlStr);
            return url.getHost();
        } catch (Exception e) {
            return "";
        }
    }

    public static int getPort(String urlStr) {
        try {
            URL url = new URL(urlStr);
            // Thanks for Giordano Sassaroli <sassarol@cefriel.it> (08/30/03)
            int port = url.getPort();
            if (port <= 0)
                port = DEFAULT_PORT;
            return port;
        } catch (Exception e) {
            return DEFAULT_PORT;
        }
    }

    public static String getRequestHostURL(String host, int port) {
        return "http://" + host + ":" + port;
    }

    public static String toRelativeURL(String urlStr, boolean withParam) {
        String uri = urlStr;
        if (!isAbsoluteURL(urlStr)) {
            if (0 < urlStr.length() && urlStr.charAt(0) != '/')
                uri = "/" + urlStr;
        } else {
            try {
                URL url = new URL(urlStr);
                uri = url.getPath();
                if (withParam) {
                    String queryStr = url.getQuery();
                    if (!queryStr.equals("")) {
                        uri += "?" + queryStr;
                    }
                }
                if (uri.endsWith("/"))
                    uri = uri.substring(0, uri.length() - 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return uri;
    }

    public static String toRelativeURL(String urlStr) {
        return toRelativeURL(urlStr, true);
    }

    public static String getAbsoluteURL(String baseURLStr, String relURlStr) {
        try {
            URL baseURL = new URL(baseURLStr);
            return baseURL.getProtocol() + "://" +
                    baseURL.getHost() + ":" +
                    baseURL.getPort() +
                    toRelativeURL(relURlStr);
        } catch (Exception e) {
            return "";
        }
    }

    ////////////////////////////////////////////////
    // Chunk Size
    ////////////////////////////////////////////////

    private static int chunkSize = DEFAULT_CHUNK_SIZE;

    public static void setChunkSize(int size) {
        chunkSize = size;
    }

    public static int getChunkSize() {
        return chunkSize;
    }

}

