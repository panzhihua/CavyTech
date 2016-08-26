package com.cavytech.wear2.http.Req;

import java.util.ArrayList;

/**
 * Created by longjining on 16/5/10.
 */
public class LaunchPkReq extends CommonReq{

    public ArrayList getLaunchPkList() {
        return launchPkList;
    }

    public void setLaunchPkList(ArrayList launchPkList) {
        this.launchPkList = launchPkList;
    }

    ArrayList launchPkList = new ArrayList<PkListEntity>();

    public static class PkListEntity {

        public PkListEntity() {
        }

        public PkListEntity(String friendId, String pkDuration, int isAllowWatch) {
            this.friendId = friendId;
            this.pkDuration = pkDuration;
            this.isAllowWatch = isAllowWatch;
        }

        public String getFriendId() {
            return friendId;
        }

        public void setFriendId(String friendId) {
            this.friendId = friendId;
        }

        public String getPkDuration() {
            return pkDuration;
        }

        public void setPkDuration(String pkDuration) {
            this.pkDuration = pkDuration;
        }

        public int getIsAllowWatch() {
            return isAllowWatch;
        }

        public void setIsAllowWatch(int isAllowWatch) {
            this.isAllowWatch = isAllowWatch;
        }

        String friendId;
        String pkDuration;
        int isAllowWatch;  // 是否好友可见 1: 好友可见，0:好友不可见
    }
}
