package com.tunshu.entity;

/**
 * Created by ShadowNight on 2015/8/20.
 */
public class PreListEntity {
    /**
     * bannerphone : http://game.tunshu.com/CavyGameCenterfile//png/680bfe47c964aaec70a0af3942b50017.png
     * bgcolor : #FFFFFF
     * prefectureId : 1
     * intro : game prefecture 1
     * title : game prefecture 1
     * type : prefecture
     */
    private String bannerphone;
    private String bgcolor;
    private String prefectureId;
    private String intro;
    private String title;
    private String type;
    private String style;

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void setBannerphone(String bannerphone) {
        this.bannerphone = bannerphone;
    }

    public void setBgcolor(String bgcolor) {
        this.bgcolor = bgcolor;
    }

    public void setPrefectureId(String prefectureId) {
        this.prefectureId = prefectureId;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBannerphone() {
        return bannerphone;
    }

    public String getBgcolor() {
        return bgcolor;
    }

    public String getPrefectureId() {
        return prefectureId;
    }

    public String getIntro() {
        return intro;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }
}
