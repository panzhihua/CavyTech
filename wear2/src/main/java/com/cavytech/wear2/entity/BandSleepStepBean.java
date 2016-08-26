package com.cavytech.wear2.entity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by LiBin on 2016/6/1.
 */

@Table(name = "postData")
public class BandSleepStepBean {
    @Column(name="date",isId=true,autoGen=false)
    private String date;
    @Column(name = "tilts")
    private int tilts;
    @Column(name = "steps")
    private int steps;
    @Column(name = "time")
    private int time;

    public int getTilts() {
        return tilts;
    }

    public void setTilts(int tilts) {
        this.tilts = tilts;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getData() {
        return date;
    }

    public void setData(String data) {
        this.date = data;
    }
    /*    private int titlts;

    private int stepCount;

    private String countDate;

    private String data;


    public BandSleepStepBean(int stepCount, String countDate) {
        this.stepCount = stepCount;
        this.countDate = countDate;

    }

    public int getTitlts() {
        return titlts;
    }

    public void setTitlts(int titlts) {
        this.titlts = titlts;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public String getCountDate() {
        return countDate;
    }

    public void setCountDate(String countDate) {
        this.countDate = countDate;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }*/
}
