package com.cavytech.wear2.entity;

import com.cavytech.wear2.http.HttpUtils;

/**
 * Created by doudou on 16/7/6.
 */
public class UserCommonEntity {
    private int code;
    private String msg;
    private int time;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void seStringde(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccess(){

        if(this.code == HttpUtils.newSUCCESS ){
            return true;
        }else{
            return false;
        }
    }
}
