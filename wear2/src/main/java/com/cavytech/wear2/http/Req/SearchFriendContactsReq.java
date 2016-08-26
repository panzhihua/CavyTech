package com.cavytech.wear2.http.Req;

import java.util.List;

/**
 * Created by longjining on 16/5/12.
 */
public class SearchFriendContactsReq extends CommonReq{


    public List<String> getPhoneNumList() {
        return phoneNumList;
    }

    public void setPhoneNumList(List<String> phoneNumList) {
        this.phoneNumList = phoneNumList;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    String searchType;
    List<String> phoneNumList;
}
