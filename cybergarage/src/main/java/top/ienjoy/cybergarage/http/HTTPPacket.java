package top.ienjoy.cybergarage.http;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.Vector;

import top.ienjoy.cybergarage.net.HostInterface;
import top.ienjoy.cybergarage.util.Debug;
import top.ienjoy.cybergarage.util.StringUtil;

@SuppressWarnings("unused")
public class HTTPPacket {
    ////////////////////////////////////////////////
    //	Constructor
    ////////////////////////////////////////////////

    public HTTPPacket() {
        setVersion(HTTP.VERSION);
        setContentInputStream(null);
    }

    public HTTPPacket(InputStream in) {
        setVersion(HTTP.VERSION);
        set(in);
        setContentInputStream(null);
    }

    ////////////////////////////////////////////////
    //	init
    ////////////////////////////////////////////////

    public void init() {
        setFirstLine("");
        clearHeaders();
        setContent(new byte[0], false);
        setContentInputStream(null);
    }

    ////////////////////////////////////////////////
    //	Version
    ////////////////////////////////////////////////

    private String version;

    public void setVersion(String ver) {
        version = ver;
    }

    public String getVersion() {
        return version;
    }

    ////////////////////////////////////////////////
    //	set
    ////////////////////////////////////////////////

    private String readLine(BufferedInputStream in) {
        ByteArrayOutputStream lineBuf = new ByteArrayOutputStream();
        byte[] readBuf = new byte[1];

        try {
            int readLen = in.read(readBuf);
            while (0 < readLen) {
                if (readBuf[0] == HTTP.LF)
                    break;
                if (readBuf[0] != HTTP.CR)
                    lineBuf.write(readBuf[0]);
                readLen = in.read(readBuf);
            }
        } catch (InterruptedIOException e) {
            //Ignoring warning because it's a way to break the HTTP connecttion
        } catch (IOException e) {
            Debug.warning(e);
        }

        return lineBuf.toString();
    }

    protected boolean set(InputStream in, boolean onlyHeaders) {
        try {
            BufferedInputStream reader = new BufferedInputStream(in);

            String firstLine = readLine(reader);
            if (firstLine.length() == 0)
                return false;
            setFirstLine(firstLine);

            // Thanks for Giordano Sassaroli <sassarol@cefriel.it> (09/03/03)
            HTTPStatus httpStatus = new HTTPStatus(firstLine);
            int statCode = httpStatus.getStatusCode();
            if (statCode == HTTPStatus.CONTINUE) {
                //ad hoc code for managing iis non-standard behaviour
                //iis sends 100 code response and a 200 code response in the same
                //stream, so the code should check the presence of the actual
                //response in the stream.
                //skip all header lines
                String headerLine = readLine(reader);
                while (0 < headerLine.length()) {
                    HTTPHeader header = new HTTPHeader(headerLine);
                    if (header.hasName())
                        setHeader(header);
                    headerLine = readLine(reader);
                }
                //look forward another first line
                String actualFirstLine = readLine(reader);
                if (0 < actualFirstLine.length()) {
                    //this is the actual first line
                    setFirstLine(actualFirstLine);
                } else {
                    return true;
                }
            }

            String headerLine = readLine(reader);
            while (0 < headerLine.length()) {
                HTTPHeader header = new HTTPHeader(headerLine);
                if (header.hasName())
                    setHeader(header);
                headerLine = readLine(reader);
            }

            if (onlyHeaders) {
                setContent("", false);
                return true;
            }

            boolean isChunkedRequest = isChunked();

            long contentLen = 0;
            if (isChunkedRequest) {
                try {
                    String chunkSizeLine = readLine(reader);
                    // Thanks for Lee Peik Feng <pflee@users.sourceforge.net> (07/07/05)
                    //contentLen = Long.parseLong(new String(chunkSizeLine.getBytes(), 0, chunkSizeLine.length()-2), 16);
                    contentLen = Long.parseLong(chunkSizeLine.trim(), 16);
                } catch (Exception ignored) {
                }
            } else
                contentLen = getContentLength();

            ByteArrayOutputStream contentBuf = new ByteArrayOutputStream();

            while (0 < contentLen) {
                int chunkSize = HTTP.getChunkSize();

                /* Thanks for Stephan Mehlhase (2010-10-26) */
                byte[] readBuf = new byte[(int) (contentLen > chunkSize ? chunkSize : contentLen)];

                long readCnt = 0;
                while (readCnt < contentLen) {
                    try {
                        // Thanks for Mark Retallack (02/02/05)
                        long bufReadLen = contentLen - readCnt;
                        if (chunkSize < bufReadLen)
                            bufReadLen = chunkSize;
                        int readLen = reader.read(readBuf, 0, (int) bufReadLen);
                        if (readLen < 0)
                            break;
                        contentBuf.write(readBuf, 0, readLen);
                        readCnt += readLen;
                    } catch (Exception e) {
                        Debug.warning(e);
                        break;
                    }
                }
                if (isChunkedRequest) {
                    // skip CRLF
                    long skipLen = 0;
                    do {
                        long skipCnt = reader.skip(HTTP.CRLF.length() - skipLen);
                        skipLen += skipCnt;
                    } while (skipLen < HTTP.CRLF.length());
                    // read next chunk size
                    try {
                        String chunkSizeLine = readLine(reader);
                        // Thanks for Lee Peik Feng <pflee@users.sourceforge.net> (07/07/05)
                        contentLen = Long.parseLong(new String(chunkSizeLine.getBytes(), 0, chunkSizeLine.length() - 2), 16);
                    } catch (Exception e) {
                        contentLen = 0;
                    }
                } else
                    contentLen = 0;
            }

            setContent(contentBuf.toByteArray(), false);
        } catch (Exception e) {
            Debug.warning(e);
            return false;
        }

        return true;
    }

    protected boolean set(InputStream in) {
        return set(in, false);
    }

    protected boolean set(HTTPSocket httpSock) {
        return set(httpSock.getInputStream());
    }

    protected void set(HTTPPacket httpPacket) {
        setFirstLine(httpPacket.getFirstLine());

        clearHeaders();
        int nHeaders = httpPacket.getNHeaders();
        for (int n = 0; n < nHeaders; n++) {
            HTTPHeader header = httpPacket.getHeader(n);
            addHeader(header);
        }
        setContent(httpPacket.getContent());
    }

    ////////////////////////////////////////////////
    //	read
    ////////////////////////////////////////////////

    public boolean read(HTTPSocket httpSock) {
        init();
        return set(httpSock);
    }

    ////////////////////////////////////////////////
    //	String
    ////////////////////////////////////////////////

    private String firstLine = "";

    private void setFirstLine(String value) {
        firstLine = value;
    }

    protected String getFirstLine() {
        return firstLine;
    }

    protected String getFirstLineToken(int num) {
        StringTokenizer st = new StringTokenizer(firstLine, HTTP.REQEST_LINE_DELIM);
        String lastToken = "";
        for (int n = 0; n <= num; n++) {
            if (!st.hasMoreTokens())
                return "";
            lastToken = st.nextToken();
        }
        return lastToken;
    }

    public boolean hasFirstLine() {
        return 0 < firstLine.length();
    }

    ////////////////////////////////////////////////
    //	Header
    ////////////////////////////////////////////////

    private Vector<HTTPHeader> httpHeaderList = new Vector<>();

    public int getNHeaders() {
        return httpHeaderList.size();
    }

    public void addHeader(HTTPHeader header) {
        httpHeaderList.add(header);
    }

    public void addHeader(String name, String value) {
        HTTPHeader header = new HTTPHeader(name, value);
        httpHeaderList.add(header);
    }

    public HTTPHeader getHeader(int n) {
        return httpHeaderList.get(n);
    }

    public HTTPHeader getHeader(String name) {
        int nHeaders = getNHeaders();
        for (int n = 0; n < nHeaders; n++) {
            HTTPHeader header = getHeader(n);
            String headerName = header.getName();
            if (headerName.equalsIgnoreCase(name))
                return header;
        }
        return null;
    }

    public void clearHeaders() {
        httpHeaderList.clear();
        httpHeaderList = new Vector<>();
    }

    public boolean hasHeader(String name) {
        return getHeader(name) != null;
    }

    public void setHeader(String name, String value) {
        HTTPHeader header = getHeader(name);
        if (header != null) {
            header.setValue(value);
            return;
        }
        addHeader(name, value);
    }

    public void setHeader(String name, int value) {
        setHeader(name, Integer.toString(value));
    }

    public void setHeader(String name, long value) {
        setHeader(name, Long.toString(value));
    }

    public void setHeader(HTTPHeader header) {
        setHeader(header.getName(), header.getValue());
    }

    public String getHeaderValue(String name) {
        HTTPHeader header = getHeader(name);
        if (header == null)
            return "";
        return header.getValue();
    }

    ////////////////////////////////////////////////
    // set*Value
    ////////////////////////////////////////////////

    public void setStringHeader(String name, String value, String startWidth, String endWidth) {
        String headerValue = value;
        if (!headerValue.startsWith(startWidth))
            headerValue = startWidth + headerValue;
        if (!headerValue.endsWith(endWidth))
            headerValue = headerValue + endWidth;
        setHeader(name, headerValue);
    }

    public void setStringHeader(String name, String value) {
        setStringHeader(name, value, "\"", "\"");
    }

    public String getStringHeaderValue(String name, String startWidth, String endWidth) {
        String headerValue = getHeaderValue(name);
        if (headerValue.startsWith(startWidth))
            headerValue = headerValue.substring(1);
        if (headerValue.endsWith(endWidth))
            headerValue = headerValue.substring(0, headerValue.length() - 1);
        return headerValue;
    }

    public String getStringHeaderValue(String name) {
        return getStringHeaderValue(name, "\"", "\"");
    }

    public void setIntegerHeader(String name, int value) {
        setHeader(name, Integer.toString(value));
    }

    public void setLongHeader(String name, long value) {
        setHeader(name, Long.toString(value));
    }

    public int getIntegerHeaderValue(String name) {
        HTTPHeader header = getHeader(name);
        if (header == null)
            return 0;
        return StringUtil.toInteger(header.getValue());
    }

    public long getLongHeaderValue(String name) {
        HTTPHeader header = getHeader(name);
        if (header == null)
            return 0;
        return StringUtil.toLong(header.getValue());
    }

    ////////////////////////////////////////////////
    //	getHeader
    ////////////////////////////////////////////////

    public String getHeaderString() {
        StringBuilder str = new StringBuilder();

        int nHeaders = getNHeaders();
        for (int n = 0; n < nHeaders; n++) {
            HTTPHeader header = getHeader(n);
            str.append(header.getName()).append(": ").append(header.getValue()).append(HTTP.CRLF);
        }

        return str.toString();
    }

    ////////////////////////////////////////////////
    //	Contents
    ////////////////////////////////////////////////

    private byte[] content = new byte[0];

    public void setContent(byte[] data, boolean updateWithContentLength) {
        content = data;
        if (updateWithContentLength)
            setContentLength(data.length);
    }

    public void setContent(byte[] data) {
        setContent(data, true);
    }

    public void setContent(String data, boolean updateWithContentLength) {
        setContent(data.getBytes(), updateWithContentLength);
    }

    public void setContent(String data) {
        setContent(data, true);
    }

    public byte[] getContent() {
        return content;
    }

    public String getContentString() {
        String charSet = getCharSet();
        if (charSet == null || charSet.length() == 0)
            return new String(content);
        try {
            return new String(content, charSet);
        } catch (Exception e) {
            Debug.warning(e);
        }
        return new String(content);
    }

    public boolean hasContent() {
        return content.length > 0;
    }

    ////////////////////////////////////////////////
    //	Contents (InputStream)
    ////////////////////////////////////////////////

    private InputStream contentInput = null;

    public void setContentInputStream(InputStream in) {
        contentInput = in;
    }

    public InputStream getContentInputStream() {
        return contentInput;
    }

    public boolean hasContentInputStream() {
        return contentInput != null;
    }

    ////////////////////////////////////////////////
    //	ContentType
    ////////////////////////////////////////////////

    public void setContentType(String type) {
        setHeader(HTTP.CONTENT_TYPE, type);
    }

    public String getContentType() {
        return getHeaderValue(HTTP.CONTENT_TYPE);
    }

    ////////////////////////////////////////////////
    //	ContentLanguage
    ////////////////////////////////////////////////

    public void setContentLanguage(String code) {
        setHeader(HTTP.CONTENT_LANGUAGE, code);
    }

    public String getContentLanguage() {
        return getHeaderValue(HTTP.CONTENT_LANGUAGE);
    }

    ////////////////////////////////////////////////
    //	Charset
    ////////////////////////////////////////////////

    public String getCharSet() {
        String contentType = getContentType();
        if (contentType == null)
            return "";
        contentType = contentType.toLowerCase();
        int charSetIdx = contentType.indexOf(HTTP.CHARSET);
        if (charSetIdx < 0)
            return "";
        int charSetEndIdx = charSetIdx + HTTP.CHARSET.length() + 1;
        String charSet = new String(contentType.getBytes(), charSetEndIdx, (contentType.length() - charSetEndIdx));
//		if (charSet.length() < 0) return "";
        if (charSet.charAt(0) == '\"')
            charSet = charSet.substring(1, (charSet.length() - 1));
//		if (charSet.length() < 0) return "";
        if (charSet.charAt((charSet.length() - 1)) == '\"')
            charSet = charSet.substring(0, (charSet.length() - 1));
        return charSet;
    }

    ////////////////////////////////////////////////
    //	ContentLength
    ////////////////////////////////////////////////

    public void setContentLength(long len) {
        setLongHeader(HTTP.CONTENT_LENGTH, len);
    }

    public long getContentLength() {
        return getLongHeaderValue(HTTP.CONTENT_LENGTH);
    }

    ////////////////////////////////////////////////
    //	Connection
    ////////////////////////////////////////////////

    public boolean hasConnection() {
        return hasHeader(HTTP.CONNECTION);
    }

    public void setConnection(String value) {
        setHeader(HTTP.CONNECTION, value);
    }

    public String getConnection() {
        return getHeaderValue(HTTP.CONNECTION);
    }

    public boolean isCloseConnection() {
        if (!hasConnection())
            return false;
        String connection = getConnection();
        if (connection == null)
            return false;
        return connection.equalsIgnoreCase(HTTP.CLOSE);
    }

    public boolean isKeepAliveConnection() {
        if (!hasConnection())
            return false;
        String connection = getConnection();
        if (connection == null)
            return false;
        return connection.equalsIgnoreCase(HTTP.KEEP_ALIVE);
    }

    ////////////////////////////////////////////////
    //	ContentRange
    ////////////////////////////////////////////////

    public boolean hasContentRange() {
        return (hasHeader(HTTP.CONTENT_RANGE) || hasHeader(HTTP.RANGE));
    }

    public void setContentRange(long firstPos, long lastPos, long length) {
        String rangeStr = "";
        rangeStr += HTTP.CONTENT_RANGE_BYTES + " ";
        rangeStr += firstPos + "-";
        rangeStr += lastPos + "/";
        rangeStr += ((0 < length) ? Long.toString(length) : "*");
        setHeader(HTTP.CONTENT_RANGE, rangeStr);
    }

    public long[] getContentRange() {
        long[] range = new long[3];
        if (!hasContentRange())
            return range;
        String rangeLine = getHeaderValue(HTTP.CONTENT_RANGE);
        // Thanks for Brent Hills (10/20/04)
        if (rangeLine.length() == 0)
            rangeLine = getHeaderValue(HTTP.RANGE);
        if (rangeLine.length() == 0)
            return range;
        // Thanks for Brent Hills (10/20/04)
        StringTokenizer strToken = new StringTokenizer(rangeLine, " =");
        // Skip bytes
        if (!strToken.hasMoreTokens())
            return range;
        String bytesStr = strToken.nextToken(" ");
        // Get first-byte-pos
        if (!strToken.hasMoreTokens())
            return range;
        String firstPosStr = strToken.nextToken(" -");
        try {
            range[0] = Long.parseLong(firstPosStr);
        } catch (NumberFormatException ignored) {
        }
        if (!strToken.hasMoreTokens())
            return range;
        String lastPosStr = strToken.nextToken("-/");
        try {
            range[1] = Long.parseLong(lastPosStr);
        } catch (NumberFormatException ignored) {
        }
        if (!strToken.hasMoreTokens())
            return range;
        String lengthStr = strToken.nextToken("/");
        try {
            range[2] = Long.parseLong(lengthStr);
        } catch (NumberFormatException ignored) {
        }
        return range;
    }

    public long getContentRangeFirstPosition() {
        long[] range = getContentRange();
        return range[0];
    }

    public long getContentRangeLastPosition() {
        long[] range = getContentRange();
        return range[1];
    }

    public long getContentRangeInstanceLength() {
        long[] range = getContentRange();
        return range[2];
    }

    ////////////////////////////////////////////////
    //	CacheControl
    ////////////////////////////////////////////////

    public void setCacheControl(String directive) {
        setHeader(HTTP.CACHE_CONTROL, directive);
    }

    public void setCacheControl(String directive, int value) {
        String strVal = directive + "=" + value;
        setHeader(HTTP.CACHE_CONTROL, strVal);
    }

    public void setCacheControl(int value) {
        setCacheControl(HTTP.MAX_AGE, value);
    }

    public String getCacheControl() {
        return getHeaderValue(HTTP.CACHE_CONTROL);
    }

    ////////////////////////////////////////////////
    //	Server
    ////////////////////////////////////////////////

    public void setServer(String name) {
        setHeader(HTTP.SERVER, name);
    }

    public String getServer() {
        return getHeaderValue(HTTP.SERVER);
    }

    ////////////////////////////////////////////////
    //	Host
    ////////////////////////////////////////////////

    public void setHost(String host, int port) {
        String hostAddr = host;
        if (HostInterface.isIPv6Address(host))
            hostAddr = "[" + host + "]";
        setHeader(HTTP.HOST, hostAddr + ":" + port);
    }

    public void setHost(String host) {
        String hostAddr = host;
        if (HostInterface.isIPv6Address(host))
            hostAddr = "[" + host + "]";
        setHeader(HTTP.HOST, hostAddr);
    }

    public String getHost() {
        return getHeaderValue(HTTP.HOST);
    }


    ////////////////////////////////////////////////
    //	Date
    ////////////////////////////////////////////////

    public void setDate(Calendar cal) {
        Date date = new Date(cal);
        setHeader(HTTP.DATE, date.getDateString());
    }

    public String getDate() {
        return getHeaderValue(HTTP.DATE);
    }

    ////////////////////////////////////////////////
    //	Connection
    ////////////////////////////////////////////////

    public boolean hasTransferEncoding() {
        return hasHeader(HTTP.TRANSFER_ENCODING);
    }

    public void setTransferEncoding(String value) {
        setHeader(HTTP.TRANSFER_ENCODING, value);
    }

    public String getTransferEncoding() {
        return getHeaderValue(HTTP.TRANSFER_ENCODING);
    }

    public boolean isChunked() {
        if (!hasTransferEncoding())
            return false;
        String transEnc = getTransferEncoding();
        if (transEnc == null)
            return false;
        return transEnc.equalsIgnoreCase(HTTP.CHUNKED);
    }
}

