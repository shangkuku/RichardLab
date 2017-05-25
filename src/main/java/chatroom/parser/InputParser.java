package chatroom.parser;

import chatroom.model.Command;
import chatroom.model.InputMessage;
import chatroom.server.ChatProtocol;
import chatroom.server.ServerHandler;
import chatroom.util.CommonUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by RichardYuan on 2017/5/21 0021.
 */
public abstract class InputParser implements Runnable {

    protected InputStream in;

    public InputParser(InputStream in) {
        this.in = in;
    }

    private StringBuilder notFinshed = new StringBuilder();

    @Override
    public void run() {
        try {
            parseInput();
        } catch (IOException e) {
            shutdown();
        }

    }

    abstract protected void shutdown();

    protected void parseInput() throws IOException {

        while (true) {
            String msg = CommonUtils.readStream(in);
            parseMsg(msg);
        }
    }

    protected void parseMsg(String msg) throws IOException {
        String inputMsg;
        int pos;
        if (notFinshed.length() > 0) {
            msg = notFinshed.append(msg).toString();
            notFinshed.setLength(0);
        }
        while ((pos = msg.indexOf(ChatProtocol.END_FLAG)) > 0) {
            inputMsg = msg.substring(0, pos);
            msg = msg.substring(pos + ChatProtocol.END_FLAG.length());
            parseInputInternal(inputMsg);
        }
        if (msg.length() > 0) notFinshed.append(msg);
    }

    public abstract void parseInputInternal(String msg) throws IOException;

}