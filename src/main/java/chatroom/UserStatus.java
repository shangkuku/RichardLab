package chatroom;

/**
 * Created by RichardYuan on 2017/5/16 0016.
 */
public class UserStatus {

    private long lastActiveTime;

    private short loginTimes;


    public long getLastActiveTime() {
        return lastActiveTime;
    }

    public void setLastActiveTime(long lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }

    public short getLoginTimes() {
        return loginTimes;
    }

    public void setLoginTimes(short loginTimes) {
        this.loginTimes = loginTimes;
    }
}
