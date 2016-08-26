package com.cavytech.wear2.entity;

import java.util.List;

/**
 * Created by LiBin on 2016/6/16.
 */
public class FwVisionBean {

    /**
     * code : 0000
     * msg : success
     * versionList : [{"url":"http://115.28.144.243/cavylife/res/package/ios/Cavy2PR3F33.bin","version":"2.3.3"}]
     */

    private String code;
    private String msg;
    /**
     * url : http://115.28.144.243/cavylife/res/package/ios/Cavy2PR3F33.bin
     * version : 2.3.3
     */

    private List<VersionListBean> versionList;

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

    public List<VersionListBean> getVersionList() {
        return versionList;
    }

    public void setVersionList(List<VersionListBean> versionList) {
        this.versionList = versionList;
    }

    public static class VersionListBean {
        private String url;
        private String version;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
