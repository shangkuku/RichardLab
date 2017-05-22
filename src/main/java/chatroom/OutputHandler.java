package chatroom;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by RichardYuan on 2017/5/21 0021.
 */
public class OutputHandler {

    private OutputStream out;

    public OutputHandler(OutputStream out) {
        this.out = out;
    }

    public void writeMessage (String str) {
        try {
            out.write(CommonUtils.encode(str));
        } catch (IOException e) {
            LogUtils.log("服务器内部错误", e);
        }
    }
}