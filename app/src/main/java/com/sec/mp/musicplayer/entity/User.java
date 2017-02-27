package com.sec.mp.musicplayer.entity;

/**
 * Created by Administrator on 2017/2/27.
 */
public class User {
    /**
     * token : 12dasd21
     * userId : ferrari@porsche.com
     * userName : Ferrari
     * phoneNumber : 18888888888
     * email : ferrari@porsche.com
     */

    private String token;
    private String userId;
    private String userName;
    private String phoneNumber;
    private String email;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
