package com.cavytech.wear2.entity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.List;

/**
 * Created by LiBin on 2016/6/29.
 */
public class GetSleepentity {

    /**
     * code : 1000
     * data : {"end_date":"2016-06-20","sleep_data":[{"date":"2016-06-17","deep_time":0,"total_time":0},{"date":"2016-06-18","deep_time":0,"total_time":0}],"start_date":"2016-06-05"}
     * msg :
     * time : 1467181036
     */

    private int code;
    /**
     * end_date : 2016-06-20
     * sleep_data : [{"date":"2016-06-17","deep_time":0,"total_time":0},{"date":"2016-06-18","deep_time":0,"total_time":0}]
     * start_date : 2016-06-05
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

    public static class DataBean {
        private String end_date;
        private String start_date;
        /**
         * date : 2016-06-17
         * deep_time : 0
         * total_time : 0
         */

        private List<SleepDataBean> sleep_data;

        public String getEnd_date() {
            return end_date;
        }

        public void setEnd_date(String end_date) {
            this.end_date = end_date;
        }

        public String getStart_date() {
            return start_date;
        }

        public void setStart_date(String start_date) {
            this.start_date = start_date;
        }

        public List<SleepDataBean> getSleep_data() {
            return sleep_data;
        }

        public void setSleep_data(List<SleepDataBean> sleep_data) {
            this.sleep_data = sleep_data;
        }

        @Table(name = "sleepData")
        public static class SleepDataBean {
            @Column(name="dateTime",isId=true,autoGen=false)
            private String date;
            @Column(name = "deep_time")
            private int deep_time;
            @Column(name = "total_time")
            private int total_time;

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public int getDeep_time() {
                return deep_time;
            }

            public void setDeep_time(int deep_time) {
                this.deep_time = deep_time;
            }

            public int getTotal_time() {
                return total_time;
            }

            public void setTotal_time(int total_time) {
                this.total_time = total_time;
            }
        }
    }

}
