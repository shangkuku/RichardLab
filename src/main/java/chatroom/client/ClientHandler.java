package chatroom.client;

import chatroom.exception.UnknownCommandException;
import chatroom.parser.InputParser;
import chatroom.model.Command;
import chatroom.server.ChatProtocol;
import chatroom.util.CommonUtils;
import chatroom.util.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

/**
 * Created by RichardYuan on 2017/5/18 0018.
 */
public class ClientHandler {

    private ChatClient client;

    private volatile Boolean running = true;

    private Scanner scanner = new Scanner(System.in);

    public ClientHandler(ChatClient client) {
        this.client = client;
    }

    public void startHandle() {
        new Thread(new RequestHandler()).run();
    }

    private void shutdown () {
        this.running = false;
        this.scanner.close();
        this.client.shutdown();
    }

    private class ResponseHandler extends InputParser {

        private ResponseHandler(InputStream in) {
            super(in);
        }

        @Override
        protected void shutdown() {
           ClientHandler.this.shutdown();
        }

        @Override
        public void parseInputInternal(String msg) throws IOException {
            LogUtils.log(msg);
        }
    }

    private class RequestHandler implements Runnable {

        private ClientHandler clientHandler = ClientHandler.this;

        private boolean isLoginSuccess (String res) {
            return (ChatProtocol.SUCCESS_FLAG + ChatProtocol.END_FLAG).equals(res);
        }

        @Override
        public void run() {
            System.out.println("请先登陆啦");
            while (running) {
                String inputMsg = scanner.nextLine();
                if (!running) break;
                try {
                    CommonUtils.writeMessage(clientHandler.client.socket.getOutputStream(), inputMsg);
                    String[] m = inputMsg.split("\\s");
                    if (Command.LOGIN.equals(Command.resolveCommand(m[0]))) {
                        String res = CommonUtils.readStream(client.socket.getInputStream());
                        if (isLoginSuccess(res)) {
                            clientHandler.client.user = m[1];
                            Thread t = new Thread(new ResponseHandler(client.socket.getInputStream()));
                            t.setDaemon(true);
                            t.setName("Client Reader");
                            t.start();
                        }
                    }
                } catch (IOException e) {
                    clientHandler.shutdown();
                } catch (UnknownCommandException ee) {
                    System.out.println("未知命令");
                }
            }
            System.out.println("客户端退出");
        }
    }
}
