package com.cavytech.wear2.entity;

/**
 * Created by LiBin on 2016/6/7.
 */
public class TImeBean {

    private String startTime;

    private String endTime;

    private String shouDate;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TImeBean tImeBean = (TImeBean) o;

        if (startTime != null ? !startTime.equals(tImeBean.startTime) : tImeBean.startTime != null)
            return false;
        if (endTime != null ? !endTime.equals(tImeBean.endTime) : tImeBean.endTime != null)
            return false;
        return shouDate != null ? shouDate.equals(tImeBean.shouDate) : tImeBean.shouDate == null;

    }

    @Override
    public int hashCode() {
        int result = startTime != null ? startTime.hashCode() : 0;
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        result = 31 * result + (shouDate != null ? shouDate.hashCode() : 0);
        return result;
    }

    public String getShouDate() {
        return shouDate;
    }

    public void setShouDate(String shouDate) {
        this.shouDate = shouDate;
    }
}

