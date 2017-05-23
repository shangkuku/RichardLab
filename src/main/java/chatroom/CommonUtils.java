package chatroom;

import java.io.*;
import java.util.stream.Collectors;

/**
 * Created by RichardYuan on 2017/5/17 0017.
 */
public class CommonUtils {

    public static long now() {
        return System.currentTimeMillis();
    }

    public static byte[] encode(String str) {
        return str.getBytes(ChatProtocol.DEFAULT_CHARSET);
    }

    public static String readStream(InputStream in) {

        byte[] b = new byte[2048];
        int length;
        String msg = null;
        synchronized (in) {
            try {
                while ((length = in.read(b)) != -1) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
                    baos.write(b, 0, length);
                    msg = baos.toString(ChatProtocol.DEFAULT_CHARSET.toString());
                    return msg;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return msg;
    }

    public static void writeMessage(OutputStream out, String msg) {
        synchronized (out) {
            try {
                out.write(encode(msg));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
