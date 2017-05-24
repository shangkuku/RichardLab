package chatroom.exception;

/**
 * Created by RichardYuan on 2017/5/24 0024.
 */
public class UnknownException extends RuntimeException {

    public UnknownException() {
    }

    public UnknownException(String message) {
        super(message);
    }

    public UnknownException(String message, Throwable cause) {
        super(message, cause);
    }
}
