package com.tunshu.entity;

import java.util.List;

/**
 * Created by ShadowNight on 2015/7/17.
 */
public class UploadFileEntity extends CommonEntity{

    private DataEntity data;

    public DataEntity getData() {
        return data;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public class DataEntity {
       private String img;

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }
    }
}
