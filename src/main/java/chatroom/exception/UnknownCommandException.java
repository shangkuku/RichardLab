package chatroom.exception;

/**
 * Created by RichardYuan on 2017/5/22 0022.
 */
public class UnknownCommandException extends UnsupportedOperationException{
    public UnknownCommandException(String message) {
        super(message);
    }

    public UnknownCommandException(String message, Throwable e) {
        super(message, e);
    }
}
