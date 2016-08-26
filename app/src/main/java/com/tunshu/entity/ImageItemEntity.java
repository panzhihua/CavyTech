package com.tunshu.entity;

import java.io.Serializable;

/**
 * Created by ShadowNight on 2015/7/17.
 */
public class ImageItemEntity implements Serializable{
    private String bigimage;
    private String smallimage;

    public String getBigimage() {
        return bigimage;
    }

    public void setBigimage(String bigimage) {
        this.bigimage = bigimage;
    }

    public String getSmallimage() {
        return smallimage;
    }

    public void setSmallimage(String smallimage) {
        this.smallimage = smallimage;
    }
}
