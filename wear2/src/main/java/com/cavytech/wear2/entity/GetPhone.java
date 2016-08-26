package com.cavytech.wear2.entity;

import java.util.List;

/**
 * Created by Administrator on 2016/5/30.
 * 获取紧急联系人
 */
public class GetPhone {
    /**
     * code : 1000
     * contacts : [{"name":"papa","phone":"15811111111"},{"name":"mama","phone":"15811111112"},{"name":"gogo","phone":"15811111113"}]
     * msg :
     * time : 1466498836
     */

    private int code;
    private String msg;
    private int time;
    /**
     * name : papa
     * phone : 15811111111
     */

    private List<ContactsBean> contacts;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public List<ContactsBean> getContacts() {
        return contacts;
    }

    public void setContacts(List<ContactsBean> contacts) {
        this.contacts = contacts;
    }

    public static class ContactsBean {
        private String name;
        private String phone;

        public ContactsBean(String name, String phone) {
            this.name = name;
            this.phone = phone;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ContactsBean that = (ContactsBean) o;

            if (name != null ? !name.equals(that.name) : that.name != null) return false;
            return phone != null ? phone.equals(that.phone) : that.phone == null;

        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (phone != null ? phone.hashCode() : 0);
            return result;
        }
    }


   /* *//**
     * code : 0000
     * msg : success
     * phoneList : [{"name":"xhfsjhs","phoneNum":"1854642675"}]
     *//*

    private String code;
    private String msg;
    *//**
     * name : xhfsjhs
     * phoneNum : 1854642675
     *//*

    private List<PhoneListBean> phoneList;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<PhoneListBean> getPhoneList() {
        return phoneList;
    }

    public void setPhoneList(List<PhoneListBean> phoneList) {
        this.phoneList = phoneList;
    }

    public static class PhoneListBean {
        private String name;
        private String phoneNum;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PhoneListBean that = (PhoneListBean) o;

            if (name != null ? !name.equals(that.name) : that.name != null) return false;
            return phoneNum != null ? phoneNum.equals(that.phoneNum) : that.phoneNum == null;

        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (phoneNum != null ? phoneNum.hashCode() : 0);
            return result;
        }

        public PhoneListBean(String name, String phoneNum) {
            this.name = name;
            this.phoneNum = phoneNum;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhoneNum() {
            return phoneNum;
        }

        public void setPhoneNum(String phoneNum) {
            this.phoneNum = phoneNum;
        }
    }*/
}
