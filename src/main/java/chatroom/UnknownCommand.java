package chatroom;

/**
 * Created by RichardYuan on 2017/5/22 0022.
 */
public class UnknownCommand extends UnsupportedOperationException{
    public UnknownCommand(String message) {
        super(message);
    }
}
