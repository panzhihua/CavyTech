package com.tunshu.entity;

import java.util.List;

/**
 * Created by ShadowNight on 2015/7/17.
 */
public class SearchKeyEntity extends CommonEntity{

    /**
     * msg : 数据加载成功
     * code : 1001
     * data : {"hotkey":["平衡球","网球","篮球"]}
     */
    private DataEntity data;


    public void setData(DataEntity data) {
        this.data = data;
    }


    public DataEntity getData() {
        return data;
    }

    public class DataEntity {
        /**
         * hotkey : ["平衡球","网球","篮球"]
         */
        private List<String> hotkey;

        public void setHotkey(List<String> hotkey) {
            this.hotkey = hotkey;
        }

        public List<String> getHotkey() {
            return hotkey;
        }
    }
}
