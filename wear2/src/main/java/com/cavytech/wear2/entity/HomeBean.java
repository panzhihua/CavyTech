package com.cavytech.wear2.entity;

import java.util.List;

/**
 * Created by doudou on 16/7/8.
 */
public class HomeBean {

    /**
     * start_date : 2016-07-05
     * end_date : 2016-07-05
     * dailies_data : [{"date":"2016-07-05","total_steps":2066,"total_steps_time":330,"total_sleep_time":570,"total_deep_time":414,"awards":[2]}]
     */

    private DataEntity data;
    /**
     * data : {"start_date":"2016-07-05","end_date":"2016-07-05","dailies_data":[{"date":"2016-07-05","total_steps":2066,"total_steps_time":330,"total_sleep_time":570,"total_deep_time":414,"awards":[2]}]}
     * code : 1000
     * msg :
     * time : 1467946062
     */

    private int code;
    private String msg;
    private int time;

    public void setData(DataEntity data) {
        this.data = data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public DataEntity getData() {
        return data;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public int getTime() {
        return time;
    }

    public static class DataEntity {
        private String start_date;
        private String end_date;
        /**
         * date : 2016-07-05
         * total_steps : 2066
         * total_steps_time : 330
         * total_sleep_time : 570
         * total_deep_time : 414
         * awards : [2]
         */

        private List<DailiesDataEntity> dailies_data;

        public void setStart_date(String start_date) {
            this.start_date = start_date;
        }

        public void setEnd_date(String end_date) {
            this.end_date = end_date;
        }

        public void setDailies_data(List<DailiesDataEntity> dailies_data) {
            this.dailies_data = dailies_data;
        }

        public String getStart_date() {
            return start_date;
        }

        public String getEnd_date() {
            return end_date;
        }

        public List<DailiesDataEntity> getDailies_data() {
            return dailies_data;
        }

        public static class DailiesDataEntity {
            private String date;
            private int total_steps;
            private int total_steps_time;
            private int total_sleep_time;
            private int total_deep_time;
            private List<Integer> awards;

            public void setDate(String date) {
                this.date = date;
            }

            public void setTotal_steps(int total_steps) {
                this.total_steps = total_steps;
            }

            public void setTotal_steps_time(int total_steps_time) {
                this.total_steps_time = total_steps_time;
            }

            public void setTotal_sleep_time(int total_sleep_time) {
                this.total_sleep_time = total_sleep_time;
            }

            public void setTotal_deep_time(int total_deep_time) {
                this.total_deep_time = total_deep_time;
            }

            public void setAwards(List<Integer> awards) {
                this.awards = awards;
            }

            public String getDate() {
                return date;
            }

            public int getTotal_steps() {
                return total_steps;
            }

            public int getTotal_steps_time() {
                return total_steps_time;
            }

            public int getTotal_sleep_time() {
                return total_sleep_time;
            }

            public int getTotal_deep_time() {
                return total_deep_time;
            }

            public List<Integer> getAwards() {
                return awards;
            }
        }
    }
}
