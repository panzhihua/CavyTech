package com.tunshu.entity;

import java.util.List;

/**
 * Created by ShadowNight on 2015/7/15.
 */
public class TopListEntity extends CommonEntity {
    private DataEntity data;

    public DataEntity getData() {
        return data;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public class DataEntity {
        private List<GameListEntity> gameList;

        public List<GameListEntity> getGameList() {
            return gameList;
        }

        public void setGameList(List<GameListEntity> gameList) {
            this.gameList = gameList;
        }
    }
}
