package com.cavytech.wear2.entity;

import java.util.List;

/**
 * Created by LiBin on 2016/6/14.
 */
public class RelatedAppBean {


    /**
     * code : 1000
     * game_list : [{"app_url":"https://cavy.oss-cn-qingdao.aliyuncs.com/apk/d11623cf407ba40cd81bfa2922d8f795.apk","desc":"☺灵活的小松鼠穿越长满仙人掌的沙漠，你能帮助它完成旅程么？","developer":"豚鼠科技","html_url":"http://pay.tunshu.com:8080/games/recommend/GKPrBvbJDv4XBsKFa","icon":"https://cavy.oss-cn-qingdao.aliyuncs.com/jpg/4d4a6d4c8110f9dc0ba10e22ed041db8.jpg","platform":"android","title":"算术跑酷","type":"益智早教","updatedAt":"2016-03-04 18:08:07.0"}]
     * msg :
     * time : 1468813317
     */

    private int code;
    private String msg;
    private int time;
    /**
     * app_url : https://cavy.oss-cn-qingdao.aliyuncs.com/apk/d11623cf407ba40cd81bfa2922d8f795.apk
     * desc : ☺灵活的小松鼠穿越长满仙人掌的沙漠，你能帮助它完成旅程么？
     * developer : 豚鼠科技
     * html_url : http://pay.tunshu.com:8080/games/recommend/GKPrBvbJDv4XBsKFa
     * icon : https://cavy.oss-cn-qingdao.aliyuncs.com/jpg/4d4a6d4c8110f9dc0ba10e22ed041db8.jpg
     * platform : android
     * title : 算术跑酷
     * type : 益智早教
     * updatedAt : 2016-03-04 18:08:07.0
     */

    private List<GameListBean> game_list;

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

    public List<GameListBean> getGame_list() {
        return game_list;
    }

    public void setGame_list(List<GameListBean> game_list) {
        this.game_list = game_list;
    }

    public static class GameListBean {
        private String app_url;
        private String desc;
        private String developer;
        private String html_url;
        private String icon;
        private String platform;
        private String title;
        private String type;
        private String updatedAt;

        public String getApp_url() {
            return app_url;
        }

        public void setApp_url(String app_url) {
            this.app_url = app_url;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getDeveloper() {
            return developer;
        }

        public void setDeveloper(String developer) {
            this.developer = developer;
        }

        public String getHtml_url() {
            return html_url;
        }

        public void setHtml_url(String html_url) {
            this.html_url = html_url;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}
