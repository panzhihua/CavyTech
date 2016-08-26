package com.cavytech.wear2.http.Req;

import com.cavytech.wear2.entity.BandSleepStepBean;

import java.util.List;

/**
 * Created by LiBin on 2016/6/1.
 * 计步上传
 */
public class StepsReq extends CommonReq{

    List<BandSleepStepBean> raw;
    int time_scale;

    public int getTime_scale() {
        return time_scale;
    }

    public void setTime_scale(int time_scale) {
        this.time_scale = time_scale;
    }

    public List<BandSleepStepBean> getRaw() {
        return raw;
    }

    public void setRaw(List<BandSleepStepBean> raw) {
        this.raw = raw;
    }
}
