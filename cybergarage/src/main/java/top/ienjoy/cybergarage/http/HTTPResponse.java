package top.ienjoy.cybergarage.http;

import java.io.InputStream;

public class HTTPResponse extends HTTPPacket {
    ////////////////////////////////////////////////
    //	Constructor
    ////////////////////////////////////////////////

    public HTTPResponse() {
        setVersion(HTTP.VERSION_11);
        setContentType(HTML.CONTENT_TYPE);
        setServer(HTTPServer.getName());
        setContent("");
    }

    public HTTPResponse(HTTPResponse httpRes) {
        set(httpRes);
    }

    public HTTPResponse(InputStream in) {
        super(in);
    }

    public HTTPResponse(HTTPSocket httpSock) {
        this(httpSock.getInputStream());
    }

    ////////////////////////////////////////////////
    //	Status Line
    ////////////////////////////////////////////////

    private int statusCode = 0;

    public void setStatusCode(int code) {
        statusCode = code;
    }

    public int getStatusCode() {
        if (statusCode != 0)
            return statusCode;
        HTTPStatus httpStatus = new HTTPStatus(getFirstLine());
        return httpStatus.getStatusCode();
    }

    public boolean isSuccessful() {
        return HTTPStatus.isSuccessful(getStatusCode());
    }

    public String getStatusLineString() {
        return "HTTP/" + getVersion() + " " + getStatusCode() + " " + HTTPStatus.code2String(statusCode) + HTTP.CRLF;
    }

    ////////////////////////////////////////////////
    //	getHeader
    ////////////////////////////////////////////////

    public String getHeader() {
        return getStatusLineString() + getHeaderString();
    }

    ////////////////////////////////////////////////
    //	toString
    ////////////////////////////////////////////////

    public String toString() {
        return getStatusLineString() + getHeaderString() + HTTP.CRLF + getContentString();
    }

    public void print() {
        System.out.println(this);
    }
}
