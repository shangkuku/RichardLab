package chatroom.client;

import chatroom.exception.UnknownCommandException;
import chatroom.parser.InputParser;
import chatroom.model.Command;
import chatroom.server.ChatProtocol;
import chatroom.util.CommonUtils;
import chatroom.util.LogUtils;

import java.io.IOException;
import java.io.InputStream;
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

    private class responseHandler extends InputParser {

        private responseHandler(InputStream in) {
            super(in);
        }

        @Override
        protected void shutdown() {
            ClientHandler.this.running = false;
            ClientHandler.this.scanner.close();
            ClientHandler.this.client.shutdown();
        }

        @Override
        public void parseInputInternal(String msg) throws IOException {
            LogUtils.log(msg);
        }
    }

    private class RequestHandler implements Runnable {

        private ClientHandler clientHandler = ClientHandler.this;

        @Override
        public void run() {
            System.out.println("请先登陆啦");
            while (running) {
                String inputMsg = scanner.nextLine();
                if (!running) break;
                try {
                    String[] m = inputMsg.split("\\s");
                    CommonUtils.writeMessage(clientHandler.client.socket.getOutputStream(), inputMsg);
                    if (Command.LOGIN.equals(Command.resolveCommand(m[0]))) {
                        String res = CommonUtils.readStream(client.socket.getInputStream());
                        if (ChatProtocol.SUCCESS_FLAG.equals(res)) {
                            clientHandler.client.user = m[1];
                            Thread t = new Thread(new responseHandler(client.socket.getInputStream()));
                            t.setDaemon(true);
                            t.setName("Client Reader");
                            t.start();
                        }
                    }
                } catch (IOException e) {
                    clientHandler.client.shutdown();
                } catch (UnknownCommandException ee) {
                    System.out.println("未知命令");
                }
            }
            System.out.println("客户端退出");
        }
    }
}
