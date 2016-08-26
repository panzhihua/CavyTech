package com.cavytech.wear2.util;


public class Constants {
    public static final int DB_VERSION=1;
   // public static final String URL = "http://game.tunshu.com/";//正式环境

    public static final String LICENSE_URL = "http://bbs.tunshu.com/r/cms/www/blue/bbs_forum/img/top/phone_xieyi.html";

    public static final int SEX_BOY = 0;
    public static final int SEX_GIRL = 1;
    public static final int SEX_NULL = 3;
    public static final String USERID = "userId";
    public static final String CITY = "city";

    public static final String USERNAME="username";
    public static final String PASSWORD="cavytech_life_band_password";
    public static final String USER_ICON="user_icon";
    public static final String USER_INFO_REFRESH="user_info_refresh";

    public static final String SHAREDPREFERENCES_NAME = "first_pref";

    public static final String LONGITUDE = "Longitude";
    public static final String LATITUDE = "Latitude";

    public static final String INTENT_EXTRA_HINT         = "hint";
    public static final String INTENT_EXTRA_TITLE        = "title";
    public static final String INTENT_EXTRA_WEBURL       = "webUrl";
    public static final String INTENT_EXTRA_BTNTEXT      = "btnText";
    public static final String INTENT_EXTRA_EDITTEXT     = "editText";
    public static final String INTENT_EXTRA_ISEDIT       = "editUserInfo";
    public static final String INTENT_EXTRA_USERID       = "userid";
    public static final String INTENT_EXTRA_FRDID        = "friendId";
    public static final String INTENT_EXTRA_MAXLEN       = "maxLen";

    public static final String INTENT_EXTRA_RECONNECTDEV = "reconnectBand";

    // Serialize name
    public static final String SERIALIZE_USERINFO = "userInfo.ser";
    public static final String MACADDRESSCONNECTIONUSERINFO = "MacAddressConnectionUserInfo";
    public static final String SERIALIZE_CLOCKBEAN = "clock.ser";
    public static final String SERIALIZE_BAND_INFO = "bandinfo.ser";
    public static final String SERIALIZE_USERBAND_INFO = "userbandinfo.ser";

    // BroadcastReceiver ID
    public static final String UPDATE_USERINFO_RECEIVER = "updateUserInfo";
    public static final String UPDATE_BANDINFO_RECEIVER = "bandInfo";

    // Band
    public static final String MCONNECTEDMACADDRESS = "mConnectedMacAddress";
    public static final String STATUS = "status";
    public static final String FWVISION = "fwvision";
    //status
    public static final String COM_CAVYTECH_WEAR2_SERVICE_STATUSRECEIVER= "com.cavytech.wear2.service.StatusReceiver";
    //data.fwVersion
    public static final String DATA_FWVERSION= "data.fwVersion";
    public static final String ISCONNECTIONBAND= "isConnectionBand";

    public static final String SLEEPCOMPLETE= "sleepComplete";
    public static final String STEPCOMPLETE= "stepComplete";
    public static final String SINGUP= "singUp";
    public static final String PHONEISCHECKED= "phoneIsChecked";
    public static final String PHONENOTICE= "phonenotice";
    public static final String CESHI= "ceshi";

    //new Constants
    public static final String HOWSHOW= "howShow";
    public static final String VERSIONINFO= "versionInfo";
    public static final String APP= "APP";
    public static final String GUJIAN= "gujian";
    public static final String TOKEN= "token";

    // new URl
    public static final String APPUPDateUrl= "http://pay.tunshu.com/live/api/v1/live_app"; //APP版本更新
    public static final String FIRMWAREUPDATEURL= "http://pay.tunshu.com/live/api/v1/firmware"; //firmware版本更新

    //友盟eventcode
    public static final String USER_SIGNUP= "USER_SIGNUP";
    public static final String USER_LOGOUT= "USER_LOGOUT";
    public static final String USER_LOGIN= "USER_LOGIN";

    public static final String APP_OPEN= "APP_OPEN";
    public static final String APP_QUIT= "APP_QUIT";

    public static final String BAND_CONNECT= "BAND_CONNECT";
    public static final String BAND_DISCONNECT= "BAND_DISCONNECT";


    public static final String APPURL = "url";
    public static final String MAXSIZE = "maxsize";
    public static final String GUJIANURL = "gujianurl";
    public static final String HWVERSION = "hwVersion";
    public static final String FWVERSION = "fwVersion";
    public static final String APPSIZE = "appsize";
    public static final String SAVENOTICE = "savenotice";
    public static final String forgetPwd = "forgetPwd";
    public static final String CANREFRESH = "canrefresh";
    public static final String HWVISION = "hwVersion";
    public static final String step="step";//计步缓存
    public static final String sleep = "sleep";//睡眠缓存
    public static final String SLEEPSHOW = "sleepshow";//睡眠缓存
}
