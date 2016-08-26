package com.cavytech.wear2.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.cavytech.wear2.application.CommonApplication;
import com.cavytech.wear2.entity.CommonEntity;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.RequestCallback;
import com.squareup.okhttp.Request;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.List;

/**
 * Created by LiBin on 2016/7/21.
 */
public class MyActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks{

    private boolean isActive;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {

        MobclickAgent.onResume(activity);

        if (!isActive) {
             isActive = true;
//            Log.e("TAG","APP进入前台---");

            HashMap<String,String> map = new HashMap<String,String>();
            map.put("device_serial", PhoneUtils.getUDID(activity));
            map.put("device_model",android.os.Build.MODEL+"-"+Build.VERSION.SDK_INT);
            map.put("band_mac",CacheUtils.getMacAdress(activity));
            map.put("user_id", CommonApplication.userID);
            map.put("longitude",CacheUtils.getString(activity, Constants.LONGITUDE));
            map.put("latitude",CacheUtils.getString(activity, Constants.LATITUDE));

            MobclickAgent.onEvent(activity, Constants.APP_OPEN,map);

            HttpUtils.getInstance().activities(activity,CacheUtils.getString(activity, Constants.LONGITUDE),
                    CacheUtils.getString(activity, Constants.LATITUDE), CacheUtils.getMacAdress(activity),""+ Build.VERSION.SDK_INT, "APP_OPEN",new RequestCallback<CommonEntity>(){

                        @Override
                        public void onError(Request request, Exception e) {
                            Log.e("TAG","活动上传失败--app open--"+e.getLocalizedMessage()+e.getMessage().toString());
                        }

                        @Override
                        public void onResponse(CommonEntity response) {
                            Log.e("TAG","活动上传成功--app open--"+response.getCode());
                        }
                    });

        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        MobclickAgent.onPause(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (!isAppOnForeground(activity)) {
            isActive = false;
            Log.e("TAG","监听到APP进入后台---");

            HashMap<String,String> map = new HashMap<String,String>();
            map.put("device_serial", PhoneUtils.getUDID(activity));
            map.put("device_model",android.os.Build.MODEL+"-"+Build.VERSION.SDK_INT);
            map.put("band_mac",CacheUtils.getMacAdress(activity));
            map.put("user_id",CommonApplication.userID);
            map.put("longitude",CacheUtils.getString(activity, Constants.LONGITUDE));
            map.put("latitude",CacheUtils.getString(activity, Constants.LATITUDE));

            MobclickAgent.onEvent(activity, Constants.APP_QUIT,map);

            HttpUtils.getInstance().activities(activity,CacheUtils.getString(activity, Constants.LONGITUDE),
                    CacheUtils.getString(activity, Constants.LATITUDE), CacheUtils.getMacAdress(activity),""+ Build.VERSION.SDK_INT, "APP_QUIT",new RequestCallback<CommonEntity>(){

                        @Override
                        public void onError(Request request, Exception e) {
                            Log.e("TAG","活动上传失败--app quit--"+e.getLocalizedMessage()+e.getMessage().toString());
                        }

                        @Override
                        public void onResponse(CommonEntity response) {
                            Log.e("TAG","活动上传成功--app quit--"+response.getCode());
                        }
                    });

        }

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public boolean isAppOnForeground(Activity activity) {
        ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = activity.getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

}
