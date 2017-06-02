package chatroom.server;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by RichardYuan on 2017/5/18 0018.
 */
public class ChatProtocol {

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;


    public static final String SUCCESS_FLAG = "success";

    public static final String END_FLAG = "$%^!@$#^&%!@#";

    public static final String SERVER_END = "quit";


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
