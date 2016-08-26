package com.cavytech.wear2.entity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by hf on 2016/4/25.
 */

@Table(name = "friend")
public class DBfriendBean {
//    @Column(name = "Id", isId = true)
//    private String id;
    @Column(name="id",isId=true,autoGen=false)
    private String userId;
    @Column(name = "avatarUrl")
    private String avatarUrl;
    @Column(name = "isFollow")
    private int isFollow;
    @Column(name = "nickname")
    private String nickname;
    @Column(name = "phoneNum")
    private String phoneNum;
    @Column(name = "pinyin")
    private String pinyin;

    @Column(name = "remark")
    private String remark;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getPinyin() {
        return pinyin;
    }
    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }
    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
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

    public int getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(int isFollow) {
        this.isFollow = isFollow;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


}
