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
            String msg = CommonUtils.readStream(in);
            parseInputInternal(msg);
        }
    }


    abstract void parseInputInternal(String msg) throws IOException;

}