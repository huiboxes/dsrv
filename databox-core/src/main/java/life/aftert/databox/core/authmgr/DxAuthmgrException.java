package life.aftert.databox.core.authmgr;

import life.aftert.databox.core.DxException;

public class DxAuthmgrException extends DxException {
    private int code;
    private String message;

    public DxAuthmgrException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public DxAuthmgrException(int code, String message) {
        super(message, null);
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public int errorCode() {
        return this.code;
    }
}
