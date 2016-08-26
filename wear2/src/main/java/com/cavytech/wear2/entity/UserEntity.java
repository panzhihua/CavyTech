package com.cavytech.wear2.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：yzb on 2015/7/15 13:22
 */

public class UserEntity extends CommonEntity  {


    public boolean equals(UserEntity userInfo){

        if(this.getProfile().getNickname() == null || !new ProfileEntity().getNickname().equals(userInfo.getProfile().getNickname())){
            return false;
        }

        if(this.getProfile().getSex() != userInfo.getProfile().getSex()){
            return false;
        }

        if(this.getProfile().getHeight() != userInfo.getProfile().getHeight()){
            return false;
        }

        if(this.getProfile().getWeight() != userInfo.getProfile().getWeight()){
            return false;
        }

        if(this.getProfile().getBirthday() == null || !this.getProfile().getBirthday().equals(userInfo.getProfile().getBirthday())){
            return false;
        }

        if(this.getProfile().getAddress() == null || !this.getProfile().getAddress().equals(userInfo.getProfile().getAddress())){
            return false;
        }

        if(this.getProfile().getSleep_time_goal() != userInfo.getProfile().getSleep_time_goal()){
            return false;
        }

        if(this.getProfile().getSteps_goal() != userInfo.getProfile().getSteps_goal()){
            return false;
        }

        if(this.getProfile().isEnable_notification() != userInfo.getProfile().isEnable_notification()){
            return false;
        }

        if(this.getProfile().isShare_location() != userInfo.getProfile().isShare_location()){
            return false;
        }

        if(this.getProfile().isShare_birthday() != userInfo.getProfile().isShare_birthday()) {
            return false;
        }

        if(this.getProfile().isShare_height() != userInfo.getProfile().isShare_height()){
            return false;
        }

        if(this.getProfile().isShare_weight() != userInfo.getProfile().isShare_weight()){
            return false;
        }

        if(getProfile().getAwards() == null || userInfo.getProfile().getAwards() == null || getProfile().getAwards().size() != userInfo.getProfile().getAwards().size()){
            return false;
        }

        String str1, str2;
        for(int i = 0; i < getProfile().getAwards().size(); i++) {

            str1 = getProfile().getAwards().get(i).getNumber()+"";
            str2 = userInfo.getProfile().getAwards().get(i).getNumber()+"";
            if (!str1.equals(str2)) {

                return false;
            }

            str1 = getProfile().getAwards().get(i).getDate();
            str2 = userInfo.getProfile().getAwards().get(i).getDate();
            if (!str1.equals(str2)) {

                return false;
            }
        }

        return true;
    }

    /**
     * coins : 99
     * phone : 15811111111
     * address : gfgui
     * sex : 1
     * height : 165.6
     * weight : 107.5
     * figure : 2
     * birthday : 2016-07-06 07:00:58
     * location :
     * steps : 0
     * nickname : bdjididhdhdh
     * avatar : 02.gif
     * language : zh-CN
     * steps_goal : 12000
     * sleep_time_goal : 8
     * enable_notification : true
     * share_location : true
     * share_birthday : false
     * share_height : true
     * share_weight : true
     * awards : []
     * signupAt : 2016-06-03 11:57:56
     */

    private ProfileEntity profile;
    /**
     * profile : {"coins":99,"phone":"15811111111","address":"gfgui","sex":1,"height":165.6,"weight":107.5,"figure":2,"birthday":"2016-07-06 07:00:58","location":"","steps":0,"nickname":"bdjididhdhdh","avatar":"02.gif","language":"zh-CN","steps_goal":12000,"sleep_time_goal":8,"enable_notification":true,"share_location":true,"share_birthday":false,"share_height":true,"share_weight":true,"awards":[],"signupAt":"2016-06-03 11:57:56"}
     * code : 1000
     * msg :
     * time : 1467788750
     */

    private int time;

    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public ProfileEntity getProfile() {
        return profile;
    }

    public int getTime() {
        return time;
    }

    public static class ProfileEntity implements Serializable {
        private int coins;
        private String phone;
        private String address;
        private int sex;
        private double height;
        private double weight;
        private int figure;
        private String birthday;
        private String location;
        private int steps;
        private String nickname;
        private String avatar;
        private String language;
        private int steps_goal;
        private int sleep_time_goal;
        private boolean enable_notification;
        private boolean share_location;
        private boolean share_birthday;
        private boolean share_height;
        private boolean share_weight;
        private String signupAt;
        private List<AwardsEntity> awards;

        public void setCoins(int coins) {
            this.coins = coins;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public void setHeight(double height) {
            this.height = height;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        public void setFigure(int figure) {
            this.figure = figure;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public void setSteps(int steps) {
            this.steps = steps;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public void setSteps_goal(int steps_goal) {
            this.steps_goal = steps_goal;
        }

        public void setSleep_time_goal(int sleep_time_goal) {
            this.sleep_time_goal = sleep_time_goal;
        }

        public void setEnable_notification(boolean enable_notification) {
            this.enable_notification = enable_notification;
        }

        public void setShare_location(boolean share_location) {
            this.share_location = share_location;
        }

        public void setShare_birthday(boolean share_birthday) {
            this.share_birthday = share_birthday;
        }

        public void setShare_height(boolean share_height) {
            this.share_height = share_height;
        }

        public void setShare_weight(boolean share_weight) {
            this.share_weight = share_weight;
        }

        public void setSignupAt(String signupAt) {
            this.signupAt = signupAt;
        }

        public void setAwards(List<AwardsEntity> awards) {
            this.awards = awards;
        }

        public int getCoins() {
            return coins;
        }

        public String getPhone() {
            return phone;
        }

        public String getAddress() {
            return address;
        }

        public int getSex() {
            return sex;
        }

        public double getHeight() {
            return height;
        }

        public double getWeight() {
            return weight;
        }

        public int getFigure() {
            return figure;
        }

        public String getBirthday() {
            return birthday;
        }

        public String getLocation() {
            return location;
        }

        public int getSteps() {
            return steps;
        }

        public String getNickname() {
            return nickname;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getLanguage() {
            return language;
        }

        public int getSteps_goal() {
            return steps_goal;
        }

        public int getSleep_time_goal() {
            return sleep_time_goal;
        }

        public boolean isEnable_notification() {
            return enable_notification;
        }

        public boolean isShare_location() {
            return share_location;
        }

        public boolean isShare_birthday() {
            return share_birthday;
        }

        public boolean isShare_height() {
            return share_height;
        }

        public boolean isShare_weight() {
            return share_weight;
        }

        public String getSignupAt() {
            return signupAt;
        }

        public List<AwardsEntity> getAwards() {
            return awards;
        }

        public static class AwardsEntity implements Serializable{
            private int number;
            private String date;

            public void setNumber(int number) {
                this.number = number;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public int getNumber() {
                return number;
            }

            public String getDate() {
                return date;
            }
        }
    }
}
