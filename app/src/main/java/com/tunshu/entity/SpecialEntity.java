package com.tunshu.entity;

import com.tunshu.fragment.CommonFragment;

import java.util.List;

/**
 * Created by ShadowNight on 2015/8/20.
 */
public class SpecialEntity extends CommonFragment {

    /**
     * msg : The data load success!
     * code : 1001
     * data : {"preList":[{"bannerphone":"http://game.tunshu.com/CavyGameCenterfile//png/680bfe47c964aaec70a0af3942b50017.png","bgcolor":"#FFFFFF","prefectureId":1,"intro":"game prefecture 1","title":"game prefecture 1","type":"prefecture"},{"bannerphone":"http://game.tunshu.com/CavyGameCenterfile//png/851b9bcf1a1b5489b1a088479ae471bf.png","bgcolor":"#FFFFFF","prefectureId":2,"intro":"game prefecture 2","title":"game prefecture 2","type":"prefecture"},{"bannerphone":"http://game.tunshu.com/CavyGameCenterfile//png/4fb3898a8a8e19153762194d3a5e4cce.png","bgcolor":"#FFFFFF","prefectureId":3,"intro":"game prefecture 3","title":"game prefecture 3","type":"prefecture"},{"bannerphone":"http://game.tunshu.com/CavyGameCenterfile//jpg/1e18d6208bab69e8b19387f3d365912d.jpg","bgcolor":"#FFFFFF","prefectureId":4,"intro":"game prefecture 4","title":"game prefecture 4","type":"prefecture"},{"bannerphone":"","bgcolor":"#FFFFFF","prefectureId":5,"intro":"game prefecture 5","title":"game prefecture 5","type":"prefecture"},{"bannerphone":"http://game.tunshu.com/CavyGameCenterfile//png/e5c0b21034b899318d83138847b76216.png","bgcolor":"#FFFFFF","prefectureId":6,"intro":"game prefecture 6","title":"game prefecture 6","type":"prefecture"},{"bannerphone":"http://game.tunshu.com/CavyGameCenterfile//png/6a7dbf11ab0361b82ff1eb284697006d.png","bgcolor":"#FFFFFF","prefectureId":7,"intro":"game prefecture 7","title":"game prefecture 7","type":"prefecture"}]}
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
         * preList : [{"bannerphone":"http://game.tunshu.com/CavyGameCenterfile//png/680bfe47c964aaec70a0af3942b50017.png","bgcolor":"#FFFFFF","prefectureId":1,"intro":"game prefecture 1","title":"game prefecture 1","type":"prefecture"},{"bannerphone":"http://game.tunshu.com/CavyGameCenterfile//png/851b9bcf1a1b5489b1a088479ae471bf.png","bgcolor":"#FFFFFF","prefectureId":2,"intro":"game prefecture 2","title":"game prefecture 2","type":"prefecture"},{"bannerphone":"http://game.tunshu.com/CavyGameCenterfile//png/4fb3898a8a8e19153762194d3a5e4cce.png","bgcolor":"#FFFFFF","prefectureId":3,"intro":"game prefecture 3","title":"game prefecture 3","type":"prefecture"},{"bannerphone":"http://game.tunshu.com/CavyGameCenterfile//jpg/1e18d6208bab69e8b19387f3d365912d.jpg","bgcolor":"#FFFFFF","prefectureId":4,"intro":"game prefecture 4","title":"game prefecture 4","type":"prefecture"},{"bannerphone":"","bgcolor":"#FFFFFF","prefectureId":5,"intro":"game prefecture 5","title":"game prefecture 5","type":"prefecture"},{"bannerphone":"http://game.tunshu.com/CavyGameCenterfile//png/e5c0b21034b899318d83138847b76216.png","bgcolor":"#FFFFFF","prefectureId":6,"intro":"game prefecture 6","title":"game prefecture 6","type":"prefecture"},{"bannerphone":"http://game.tunshu.com/CavyGameCenterfile//png/6a7dbf11ab0361b82ff1eb284697006d.png","bgcolor":"#FFFFFF","prefectureId":7,"intro":"game prefecture 7","title":"game prefecture 7","type":"prefecture"}]
         */
        private List<PreListEntity> preList;
        private String bannerphone;
        private String style;
        private String intro;
        private String title;
        private List<GameListEntity> gameList;

        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getStyle() {
            return style;
        }

        public void setStyle(String style) {
            this.style = style;
        }

        public String getBannerphone() {
            return bannerphone;
        }

        public void setBannerphone(String bannerphone) {
            this.bannerphone = bannerphone;
        }

        public List<GameListEntity> getGameList() {
            return gameList;
        }

        public void setGameList(List<GameListEntity> gameList) {
            this.gameList = gameList;
        }

        public void setPreList(List<PreListEntity> preList) {
            this.preList = preList;
        }

        public List<PreListEntity> getPreList() {
            return preList;
        }


    }
}
