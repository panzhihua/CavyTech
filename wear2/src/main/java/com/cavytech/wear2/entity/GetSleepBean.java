package com.cavytech.wear2.entity;

import java.util.List;

/**
 * Created by LiBin on 2016/5/25.
 */
public class GetSleepBean {

    /**
     * code : 0000
     * msg : success
     * stepList : [{"dateTime":"2016-05-24 18:56:52","stepCount":10000}]
     */

    private String code;
    private String msg;

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

    public List<SleepListBean> getSleepList() {
        return sleepList;
    }

    public void setSleepList(List<SleepListBean> sleepList) {
        this.sleepList = sleepList;
    }

    /**
     * dateTime : 2016-05-24 18:56:52
     * stepCount : 10000
     */

    private List<SleepListBean> sleepList;


    public static class SleepListBean {

        private String dateTime;

        private int rollCount;


        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public SleepListBean() {
        }

        public SleepListBean(int rollCount, String dateTime) {
            this.rollCount = rollCount;
            this.dateTime = dateTime;

        }


        public int getRollCount() {
            return rollCount;
        }

        public void setRollCount(int rollCount) {
            this.rollCount = rollCount;
        }



    }
}
