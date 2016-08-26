package com.cavytech.wear2.entity;

import java.util.List;

/**
 * Created by hf on 2016/5/5.
 * 新的朋友
 */
public class NewFriendBean extends CommonEntity{

    /**
     * avatarUrl : res/images/default_head_boy.png
     * nickname : 豚鼠会员-1972737213
     * userId : 5722cd22d346351aac65de47
     * verifyMsg : 哈喽，我是豚鼠会员-1972737213
     */

    private List<UserInfosBean> userInfos;

    public List<UserInfosBean> getUserInfos() {
        return userInfos;
    }

    public void setUserInfos(List<UserInfosBean> userInfos) {
        this.userInfos = userInfos;
    }

    public static class UserInfosBean {
        private String avatarUrl;
        private String nickname;
        private String userId;
        private String verifyMsg;

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
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
