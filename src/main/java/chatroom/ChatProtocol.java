package chatroom;

import chatroom.server.ChatServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by RichardYuan on 2017/5/18 0018.
 */
public class ChatProtocol {

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;


    private ChatServer server;

    private int maxClient = 2;

    public ChatProtocol(ChatServer server) {
        this.server = server;

    }

    public void startHandle(Socket socket) throws IOException {
        createHandler(socket).handle();
    }



    private ServerHandler createHandler(Socket socket) {
        return new ServerHandler(socket, server);
    }

}
