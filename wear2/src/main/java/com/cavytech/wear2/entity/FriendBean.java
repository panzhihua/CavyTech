package com.cavytech.wear2.entity;

import java.util.List;

/**
 * Created by hf on 2016/4/22.
 * 好友列表
 */
public class FriendBean extends CommonEntity{


    /**
     * code : 0000
     * friendInfos : [{"avatarUrl":"res/images/default_head_boy.png","distance":"","isFollow":0,"nickname":"豚鼠会员-1972737213","phoneNum":"","sleepTime":"","stepNum":0,"userId":"57198c07d3463513589c0277","verifyMsg":""},{"avatarUrl":"","distance":"","isFollow":0,"nickname":"","phoneNum":"","sleepTime":"4:58","stepNum":8000,"userId":"56e4072ad346350f5cc01875","verifyMsg":""}]
     * msg : success
     */

    /**
     * avatarUrl : res/images/default_head_boy.png
     * distance :
     * isFollow : 0
     * nickname : 豚鼠会员-1972737213
     * phoneNum :
     * sleepTime :
     * stepNum : 0
     * userId : 57198c07d3463513589c0277
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
        private String userId;
        private String avatarUrl;
        private int isFollow;
        private String nickname;
        private String phoneNum;
        private String distance;
        private String sleepTime;
        private int stepNum;
        private String remark;
        private String verifyMsg;

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

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
