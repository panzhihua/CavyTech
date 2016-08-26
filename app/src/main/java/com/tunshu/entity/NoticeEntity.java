package com.tunshu.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ShadowNight on 2015/7/9.
 */
public class NoticeEntity extends CommonEntity implements Serializable{

    /**
     * msg : 列表更新成功
     * code : 1001
     * data : [{"create_time":"2015-06-30 20:01:00","receiver":0,"sender":0,"nickname":"豚鼠会员","msg_id":34,"title":"系统消息","content":"豚鼠游戏新版本App1.2上线了！","detia_url":""}]
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
         * create_time : 2015-06-30 20:01:00
         * receiver : 0
         * sender : 0
         * nickname : 豚鼠会员
         * msg_id : 34
         * title : 系统消息
         * content : 豚鼠游戏新版本App1.2上线了！
         * detia_url :
         */
        private String create_time;
        private String receiver;
        private String sender;
        private String nickname;
        private String msg_id;
        private String title;
        private String content;
        private String detailUrl;
        private String msgicon;
        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public void setReceiver(String receiver) {
            this.receiver = receiver;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public void setMsg_id(String msg_id) {
            this.msg_id = msg_id;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setContent(String content) {
            this.content = content;
        }


        public String getCreate_time() {
            return create_time;
        }

        public String getReceiver() {
            return receiver;
        }

        public String getSender() {
            return sender;
        }

        public String getNickname() {
            return nickname;
        }

        public String getMsg_id() {
            return msg_id;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public String getDetailUrl() {
            return detailUrl;
        }

        public void setDetailUrl(String detailUrl) {
            this.detailUrl = detailUrl;
        }

        public String getMsgicon() {
            return msgicon;
        }

        public void setMsgicon(String msgicon) {
            this.msgicon = msgicon;
        }
    }
}
