package life.aftert.databox.core;

/**
 * base exception,all exception should extend it.
 */
public abstract class DxException extends RuntimeException {
    protected String errorMessage;

    public DxException(String message, Throwable cause) {
        super(cause);
        this.errorMessage = message;
    }

    public abstract int errorCode();

    public String errorMessage() {
        return this.errorMessage;
    }
}
