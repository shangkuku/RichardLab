package chatroom.util;

import chatroom.exception.UnknownException;
import chatroom.model.Constants;
import chatroom.server.ChatProtocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

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

    public static String readStream(InputStream in) throws IOException {

        byte[] b = new byte[2048];
        int length;
        String msg = null;
        synchronized (in) {
            while ((length = in.read(b)) != -1) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
                baos.write(b, 0, length);
                msg = baos.toString(ChatProtocol.DEFAULT_CHARSET.toString());
                return msg;
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

    public static String Col2String(Collection col, String delimiter) {
        if (col ==null || col.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        col.forEach(o -> {
            if (sb.length() > 0) sb.append(delimiter);
            sb.append(o);
        });
        return sb.toString();
    }

    public static String Col2String(Collection col) {
        return Col2String(col, Constants.COMMA);
    }

    public static void unknownException(Throwable e) {
        throw new UnknownException("未知错误", e);
    }

}
