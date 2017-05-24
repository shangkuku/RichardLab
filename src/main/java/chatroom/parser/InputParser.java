package chatroom.parser;

import chatroom.util.CommonUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by RichardYuan on 2017/5/21 0021.
 */
public abstract class InputParser implements Runnable {

    private InputStream in;

    public InputParser(InputStream in) {
        this.in = in;
    }


    @Override
    public void run() {
        try {
            parseInput();
        } catch (IOException e) {
            shutdown();
        }

    }

    abstract protected void shutdown();

    protected void parseInput() throws IOException {

        while (true) {
            String msg = CommonUtils.readStream(in);
            parseInputInternal(msg);
        }
    }


    public abstract void parseInputInternal(String msg) throws IOException;

}