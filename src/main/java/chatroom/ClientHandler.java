package chatroom;

import chatroom.client.ChatClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by RichardYuan on 2017/5/18 0018.
 */
public class ClientHandler {

    private ChatClient client;

    public ClientHandler(ChatClient client) {
        this.client = client;
    }

    public void startHandle() throws IOException {
        new Thread(new requestHandler()).run();
    }

    private class responseHandler extends InputHandler {

        private responseHandler(InputStream in) {
            super(in);
        }

        @Override
        void parseInputInternal(String msg) throws IOException {
            LogUtils.log(msg);
        }
    }

    private class requestHandler implements Runnable {

        @Override
        public void run() {
                Scanner scanner = new Scanner( System.in );
            while (true) {
                String inputMsg = scanner.nextLine();
                try {
                    String[] m = inputMsg.split("\\s");

                    ClientHandler.this.client.socket.getOutputStream().write(CommonUtils.encode(inputMsg));
                    if (Command.LOGIN.equals(Command.resolveCommand(m[0]))) {
                        String res = CommonUtils.readStream(client.socket.getInputStream());
                        if (ChatProtocol.SUCCESS_FLAG.equals(res)) {
                            ClientHandler.this.client.user = m[1];
                            new Thread(new responseHandler(client.socket.getInputStream())).start();
                        }
                    }
                } catch (IOException e) {

                }
            }
        }
    }
}
