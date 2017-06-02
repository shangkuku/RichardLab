package chatroom.server;

import chatroom.model.ChatMessage;
import chatroom.model.User;
import chatroom.util.CommonUtils;
import chatroom.util.LogUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * Created by RichardYuan on 2017/5/16 0016.
 */
public class ChatServer {


    // timestamp
    public static long BLOCK_DURATION;

    public final ConcurrentHashMap<String, Socket> onlineMap = new ConcurrentHashMap<String, Socket>();

    public final ConcurrentHashMap<String, User> credentials = new ConcurrentHashMap<String, User>();

    public final ConcurrentHashMap<String, List<String>> blackList = new ConcurrentHashMap<>();

    public final ConcurrentHashMap<String, Queue<ChatMessage>> offlineMsg = new ConcurrentHashMap<>();

    public final Executor executor = Executors.newCachedThreadPool();
    private final int DEFAULT_PORT = 4396;

    private final int port;

    private static Boolean running = Boolean.TRUE;

    public static void main(String[] args) {
        new ChatServer(1).startup();
    }

    public ChatServer(int port, long blockDuration) {
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

    }

    private void startTimeoutServer() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        Thread t = new Thread(new TimeoutServer());
        t.setName("timeout");
        t.setDaemon(true);
        service.scheduleAtFixedRate(t, 5, TimeoutServer.timeInternal, TimeUnit.SECONDS);
    }


    private void initCredentials() {

        try {
            String resourceName = "credentials.txt";
            List<String> credentials = Files.readAllLines(Paths.get(ClassLoader.getSystemResource(resourceName).toURI()));
            credentials.forEach((credential) -> {
                String[] strs = credential.split(" ");
                String userName = strs[0], password = strs[1];
                User u = new User(userName, password);
                this.credentials.put(userName, u);
            });
        } catch (IOException e) {
            LogUtils.log("读取凭证失败", e);
            System.exit(0);
        } catch (URISyntaxException e) {
            LogUtils.log("读取凭证失败", e);
            System.exit(0);
        }
    }


    private void startup() {
        try (ServerSocket ss = new ServerSocket(this.port)) {
            LogUtils.log("服务器已启动");
            startTimeoutServer();
            startInputServer();
            while (running) {
                try {
                    Socket socket = ss.accept();
                    ChatProtocol protocol = new ChatProtocol(this);
                    protocol.startHandle(socket);
                } catch (IOException e) {
                    CommonUtils.unknownException(e);
                }
            }
        } catch (IOException e) {
            LogUtils.log("服务器启动异常", e);
            CommonUtils.unknownException(e);
        }
    }

    private void startInputServer() {
    }


    private class InputServer implements Runnable {

        @Override
        public void run() {
            while (true) {
                Scanner sc = new Scanner(System.in);
                String str = sc.nextLine();
                if (ChatProtocol.SERVER_END.equals(str)) {
                    running = false;
                    break;
                }
            }
        }
    }


    private class TimeoutServer implements Runnable {

        /**
         * 单位为秒
         */
        private static final long timeInternal = 1;

        @Override
        public void run() {
            checkOnlineUserTimeOut();
        }

        private void checkOnlineUserTimeOut() {
            onlineMap.forEach((k, v) -> {
                        User u = credentials.get(k);
                        if (isTimeOut(u.getLastActiveTime())) {
                            onlineMap.remove(k);
                            System.out.println(String.format("用户【%s】超时未响应，已离线", u.getUserName()));
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
