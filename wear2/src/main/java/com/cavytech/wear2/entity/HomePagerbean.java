package com.cavytech.wear2.entity;

import java.util.List;

/**
 * Created by LiBin on 2016/5/24.
 */
public class HomePagerbean {

    /**
     * achieveList : [1,2]
     * code : 0000
     * healthList : [{"iconUrl":"http://115.28.144.243/cavylife/res/icon/8l30hLB9RB-x98zVJo_WsQ.png","nickName":"豚鼠会员-1972737213","userId":"5722cd22d346351aac65de47"}]
     * msg : success
     * pkList : [{"friendName":"豚鼠会员-1972737213","pkId":"57443029d346350a9438ae9b","status":1}]
     * sleepTime : 0
     * stepCount : 10000
     */

    private String code;
    private String msg;
    private int sleepTime;
    private int stepCount;
    private List<Integer> achieveList;
    /**
     * iconUrl : http://115.28.144.243/cavylife/res/icon/8l30hLB9RB-x98zVJo_WsQ.png
     * nickName : 豚鼠会员-1972737213
     * userId : 5722cd22d346351aac65de47
     */

    private List<HealthListBean> healthList;
    /**
     * friendName : 豚鼠会员-1972737213
     * pkId : 57443029d346350a9438ae9b
     * status : 1
     */

    private List<PkListBean> pkList;

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

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public List<Integer> getAchieveList() {
        return achieveList;
    }

    public void setAchieveList(List<Integer> achieveList) {
        this.achieveList = achieveList;
    }

    public List<HealthListBean> getHealthList() {
        return healthList;
    }

    public void setHealthList(List<HealthListBean> healthList) {
        this.healthList = healthList;
    }

    public List<PkListBean> getPkList() {
        return pkList;
    }

    public void setPkList(List<PkListBean> pkList) {
        this.pkList = pkList;
    }

    public static class HealthListBean {
        private String iconUrl;
        private String nickName;
        private String userId;

        public String getIconUrl() {
            return iconUrl;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

    public static class PkListBean {
        private String friendName;
        private String pkId;
        private int status;

        public String getFriendName() {
            return friendName;
        }

        public void setFriendName(String friendName) {
            this.friendName = friendName;
        }

        public String getPkId() {
            return pkId;
        }

        public void setPkId(String pkId) {
            this.pkId = pkId;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
