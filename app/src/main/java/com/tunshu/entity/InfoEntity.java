package com.tunshu.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：yzb on 2015/7/14 14:13
 */
public class InfoEntity  extends CommonEntity{
    private DataEntity data;
    public void setData(DataEntity data) {
        this.data = data;
    }
    public DataEntity getData() {
        return data;
    }

    public class DataEntity {
        private List<GameListEntity> gameList;
        private Game2Entity game2;
        private Game1Entity game1;
        private List<BannerEntity> banner;

        public void setGameList(List<GameListEntity> gameList) {
            this.gameList = gameList;
        }

        public void setGame2(Game2Entity game2) {
            this.game2 = game2;
        }

        public void setGame1(Game1Entity game1) {
            this.game1 = game1;
        }

        public void setBanner(List<BannerEntity> banner) {
            this.banner = banner;
        }

        public List<GameListEntity> getGameList() {
            return gameList;
        }

        public Game2Entity getGame2() {
            return game2;
        }

        public Game1Entity getGame1() {
            return game1;
        }

        public List<BannerEntity> getBanner() {
            return banner;
        }


        public class Game2Entity {
            private String name;
            private String images;
            private String gameid;
            private String type;
            private String style;

            public String getStyle() {
                return style;
            }

            public void setStyle(String style) {
                this.style = style;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public void setName(String name) {
                this.name = name;
            }

            public void setImages(String images) {
                this.images = images;
            }

            public void setGameid(String gameid) {
                this.gameid = gameid;
            }

            public String getName() {
                return name;
            }

            public String getImages() {
                return images;
            }

            public String getGameid() {
                return gameid;
            }
        }

        public class Game1Entity {
            private String name;
            private String images;
            private String gameid;
            private String type;
            private String style;

            public String getStyle() {
                return style;
            }

            public void setStyle(String style) {
                this.style = style;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public void setName(String name) {
                this.name = name;
            }

            public void setImages(String images) {
                this.images = images;
            }

            public void setGameid(String gameid) {
                this.gameid = gameid;
            }

            public String getName() {
                return name;
            }

            public String getImages() {
                return images;
            }

            public String getGameid() {
                return gameid;
            }
        }

        public class BannerEntity implements Serializable{
            private String bannerphone;
            private String gameid;
            private String style;
            private String type;
            private String url;
            private String title;

            public String getStyle() {
                return style;
            }

            public void setStyle(String style) {
                this.style = style;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getGameid() {
                return gameid;
            }

            public void setGameid(String gameid) {
                this.gameid = gameid;
            }

            public void setBannerphone(String bannerphone) {
                this.bannerphone = bannerphone;
            }

            public String getBannerphone() {
                return bannerphone;
            }

            public void setUrl(String url){this.url = url;}

            public String getUrl(){return this.url;}

            public void setTitle(String title){this.title = title;}

            public String getTitle(){return this.title;}
        }
    }
}
