package top.ienjoy.cybergarage.http;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Calendar;

public class HTTPSocket {
    ////////////////////////////////////////////////
    //	Constructor
    ////////////////////////////////////////////////

    public HTTPSocket(Socket socket) {
        setSocket(socket);
        open();
    }

    public HTTPSocket(HTTPSocket socket) {
        setSocket(socket.getSocket());
        setInputStream(socket.getInputStream());
        setOutputStream(socket.getOutputStream());
    }

    protected void finalize() {
        close();
    }

    ////////////////////////////////////////////////
    //	Socket
    ////////////////////////////////////////////////

    private Socket socket = null;

    private void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    ////////////////////////////////////////////////
    //	local address/port
    ////////////////////////////////////////////////

    public String getLocalAddress() {
        return getSocket().getLocalAddress().getHostAddress();
    }

    public int getLocalPort() {
        return getSocket().getLocalPort();
    }

    ////////////////////////////////////////////////
    //	in/out
    ////////////////////////////////////////////////

    private InputStream sockIn = null;
    private OutputStream sockOut = null;

    private void setInputStream(InputStream in) {
        sockIn = in;
    }

    public InputStream getInputStream() {
        return sockIn;
    }

    private void setOutputStream(OutputStream out) {
        sockOut = out;
    }

    private OutputStream getOutputStream() {
        return sockOut;
    }

    ////////////////////////////////////////////////
    //	open/close
    ////////////////////////////////////////////////

    public boolean open() {
        Socket sock = getSocket();
        try {
            sockIn = sock.getInputStream();
            sockOut = sock.getOutputStream();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean close() {
        try {
            if (sockIn != null)
                sockIn.close();
            if (sockOut != null)
                sockOut.close();
            getSocket().close();
        } catch (Exception e) {
            //Debug.warning(e);
            return false;
        }
        return true;
    }

    ////////////////////////////////////////////////
    //	post
    ////////////////////////////////////////////////

    private boolean post(HTTPResponse httpRes, byte[] content, long contentOffset, long contentLength, boolean isOnlyHeader) {
        httpRes.setDate(Calendar.getInstance());

        OutputStream out = getOutputStream();

        try {
            httpRes.setContentLength(contentLength);

            out.write(httpRes.getHeader().getBytes());
            out.write(HTTP.CRLF.getBytes());
            if (isOnlyHeader) {
                out.flush();
                return true;
            }

            boolean isChunkedResponse = httpRes.isChunked();

            if (isChunkedResponse) {
                // Thanks for Lee Peik Feng <pflee@users.sourceforge.net> (07/07/05)
                String chunSizeBuf = Long.toHexString(contentLength);
                out.write(chunSizeBuf.getBytes());
                out.write(HTTP.CRLF.getBytes());
            }

            out.write(content, (int) contentOffset, (int) contentLength);

            if (isChunkedResponse) {
                out.write(HTTP.CRLF.getBytes());
                out.write("0".getBytes());
                out.write(HTTP.CRLF.getBytes());
            }

            out.flush();
        } catch (Exception e) {
            //Debug.warning(e);
            return false;
        }

        return true;
    }

    private boolean post(HTTPResponse httpRes, InputStream in, long contentOffset, long contentLength, boolean isOnlyHeader) {
        httpRes.setDate(Calendar.getInstance());

        OutputStream out = getOutputStream();

        try {
            httpRes.setContentLength(contentLength);

            out.write(httpRes.getHeader().getBytes());
            out.write(HTTP.CRLF.getBytes());

            if (isOnlyHeader) {
                out.flush();
                return true;
            }

            boolean isChunkedResponse = httpRes.isChunked();

            if (0 < contentOffset)
                in.skip(contentOffset);

            int chunkSize = HTTP.getChunkSize();
            byte[] readBuf = new byte[chunkSize];
            long readCnt = 0;
            long readSize = (chunkSize < contentLength) ? chunkSize : contentLength;
            int readLen = in.read(readBuf, 0, (int) readSize);
            while (0 < readLen && readCnt < contentLength) {
                if (isChunkedResponse) {
                    // Thanks for Lee Peik Feng <pflee@users.sourceforge.net> (07/07/05)
                    String chunSizeBuf = Long.toHexString(readLen);
                    out.write(chunSizeBuf.getBytes());
                    out.write(HTTP.CRLF.getBytes());
                }
                out.write(readBuf, 0, readLen);
                if (isChunkedResponse)
                    out.write(HTTP.CRLF.getBytes());
                readCnt += readLen;
                readSize = (chunkSize < (contentLength - readCnt)) ? chunkSize : (contentLength - readCnt);
                readLen = in.read(readBuf, 0, (int) readSize);
            }

            if (isChunkedResponse) {
                out.write("0".getBytes());
                out.write(HTTP.CRLF.getBytes());
            }

            out.flush();
        } catch (Exception e) {
            //Debug.warning(e);
            return false;
        }

        return true;
    }

    public boolean post(HTTPResponse httpRes, long contentOffset, long contentLength, boolean isOnlyHeader) {
        if (httpRes.hasContentInputStream())
            return post(httpRes, httpRes.getContentInputStream(), contentOffset, contentLength, isOnlyHeader);
        return post(httpRes, httpRes.getContent(), contentOffset, contentLength, isOnlyHeader);
    }
}
