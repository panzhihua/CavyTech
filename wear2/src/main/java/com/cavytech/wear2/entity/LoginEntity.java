package com.cavytech.wear2.entity;

/**
 * Created by longjining on 16/4/18.
 */
public class LoginEntity  extends CommonEntity{

    /**
     * user_id : 3BiHxcDQJsgcrSEp4
     * username : 15811111112
     * auth_token : rkep699swhFPnqjW8
     * time : 1467601252
     */

    private String user_id;
    private String username;
    private String auth_token;
    private int time;

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public String getAuth_token() {
        return auth_token;
    }

    public int getTime() {
        return time;
    }
}
