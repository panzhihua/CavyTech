package com.tunshu.entity;

import java.util.List;

/**
 * Created by ShadowNight on 2015/7/16.
 */
public class CommentEntity extends CommonEntity{

    /**
     * msg : 数据加载成功
     * code : 1001
     * data : [{"com_content":"很好玩","nickname":"游客","com_datetime":"2015-06-08 00:10:04","avatar":"http://game.tunshu.com/images/touxiang2.jpg","userId":"-1"},{"com_content":"游戏好玩","nickname":"游客","com_datetime":"2015-06-08 00:09:50","avatar":"http://game.tunshu.com/images/touxiang2.jpg","userId":"-1"}]
     */
    private List<DataEntity> data;


    public void setData(List<DataEntity> data) {
        this.data = data;
    }


    public List<DataEntity> getData() {
        return data;
    }

    public class DataEntity {
        /**
         * com_content : 很好玩
         * nickname : 游客
         * com_datetime : 2015-06-08 00:10:04
         * avatar : http://game.tunshu.com/images/touxiang2.jpg
         * userId : -1
         */
        private String com_content;
        private String nickname;
        private String com_datetime;
        private String avatar;
        private String userId;
        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setCom_content(String com_content) {
            this.com_content = com_content;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public void setCom_datetime(String com_datetime) {
            this.com_datetime = com_datetime;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getCom_content() {
            return com_content;
        }

        public String getNickname() {
            return nickname;
        }

        public String getCom_datetime() {
            return com_datetime;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getUserId() {
            return userId;
        }
    }
}
