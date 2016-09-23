package com.cavytech.wear2.entity;

import android.app.Activity;

import com.cavytech.wear2.R;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by libin on 5/3 0003.
 * 邮箱：bin.li@tunshu.com
 */
public class ClockBean implements Serializable {
    private static final long serialVersionUID = 1466464664646164L;
    private int hour ;
    private int minute;
    private boolean isOpen;

    public boolean isNotSelect() {
        return isNotSelect;
    }

    public void setNotSelect(boolean notSelect) {
        isNotSelect = notSelect;
    }

    private boolean isNotSelect;

    private ArrayList<Boolean> weekSet;

    public ClockBean(int hour, int minute, boolean isOpen, ArrayList weekSet) {

        this.hour = hour;
        this.minute = minute;
        this.isOpen = isOpen;
        this.weekSet = weekSet;
    }
    public ClockBean() {
        this.weekSet = new ArrayList<Boolean>();
        for(int i = 0; i < 7; i++){
            this.weekSet.add(false);
        }

    }
    public ClockBean(boolean defult) {
        this.weekSet = new ArrayList<Boolean>();
        for(int i = 0; i < 7; i++){
            this.weekSet.add(defult);
        }

    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public ArrayList<Boolean> getWeekSet() {
        return weekSet;
    }

    public void setWeekSet(ArrayList<Boolean> weekSet) {
        this.weekSet = weekSet;
    }

    /*

    *@param everyday "每天"的字符串  处理多语言
     */
    public String getWeekCheckString(String everyday, Activity activity) {

        String week[] = {activity.getString(R.string.mon),activity.getString(R.string.tues), activity.getString(R.string.wed), activity.getString(R.string.thurs), activity.getString(R.string.fri), activity.getString(R.string.sat), activity.getString(R.string.sun)};

        boolean isEvryDay = true;

        String weekString = "";
        try {
            if (weekSet.get(0) && weekSet.get(1) && weekSet.get(2) && weekSet.get(3) && weekSet.get(4) && !weekSet.get(5) && !weekSet.get(6)) {
                weekString = activity.getString(R.string.weekday);
            } else if (!weekSet.get(0) && !weekSet.get(1) && !weekSet.get(2) && !weekSet.get(3) && !weekSet.get(4) && weekSet.get(5) && weekSet.get(6)) {
                weekString = activity.getString(R.string.weekend);
            } else {
                for (int i = 0; i < weekSet.size(); i++) {
                    if (weekSet.get(i)) {
                        weekString += week[i] + " ";
                    } else {
                        isEvryDay = false;
                    }
                }
                if (isEvryDay) {
                    return everyday;
                }
            }
        }catch(Exception e){
            for (int i = 0; i < weekSet.size(); i++) {
                if (weekSet.get(i)) {
                    weekString += week[i] + " ";
                } else {
                    isEvryDay = false;
                }
            }
            if (isEvryDay) {
                return everyday;
            }
        }

        return weekString;
    }
}
