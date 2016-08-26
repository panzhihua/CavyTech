package com.tunshu.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ShadowNight on 2015/7/15.
 */
public class TypeListEntity extends CommonEntity implements Serializable{
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
        /**
         * classification : [{"classid":"1","classname":"热门游戏"}]
         */
        private ClassificationEntity classification;

        public ClassificationEntity getClassification() {
            return classification;
        }

        public void setClassification(ClassificationEntity classification) {
            this.classification = classification;
        }

        public class ClassificationEntity implements Serializable{
            /**
             * classid : 1
             * classname : 热门游戏
             */
            private String classid;
            private String classname;
            private String type;
            private List<GameListEntity> gameList;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public List<GameListEntity> getGameList() {
                return gameList;
            }

            public void setGameList(List<GameListEntity> gameList) {
                this.gameList = gameList;
            }

            public void setClassid(String classid) {
                this.classid = classid;
            }

            public void setClassname(String classname) {
                this.classname = classname;
            }

            public String getClassid() {
                return classid;
            }

            public String getClassname() {
                return classname;
            }
        }
    }
}
