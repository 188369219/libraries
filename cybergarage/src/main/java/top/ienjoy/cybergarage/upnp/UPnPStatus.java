package top.ienjoy.cybergarage.upnp;

import top.ienjoy.cybergarage.http.HTTPStatus;

public class UPnPStatus {
    ////////////////////////////////////////////////
    //	Code
    ////////////////////////////////////////////////

    public static final int INVALID_ACTION = 401;
    public static final int INVALID_ARGS = 402;
    public static final int OUT_OF_SYNC = 403;
    public static final int INVALID_VAR = 404;
    public static final int PRECONDITION_FAILED = 412;
    public static final int ACTION_FAILED = 501;

    public static String code2String(int code) {
        return switch (code) {
            case INVALID_ACTION -> "Invalid Action";
            case INVALID_ARGS -> "Invalid Args";
            case OUT_OF_SYNC -> "Out of Sync";
            case INVALID_VAR -> "Invalid Var";
            case PRECONDITION_FAILED -> "Precondition Failed";
            case ACTION_FAILED -> "Action Failed";
            default -> HTTPStatus.code2String(code);
        };
    }

    ////////////////////////////////////////////////
    //	Member
    ////////////////////////////////////////////////

    private int code;
    private String description;

    public UPnPStatus() {
        setCode(0);
        setDescription("");
    }

    public UPnPStatus(int code, String desc) {
        setCode(code);
        setDescription(desc);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
