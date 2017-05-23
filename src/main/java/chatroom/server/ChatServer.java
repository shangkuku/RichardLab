package chatroom.server;

import chatroom.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by RichardYuan on 2017/5/16 0016.
 */
public class ChatServer implements Runnable {


    // timestamp
    public static long BLOCK_DURATION;

    public final ConcurrentHashMap<String, Socket> onlineMap = new ConcurrentHashMap<String, Socket>();

    public final ConcurrentHashMap<String, User> credentials = new ConcurrentHashMap<String, User>();

    public final Executor executor = Executors.newCachedThreadPool();
    private final int DEFAULT_PORT = 4396;

    private final int port;

    private volatile Boolean running = Boolean.TRUE;

    public static void main(String[] args) {
        new ChatServer(1).run();
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
        service.scheduleAtFixedRate(new timeoutServer(), 5, timeoutServer.timeInternal, TimeUnit.SECONDS);
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

    private boolean verifyLogin(String userName, String password) throws LoginFailException {
        User u;
        if ((u = this.credentials.get(userName)) == null) {
            throw new LoginFailException("该用户不存在");
        }
        if (!password.equals(u.getPassword())) {
            throw new LoginFailException("密码错误");
        }
        checkUserBlock(userName);
        return true;
    }

    public boolean login(Socket socket, String userName, String password) throws LoginFailException, IOException {


        if (!verifyLogin(userName, password)) {
            handleLoginFail(userName);
            return false;
        }

        if (this.isOnline(userName)) {
            throw new LoginFailException("当前用户已在线");
        }

        handleLoginSuccess(userName, socket);
        return true;
    }

    private void checkUserBlock(String userName) throws LoginFailException {
        User u = this.credentials.get(userName);
        if (u.getLoginCounts() > 3) {
            throw new LoginFailException("连续登录超过3次，请在" + (BLOCK_DURATION / 1000 / 1000) + "秒后重试");
        }
    }

    private void handleLoginFail(String userName) {
        User u = this.credentials.get(userName);
        u.setLastActiveTime(CommonUtils.now());
        u.incLoginCount();
    }

    private User handleLoginSuccess(String userName, Socket socket) throws IOException {
        User u = this.credentials.get(userName);
        this.credentials.put(userName, u);
        this.online(userName, socket);
        return u;
    }

    public void online(String userName, Socket socket) throws IOException {
        LogUtils.log("用户【" + userName + "】已登录");
        this.onlineMap.put(userName, socket);
        onLineBroadcast(userName);
    }

    private void onLineBroadcast(String userName) throws IOException {
        broadcast(userName, String.format("用户 【%s】 已上线", userName));
    }

    private void broadcast(String currentUser, String msg) throws IOException {
        for (String userName : this.onlineMap.keySet()) {
            if (userName.equals(currentUser)) continue;
            CommonUtils.writeMessage(this.onlineMap.get(userName).getOutputStream(), msg);
        }
    }

    private void offline(String userName) throws IOException {
        this.onlineMap.remove(userName);
        broadcast(userName, String.format("用户 【%s】 已下线", userName));
    }

    public boolean logout(String userName) throws IOException {
        this.offline(userName);
        return true;
    }


    public boolean isOnline(String userName) {
        return onlineMap.containsKey(userName);
    }

    public Socket getSocketByUser(String userName) {
        Socket socket;
        if ((socket = onlineMap.get(userName)) == null)
            LogUtils.log("用户【" + userName + "】未连接");
        return socket;
    }

    @Override
    public void run() {
        try (ServerSocket ss = new ServerSocket(this.port)) {
            LogUtils.log("服务器已启动");
            startTimeoutServer();
            while (running) {
                try {
                    Socket socket = ss.accept();
                    ChatProtocol protocol = new ChatProtocol(this);
                    protocol.startHandle(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
