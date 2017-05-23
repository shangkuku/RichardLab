package chatroom;

/**
 * Created by RichardYuan on 2017/5/19 0019.
 */
public enum Command {
    LOGIN ,

    LOGOUT,

    SEND_MESSAGE,

    LIST;

    public static boolean isInCommand(String command) {
        try {
            valueOf(command.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static Command resolveCommand(String command) throws UnknownCommand{
        try {
            Command c = valueOf(command.toUpperCase());
            return c;
        } catch (IllegalArgumentException e) {
            throw new UnknownCommand("未知命令");
        }
    }

}
