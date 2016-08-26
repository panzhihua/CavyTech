package com.cavytech.wear2.entity;

import java.util.List;

/**
 * Created by hf on 2016/5/23.
 */
public class PklistBean {
    private String code;

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

    public List<WaitListInfo> getWaitList() {
        return waitList;
    }

    public void setWaitList(List<WaitListInfo> waitList) {
        this.waitList = waitList;
    }

    public List<DueListInfo> getDueList() {
        return dueList;
    }

    public void setDueList(List<DueListInfo> dueList) {
        this.dueList = dueList;
    }

    public List<FinishListInfo> getFinishList() {
        return finishList;
    }

    public void setFinishList(List<FinishListInfo> finishList) {
        this.finishList = finishList;
    }

    private String msg;
    private List<WaitListInfo> waitList;
    private List<DueListInfo> dueList;
    private List<FinishListInfo> finishList;


    public static class WaitListInfo{
        private String pkId;
        private int type;
        private String userId;

        public String getPkId() {
            return pkId;
        }

        public void setPkId(String pkId) {
            this.pkId = pkId;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getLaunchedTime() {
            return launchedTime;
        }

        public void setLaunchedTime(String launchedTime) {
            this.launchedTime = launchedTime;
        }

        public String getPkDuration() {
            return pkDuration;
        }

        public void setPkDuration(String pkDuration) {
            this.pkDuration = pkDuration;
        }

        private String avatarUrl;
        private String nickname;
        private String launchedTime;
        private String pkDuration;

    }
    public static class DueListInfo{
        private String pkId;
        private String userId;
        private String avatarUrl;

        public String getPkId() {
            return pkId;
        }

        public void setPkId(String pkId) {
            this.pkId = pkId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getBeginTime() {
            return beginTime;
        }

        public void setBeginTime(String beginTime) {
            this.beginTime = beginTime;
        }

        public String getPkDuration() {
            return pkDuration;
        }

        public void setPkDuration(String pkDuration) {
            this.pkDuration = pkDuration;
        }

        private String nickname;
        private String beginTime;
        private String pkDuration;


    }
    public static class FinishListInfo{
        private String pkId;
        private String userId;
        private String avatarUrl;
        private String nickname;
        private String completeTime;

        public String getPkId() {
            return pkId;
        }

        public void setPkId(String pkId) {
            this.pkId = pkId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getCompleteTime() {
            return completeTime;
        }

        public void setCompleteTime(String completeTime) {
            this.completeTime = completeTime;
        }

        public String getPkDuration() {
            return pkDuration;
        }

        public void setPkDuration(String pkDuration) {
            this.pkDuration = pkDuration;
        }

        public boolean isWin() {
            return isWin;
        }

        public void setWin(boolean win) {
            isWin = win;
        }

        private String pkDuration;
        private boolean isWin;

    }


}
