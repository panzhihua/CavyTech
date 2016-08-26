package com.cavytech.wear2.entity;

import com.cavytech.wear2.http.HttpUtils;

/**
 * Created by ShadowNight on 2015/7/9.
 */
public class CommonEntity {
    private int code;
    private String msg;

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
