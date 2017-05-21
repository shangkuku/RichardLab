package chatroom;

/**
 * Created by RichardYuan on 2017/5/17 0017.
 */
public class User {

    private String userName;
    private String password;

    private long lastActiveTime = CommonUtils.now();

    private short loginCounts = 0;

    public void incLoginCount() {
        this.loginCounts++;
    }

    public void clearLoginCounts() {
        this.loginCounts = 0;
    }

    public long getLastActiveTime() {
        return lastActiveTime;
    }

    public void setLastActiveTime(long lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }

    public short getLoginCounts() {
        return loginCounts;
    }

    public void setLoginCounts(short loginCounts) {
        this.loginCounts = loginCounts;
    }


    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
