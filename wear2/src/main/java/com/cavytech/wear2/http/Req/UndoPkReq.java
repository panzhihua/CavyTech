package com.cavytech.wear2.http.Req;

import java.util.List;

/**
 * Created by longjining on 16/5/10.
 */
public class UndoPkReq extends CommonReq{
    String[] undoPkList;

    public void setUndoPkList(String[] undoPkList) {
        this.undoPkList = undoPkList;
    }

    public String[] getUndopk() {
        return undoPkList;
    }
}
