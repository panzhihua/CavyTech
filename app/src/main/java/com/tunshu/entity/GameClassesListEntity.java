package com.tunshu.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by longjining on 16/1/22.
 */
public class GameClassesListEntity  extends CommonEntity implements Serializable {
    /**
     * data : [{"classification":[{"classid":"1","classname":"热门游戏"}]}]
     */
    private List<DataEntity> data;

    public List<DataEntity> getData() {
        return data;
    }

    public void setData(List<DataEntity> data) {
        this.data = data;
    }

    public class DataEntity implements Serializable{
        private int class_id;
        private String class_name = "";
        private String class_imgurl = "";
        private List<GameClassEntity> markarr;

        public void setClass_id(int classid)
        {
            this.class_id = classid;
        }
        public int getClass_id(){
            return this.class_id;
        }

        public void setClass_name(String name)
        {
            this.class_name = name;
        }
        public String getClass_name(){
            return this.class_name;
        }

        public void setClass_imgurl(String icon)
        {
            this.class_imgurl = icon;
        }
        public String getClass_imgurl(){
            return this.class_imgurl;
        }

        public void setMarkarr(List<GameClassEntity> markarr)
        {
            this.markarr = markarr;
        }
        public List<GameClassEntity> getMarkarr(){
            return this.markarr;
        }

        public class GameClassEntity implements Serializable{
            private int mark_id;
            private String mark_name;
            private String mark_imgurl;
            private String mark_color;

            public void setMark_id(int id)
            {
                this.mark_id = id;
            }
            public int getMark_id(){
                return this.mark_id;
            }

            public void setMark_name(String className)
            {
                this.mark_name = className;
            }
            public String getMark_name(){
                return this.mark_name;
            }

            public void setMark_imgurl(String icon)
            {
                this.mark_imgurl = icon;
            }
            public String getMark_imgurl(){
                return this.mark_imgurl;
            }

            public void setMark_color(String textColor)
            {
                this.mark_color = textColor;
            }
            public String getMark_color(){
                return this.mark_color;
            }
        }
    }
}
