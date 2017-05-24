package chatroom.model;

import chatroom.util.CommonUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatMessage {

    private static final SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");

    private String content;

    private Date date;

    private String sender;

    private String receiver;

    public ChatMessage(String content, Date date, String sender, String receiver) {
        this.content = content;
        this.date = date;
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    @Override
    public String toString() {
        return
                 sender +
                "\t" + date +
                "\r\n '" + content + '\'';


    }

    public static ChatMessage newInstance(String content, String sender, String receiver) {
        return new ChatMessage(content, new Date(), sender, receiver);
    }
}
