package com.tunshu.entity;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

import java.io.Serializable;

/**
 * 作者：yzb on 2015/7/14 17:18
 */
@SuppressWarnings("serial")
@Table(name = "download_list")
public class GameListEntity implements Serializable {
    @Id(column = "id")
    private int id;
    private String state;
    private int taskId;
    private String percent;
    private long downloadedLength;
    private String icon;
    private String gamename;
    private String filesize;
    private String gamedesc;
    private String downurl="";
    private String packpagename;
    private String version;
    private String downcount;
    private String gameid;
    private String actionText;
    private String actionTag;
    //todo
    private String topnum;
    private String bannerphone;
    private String intro;
    private String prefectureId;
    private String title;
    private String type;
    private String style;

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

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getPrefectureId() {
        return prefectureId;
    }

    public void setPrefectureId(String prefectureId) {
        this.prefectureId = prefectureId;
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

    public String getActionTag() {
        return actionTag;
    }

    public void setActionTag(String actionTag) {
        this.actionTag = actionTag;
    }

    public String getTopnum() {
        return topnum;
    }

    public void setTopnum(String topnum) {
        this.topnum = topnum;
    }

    public long getDownloadedLength() {
        return downloadedLength;
    }

    public void setDownloadedLength(long downloadedLength) {
        this.downloadedLength = downloadedLength;
    }

    public String getFilesize() {
        return filesize;
    }

    public void setFilesize(String filesize) {
        this.filesize = filesize;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getActionText() {
        return actionText;
    }

    public void setActionText(String actionText) {
        this.actionText = actionText;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }


    public String getGameid() {
        return gameid;
    }

    public void setGameid(String gameid) {
        this.gameid = gameid;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setGamename(String gamename) {
        this.gamename = gamename;
    }


    public void setGamedesc(String gamedesc) {
        this.gamedesc = gamedesc;
    }

    public void setDownurl(String downurl) {
        this.downurl = downurl;
    }

    public void setPackpagename(String packpagename) {
        this.packpagename = packpagename;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setDowncount(String downcount) {
        this.downcount = downcount;
    }

    public String getIcon() {
        return icon;
    }

    public String getGamename() {
        return gamename;
    }


    public String getGamedesc() {
        return gamedesc;
    }

    public String getDownurl() {
        return downurl;
    }

    public String getPackpagename() {
        return packpagename;
    }

    public String getVersion() {
        return version;
    }

    public String getDowncount() {
        return downcount;
    }
}