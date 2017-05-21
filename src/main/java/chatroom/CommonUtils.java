package chatroom;

/**
 * Created by RichardYuan on 2017/5/17 0017.
 */
public class CommonUtils {

    public static long now () {
        return System.currentTimeMillis();
    }

    public static byte[] encode (String str) {
        return str.getBytes(ChatProtocol.DEFAULT_CHARSET);
    }
}
