package chatroom.client;

import chatroom.util.CommonUtils;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by RichardYuan on 2017/5/17 0017.
 */
public class ChatClient {


    public Socket socket;

    public String user;

    public void startup() {
        try (Socket socket = new Socket("localhost", 4396)) {
            this.socket = socket;
            new ClientHandler(this).startHandle();
        } catch (IOException e) {
            System.out.println("连接超时");
        }


    }


    public static void main(String[] args) {
        new ChatClient().startup();
    }

    public void shutdown() {
        try {
            this.socket.close();
        } catch (IOException e) {
            CommonUtils.unknownException(e);
        }
    }
}
