package chatroom;

/**
 * Created by RichardYuan on 2017/5/18 0018.
 */
public class InputMessage {


    private Command command;

    private String[] args;

    public InputMessage(Command command, String[] args) {
        this.command = command;
        this.args = args;
    }


    public Command getCommand() {
        return command;
    }

    public String[] getArgs() {
        return args;
    }
}
