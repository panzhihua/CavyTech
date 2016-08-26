package com.cavytech.wear2.entity;

/**
 * Created by LiBin on 2016/7/8.
 */
public class IconBean {


    /**
     * url : http://pay.tunshu.com/cdn/storage/avatarfiles/9PpDmWgYWqCMNgJNT/original/9PpDmWgYWqCMNgJNT.png
     */

    private DataBean data;
    /**
     * data : {"url":"http://pay.tunshu.com/cdn/storage/avatarfiles/9PpDmWgYWqCMNgJNT/original/9PpDmWgYWqCMNgJNT.png"}
     * code : 1000
     * msg :
     * time : 1467957942
     */

    private int code;
    private String msg;
    private int time;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public static class DataBean {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
