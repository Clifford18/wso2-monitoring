package ke.co.skyworld.domain.beans;

import ke.co.skyworld.repository.beans.FlexicoreHashMap;

/**
 * skyworld-api (ke.co.skyworld.domain.beans)
 * Created By: elon
 * On: 12 Mar, 2019 (12/03/19 21:04)
 **/
public class AuthenticationStatus {

    private boolean authenticated;
    private String message;
    private FlexicoreHashMap access_token;
    private FlexicoreHashMap user_account;

    public AuthenticationStatus(){}

    public AuthenticationStatus(boolean authenticated, String message, FlexicoreHashMap user_account) {
        this.authenticated = authenticated;
        this.message = message;
        this.user_account = user_account;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public FlexicoreHashMap getAccess_token() {
        return access_token;
    }

    public void setAccess_token(FlexicoreHashMap access_token) {
        this.access_token = access_token;
    }

    public FlexicoreHashMap getUser_account() {
        return user_account;
    }

    public void setUser_account(FlexicoreHashMap flexicoreHashmap) {
        this.user_account = flexicoreHashmap;
    }

    @Override
    public String toString() {
        return "AuthenticationStatus{" +
                "authenticated=" + authenticated +
                ", message='" + message + '\'' +
                ", accessToken=" + access_token +
                ", userAccount=" + user_account +
                '}';
    }
}
