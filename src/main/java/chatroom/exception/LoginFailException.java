package chatroom.exception;

import javax.security.auth.login.LoginException;

/**
 * Created by RichardYuan on 2017/5/18 0018.
 */
public class LoginFailException extends LoginException {

    public LoginFailException(String msg) {
        super(msg);
    }
}
