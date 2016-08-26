package com.cavytech.wear2.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.cavytech.wear2.entity.GetPhone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/29.
 */
public class CacheUtils {

    private static LocationClient mLocationClient;
    public static MyLocationListener mMyLocationListener;
    private static LocationClientOption.LocationMode tempMode = LocationClientOption.LocationMode.Hight_Accuracy;

    public static double Longitude;//经度
    public static double Latitude;//纬度

    /**
     * 保存软件参数
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences("tunshu", Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }

    /**
     * 得到软件保存的参数
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("tunshu", Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }


    /**
     * 数据缓存方法
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences("tunshu", Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }

    /**
     * 得到软件缓存的数据
     *
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("tunshu", Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    /**
     * 数据缓存方法
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putInt(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences("tunshu", Context.MODE_PRIVATE);
        sp.edit().putInt(key, value).commit();
    }

    /**
     * 得到软件缓存的数据
     *
     * @param context
     * @param key
     * @return
     */
    public static int getInt(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("tunshu", Context.MODE_PRIVATE);
        return sp.getInt(key, 0);
    }


    public static boolean saveArray(Context context, ArrayList<GetPhone.ContactsBean> key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor mEdit1 = sp.edit();
        mEdit1.putInt("Status_size", key.size()); /*sKey is an array*/
        for (int i = 0; i < key.size(); i++) {
            mEdit1.remove("Status_" + i);
            mEdit1.putString("Status_" + i, key.get(i).getPhone());
        }

        return mEdit1.commit();
    }

    public static void loadArray(Context mContext, ArrayList list) {
        SharedPreferences mSharedPreference1 = PreferenceManager.getDefaultSharedPreferences(mContext);
        list.clear();
        int size = mSharedPreference1.getInt("Status_size", 0);

        for (int i = 0; i < size; i++) {
            list.add(mSharedPreference1.getString("Status_" + i, null));

        }
    }


    public static void initGPS(final Activity mActivity) {
        LocationManager locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager
                .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            Toast.makeText(mActivity, "请打开GPS", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
            dialog.setMessage("请打开GPS");
            dialog.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // 转到手机设置界面，用户设置GPS
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    mActivity.startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                }
            });
            dialog.setNeutralButton("取消", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                }
            });
            dialog.show();
        } else {
            // 弹出Toast
            Toast.makeText(mActivity, "GPS is ready", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @param context
     * @return true 表示开启
     */
    public static final boolean isOPen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    /**
     * 清除sp缓存
     */

    public static void clearData(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("tunshu", Context.MODE_PRIVATE);
        sp.edit().remove(key).commit();
    }

    public static String getMacAdress(Context context) {

        Map<String, String> userBandInfoMap = (Map<String, String>) SerializeUtils.unserialize(Constants.SERIALIZE_USERBAND_INFO);

        if (null != userBandInfoMap) {
            String userID = CacheUtils.getString(context, Constants.USERID);
            return userBandInfoMap.get(userID);
        } else {
            return "";
        }

    }

    public static void saveMacAdress(Context context, String macAdr) {


        Map<String, String> userBandInfoMap = (Map<String, String>) SerializeUtils.unserialize(Constants.SERIALIZE_USERBAND_INFO);

        if (null == userBandInfoMap) {

            userBandInfoMap = new HashMap<String, String>();
        }

        userBandInfoMap.put(CacheUtils.getString(context, Constants.USERID), macAdr);

        SerializeUtils.serialize(userBandInfoMap, Constants.SERIALIZE_USERBAND_INFO);
    }

    /**
     * 判断手机是否联网
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
        } else {
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 百度地图SDK   获取经纬度
     */

    public static void getLoctionAddress(Context context) {
        mLocationClient = new LocationClient(context);
        mMyLocationListener = new MyLocationListener(context);
        mLocationClient.registerLocationListener(mMyLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(tempMode);//设置定位模式
//        option.setCoorType("gcj02");//返回的定位结果是百度经纬度，默认值gcj02
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度，默认值gcj02
        option.setScanSpan(60000);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    public static class MyLocationListener implements BDLocationListener {
        Context context;

        public MyLocationListener(Context context) {
            this.context = context;
        }

        @Override
        public void onReceiveLocation(BDLocation location) {
            mLocationClient.stop();
            Longitude = location.getLongitude();
            Latitude = location.getLatitude();
            CacheUtils.putString(context, Constants.LONGITUDE, Longitude + "");
            CacheUtils.putString(context, Constants.LATITUDE, Latitude + "");
            Log.e("TAG", "存入的经纬度---" + Longitude + "----" + Latitude);
        }
    }

}
