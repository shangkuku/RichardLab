package chatroom;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by RichardYuan on 2017/5/18 0018.
 */
public class OutputMessage {

    private OutputStream out;

    private String message;

    public OutputMessage(OutputStream out) {
        this.out = out;
    }

    public void init() {
        try {
            new BufferedWriter(new OutputStreamWriter(out, ChatProtocol.DEFAULT_CHARSET)).write(message);
        } catch (IOException e) {
            LogUtils.log("输出流错误", e);
        }
    }

    public OutputStream getOut() {
        return out;
    }
}


