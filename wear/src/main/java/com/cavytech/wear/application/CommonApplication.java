package com.cavytech.wear.application;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import com.basecore.application.BaseApplication;
import com.cavytech.wear.util.LogUtil;

/**
 * Created by ShadowNight on 2015/8/19.
 */
public class CommonApplication extends BaseApplication {
    private LocationClient mLocationClient;
    public MyLocationListener mMyLocationListener;
    private LocationClientOption.LocationMode tempMode = LocationClientOption.LocationMode.Hight_Accuracy;
    private String lbs;

    @Override
    public void onCreate() {
        super.onCreate();

        getLoctionAddress();
    }

    private void getLoctionAddress() {
        mLocationClient = new LocationClient(getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(tempMode);//设置定位模式
        option.setCoorType("gcj02");//返回的定位结果是百度经纬度，默认值gcj02
        option.setScanSpan(60000);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            lbs=location.getLongitude()+","+location.getLatitude();
            LogUtil.getLogger().d(lbs);
            mLocationClient.stop();
        }
    }

    public String getLBS(){
        return lbs;
    }
}
