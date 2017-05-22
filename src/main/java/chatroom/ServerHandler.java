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

    private OutputHandler outputHandler;


    public ServerHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }


    public void handle() throws IOException {
        this.handleRequest();
        this.handleResponse();
    }

    private void resolveCommand(InputMessage msg) throws IOException {
        Command command = msg.getCommand();
        String[] args = msg.getArgs();
        switch (command) {
            case LOGIN: toLogin(args); break;
            case SEND_MESSAGE: toSendMsg(args); break;
        }
    }

    private void toSendMsg(String[] args) throws IOException {
        String content = args[0];

        String receiver = args[1];

        //存入服务器消息队列
        if (!server.isOnline(receiver)) {

        } else {
            Socket receiverSocket = server.getSocketByUser(receiver);
            ServerHandler.this.outputHandler.writeMessage(content);
        }
    }

    private void toLogin(String ...args) {
        try {
            if (server.login(socket, args[0], args[1])) {
                socket.getOutputStream().write(CommonUtils.encode("登录成功"));
            }

        } catch (LoginFailException e) {
            String res = "登陆失败:" + e.getMessage();
            loginFail(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loginFail(String res) {

    }



    private void handleResponse() {
        try {
            this.outputHandler = new OutputHandler(this.socket.getOutputStream());
        } catch (IOException e) {
            LogUtils.log("服务器内部错误", e);
        }
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
                ServerHandler.this.outputHandler.writeMessage("无效命令");
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
