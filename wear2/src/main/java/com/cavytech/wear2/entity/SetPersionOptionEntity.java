package com.cavytech.wear2.entity;

import java.io.Serializable;

/**
 * Created by doudou on 16/7/6.
 */
public class SetPersionOptionEntity implements Serializable{

    /**
     * nickname : Small White
     * address : Taipei
     * sex : 0
     * height : 165.6
     * weight : 80
     * figure : 2
     * birthday : 1965-01-20
     * steps_goal : 12000
     * sleep_time_goal : 8
     * enable_notification : true
     * share_location : true
     * share_birthday : false
     * share_height : true
     * share_weight : true
     */

    private ProfileEntity profile;

    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
    }

    public ProfileEntity getProfile() {
        return profile;
    }

    public static class ProfileEntity{
        private String nickname;
        private String address;
        private int sex;
        private double height;
        private double weight;
        private int figure;
        private String birthday;
        private int steps_goal;
        private int sleep_time_goal;
        private boolean enable_notification;
        private boolean share_location;
        private boolean share_birthday;
        private boolean share_height;
        private boolean share_weight;

        public void setNickname(String nickname) {
            this.nickname = nickname;
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

        public String getNickname() {
            return nickname;
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

        public double getFigure() {
            return figure;
        }

        public String getBirthday() {
            return birthday;
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
    }
}
