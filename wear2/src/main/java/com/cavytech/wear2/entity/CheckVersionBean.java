package com.cavytech.wear2.entity;

import java.io.Serializable;

/**
 * Created by LiBin on 2016/7/6.
 * 检查版本更新
 */
public class CheckVersionBean {


    /**
     * code : 1000
     * data : {"description":"版本描述","reversion":4,"url":"pay.tunshu.com/download/bin_file.pkg","version":"0.0.4"}
     * msg :
     * time : 1467769325
     */

    private int code;
    /**
     * description : 版本描述
     * reversion : 4
     * url : pay.tunshu.com/download/bin_file.pkg
     * version : 0.0.4
     */

    private DataBean data;
    private String msg;
    private int time;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
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

    public static class DataBean implements Serializable{
        private String description;
        private int reversion;
        private String url;
        private String version;
        private boolean force_update;
        private String publish_date;
        private int size;

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getPublish_date() {
            return publish_date;
        }

        public void setPublish_date(String publish_date) {
            this.publish_date = publish_date;
        }

        public boolean isForce_update() {
            return force_update;
        }

        public void setForce_update(boolean force_update) {
            this.force_update = force_update;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getReversion() {
            return reversion;
        }

        public void setReversion(int reversion) {
            this.reversion = reversion;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
