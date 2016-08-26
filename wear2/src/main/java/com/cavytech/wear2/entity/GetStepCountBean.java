package com.cavytech.wear2.entity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.List;

/**
 * Created by LiBin on 2016/5/25.
 */
public class GetStepCountBean {
    /**
     * start_date : 2016-06-05
     * end_date : 2016-06-20
     * steps_data : [{"date":"2016-06-17","total_steps":0,"total_time":0,"hours":[{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0}]},{"date":"2016-06-18","total_steps":200,"total_time":20,"hours":[{"steps":200,"time":20},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0}]}]
     */

    private DataBean data;
    /**
     * data : {"start_date":"2016-06-05","end_date":"2016-06-20","steps_data":[{"date":"2016-06-17","total_steps":0,"total_time":0,"hours":[{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0}]},{"date":"2016-06-18","total_steps":200,"total_time":20,"hours":[{"steps":200,"time":20},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0}]}]}
     * code : 1000
     * msg :
     * time : 1467094143
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
        private String start_date;
        private String end_date;
        /**
         * date : 2016-06-17
         * total_steps : 0
         * total_time : 0
         * hours : [{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0},{"steps":0,"time":0}]
         */

        private List<StepsDataBean> steps_data;

        public String getStart_date() {
            return start_date;
        }

        public void setStart_date(String start_date) {
            this.start_date = start_date;
        }

        public String getEnd_date() {
            return end_date;
        }

        public void setEnd_date(String end_date) {
            this.end_date = end_date;
        }

        public List<StepsDataBean> getSteps_data() {
            return steps_data;
        }

        public void setSteps_data(List<StepsDataBean> steps_data) {
            this.steps_data = steps_data;
        }

        @Table(name = "stepday")
        public static class StepsDataBean {
            @Column(name = "date",isId=true,autoGen=false)
            private String date;
            @Column(name = "total_steps")
            private int total_steps;
            @Column(name = "total_time")
            private int total_time;

            /**
             * steps : 0
             * time : 0
             */
            private List<HoursBean> hours;

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public int getTotal_steps() {
                return total_steps;
            }

            public void setTotal_steps(int total_steps) {
                this.total_steps = total_steps;
            }

            public int getTotal_time() {
                return total_time;
            }

            public void setTotal_time(int total_time) {
                this.total_time = total_time;
            }

            public List<HoursBean> getHours() {
                return hours;
            }

            public void setHours(List<HoursBean> hours) {
                this.hours = hours;
            }

            @Table(name = "stephours")
            public static class HoursBean {
                @Column(name = "stepCount")
                private int steps;
                @Column(name="dateTime",isId=true,autoGen=false)
                private String time;

                public int getSteps() {
                    return steps;
                }

                public void setSteps(int steps) {
                    this.steps = steps;
                }

                public String getTime() {
                    return time;
                }

                public void setTime(String time) {
                    this.time = time;
                }
            }
        }
    }

    /**
     * code : 0000
     * msg : success
     * stepList : [{"dateTime":"2016-05-24 18:56:52","stepCount":10000}]
     *//*

    private String code;
    private String msg;
    *//**
     * dateTime : 2016-05-24 18:56:52
     * stepCount : 10000
     *//*

    private List<StepListBean> stepList;

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

    public List<StepListBean> getStepList() {
        return stepList;
    }

    public void setStepList(List<StepListBean> stepList) {
        this.stepList = stepList;
    }


    @Table(name = "step")
    public static class StepListBean {

        @Column(name="dateTime",isId=true,autoGen=false)
        private String dateTime;

        @Column(name = "stepCount")
        private int stepCount;

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public int getStepCount() {
            return stepCount;
        }

        public void setStepCount(int stepCount) {
            this.stepCount = stepCount;
        }
    }*/



}
