package com.cavytech.wear2.http.Req;

import java.util.List;

/**
 * Created by longjining on 16/5/10.
 */
public class AcceptPkReq extends CommonReq{
    public List<String> getAcceptPkList() {
        return acceptPkList;
    }

    public void setAcceptPkList(List<String> acceptPkList) {
        this.acceptPkList = acceptPkList;
    }

    List<String> acceptPkList;
}
