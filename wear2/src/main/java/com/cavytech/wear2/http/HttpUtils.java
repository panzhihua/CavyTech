package com.cavytech.wear2.http;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.basecore.application.BaseApplication;
import com.basecore.widget.CustomToast;
import com.cavytech.wear2.R;
import com.cavytech.wear2.application.CommonApplication;
import com.cavytech.wear2.entity.BandSleepStepBean;
import com.cavytech.wear2.entity.CommonEntity;
import com.cavytech.wear2.entity.EmergencyPhoneBean;
import com.cavytech.wear2.entity.GetPhone;
import com.cavytech.wear2.entity.SetPersionOptionEntity;
import com.cavytech.wear2.entity.UserEntity;
import com.cavytech.wear2.http.Req.AcceptPkReq;
import com.cavytech.wear2.http.Req.CommonReq;
import com.cavytech.wear2.http.Req.DeletePkReq;
import com.cavytech.wear2.http.Req.LaunchPkReq;
import com.cavytech.wear2.http.Req.SearchFriendContactsReq;
import com.cavytech.wear2.http.Req.StepsReq;
import com.cavytech.wear2.http.Req.UndoPkReq;
import com.cavytech.wear2.util.CacheUtils;
import com.cavytech.wear2.util.Constants;
import com.cavytech.wear2.util.MD5;
import com.cavytech.wear2.util.PhoneUtils;
import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by longjining on 16/4/20.
 */
public class HttpUtils {
    public static final String URL = "http://115.28.144.243/cavylife/";
    public static final String APP_URL = "http://game.tunshu.com/appIndex/index";

    public static final String HTTP_PRE_APPINDEX    = "api.do";
    public static final String HTTP_PRE_IMGCODE     = "imageCode.do";
    public static final String HTTP_PRE_USERICON    = "api/userIcon.do";
    public static final String DEFULTURL = URL + HTTP_PRE_APPINDEX;

    public static final String SUCCESS = "0000"; // 成功

    public static final int CODE_ACCOUNT_IS_EXIST = 1206; // 用户已存在

    public static final String Param_CMD               = "cmd";
    public static final String Param_ACCOUNT           = "username";      //账号，手机、邮箱，支持模糊搜索
    public static final String Param_PWD               = "password";
    public static final String Param_AUTHCODE          = "authCode";
    public static final String Param_CODE          = "code";
    public static final String Param_PHONENUM          = "phone";
    public static final String Param_PHONETYPE         = "phonetype";
    public static final String Param_LANGUAGE          = "language";
    public static final String Param_FRIENDID          = "friendId";      //好友ID
    public static final String PARAM_REMARKS          = "remarks";
    public static final String PARAM_CITY              = "city";          //查询天气状况的城市,如杭州：“hangzhou”
    public static final String PARAM_DATE              = "date";          // 日期，格式为“yyyy-MM-dd”
    public static final String PARAM_STARTDATE              = "startDate";          // 日期，格式为“yyyy-MM-dd”
    public static final String PARAM_ENDDATE              = "endDate";          // 日期，格式为“yyyy-MM-dd”
    public static final String PARAM_USERID            = "userId";        //用户ID
    public static final String PARAM_APPIMG            = "appImg";        //App图标
    public static final String PARAM_PLISTURL          = "plistUrl";      //App下载链接
    public static final String PARAM_OPERATE           = "operate";       //操作，true：关注；false：取消关注
    public static final String PARAM_LONGITUDE         = "longitude";     //经度
    public static final String PARAM_LATITUDE          = "latitude";      //纬度
    public static final String PARAM_VERSION           = "version";      //固件版本号
    public static final String PARAM_VERIFYMSG         = "verifyMsg";     //验证信息
    public static final String PARAM_TYPE              = "type";          //1:输入邮箱、手机号搜索; 2:豚鼠好友; 3:手机通讯录; 4:搜索附件的人; 豚鼠好友：系统推荐同城好友，每日推荐15位同城好友
    public static final String PARAM_SEARCHTYPE        = "searchType";    //1:好友请求 2：好友请求确认
    public static final String PARAM_PHONENUMLIST      = "phoneNumList";  //手机号集，不支持模糊搜索
    public static final String PARAM_NICKNAME          = "nickname";      //昵称
    public static final String PARAM_SEX               = "sex";           //性别，0:男 1：女
    public static final String PARAM_HEIGHT            = "height";        //身高
    public static final String PARAM_WEIGHT            = "weight";        //体重
    public static final String PARAM_BIRTHDAY          = "birthday";      //生日,格式1990-12-01
    public static final String PARAM_ADDRESS           = "address";       //地址，格式：省-市
    public static final String PARAM_STEPNUM           = "stepNum";       //步数     //steps_goal
    public static final String PARAM_SLEEPTIME         = "sleepTime";     //睡眠时间，格式HH:mm  //sleep_time_goal
    public static final String PARAM_ISNOTIFICATION    = "isNotification";//是否打开智能通知  //sleep_time_goal
    public static final String PARAM_ISLOCALSHARE      = "isLocalShare";  //是否打开位置共享  //share_location
    public static final String PARAM_ISOPENBIRTHDAY    = "isOpenBirthday";//是否公开生日   //share_birthday
    public static final String PARAM_ISOPENHEIGHT      = "isOpenHeight";  //是否公开身高  //share_height
    public static final String PARAM_ISOPENWEIGHT      = "isOpenWeight";  //是否公开体重  //share_weight
    public static final String PARAM_REMAKE            = "remake";        //备注
    public static final String PARAM_FILENAME          = "filename";       //头像名称
    public static final String PARAM_IMGFILE           = "imgFile";       //头像
    public static final String PARAM_FEEDBACK          = "feedback";       // 意见反馈信息
    public static final String PARAM_PKID         = "pkId";       // Pk详情

    public static final String Method_USERREG          = "userReg";
    public static final String Method_LOGIN            = "userLogin";
    public static final String Method_SENDAUTHCODE     = "sendAuthCode";
    public static final String Method_RESETPSW         = "resetPsw";
    public static final String Method_GETUSERINFO      = "getUserInfo";
    public static final String METHOD_GETPKINFO      = "getPKInfo";
    public static final String METHOD_LAUNCHPK         = "launchPK";
    public static final String METHOD_ACCEPT           = "acceptPK";
    public static final String METHOD_UNDOPK           = "undoPK";
    public static final String METHOD_DELEPK           = "delePk";
    public static final String METHOD_GETPKLIST        = "getPkList";
    public static final String METHOD_GETHELPLIST      = "getHelpList";
    public static final String METHOD_SUBMITFEEDBACK   = "submitFeedback";
    public static final String METHOD_SENDEMERGENCYMSG = "sendEmergencyMsg";
    public static final String METHOD_GETVERSION       = "getVersion";

    public static final String METHOD_GETWEATHER       = "getWeather";
    public static final String METHOD_INDEX            = "index";
    public static final String METHOD_GETSTEPCOUNT     = "getStepCount";
    public static final String METHOD_GETSLEEPINFO     = "getSleepInfo";
    public static final String METHOD_DELEFRIEND       = "deleFriend";
    public static final String METHOD_FOLLOWUSER       = "followUser";
    public static final String METHOD_SETUSERLBS       = "setUserLBS";
    public static final String METHOD_GETFRIENDREQLIST = "getFriendReqList";
    public static final String METHOD_GETFRIENDLIST    = "getFriendList";//获取好友列表
    public static final String METHOD_ADDFRIEND        = "addFriend";//添加朋友
    public static final String METHOD_SETSTEPCOUNT     = "setStepCount";//计步上传
    public static final String METHOD_REPORTSLEEPINFO     = "reportSleepInfo";//睡眠上传
    public static final String METHOD_SEARCHFRIEND     = "searchFriend";//搜索好友
    public static final String METHOD_EMERGENCYPHONE   = "setEmergencyPhone";//紧急联系人
    public static final String METHOD_GETEMERGENCYPHONE   = "getEmergencyPhone";//获取紧急联系人
    public static final String METHOD_SETUSERINFO      = "setUserInfo";
    public static final String METHOD_SETUSERICON      = "setUserIcon";
    public static final String PHONETYPE_ANDROID       = "android";
    public static final String METHOD_GETFRIENDINFO    = "getFriendInfo";//好友信息详情
    private static final String METHOD_UNDOPkLIST = "undoPkList";
    private static final String METHOD_SETFRIENDREMARK = "setFriendRemark";

    /**
     * ?ac=cavylife&pagenum=1&pagesize=10&phonetype=android
     */

    public static final String PARAM_AC                = "ac";
    public static final String PARAM_CAVYLIFE          = "cavylife";
    public static final String PARAM_PAGESIZE          = "pagesize";
    public static final String PARAM_PHONETYPE         = "android";
    public static final String PARAM_PAGENUM           = "pagenum";

    /**
     * 新方案
     */
    public static final int CODE_AUTCHCODE_ERR = 1003; // 验证码错误

    public static final int newSUCCESS = 1000; // 成功

    public static final int NEW_CODE_AUTCHCODE_ERR = 1003; // 验证码错误

    public static final String NEW_URL = "http://pay.tunshu.com/live/api/v1/";
    public static final String LOGIN = "login";//登录
    public static final String NEWLOGOUT = "logout";//退出
    public static final String AUTH_TOKEN = "auth-token";
    public static final String DAILIES = "dailies";
    public static final String USERINFORMATION = "users/profile";
    private static final String PARAM_BASE64DATA = "base64Data";
    private static final String PARAM_EMAIL = "email";
    private static final String PARAM_QUESTION = "question";
    private static final String PARAM_DETAIL = "detail";
    private static final String PARAM_DEVICE_SERIAL = "device_serial";
    public static final int CODE_ACCOUNT_NOT_LOGIN = 1205;
    public static final int PASSWORD_ERROR = 1203;//密码错误
    public static final int CODE_ACCOUNT_NOT_EXIST = 1202;//用户不存在
    public static final int VERIFICATION_CODE_NO_CORRECT = 1215;//验证码不正确
    private static final String PARAM_DEVICE_MODEL = "device_model";
    private static final String PARAM_BAND_MAC = "band_mac";
    private static final String PARAM_EVENT_TYPE = "event_type";
    private static final String PARAM_AUTH_KEY = "auth_key";
    private static final String AUTH_KEY = "jotsJDtwaCCnYR53X";

    private static HttpUtils mInstance;
    private Handler mDelivery;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static String mLanguage = "";

    public enum SearchType{
        PHONEEMAIL("1"),     // 邮箱、手机
        CAVYFRIEND("2"),     // 豚鼠好友
        CONTACT("3"),        // 手机通讯录
        NEARFRIEND("4");     // 搜索附件的人

        SearchType(String i){
            type = i;
        }
         public String getType() {
             return this.type;
         }
        final String type;
    }

    public enum AddFriendType{
        // 1:好友请求 2：好友请求确认
        ADDFRIEND("1"),
        ANSWERREQ("2");

        AddFriendType(String i){
            type = i;
        }
        public String getType() {
            return this.type;
        }
        final String type;
    }

    private HttpUtils()
    {
        mDelivery = new Handler(Looper.getMainLooper());
    }

    public static HttpUtils getInstance()
    {
        if (mInstance == null)
        {
            synchronized (HttpUtils.class)
            {
                if (mInstance == null)
                {
                    mInstance = new HttpUtils();
                }
            }
        }
        return mInstance;
    }

    public void showToast(Context context, CommonEntity result){

        switch (result.getCode()){

            case HttpUtils.CODE_AUTCHCODE_ERR:
                CustomToast.showToast(context, context.getString(R.string.MSG_AUTH_CODE_ERR));

                break;
            default:
                CustomToast.showToast(context, result.getMsg());
        }
    }

    private void sendFailedResultCallback(final Request request, final Exception e, final RequestCallback callback)
    {
        mDelivery.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                    callback.onError(request, e);
            }
        });
    }

    private void sendSuccessRequestCallback(final Object object, final RequestCallback callback)
    {
        mDelivery.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    callback.onResponse(object);
                }
            }
        });
    }

    public static String getLanguage() {
        if(!mLanguage.isEmpty()){
            return mLanguage;
        }
        Locale locale = BaseApplication.getApplication().getResources().getConfiguration().locale;
        String language = locale.getLanguage();

        if (language.endsWith("en")){
            language = "en";
        }else{
            String cnt = locale.getCountry();
            if(!"".equals(cnt)){
                language = language + "_" + locale.getCountry();
            }
        }

        return language;
    }

    public static String getLanguage2() {
        if(!mLanguage.isEmpty()){
            return mLanguage;
        }
        Locale locale = BaseApplication.getApplication().getResources().getConfiguration().locale;
        String language = locale.getLanguage();

        if (language.endsWith("en")){
            language = "en";
        }

        return language;
    }

    private void setCommon(CommonReq req){
        req.setLanguage(getLanguage());
        req.setPhonetype(PHONETYPE_ANDROID);
        req.setUserId(CommonApplication.userID);
    }

    public static Map<String, String> RequestMapParams(String cmdName) {

        Map<String, String> map = new HashMap<String, String>();
        map.put(Param_PHONETYPE, PHONETYPE_ANDROID);
        map.put(Param_LANGUAGE, getLanguage());
        if(!cmdName.isEmpty()) {
            map.put(Param_CMD, cmdName);
        }
        return map;
    }

    public static HashMap<String, String> getcommon(String cmdName) {

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Param_PHONETYPE, PHONETYPE_ANDROID);
        map.put(Param_LANGUAGE, getLanguage());
        if(!cmdName.isEmpty()) {
            map.put(Param_CMD, cmdName);
        }
        return map;
    }
    public static IdentityHashMap<String, String> RequestMapParamsWithUserID(String cmdName) {

        IdentityHashMap<String, String> map = new IdentityHashMap<String, String>();
        map.put(Param_PHONETYPE, PHONETYPE_ANDROID);
        map.put(Param_LANGUAGE, getLanguage());
        map.put(PARAM_USERID, CommonApplication.userID);
        if(!cmdName.isEmpty()) {
            map.put(Param_CMD, cmdName);
        }
        return map;
    }


    /**
     * 用户坐标上报
     *
     * @param longitude  经度
     * @param latitude   纬度
     * @param callback
     */
    public void setUserLBS(Context context,String longitude, String latitude, RequestCallback callback) {
        String url=NEW_URL+"users/location";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(PARAM_LONGITUDE, longitude);
        params.put(PARAM_LATITUDE, latitude);
        HashMap header = new HashMap<String,String>();
        header.put(AUTH_TOKEN, CacheUtils.getString(context,Constants.TOKEN));
        post(url, params, null,callback);
    }

    /**
     * 获取游戏列表
     *
     * @param callback
     */
    public void getGameList(Context context,RequestCallback callback) {
        String url=NEW_URL+"games/recommend";
        HashMap header = new HashMap<String,String>();
        header.put(AUTH_TOKEN, CacheUtils.getString(context,Constants.TOKEN));
        get(url, null, header,callback);
    }

    /**
     * 计步睡眠原始数据上报
     *
     * @param callback
     *
     */
    public void setStepCount(List<BandSleepStepBean> addsteps, Context context,RequestCallback callback) {
        StepsReq searchReq = new StepsReq();
        searchReq.setLanguage(getLanguage());
        searchReq.setTime_scale(10);
        searchReq.setRaw(addsteps);
        HashMap header = new HashMap<String,String>();
        header.put(AUTH_TOKEN, CacheUtils.getString(context,Constants.TOKEN));
        post(NEW_URL+DAILIES, searchReq, header,callback);

    }


    /**
     * 睡眠上报
     *
     * @param callback
     *
     */
    /*public void reportSleepInfo(ArrayList<GetSleepBean.SleepListBean> addsteps, RequestCallback callback) {

        SleepReq searchReq = new SleepReq();
        setCommon(searchReq);
        searchReq.setCmd(METHOD_REPORTSLEEPINFO);
        searchReq.setSleepList(addsteps);

        post(DEFULTURL, searchReq, callback);

    }
*/
    /**
     * 用户登录
     *
     * @param password 密码
     * @param callback
     */
/*    public void login(final String account, final String password, RequestCallback callback) {
        IdentityHashMap<String, String> params = RequestMapParamsWithUserID(Method_LOGIN);
        params.put(Param_ACCOUNT, account);
        params.put(Param_PWD, MD5.getMD5String(password));
        post(DEFULTURL, params, callback);
    }*/

    public void login(String band_mac,String device_model,String longitude, String latitude,Context context,final String username, final String password, RequestCallback callback) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Param_ACCOUNT, username);
        params.put(Param_PWD, MD5.getMD5String(password));
        params.put(Param_LANGUAGE, getLanguage());
        params.put(PARAM_DEVICE_SERIAL, PhoneUtils.getUDID(context));
        params.put(PARAM_LONGITUDE, longitude);
        params.put(PARAM_LATITUDE, latitude);
        params.put(PARAM_DEVICE_MODEL, "android "+ Build.VERSION.SDK_INT);
        params.put(PARAM_BAND_MAC, band_mac);
        post(NEW_URL+LOGIN, params, null,callback);
    }

    /**
     * 退出帐号
     */
    public void logout(Context context , RequestCallback callback) {
        String url = NEW_URL+"logout";
        HashMap<String, String> header = new HashMap<String, String>();
        header.put(AUTH_TOKEN, CacheUtils.getString(context,Constants.TOKEN));
        HashMap<String, String> param = new HashMap<String, String>();
        param.put(PARAM_DEVICE_SERIAL, PhoneUtils.getUDID(context));
        post(url, param, header,callback);
    }

    /**
     * 设置用户信息
     *
     * @param userInfo    用户信息
     * @param callback
     */
    public void setUserInfo(UserEntity.ProfileEntity userInfo,Context context, RequestCallback callback) {

        SetPersionOptionEntity setPersionOptionEntity = new SetPersionOptionEntity();
        setPersionOptionEntity.setProfile(new SetPersionOptionEntity.ProfileEntity());
        setPersionOptionEntity.getProfile().setSex(userInfo.getSex());
        setPersionOptionEntity.getProfile().setAddress(userInfo.getAddress());
        setPersionOptionEntity.getProfile().setBirthday(userInfo.getBirthday());
        setPersionOptionEntity.getProfile().setHeight(userInfo.getHeight());
        setPersionOptionEntity.getProfile().setWeight(userInfo.getWeight());
        setPersionOptionEntity.getProfile().setNickname(userInfo.getNickname());
        setPersionOptionEntity.getProfile().setFigure(userInfo.getFigure());
        setPersionOptionEntity.getProfile().setSleep_time_goal(userInfo.getSleep_time_goal());
        setPersionOptionEntity.getProfile().setSteps_goal(userInfo.getSteps_goal());
        setPersionOptionEntity.getProfile().setShare_birthday(userInfo.isShare_birthday());
        setPersionOptionEntity.getProfile().setShare_height(userInfo.isShare_height());
        setPersionOptionEntity.getProfile().setShare_weight(userInfo.isShare_weight());
        setPersionOptionEntity.getProfile().setShare_location(userInfo.isShare_location());
        setPersionOptionEntity.getProfile().setEnable_notification(userInfo.isEnable_notification());

        HashMap header = new HashMap<String,String>();
        header.put(AUTH_TOKEN, CacheUtils.getString(context,Constants.TOKEN));
        String url = NEW_URL+USERINFORMATION;
        post(url, setPersionOptionEntity, header,callback);

       /* HashMap<String, Integer> params = new HashMap<String, Integer>();

        params.put(Param_LANGUAGE ,getLanguage());
        params.put(PARAM_NICKNAME, userInfo.getNickname());
        params.put(PARAM_SEX, userInfo.getSex());
        params.put(PARAM_HEIGHT, userInfo.getHeight() );
        params.put(PARAM_WEIGHT, userInfo.getWeight() );
        params.put(PARAM_BIRTHDAY, userInfo.getBirthday()+"");
        params.put(PARAM_ADDRESS, userInfo.getAddress());
        params.put(PARAM_STEPNUM, userInfo.getSteps_goal());
        params.put(PARAM_SLEEPTIME, userInfo.getSleep_time_goal());
        params.put(PARAM_ISNOTIFICATION, userInfo.isEnable_notification() ? true : false);
        params.put(PARAM_ISLOCALSHARE, userInfo.isShare_location() ?  true : false);
        params.put(PARAM_ISOPENBIRTHDAY, userInfo.isShare_birthday() ?  true : false);
        params.put(PARAM_ISOPENHEIGHT, userInfo.isShare_height() ?  true : false);
        params.put(PARAM_ISOPENWEIGHT, userInfo.isShare_weight() ?  true : false);
        HashMap header = new HashMap<String,String>();
        header.put(AUTH_TOKEN, CacheUtils.getString(context,Constants.TOKEN));
        post(NEW_URL+"users/profile", params, header,callback);*/
    }

    /**
     * 获取用户信息
     *
     * @param callback
     */
    public void getUserInfo(Context context ,RequestCallback callback) {
        HashMap params = new HashMap<String,String>();
        params.put(Param_LANGUAGE ,getLanguage());
        HashMap header = new HashMap<String,String>();
        header.put(AUTH_TOKEN, CacheUtils.getString(context,Constants.TOKEN));
        get(NEW_URL+USERINFORMATION, params, header,callback);
    }

    /**
     * Pk详情
     */
    public void getPKInfo(final String pkId,RequestCallback callback) {
        IdentityHashMap<String, String> params = RequestMapParamsWithUserID(METHOD_GETPKINFO);
        params.put(PARAM_PKID, pkId);
        post(DEFULTURL, params, null,callback);
    }

    /**
     * 修改好友备注
     *
     * METHOD_SETFRIENDREMARK
     */

    public void setFriendRemark(final String friendid,final String remarks,RequestCallback callback) {
        IdentityHashMap<String, String> params = RequestMapParamsWithUserID(METHOD_SETFRIENDREMARK);
        params.put(Param_FRIENDID, friendid);
        params.put(PARAM_REMARKS, remarks);
        post(DEFULTURL, params, null,callback);
    }




    /**
     * 手机号注册--短信验证码
     * 忘记密码---手机验证码
     *
     * @param account  用户名
     * @param callback
     */
    public void getVerCode(boolean isemail,String account, boolean isForgetPwd,RequestCallback callback) {
        String url = "";
        HashMap<String, String> header = new HashMap<String, String>();
        header.put(Param_LANGUAGE, getLanguage2());
        if(isemail){
            if(isForgetPwd == false){
                url = NEW_URL+"signup/email/verify_code";
            }else{
                url = NEW_URL+"reset_password/email/verify_code";
            }
            HashMap<String, String> params = new HashMap<>();
            params.put(PARAM_EMAIL, account);
            post(url, params, header,callback);
        }else {
            if(isForgetPwd == false){
                url = NEW_URL+"signup/phone/verify_code";
            }else{
                url = NEW_URL+"reset_password/phone/verify_code";
            }
            HashMap<String, String> params = new HashMap<>();
            params.put(Param_PHONENUM, account);
            post(url, params, header,callback);
        }
    }

    /**
     * 根据经纬度获取城市名称
     *
     * @param longitud  经度
     * @param latitude  纬度
     * @param callback
     */
    public void getCityInfo(String longitud, String latitude, final RequestCallback callback) {

        String url = "http://api.map.baidu.com/geocoder?";
        url += "location=" + latitude + "," + longitud;
        url += "&output=" + "json";
        url += "&key=" + "34mQlpiuXAsX1SUUGctFbkGK";

        Log.e("TAG","查询天气的url---"+url);

        IdentityHashMap<String, String> params = new IdentityHashMap<String, String>();

        params.put("null", "null");  // 需要一个参数，否则会异常

        OkHttpUtils
                .post()
                .url(url)
                .params(params)
                .build()
                .execute(new Callback() {
                    @Override
                    public Object parseNetworkResponse(Response response) throws IOException {

                        String strJson = response.body().string();
                        Object entity = null;
                        if(callback != null && null != strJson){
                            int lngStart = strJson.indexOf("lng");
                            int lngEnd = strJson.indexOf("lat");

                            if(lngStart > 0 && lngEnd > 0){
                                entity = new Gson().fromJson(strJson, callback.mType);
                            }
                          }

                        return entity;
                    }

                    @Override
                    public void onError(Request request, Exception e) {
                        sendFailedResultCallback(request, e, callback);
                    }

                    @Override
                    public void onResponse(Object response) {
                        sendSuccessRequestCallback(response, callback);
                    }
                });
    }

    /**
     * 手机号，，邮箱，，注册、、忘记密码（手机号）
     *
     * @param account
     * @param password
     * @param code
     * @param isForgetPwd  是否是忘记密码
     * @param callback
     */
 public void regForget(Context context,boolean isemail,String account, String password, String code, boolean isForgetPwd, RequestCallback callback) {
     String url = "";
     if(isemail){//邮箱
         if(isForgetPwd == false){//注册
             url = NEW_URL+"signup/email";
             HashMap<String, String> params = new HashMap<String, String>();
             params.put(PARAM_EMAIL, account);
             params.put(Param_PWD, MD5.getMD5String(password));
             params.put(Param_CODE, code);
             params.put(PARAM_DEVICE_SERIAL, PhoneUtils.getUDID(context));
             params.put(PARAM_DEVICE_MODEL, "android "+Build.VERSION.SDK_INT);
             params.put(PARAM_AUTH_KEY, AUTH_KEY);
             params.put(PARAM_BAND_MAC, CacheUtils.getMacAdress(context));
             params.put(PARAM_LONGITUDE, CacheUtils.getString(context, Constants.LONGITUDE));
             params.put(PARAM_LATITUDE,  CacheUtils.getString(context, Constants.LATITUDE));
             params.put(PARAM_BAND_MAC, CacheUtils.getMacAdress(context));
             post(url, params, null,callback);
         }else{//忘记密码
             url = NEW_URL+"reset_password/email";
             HashMap<String, String> params = new HashMap<String, String>();
             params.put(PARAM_EMAIL, account);
             params.put(Param_PWD, MD5.getMD5String(password));
             params.put(Param_CODE, code);
             post(url, params, null,callback);
         }
     }else {//手机号
         if(isForgetPwd == false){//注册
             url = NEW_URL+"signup/phone";
             HashMap<String, String> params = new HashMap<String, String>();
             params.put(Param_PHONENUM, account);
             params.put(Param_PWD, MD5.getMD5String(password));
             params.put(Param_CODE, code);
             params.put(PARAM_DEVICE_SERIAL, PhoneUtils.getUDID(context));
             params.put(PARAM_DEVICE_MODEL, "android "+Build.VERSION.SDK_INT);
             params.put(PARAM_AUTH_KEY, AUTH_KEY);
             params.put(PARAM_BAND_MAC, CacheUtils.getMacAdress(context));
             params.put(PARAM_LONGITUDE, CacheUtils.getString(context, Constants.LONGITUDE));
             params.put(PARAM_LATITUDE,  CacheUtils.getString(context, Constants.LATITUDE));
             params.put(PARAM_BAND_MAC, CacheUtils.getMacAdress(context));
             post(url, params, null,callback);
         }else{//忘记密码
             url = NEW_URL+"reset_password/phone";
             HashMap<String, String> params = new HashMap<String, String>();
             params.put(Param_PHONENUM, account);
             params.put(Param_PWD, MD5.getMD5String(password));
             params.put(Param_CODE, code);
             post(url, params, null,callback);
         }
     }
    }

    /**
     * 添加/同意 好友
     *
     * @param friendId    // 好友ID
     * @param addType     // 1:好友请求 2：好友请求确认
     * @param msg         // 验证信息
     * @param callback
     */
    public void addFriend(String friendId, AddFriendType addType, String msg, RequestCallback callback){

        IdentityHashMap<String, String> params = RequestMapParamsWithUserID(METHOD_ADDFRIEND);

        params.put(Param_FRIENDID, friendId);
        params.put(PARAM_TYPE, addType.getType());
        params.put(PARAM_VERIFYMSG, msg);

        post(DEFULTURL, params, null,callback);
    }

    /**
     * 查询请求添加好友列表
     *
     * @param callback
     */
    public void getFriendReqList(RequestCallback callback){

        IdentityHashMap<String, String> params = RequestMapParamsWithUserID(METHOD_GETFRIENDREQLIST);

        post(DEFULTURL, params, null,callback);
    }

    /**
     * 搜索好友
     *
     * @param searchContent     // 账号，手机、邮箱，支持模糊搜索
     * @param callback
     */
    public void searchFriend(String searchContent, RequestCallback callback){

        IdentityHashMap<String, String> params = RequestMapParamsWithUserID(METHOD_SEARCHFRIEND);

        params.put(PARAM_SEARCHTYPE, SearchType.PHONEEMAIL.getType());
        params.put(Param_ACCOUNT, searchContent);
        post(DEFULTURL, params, null,callback);
    }

    /**
     * 搜索豚鼠好友
     *
     * @param callback
     */
    public void searchFriend(RequestCallback callback){

        IdentityHashMap<String, String> params = RequestMapParamsWithUserID(METHOD_SEARCHFRIEND);

        params.put(PARAM_SEARCHTYPE, SearchType.CAVYFRIEND.getType());

        post(DEFULTURL, params, null,callback);
    }

    /**
     * 搜索手机通讯录好友
     *
     * @param phoneNumList      // 手机号集，不支持模糊搜索
     * @param callback
     */
    public void searchFriend(List<String> phoneNumList, RequestCallback callback){
        SearchFriendContactsReq  searchReq = new SearchFriendContactsReq();
        setCommon(searchReq);
        searchReq.setCmd(METHOD_SEARCHFRIEND);
        searchReq.setPhoneNumList(phoneNumList);
        searchReq.setSearchType(SearchType.CONTACT.getType());

        post(DEFULTURL, searchReq, null,callback);
    }

    /**
     * 搜索附近好友
     *
     * @param longitude        // 经度
     * @param latitude         // 纬度
     * @param callback
     */
    public void searchFriend(double longitude, double latitude, RequestCallback callback){
        IdentityHashMap<String, String> params = RequestMapParamsWithUserID(METHOD_SEARCHFRIEND);
        params.put(PARAM_SEARCHTYPE, SearchType.NEARFRIEND.getType());
        params.put(PARAM_LONGITUDE, "" + longitude);
        params.put(PARAM_LATITUDE, "" + latitude);
        post(DEFULTURL, params, null,callback);
    }

    /**
     * 查询好友信息
     *
     * @param friendId    好友ID
     * @param callback
     */
    public void getFriendInfo(String friendId, RequestCallback callback){
        IdentityHashMap<String, String> params = RequestMapParamsWithUserID(METHOD_GETFRIENDINFO);
        params.put(Param_FRIENDID, friendId);
        post(DEFULTURL, params, null,callback);
    }

    /**
     * 获取好友列表信息
     *
     * @param callback
     */
    public void getFriendList(RequestCallback callback){
        IdentityHashMap<String, String> params = RequestMapParamsWithUserID(METHOD_GETFRIENDLIST);
        params.put(PARAM_TYPE,"0");
        post(DEFULTURL, params, null,callback);
    }

    /**
     * 删除好友
     *
     * @param friendId    好友ID
     * @param callback
     */
    public void deleteFriend(String friendId, RequestCallback callback){
        IdentityHashMap<String, String> params = RequestMapParamsWithUserID(METHOD_DELEFRIEND);
        params.put(Param_FRIENDID, friendId);

        post(DEFULTURL, params, null,callback);
    }

    /**
     * 关注/取消关注好友
     * @param friendId    好友ID
     * @param isFollow    1：关注；0：取消关注
     * @param callback
     */
    public void followFriend(String friendId, boolean isFollow, RequestCallback callback){
        IdentityHashMap<String, String> params = RequestMapParamsWithUserID(METHOD_FOLLOWUSER);
        params.put(Param_FRIENDID, friendId);
        params.put(PARAM_OPERATE, isFollow ? "1" : "0");

        post(DEFULTURL, params, null,callback);
    }

    /**
     * PK列表查询
     *
     * @param callback
     */
    public void getPkList(RequestCallback callback){

        IdentityHashMap<String, String> params = RequestMapParamsWithUserID(METHOD_GETPKLIST);
        params.put("type","0");
        post(DEFULTURL, params, null,callback);
    }

    /**
     * 好友PK列表查询
     *
     * @param callback
     */
    public void getfriendPkList(String fid,RequestCallback callback){

        IdentityHashMap<String, String> params = RequestMapParamsWithUserID(METHOD_GETPKLIST);
        params.put("type","1");
        params.put(Param_FRIENDID,fid);
        post(DEFULTURL, params, null,callback);
    }

    /**
     * 发起PK
     *
     * @param pkList    PK列表
     * @param callback
     */
    public void launchPK(ArrayList<LaunchPkReq.PkListEntity> pkList, RequestCallback callback){

        LaunchPkReq pkReq = new LaunchPkReq();

        setCommon(pkReq);
        pkReq.setCmd(METHOD_LAUNCHPK);

        pkReq.setLaunchPkList(pkList);

        post(DEFULTURL, pkReq, null,callback);
    }

    /**
     * 接受PK
     *
     * @param pkId    pkId
     * @param callback
     */
    public void acceptPk(String pkId, RequestCallback callback){

        AcceptPkReq req = new AcceptPkReq();
        setCommon(req);

        req.setCmd(METHOD_ACCEPT);

        List<String> pkList = new ArrayList<String>();
        pkList.add(pkId);
        req.setAcceptPkList(pkList);


        post(DEFULTURL, req, null,callback);
    }

    /**
     * 撤销PK
     *
     * @param pkId    pkId
     * @param callback
     */
    public void undoPk(String[] pkId, RequestCallback callback){

        UndoPkReq req = new UndoPkReq();
        setCommon(req);
        req.setCmd(METHOD_UNDOPK);
        req.setUndoPkList(pkId);

        post(DEFULTURL, req, null,callback);
    }

    /**
     * 删除已完成PK
     * @param pkId    pkId
     * @param callback
     */
    public void deletePk(String pkId, RequestCallback callback){

        DeletePkReq req = new DeletePkReq();
        setCommon(req);

        req.setCmd(METHOD_DELEPK);

        List<String> pkList = new ArrayList<String>();
        pkList.add(pkId);
        req.setDelPkList(pkList);

        post(DEFULTURL, req, null,callback);
    }

    /**
     * 获取帮助与反馈列表
     * @param callback
     */
    public void getHelpList(Context context,RequestCallback callback){
//        IdentityHashMap<String, String> params = RequestMapParamsWithUserID(METHOD_GETHELPLIST);
//        post(DEFULTURL, params, null,callback);

        String url = NEW_URL+"helps";
        HashMap header = new HashMap<String,String>();
        header.put(AUTH_TOKEN, CacheUtils.getString(context, Constants.TOKEN));
        header.put(Param_LANGUAGE, getLanguage());
        header.put(Param_PHONETYPE, PHONETYPE_ANDROID);
        get(url, null, header,callback);
    }


    /**
     * 天气查询
     * @param pinYinCity   城市的拼音
     * @param callback
     */
    public void getWeather(Context context,String pinYinCity, RequestCallback callback){
        Log.e("TAG","天气查询----"+pinYinCity);
        String url = NEW_URL+"weather?city="+pinYinCity;
        HashMap header = new HashMap<String,String>();
        header.put(AUTH_TOKEN, CacheUtils.getString(context, Constants.TOKEN));
        get(url, null, header,callback);
    }


    /**
     * 首页
     * @param date
     * @param callback
     */
    public void getHomePager(String date, RequestCallback callback){

        IdentityHashMap<String, String> params = RequestMapParamsWithUserID(METHOD_INDEX);

        params.put(PARAM_DATE, date);

        post(DEFULTURL, params, null,callback);
    }

    /**
     * 计步信息
     * @param callback
     */
 /*   public void getStepData(RequestCallback callback){

        IdentityHashMap<String, String> params = RequestMapParamsWithUserID(METHOD_GETSTEPCOUNT);

        post(DEFULTURL, params, callback);
    }
*/
    public void getStepData(String startdate,String enddata,Context context,RequestCallback callback) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Param_LANGUAGE, getLanguage());
        String url = NEW_URL + "steps?start_date=" + startdate + "&end_date=" + enddata;
        HashMap header = new HashMap<String,String>();
        header.put(AUTH_TOKEN, CacheUtils.getString(context, Constants.TOKEN));
        Log.e("pip",CacheUtils.getString(context, Constants.TOKEN));
        get(url, params, header,callback);
    }
    /**
     * 计步信息
     * @param callback
     */
    /*public void getStepDataday(String startdate,String enddata,RequestCallback callback){
        IdentityHashMap<String, String> params = RequestMapParamsWithUserID(METHOD_GETSLEEPINFO);
        params.put(PARAM_STARTDATE, startdate);
        params.put(PARAM_ENDDATE, enddata);
        post(DEFULTURL, params, callback);
    }*/


    public void getStepDataday(String start_date,String end_date,RequestCallback callback){
       //http://pay.tunshu.com/live/api/v1/steps?start_date=2016-6-5&end_date=2016-6-20
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Param_LANGUAGE, getLanguage());
        String url = NEW_URL+"steps?start_date="+start_date+"&end_date="+end_date;
        get(url, params, null,callback);
    }

    /**
     * 睡眠信息
     * @param callback
     *
     * sleep?start_date=2016-6-5&end_date=2016-6-20
     *
     *
     */
    public void getSleepInfo(String startdate,String enddata,Context context,RequestCallback callback){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Param_LANGUAGE, getLanguage());
        String url = NEW_URL + "sleep?start_date=" + startdate + "&end_date=" + enddata;
        HashMap header = new HashMap<String,String>();
        header.put(AUTH_TOKEN, CacheUtils.getString(context,Constants.TOKEN));
        get(url, params, header,callback);
    }


    /**
     * 提交意见反馈
     * @param callback
     */
    public void submitFeedback(Context context,String detail, RequestCallback callback){
        String url = NEW_URL+"issues";
        HashMap header = new HashMap<String,String>();
        header.put("auth-token", CacheUtils.getString(context,"token"));
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(PARAM_QUESTION, "1");
        params.put(PARAM_DETAIL, detail);
        post(url, params, header,callback);
    }

    /**
     * 设置紧急联系人
     */
    public void setEmergencyPhone(Context context,List<GetPhone.ContactsBean> phoneNumList, RequestCallback callback){
        EmergencyPhoneBean searchReq = new EmergencyPhoneBean();
        searchReq.setLanguage(getLanguage());
        searchReq.setContacts(phoneNumList);
        HashMap header = new HashMap<String,String>();
        header.put("auth-token", CacheUtils.getString(context,"token"));
        String url = NEW_URL+"emergency/contacts";
        post(url, searchReq, header,callback);
    }


    /**
     * 获取紧急联系人
     */
    public void getEmergencyPhone(Context context,RequestCallback callback){
        String url = NEW_URL+"emergency/contacts";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Param_LANGUAGE, getLanguage());
        HashMap header = new HashMap<String,String>();
        header.put("auth-token", CacheUtils.getString(context,"token"));
        get(url,params,header,callback);
    }

    /**
     * post
     *
     * @param params     // 参数 可以是map 或者 bean
     * @param callback
     */
    private void post(String url, Object params, Map header,final RequestCallback callback){
        if(header!=null){
            if(!header.containsKey(Param_LANGUAGE)){
                header.put(Param_LANGUAGE, getLanguage());//用于解决短信验证码bug
            }
            header.put(Param_PHONETYPE, PHONETYPE_ANDROID);
        }
        OkHttpUtils
                .postString()
                .url(url)
                .headers(header)
                .content(new Gson().toJson(params))
                .mediaType(JSON)
                .build()
                .execute(new Callback() {
                    @Override
                    public Object parseNetworkResponse(Response response) throws IOException {

                        String string = response.body().string();
                        Log.w("TAG","--------string--------"+string);

                        Object entity = null;
                        if(callback != null){
                            entity = new Gson().fromJson(string, callback.mType);
                        }
                        return entity;
                    }

                    @Override
                    public void onError(Request request, Exception e) {
                        sendFailedResultCallback(request, e, callback);
                    }

                    @Override
                    public void onResponse(Object response) {
                        sendSuccessRequestCallback(response, callback);
                     }
                    });
    }

    /**
     * get
     *
     * @param params     // 参数 可以是map 或者 bean
     * @param callback
     */
    private void get(String url, Object params, Map header,final RequestCallback callback){

        if(header!=null){
            header.put(Param_LANGUAGE, getLanguage());
            header.put(Param_PHONETYPE, PHONETYPE_ANDROID);

        }

        OkHttpUtils
                .get()
                .url(url)
                .headers(header)
                .build()
                .execute(new Callback() {
                    @Override
                    public Object parseNetworkResponse(Response response) throws IOException {

                        String string = response.body().string();
                        Log.w("TAG","--------string--------"+string);
                        Object entity = null;
                        if(callback != null){
                            entity = new Gson().fromJson(string, callback.mType);
                        }

                        return entity;
                    }

                    @Override
                    public void onError(Request request, Exception e) {
                        sendFailedResultCallback(request, e, callback);
                    }

                    @Override
                    public void onResponse(Object response) {
                        sendSuccessRequestCallback(response, callback);
                    }
                });
    }

    /**
     * 上传用户头像
     * @param imgPath     图片路径
     * @param callback
     */
/*    public void uploadFile(String userId, String imgPath, final OkHttpClientManager.ResultCallback callback) {

        try {
            File file = new File(new URI(imgPath));
            OkHttpClientManager.postAsyn(URL+ HTTP_PRE_USERICON,callback,file,PARAM_IMGFILE, new OkHttpClientManager.Param[]{
                    new OkHttpClientManager.Param(Param_CMD, METHOD_SETUSERICON),
                    new OkHttpClientManager.Param(PARAM_USERID, userId),
                    new OkHttpClientManager.Param(PARAM_FILENAME, file.getName())});

        }catch (Exception e){

        }
     }*/

    public void uploadFile(Context context,String imgPath, RequestCallback callback) {
        HashMap header = new HashMap<String,String>();
        header.put("auth-token", CacheUtils.getString(context,"token"));
        String url = NEW_URL+"avatar";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(PARAM_BASE64DATA, imgPath);
        post(url, params, header,callback);
     }


    /**
     * 发送紧急消息
     *
     * @param callback
     */
    public void sendEmergencyMsg(Context context,String longitude, String latitude,RequestCallback callback){
        String url = NEW_URL+"emergency";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(PARAM_LONGITUDE, longitude);
        params.put(PARAM_LATITUDE, latitude);
        params.put(Param_LANGUAGE, getLanguage());
        HashMap header = new HashMap<String,String>();
        header.put("auth-token", CacheUtils.getString(context,"token"));
        post(url, params, header,callback);
    }

    /**
     * 查询版本信息
     *
     * @param callback
     */
    public void getVersion(Context context,RequestCallback callback){
        String url = NEW_URL+"live_app";
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("auth-token", CacheUtils.getString(context,"token"));
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Param_LANGUAGE, getLanguage());
        get(url, params, header,callback);
    }

    /**
     * 首页信息
     * @param callback
     */
    public void getHomepagerDate(String start_date,String end_date,Context context,RequestCallback callback){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Param_LANGUAGE, getLanguage());
        String url = NEW_URL+"dailies?start_date="+start_date+"&end_date="+end_date;
        HashMap header = new HashMap<String,String>();
        header.put(AUTH_TOKEN, CacheUtils.getString(context,Constants.TOKEN));
        get(url, params, header,callback);
    }


    /** 查询固件信息
     *
     * @param callback
     */
    public void getGuJianVersion(Context context,RequestCallback callback){
        String url = NEW_URL+"firmware";
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("auth-token", CacheUtils.getString(context,"token"));
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put(Param_LANGUAGE, getLanguage());
        get(url, null, header,callback);
    }


    /**
     * 上传活动事件
     */

    public void activities(Context context,String longitude,String latitude,String band_mac,String device_model,String event_type,RequestCallback callback){
        String url = NEW_URL+"activities";
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("auth-token", CacheUtils.getString(context,"token"));
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(PARAM_EVENT_TYPE, event_type);
        params.put(PARAM_DEVICE_SERIAL, PhoneUtils.getUDID(context));
        params.put(PARAM_DEVICE_MODEL, android.os.Build.MODEL+"-"+device_model);
        params.put(PARAM_AUTH_KEY, AUTH_KEY);
        params.put(PARAM_BAND_MAC, band_mac);
        params.put(PARAM_LONGITUDE, longitude);
        params.put(PARAM_LATITUDE, latitude);
        post(url, params, header,callback);
    }

}
