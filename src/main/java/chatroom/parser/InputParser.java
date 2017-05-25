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

    protected Queue<InputMessage> mq = new LinkedList();

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
            parseInputInternal(msg);
        }
    }

    private final void parseMsg () {
        byte[] b = new byte[2048];
        int length;
        String msg = null;
        synchronized (in) {
            try {

                while ((length = in.read(b)) != -1) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
                    baos.write(b, 0, length);
                    msg = baos.toString(ChatProtocol.DEFAULT_CHARSET.toString());
//                    msg.split(ChatProtocol.END_FLAG)
                    String[] split = msg.split("\\s");
                    String command = split[0];
                    String[] args = new String[split.length];
                    System.arraycopy(split, 1, args, 0, split.length - 1);
                    try {
                        Command c = Command.valueOf(command.toUpperCase());
                        InputMessage im = new InputMessage(c, args);
//                        ServerHandler.this.resolveCommand(im);
                    } catch (IllegalArgumentException e) {
//                        CommonUtils.writeMessage(socket.getOutputStream(), String.format("无效命令<%s>", msg));
                    }

//                    mq.add()
//                    return msg;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        return msg;
    }

    public abstract void parseInputInternal(String msg) throws IOException;

}