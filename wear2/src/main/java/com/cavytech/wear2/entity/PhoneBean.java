package com.cavytech.wear2.entity;

/**
 * Created by hf on 2016/5/27.
 * 紧急联系人
 */
public class PhoneBean {

    private String name;
    private String phoneNum;

    public PhoneBean(String name, String phoneNum) {
        this.name = name;
        this.phoneNum = phoneNum;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
