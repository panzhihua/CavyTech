package com.cavytech.wear2.entity;

import java.util.List;

/**
 * Created by longjining on 16/5/17.
 */
public class HelpEntity extends CommonEntity{
    /**
     * title : 手环连接失败怎么办
     * number : 1
     * url : http://pay.tunshu.com/mobile/helps/RbPpRShyFJfqu7yMj
     */

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private String title;
        private int number;
        private String url;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }



/*    public ArrayList<HelpDetailEntity> getHelpList() {
        return helpList;
    }

    public void setHelpList(ArrayList<HelpDetailEntity> helpList) {
        this.helpList = helpList;
    }

    ArrayList<HelpDetailEntity> helpList;

    public static class HelpDetailEntity{
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getWebUrl() {
            return webUrl;
        }

        public void setWebUrl(String webUrl) {
            this.webUrl = webUrl;
        }

        String title;
        String webUrl;
    }*/
}
