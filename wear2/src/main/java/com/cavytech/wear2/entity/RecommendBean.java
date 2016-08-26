package com.cavytech.wear2.entity;

import java.util.List;

/**
 * Created by hf on 2016/4/27.
 * 推荐好友
 */
public class RecommendBean extends CommonEntity {


    /**
     * code : 0000
     * friendInfos : [{"avatarUrl":"","distance":"","isFollow":0,"nickname":"","phoneNum":"","remark":"","sleepTime":"","stepNum":0,"userId":"","verifyMsg":""},{"avatarUrl":"","distance":"","isFollow":0,"nickname":"","phoneNum":"","remark":"","sleepTime":"","stepNum":0,"userId":"","verifyMsg":""},{"avatarUrl":"","distance":"","isFollow":0,"nickname":"","phoneNum":"","remark":"","sleepTime":"","stepNum":0,"userId":"","verifyMsg":""},{"avatarUrl":"","distance":"","isFollow":0,"nickname":"","phoneNum":"","remark":"","sleepTime":"","stepNum":0,"userId":"","verifyMsg":""},{"avatarUrl":"","distance":"","isFollow":0,"nickname":"","phoneNum":"","remark":"","sleepTime":"","stepNum":0,"userId":"","verifyMsg":""},{"avatarUrl":"","distance":"","isFollow":0,"nickname":"","phoneNum":"","remark":"","sleepTime":"","stepNum":0,"userId":"","verifyMsg":""},{"avatarUrl":"","distance":"","isFollow":0,"nickname":"","phoneNum":"","remark":"","sleepTime":"","stepNum":0,"userId":"","verifyMsg":""},{"avatarUrl":"","distance":"","isFollow":0,"nickname":"","phoneNum":"","remark":"","sleepTime":"","stepNum":0,"userId":"","verifyMsg":""},{"avatarUrl":"","distance":"","isFollow":0,"nickname":"","phoneNum":"","remark":"","sleepTime":"","stepNum":0,"userId":"","verifyMsg":""},{"avatarUrl":"","distance":"","isFollow":0,"nickname":"","phoneNum":"","remark":"","sleepTime":"","stepNum":0,"userId":"","verifyMsg":""},{"avatarUrl":"","distance":"","isFollow":0,"nickname":"","phoneNum":"","remark":"","sleepTime":"","stepNum":0,"userId":"","verifyMsg":""},{"avatarUrl":"","distance":"","isFollow":0,"nickname":"","phoneNum":"","remark":"","sleepTime":"7:51","stepNum":1000,"userId":"56d6ea3bd34635186c60492b","verifyMsg":""},{"avatarUrl":"","distance":"","isFollow":0,"nickname":"","phoneNum":"","remark":"","sleepTime":"4:58","stepNum":8000,"userId":"56e40684d346350f5cc01873","verifyMsg":""},{"avatarUrl":"","distance":"","isFollow":0,"nickname":"","phoneNum":"","remark":"","sleepTime":"4:58","stepNum":0,"userId":"56e4037fd346350f5cc0186f","verifyMsg":""},{"avatarUrl":"","distance":"","isFollow":0,"nickname":"","phoneNum":"","remark":"","sleepTime":"4:58","stepNum":0,"userId":"56e621ccd346350f5cc0187a","verifyMsg":""}]
     * msg : success
     */
    /**
     * avatarUrl :
     * distance :
     * isFollow : 0
     * nickname :
     * phoneNum :
     * remark :
     * sleepTime :
     * stepNum : 0
     * userId :
     * verifyMsg :
     */

    private List<FriendInfosBean> friendInfos;

    public List<FriendInfosBean> getFriendInfos() {
        return friendInfos;
    }

    public void setFriendInfos(List<FriendInfosBean> friendInfos) {
        this.friendInfos = friendInfos;
    }

    public static class FriendInfosBean {
        private String avatarUrl;
        private String distance;
        private int isFollow;
        private String nickname;
        private String phoneNum;
        private String remark;
        private String sleepTime;
        private int stepNum;
        private String userId;
        private String verifyMsg;

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public int getIsFollow() {
            return isFollow;
        }

        public void setIsFollow(int isFollow) {
            this.isFollow = isFollow;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getPhoneNum() {
            return phoneNum;
        }

        public void setPhoneNum(String phoneNum) {
            this.phoneNum = phoneNum;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getSleepTime() {
            return sleepTime;
        }

        public void setSleepTime(String sleepTime) {
            this.sleepTime = sleepTime;
        }

        public int getStepNum() {
            return stepNum;
        }

        public void setStepNum(int stepNum) {
            this.stepNum = stepNum;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getVerifyMsg() {
            return verifyMsg;
        }

        public void setVerifyMsg(String verifyMsg) {
            this.verifyMsg = verifyMsg;
        }
    }
}
