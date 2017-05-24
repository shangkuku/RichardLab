package chatroom.util;

/**
 * Created by RichardYuan on 2017/5/16 0016.
 */
public class LogUtils {
    public static void log(String msg, Throwable e) {
        e.printStackTrace();
        System.out.println(msg);
    }

    public static void log(String msg) {
        System.out.println(msg);
    }
}
