package chatroom;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by RichardYuan on 2017/5/16 0016.
 */
public class ChatServer implements Runnable {

    // timestamp
    public static long BLOCK_DURATION;

    public final ConcurrentHashMap<String, Boolean> onlineMap = new ConcurrentHashMap<String, Boolean>();

    public final ConcurrentHashMap<String, String> credentials = new ConcurrentHashMap<String, String>();

    public final ConcurrentHashMap<String, UserStatus> loginInfoMap = new ConcurrentHashMap<String, UserStatus>();

    private final int DEFAULT_PORT = 4396;

    private final int port;
    private ChatServer(int port, long blockDuration) {
        BLOCK_DURATION = blockDuration * 1000 * 1000;
        this.port = port;
        init();
    }

    public ChatServer(long blockDuration) {
        BLOCK_DURATION = blockDuration * 1000 * 1000;
        this.port = DEFAULT_PORT;
        init();
    }

    private void init() {
        initCredentials();
        startTimeoutServer();

    }

    private void startTimeoutServer() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(new timeoutServer(), 5, timeoutServer.timeInternal, TimeUnit.SECONDS);
    }


    private void initCredentials() {

        try {
            Files.readAllLines(Paths.get("credentials.txt"));
        } catch (IOException e) {
            LogUtils.log("读取凭证失败", e);
        }
    }

    boolean verifyLogin(String userName, String password) {
        return false;
    }

    boolean login(String userName, String password) {
        return true;
    }

    boolean logout(String userName) {
        return true;
    }

    boolean isOnline(String userName) {
        return onlineMap.containsKey(userName);
    }

    @Override
    public void run() {
        try (ServerSocket ss = new ServerSocket(this.port)) {
            Socket socket = ss.accept();
        } catch (IOException e) {
            LogUtils.log("服务器启动异常", e);
            System.exit(0);
        }
    }

    private class timeoutServer implements Runnable {

        /**
         * 单位为秒
         */
        private static final long timeInternal = 1;

        @Override
        public void run() {
            checkOnlineUserTimeOut();
        }

        private void checkOnlineUserTimeOut() {
            loginInfoMap.forEach((k, v) -> {
                        if (isTimeOut(v.getLastActiveTime())) {
                            onlineMap.remove(k);
                        }
                    }
            );
        }

        private boolean isTimeOut(long lastActiveTime) {
            long now = System.currentTimeMillis();
            return lastActiveTime + BLOCK_DURATION < now;
        }


    }
}
