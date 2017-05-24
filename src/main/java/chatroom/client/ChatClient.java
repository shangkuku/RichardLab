package chatroom.client;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by RichardYuan on 2017/5/17 0017.
 */
public class ChatClient implements Runnable {


    public Socket socket;

    public String user;

    @Override
    public void run() {
        try (Socket socket = new Socket("localhost", 4396)) {
            this.socket = socket;
            new ClientHandler(this).startHandle();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public static void main(String[] args) {
        new ChatClient().run();
    }
}
