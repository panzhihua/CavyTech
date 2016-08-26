package com.tunshu.entity;

import java.util.List;

/**
 * Created by ShadowNight on 2015/7/16.
 */
public class GameDetailEntity extends CommonEntity {

    /**
     * msg : 数据加载成功
     * code : 1001
     * data : {"gameid":"00f83b4e85764008bc68dc3f265ae505","gamename":"testtest","icon":"http://192.168.3.44:8080/CavyGameCenterfile//png/c7c782cae089ee2d9bba4b3a72a3f6cd.png","gamedetial":"testtest","updateTime":"2015-07-08 11:25:30.0","filesize":"34.0","version":"1","viewimage":[{"image":"http://192.168.3.44:8080/CavyGameCenterfile//png/6afaa5d8626a891bea0b1a30011019c5.png"}],"downcount":"0","downurl":"http://192.168.3.44:8080/CavyGameCenterfile//apk/a1c6061c4d227219622324d82c1e2dd3.apk","smallviewimage":[{"image":"http://192.168.3.44:8080/CavyGameCenterfile//png/6afaa5d8626a891bea0b1a30011019c5.png"}],"gamedesc":"testtest","gametype":"ANDROID"}
     */
    private String msg;
    private String code;
    private DataEntity data;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public String getCode() {
        return code;
    }

    public DataEntity getData() {
        return data;
    }

    public class DataEntity {
        /**
         * gameid : 00f83b4e85764008bc68dc3f265ae505
         * gamename : testtest
         * icon : http://192.168.3.44:8080/CavyGameCenterfile//png/c7c782cae089ee2d9bba4b3a72a3f6cd.png
         * gamedetial : testtest
         * updateTime : 2015-07-08 11:25:30.0
         * filesize : 34.0
         * version : 1
         * viewimage : [{"image":"http://192.168.3.44:8080/CavyGameCenterfile//png/6afaa5d8626a891bea0b1a30011019c5.png"}]
         * downcount : 0
         * downurl : http://192.168.3.44:8080/CavyGameCenterfile//apk/a1c6061c4d227219622324d82c1e2dd3.apk
         * smallviewimage : [{"image":"http://192.168.3.44:8080/CavyGameCenterfile//png/6afaa5d8626a891bea0b1a30011019c5.png"}]
         * gamedesc : testtest
         * gametype : ANDROID
         */
        private String gameid;
        private String gamename;
        private String icon;
        private String gamedetial;
        private String updateTime;
        private String filesize;
        private String version;
        private String viewVersion;
        private List<ImageItemEntity> viewimage;
        private List<ImageItemEntity> verimage;
        private String downcount;
        private String downurl;
        private String gamedesc;
        private String gametype;
        private String developers;
        private String pageName;
        private String shareUrl;
        private String situation;

        public String getPageName() {
            return pageName;
        }

        public void setPageName(String pageName) {
            this.pageName = pageName;
        }

        public String getShareUrl() {
            return shareUrl;
        }

        public void setSituation(String situation)
        {
            this.situation = situation;
        }

        public String getSituation()
        {
            return this.situation;
        }

        public void setShareUrl(String shareUrl) {
            this.shareUrl = shareUrl;
        }

        public List<ImageItemEntity> getViewimage() {
            return viewimage;
        }

        public void setViewimage(List<ImageItemEntity> viewimage) {
            this.viewimage = viewimage;
        }

        public String getDevelopers() {
            return developers;
        }

        public void setDevelopers(String developers) {
            this.developers = developers;
        }

        public void setGameid(String gameid) {
            this.gameid = gameid;
        }

        public void setGamename(String gamename) {
            this.gamename = gamename;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public void setGamedetial(String gamedetial) {
            this.gamedetial = gamedetial;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public void setFilesize(String filesize) {
            this.filesize = filesize;
        }

        public void setVersion(String version) {
            this.version = version;
        }
        public void setViewVersion(String version) {
            this.viewVersion = version;
        }


        public void setDowncount(String downcount) {
            this.downcount = downcount;
        }

        public void setDownurl(String downurl) {
            this.downurl = downurl;
        }

        public List<ImageItemEntity> getVerimage() {
            return verimage;
        }

        public void setVerimage(List<ImageItemEntity> verimage) {
            this.verimage = verimage;
        }

        public void setGamedesc(String gamedesc) {
            this.gamedesc = gamedesc;
        }

        public void setGametype(String gametype) {
            this.gametype = gametype;
        }

        public String getGameid() {
            return gameid;
        }

        public String getGamename() {
            return gamename;
        }

        public String getIcon() {
            return icon;
        }

        public String getGamedetial() {
            return gamedetial;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public String getFilesize() {
            return filesize;
        }

        public String getVersion() {
            return version;
        }
        public String getViewVersion() {
            return viewVersion;
        }

        public String getDowncount() {
            return downcount;
        }

        public String getDownurl() {
            return downurl;
        }

        public String getGamedesc() {
            return gamedesc;
        }

        public String getGametype() {
            return gametype;
        }

        public class ViewimageEntity {
            /**
             * image : http://192.168.3.44:8080/CavyGameCenterfile//png/6afaa5d8626a891bea0b1a30011019c5.png
             */
            private String image;

            public void setImage(String image) {
                this.image = image;
            }

            public String getImage() {
                return image;
            }
        }

        public class SmallviewimageEntity {
            /**
             * image : http://192.168.3.44:8080/CavyGameCenterfile//png/6afaa5d8626a891bea0b1a30011019c5.png
             */
            private String image;

            public void setImage(String image) {
                this.image = image;
            }

            public String getImage() {
                return image;
            }
        }
    }
}
