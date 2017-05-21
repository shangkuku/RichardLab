package chatroom;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Collectors;

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
