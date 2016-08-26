package com.cavytech.wear2.entity;

/**
 * Created by libin on 4/22 0022.
 * 邮箱：bin.li@tunshu.com
 */
public class WhetherEntity{
    /**
     * cond : 阵雨
     * tmp : 34
     * pm25 : 28
     */

    private DataBean data;
    /**
     * data : {"cond":"阵雨","tmp":34,"pm25":28}
     * code : 1000
     * msg :
     * time : 1468309928
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
        private String cond;
        private int tmp;
        private int pm25;

        public String getCond() {
            return cond;
        }

        public void setCond(String cond) {
            this.cond = cond;
        }

        public int getTmp() {
            return tmp;
        }

        public void setTmp(int tmp) {
            this.tmp = tmp;
        }

        public int getPm25() {
            return pm25;
        }

        public void setPm25(int pm25) {
            this.pm25 = pm25;
        }
    }

   /* *//**
     * condition : 多云
     * tmp : 34
     * pm25 : 127
     *//*

    private DataBean data;
    *//**
     * data : {"condition":"多云","tmp":34,"pm25":127}
     * code : 1000
     * msg :
     * time : 1468048519
     *//*

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
        private String condition;
        private int tmp;
        private int pm25;

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public int getTmp() {
            return tmp;
        }

        public void setTmp(int tmp) {
            this.tmp = tmp;
        }

        public int getPm25() {
            return pm25;
        }

        public void setPm25(int pm25) {
            this.pm25 = pm25;
        }
    }*/


}
