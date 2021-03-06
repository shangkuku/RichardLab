package chatroom.server;

import chatroom.exception.LoginFailException;
import chatroom.exception.UnknownCommandException;
import chatroom.model.ChatMessage;
import chatroom.model.Command;
import chatroom.model.InputMessage;
import chatroom.model.User;
import chatroom.parser.InputParser;
import chatroom.util.CommonUtils;
import chatroom.util.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.text.Collator;
import java.util.*;

/**
 * Created by RichardYuan on 2017/5/18 0018.
 */
public class ServerHandler {

    private Socket socket;

    private ChatServer server;

    private String userName;


    public ServerHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }


    public void handle() throws IOException {
        this.handleRequest();
    }

    private void resolveCommand(InputMessage msg) throws IOException, IllegalArgumentException {
        Command command = msg.getCommand();
        String[] args = msg.getArgs();
        switch (command) {
            case LOGIN: toLogin(args); break;
            case LOGOUT: logout(this.userName); break;
            case SEND_MESSAGE: toSendMsg(args); break;
            case LIST: toList(args); break;
            case BLACKLIST: toBlackList(args); break;
        }
    }


    private boolean verifyLogin(String userName, String password) throws LoginFailException {
        User u;
        if ((u = server.credentials.get(userName)) == null) {
            throw new LoginFailException("该用户不存在");
        }
        if (!u.getPassword().equals(password)) {
            throw new LoginFailException("密码错误");
        }
        checkUserBlock();
        return true;
    }

    public boolean login(String userName, String password) throws LoginFailException, IOException {


        if (!verifyLogin(userName, password)) {
            handleLoginFail();
            return false;
        }

        if (this.isOnline(userName)) {
            throw new LoginFailException("当前用户已在线");
        }

        handleLoginSuccess();
        return true;
    }

    private void checkUserBlock() throws LoginFailException {
        User u = server.credentials.get(userName);
        if (u.getLoginCounts() > 3) {
            throw new LoginFailException("连续登录超过3次，请在" + (ChatServer.BLOCK_DURATION / 1000 / 1000) + "秒后重试");
        }
    }

    private void handleLoginFail() {
        User u = server.credentials.get(userName);
        u.setLastActiveTime(CommonUtils.now());
        u.incLoginCount();
    }

    private User handleLoginSuccess() throws IOException {
        User u = server.credentials.get(userName);
        server.credentials.put(userName, u);
        this.online(userName, socket);
        CommonUtils.writeMessage(socket.getOutputStream(), ChatProtocol.SUCCESS_FLAG);
        this.sendOfflineMsg();
        return u;
    }

    private void sendOfflineMsg() throws IOException {
        Queue<ChatMessage> msgQ = server.offlineMsg.get(this.userName);
        if (msgQ != null && !msgQ.isEmpty()) {
            CommonUtils.writeMessage(socket.getOutputStream(), msgQ.poll().toString());
        }
    }

    public void online(String userName, Socket socket) throws IOException {
        LogUtils.log("用户【" + userName + "】已登录");
        server.onlineMap.put(userName, socket);
        onLineBroadcast(userName);
    }

    private void onLineBroadcast(String userName) throws IOException {
        broadcast(userName, String.format("用户 【%s】 已上线", userName));
    }

    private void broadcast(String currentUser, String msg) throws IOException {
        for (String userName : server.onlineMap.keySet()) {
            if (userName.equals(currentUser)) continue;
            CommonUtils.writeMessage(server.onlineMap.get(userName).getOutputStream(), msg);
        }
    }

    private void offline(String userName) throws IOException {
        if (userName != null) {
            server.onlineMap.remove(userName);
        }

        broadcast(userName, String.format("用户 【%s】 已下线", userName));
    }

    public boolean logout(String userName) throws IOException {
        this.offline(userName);
        return true;
    }


    public boolean isOnline(String userName) {
        return server.onlineMap.containsKey(userName);
    }

    public Socket getSocketByUser(String userName) {
        Socket socket;
        if ((socket = server.onlineMap.get(userName)) == null)
            LogUtils.log("用户【" + userName + "】未连接");
        return socket;
    }

    private void toBlackList(String[] args) {
        String u = args[0];
        List list = server.blackList.get(this.userName);
        list = list == null ? new ArrayList() : list;
        list.add(u);
        server.blackList.put(this.userName, list);
    }

    private void toList(String ... args) throws IOException {
        String type = args[0];
        Command.ListCommand clc;
        try {
            clc = Command.ListCommand.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnknownCommandException("未知参数", e);
        }
        String msg = clc.getValue();
        switch (clc) {
            case ALL: msg += listAll(this.userName); break;
            case ONLINE: msg += listOnline(this.userName); break;
            case OFFLINE: msg += listOffline(this.userName); break;
            case BLACKLIST: msg += listBlacklist(this.userName); break;
        }
        CommonUtils.writeMessage(this.socket.getOutputStream(), String.format(msg));
    }


    public String listAll(String userName) {
        Collection list = new TreeSet<String>(Collator.getInstance());
        server.credentials.entrySet()
                .stream()
                .filter(stringUserEntry -> !stringUserEntry.getKey().equals(userName))
                .forEach(stringUserEntry -> {
                    list.add(stringUserEntry.getKey());
                });
        return CommonUtils.Col2String(list);
    }

    public String listOnline(String userName) {
        Collection list = new TreeSet<String>(Collator.getInstance());
        server.onlineMap.entrySet()
                .stream()
                .filter(stringUserEntry -> !stringUserEntry.getKey().equals(userName))
                .forEach(stringUserEntry -> {
                    list.add(stringUserEntry.getKey());
                });
        return CommonUtils.Col2String(list);
    }

    public String listOffline(String userName) {
        Collection list = new TreeSet<String>(Collator.getInstance());
        server.credentials.entrySet()
                .stream()
                .filter(stringUserEntry -> {
                    String u = stringUserEntry.getKey();
                    return !u.equals(userName) && !server.onlineMap.containsKey(u);
                })
                .forEach(stringUserEntry -> {
                    list.add(stringUserEntry.getKey());
                });
        return CommonUtils.Col2String(list);
    }

    public String listBlacklist(String userName) {
        List list = server.blackList.get(userName);
        return CommonUtils.Col2String(list);
    }

    private void toSendMsg(String[] args) throws IOException {
        String content = args[0];

        String receiver = args[1];

        ChatMessage cm = ChatMessage.newInstance(content, this.userName, receiver);

        //存入服务器消息队列
        if (!this.isOnline(receiver)) {
            Queue q = server.offlineMsg.get(receiver);
            q = q == null ? new LinkedList() : q;
            q.add(cm);
            server.offlineMsg.put(receiver, q);
        } else {
            Socket receiverSocket = this.getSocketByUser(receiver);
            CommonUtils.writeMessage(receiverSocket.getOutputStream(),
                    cm.toString());
        }
    }

    private void toLogin(String ...args) throws IOException {
        try {
            this.userName = args[0];
            this.login(args[0], args[1]);

        } catch (LoginFailException e) {
            String res = "登陆失败:" + e.getMessage();
            loginFail(res);
        }
    }

    private void loginFail(String res) throws IOException {
        CommonUtils.writeMessage(socket.getOutputStream(), res);
    }

    private void shutdown() {
        try {
            this.offline(ServerHandler.this.userName);
            this.socket.close();
        } catch (IOException e) {
            CommonUtils.unknownException(e);
        }
    }




    private void handleRequest() throws IOException {
        server.executor.execute(new RequestHandle(socket.getInputStream()));
    }

    private class RequestHandle extends InputParser {


        RequestHandle(InputStream in) {
            super(in);
        }

        @Override
        protected void shutdown() {
            ServerHandler.this.shutdown();
        }

        @Override
        public void parseInputInternal(String msg) throws IOException {
            String[] split = msg.split("\\s");
            String command = split[0];
            String[] args = new String[split.length];
            System.arraycopy(split, 1, args, 0, split.length - 1);
            try {
                Command c = Command.valueOf(command.toUpperCase());
                InputMessage im = new InputMessage(c, args);
                ServerHandler.this.resolveCommand(im);
            } catch (IllegalArgumentException e) {
                        CommonUtils.writeMessage(socket.getOutputStream(), String.format("无效命令<%s>", msg));
            }
        }
    }


}
