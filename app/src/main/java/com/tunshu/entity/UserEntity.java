package com.tunshu.entity;

/**
 * 作者：yzb on 2015/7/15 13:22
 */
public class UserEntity extends CommonEntity {

    /**
     * data : {"birthday":"1993-02-08","phone":"","username":"666666@qq.com","email":"666666@qq.com","last_login_time":"2015-07-01 23:47:13","userid":462,"gender":"1","nikename":"我是哥你是弟","avatar":"http://game.tunshu.com/CavyGameCenterfile/user/2015/07/15/FM9S.jpg","is_disabled":false,"comefrom":"浙江省杭州市下城区沈半路"}
     * code : 1001
     * msg : 登录成功！
     */
    private DataEntity data;
    public void setData(DataEntity data) {
        this.data = data;
    }
    public DataEntity getData() {
        return data;
    }
    public class DataEntity {
        /**
         * birthday : 1993-02-08
         * phone :
         * username : 666666@qq.com
         * email : 666666@qq.com
         * last_login_time : 2015-07-01 23:47:13
         * userid : 462
         * gender : 1
         * nikename : 我是哥你是弟
         * avatar : http://game.tunshu.com/CavyGameCenterfile/user/2015/07/15/FM9S.jpg
         * is_disabled : false
         * comefrom : 浙江省杭州市下城区沈半路
         */
        private String birthday;
        private String phone;
        private String username;
        private String email;
        private String last_login_time;
        private String userid;
        private String gender;
        private String nikename;
        private String avatar;
        private boolean is_disabled;
        private String comefrom;
        private String usertoken;

        public boolean is_disabled() {
            return is_disabled;
        }

        public String getUsertoken() {
            return usertoken;
        }

        public void setUsertoken(String usertoken) {
            this.usertoken = usertoken;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setLast_login_time(String last_login_time) {
            this.last_login_time = last_login_time;
        }


        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public void setNikename(String nikename) {
            this.nikename = nikename;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public void setIs_disabled(boolean is_disabled) {
            this.is_disabled = is_disabled;
        }

        public void setComefrom(String comefrom) {
            this.comefrom = comefrom;
        }

        public String getBirthday() {
            return birthday;
        }

        public String getPhone() {
            return phone;
        }

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }

        public String getLast_login_time() {
            return last_login_time;
        }



        public String getGender() {
            return gender;
        }

        public String getNikename() {
            return nikename;
        }

        public String getAvatar() {
            return avatar;
        }

        public boolean isIs_disabled() {
            return is_disabled;
        }

        public String getComefrom() {
            return comefrom;
        }
    }
}
