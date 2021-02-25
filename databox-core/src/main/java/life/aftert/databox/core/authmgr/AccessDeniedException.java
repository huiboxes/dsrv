package life.aftert.databox.core.authmgr;

import life.aftert.databox.core.DxException;
import life.aftert.databox.core.ErrorCodes;

public class AccessDeniedException extends DxException {

    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccessDeniedException(String resPath, long userId, String accessType) {
        super(String.format("access denied:%d->%s,%s", userId, resPath, accessType), null);
    }

    public AccessDeniedException(String resPath, long userId) {
        super(String.format("access denied:%d->%s not owner", userId, resPath), null);
    }

    @Override
    public int errorCode() {
        return ErrorCodes.ERROR_PERMISSION_DENIED;
    }

}
