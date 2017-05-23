package chatroom;

import chatroom.server.ChatServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

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

    private void resolveCommand(InputMessage msg) throws IOException {
        Command command = msg.getCommand();
        String[] args = msg.getArgs();
        switch (command) {
            case LOGIN: toLogin(args); break;
            case SEND_MESSAGE: toSendMsg(args); break;
            case LIST: toList(args); break;
        }
    }

    private void toList(String ... args) {

    }

    private void toSendMsg(String[] args) throws IOException {
        String content = args[0];

        String receiver = args[1];

        //存入服务器消息队列
        if (!server.isOnline(receiver)) {

        } else {
            Socket receiverSocket = server.getSocketByUser(receiver);
            CommonUtils.writeMessage(receiverSocket.getOutputStream(), "【"+this.userName +"】"+content);
        }
    }

    private void toLogin(String ...args) throws IOException {
        try {
            if (server.login(socket, args[0], args[1])) {
                this.userName = args[0];
                socket.getOutputStream().write(CommonUtils.encode(ChatProtocol.SUCCESS_FLAG));
            }

        } catch (LoginFailException e) {
            String res = "登陆失败:" + e.getMessage();
            loginFail(res);
        }
    }

    private void loginFail(String res) throws IOException {
        CommonUtils.writeMessage(socket.getOutputStream(), res);
    }





    private void handleRequest() throws IOException {
        server.executor.execute(new RequestHandle(socket.getInputStream()));
    }

    private class RequestHandle extends InputHandler {


        RequestHandle(InputStream in) {
            super(in);
        }

        @Override
        void parseInputInternal(String msg) throws IOException {
            String[] split = msg.split("\\s");
            String command = split[0];
            String[] args = new String[split.length];
            System.arraycopy(split, 1, args, 0, split.length - 1);
            try {
                Command c = Command.valueOf(command.toUpperCase());
                InputMessage im = new InputMessage(c, args);
                ServerHandler.this.resolveCommand(im);
            } catch (IllegalArgumentException e) {
                CommonUtils.writeMessage(socket.getOutputStream(), "无效命令");
            }

        }
    }

//    private class ResponseHandle implements Runnable {
//
//        private OutputStream out;
//
//        public ResponseHandle(OutputStream out) {
//            this.out = out;
//        }
//
//
//        @Override
//        public void run() {
//            parseResponse();
//        }
//
//        private void parseResponse() {
//
//        }
//    }


}
