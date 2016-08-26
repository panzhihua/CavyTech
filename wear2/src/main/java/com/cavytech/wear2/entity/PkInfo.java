package com.cavytech.wear2.entity;

/**
 * Created by hf on 2016/5/24.
 */
public class PkInfo {

    private String code;
    private String msg;
    private int userStepCount;
    private int friendStepCount;
    private int isAllowWatch;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getUserStepCount() {
        return userStepCount;
    }

    public void setUserStepCount(int userStepCount) {
        this.userStepCount = userStepCount;
    }

    public int getFriendStepCount() {
        return friendStepCount;
    }

    public void setFriendStepCount(int friendStepCount) {
        this.friendStepCount = friendStepCount;
    }

    public int getIsAllowWatch() {
        return isAllowWatch;
    }

    public void setIsAllowWatch(int isAllowWatch) {
        this.isAllowWatch = isAllowWatch;
    }
}
