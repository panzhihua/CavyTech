package com.cavytech.wear2.entity;

import com.cavytech.wear2.http.Req.CommonReq;

import java.util.List;

/**
 * Created by hf on 2016/5/30.
 * 紧急联系人
 */
public class EmergencyPhoneBean extends CommonReq{

    List<GetPhone.ContactsBean> contacts;

    public List<GetPhone.ContactsBean> getContacts() {
        return contacts;
    }

    public void setContacts(List<GetPhone.ContactsBean> contacts) {
        this.contacts = contacts;
    }
}
