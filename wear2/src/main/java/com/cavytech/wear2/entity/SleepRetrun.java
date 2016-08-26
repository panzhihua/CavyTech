package com.cavytech.wear2.entity;

/**
 * Created by LiBin on 2016/7/1.
 *
 *调用返回值
 */
public class SleepRetrun {

    int deeptime;//深睡
    int normaltime;//浅睡
    int sleeptime;//睡眠时间

    public int getDeeptime() {
        return deeptime;
    }

    public void setDeeptime(int deeptime) {
        this.deeptime = deeptime;
    }

    public int getNormaltime() {
        return normaltime;
    }

    public void setNormaltime(int normaltime) {
        this.normaltime = normaltime;
    }

    public int getSleeptime() {
        return sleeptime;
    }

    public void setSleeptime(int sleeptime) {
        this.sleeptime = sleeptime;
    }
}
