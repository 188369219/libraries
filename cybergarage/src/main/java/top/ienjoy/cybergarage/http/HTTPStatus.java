package top.ienjoy.cybergarage.http;

import java.util.StringTokenizer;

import top.ienjoy.cybergarage.util.Debug;

@SuppressWarnings("unused")
public class HTTPStatus {
    ////////////////////////////////////////////////
    //	Code
    ////////////////////////////////////////////////

    public static final int CONTINUE = 100;
    public static final int OK = 200;
    //	Thanks for Brent Hills (10/20/04)
    public static final int PARTIAL_CONTENT = 206;
    public static final int BAD_REQUEST = 400;
    public static final int NOT_FOUND = 404;
    public static final int PRECONDITION_FAILED = 412;
    //	Thanks for Brent Hills (10/20/04)
    public static final int INVALID_RANGE = 416;
    public static final int INTERNAL_SERVER_ERROR = 500;

    public static String code2String(int code) {
        return switch (code) {
            case CONTINUE -> "Continue";
            case OK -> "OK";
            case PARTIAL_CONTENT -> "Partial Content";
            case BAD_REQUEST -> "Bad Request";
            case NOT_FOUND -> "Not Found";
            case PRECONDITION_FAILED -> "Precondition Failed";
            case INVALID_RANGE -> "Invalid Range";
            case INTERNAL_SERVER_ERROR -> "Internal Server Error";
            default -> "";
        };
    }

    ////////////////////////////////////////////////
    //	Constructor
    ////////////////////////////////////////////////

    public HTTPStatus() {
        setVersion("");
        setStatusCode(0);
        setReasonPhrase("");
    }

    public HTTPStatus(String ver, int code, String reason) {
        setVersion(ver);
        setStatusCode(code);
        setReasonPhrase(reason);
    }

    public HTTPStatus(String lineStr) {
        set(lineStr);
    }

    ////////////////////////////////////////////////
    //	Member
    ////////////////////////////////////////////////

    private String version = "";
    private int statusCode = 0;
    private String reasonPhrase = "";

    public void setVersion(String value) {
        version = value;
    }

    public void setStatusCode(int value) {
        statusCode = value;
    }

    public void setReasonPhrase(String value) {
        reasonPhrase = value;
    }

    public String getVersion() {
        return version;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    ////////////////////////////////////////////////
    //	Status
    ////////////////////////////////////////////////

    public static boolean isSuccessful(int statCode) {
        return 200 <= statCode && statCode < 300;
    }

    public boolean isSuccessful() {
        return isSuccessful(getStatusCode());
    }

    ////////////////////////////////////////////////
    //	set
    ////////////////////////////////////////////////

    public void set(String lineStr) {
        if (lineStr == null) {
            setVersion(HTTP.VERSION);
            setStatusCode(INTERNAL_SERVER_ERROR);
            setReasonPhrase(code2String(INTERNAL_SERVER_ERROR));
            return;
        }

        try {
            StringTokenizer st = new StringTokenizer(lineStr, HTTP.STATUS_LINE_DELIM);

            if (!st.hasMoreTokens())
                return;
            String ver = st.nextToken();
            setVersion(ver.trim());

            if (!st.hasMoreTokens())
                return;
            String codeStr = st.nextToken();
            int code = 0;
            try {
                code = Integer.parseInt(codeStr);
            } catch (Exception ignored) {
            }
            setStatusCode(code);

            StringBuilder reason = new StringBuilder();
            while (st.hasMoreTokens()) {
                if (0 <= reason.length())
                    reason.append(" ");
                reason.append(st.nextToken());
            }
            setReasonPhrase(reason.toString().trim());
        } catch (Exception e) {
            Debug.warning(e);
        }
    }
}
