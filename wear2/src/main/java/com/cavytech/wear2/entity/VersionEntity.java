package com.cavytech.wear2.entity;

/**
 * Created by ShadowNight on 2015/8/28.
 */
public class VersionEntity extends CommonEntity {
    private String build;
    private String intro;
    private String needupdate;
    private String pagename;
    private String url;
    private String version;
    private String icon;
    private String size;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getNeedupdate() {
        return needupdate;
    }

    public void setNeedupdate(String needupdate) {
        this.needupdate = needupdate;
    }

    public String getPagename() {
        return pagename;
    }

    public void setPagename(String pagename) {
        this.pagename = pagename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
