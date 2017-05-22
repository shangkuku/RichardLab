package chatroom;

import java.io.*;
import java.util.stream.Collectors;

/**
 * Created by RichardYuan on 2017/5/21 0021.
 */
public abstract class InputHandler implements Runnable {

    private InputStream in;

    InputHandler(InputStream in) {
        this.in = in;
    }


    @Override
    public void run() {
        try {
            parseInput();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void parseInput() throws IOException {

        while (true) {

            byte[] b = new byte[2048];
            int length;
            String msg = null;
            while ((length = in.read(b)) != -1) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
                baos.write(b, 0, length);
                msg = baos.toString(ChatProtocol.DEFAULT_CHARSET.toString());
                parseInputInternal(msg);
            }
//             msg = new BufferedReader(new InputStreamReader(in, ChatProtocol.DEFAULT_CHARSET))
//                    .lines().collect(Collectors.joining("\n"));
        }
    }


    abstract void parseInputInternal(String msg) throws IOException;

}