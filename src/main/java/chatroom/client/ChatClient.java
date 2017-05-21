package chatroom.client;

import chatroom.ChatProtocol;
import chatroom.ClientHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by RichardYuan on 2017/5/17 0017.
 */
public class ChatClient implements Runnable {

    @Override
    public void run() {
        try (Socket socket = new Socket("localhost", 4396)) {
            new ClientHandler(socket, this).startHandle();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    public static void main(String[] args) {
        new ChatClient().run();
    }
}
