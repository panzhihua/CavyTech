package com.tunshu.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by longjining on 16/1/28.
 */
public class TopTypeListEntity extends CommonEntity{

    private List<DataEntity> data;

    public List<DataEntity> getData() {
        return data;
    }

    public void setData(List<DataEntity> data) {
        this.data = data;
    }

    public class DataEntity implements Serializable {
        private String rankname;
        private String ranktype;

        public String getRankname(){
            return rankname;
        }
        public void setRankname(String rankname) {
            this.rankname = rankname;
        }

        public String getRanktype(){
            return ranktype;
        }
        public void setRanktype(String ranktype) {
            this.ranktype = ranktype;
        }
    }
}
