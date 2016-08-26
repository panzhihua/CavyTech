package com.cavytech.wear2.http.Req;

import java.util.List;

/**
 * Created by longjining on 16/5/10.
 */
public class DeletePkReq extends CommonReq{
    public List<String> getDelPkList() {
        return delPkList;
    }

    public void setDelPkList(List<String> delPkList) {
        this.delPkList = delPkList;
    }

    List<String> delPkList;
}
