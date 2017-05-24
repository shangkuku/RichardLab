package chatroom.model;

import chatroom.exception.UnknownCommandException;

/**
 * Created by RichardYuan on 2017/5/19 0019.
 */
public enum Command {
    LOGIN ,

    LOGOUT,

    SEND_MESSAGE,

    LIST,

    BLACKLIST;



    public static enum ListCommand {

        ALL("所有人"),
        BLACKLIST("黑名单"),
        ONLINE("在线"),
        OFFLINE("离线");

        ListCommand(String value) {
            this.value = value;
        }

        private final String value;

        public String getValue() {
            return value;
        }
    }

    public static boolean isInCommand(String command) {
        try {
            valueOf(command.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static Command resolveCommand(String command) throws UnknownCommandException {
        try {
            Command c = valueOf(command.toUpperCase());
            return c;
        } catch (IllegalArgumentException e) {
            throw new UnknownCommandException("未知命令");
        }
    }


}
