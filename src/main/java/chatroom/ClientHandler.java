package chatroom;

import chatroom.client.ChatClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by RichardYuan on 2017/5/18 0018.
 */
public class ClientHandler {

    private Socket socket;

    private ChatClient Client;

    public ClientHandler(Socket socket, ChatClient client) {
        this.socket = socket;
        Client = client;
    }

    public void startHandle() throws IOException {
        new Thread(new responseHandler(this.socket.getInputStream())).start();
        new Thread(new requestHandler()).start();
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
                    ClientHandler.this.socket.getOutputStream().write(CommonUtils.encode(inputMsg));
                } catch (IOException e) {

                }
            }
        }
    }
}
