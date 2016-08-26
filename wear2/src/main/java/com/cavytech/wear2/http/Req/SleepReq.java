package com.cavytech.wear2.http.Req;

import com.cavytech.wear2.entity.GetSleepBean;

import java.util.List;

/**
 * Created by LiBin on 2016/6/1.
 * 睡眠上传
 */
public class SleepReq extends CommonReq{

    List<GetSleepBean.SleepListBean> sleepList;

    public List<GetSleepBean.SleepListBean> getSleepList() {
        return sleepList;
    }

    public void setSleepList(List<GetSleepBean.SleepListBean> sleepList) {
        this.sleepList = sleepList;
    }
}
