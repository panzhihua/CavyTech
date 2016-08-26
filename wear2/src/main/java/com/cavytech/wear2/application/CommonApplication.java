package com.cavytech.wear2.application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.basecore.application.BaseApplication;
import com.cavytech.wear2.util.CacheUtils;
import com.cavytech.wear2.util.Constants;
import com.cavytech.wear2.util.MyActivityLifecycleCallbacks;

import org.xutils.DbManager;
import org.xutils.x;

public class  CommonApplication extends BaseApplication {

    public static double Longitude;//经度
    public static double Latitude;//纬度
    public static DbManager dm;
    public static String userID;

    public static boolean isLogin = false;
    private DbManager.DaoConfig daoconfig;

    public static int connectionCode;

    @Override
    public void onCreate() {
        super.onCreate();
        CacheUtils.getLoctionAddress(this);
        x.Ext.init(this);
        x.Ext.setDebug(true);

        initdao();

        registerReceiver();

        getFwVision();

        getBandStudsReceiver();

        registerActivityLifecycleCallbacks(new MyActivityLifecycleCallbacks());

    }


    private void registerReceiver() {

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("isConnectionBand");//建议把它写一个公共的变量，这里方便阅读就不写了。
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int eventCode = intent.getIntExtra("eventCode", -1);
                connectionCode = eventCode;
                CacheUtils.putInt(context, Constants.ISCONNECTIONBAND, eventCode);
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }

    /**
     * fw版本号
     */
    private void getFwVision() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.DATA_FWVERSION);//建议把它写一个公共的变量，这里方便阅读就不写了。
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int fwVersion = intent.getIntExtra("fwVersion", 0);

                CacheUtils.putInt(context, Constants.FWVISION, fwVersion);
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }

    /**
     * 手环电量
     */
    private void getBandStudsReceiver() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.cavytech.wear2.service.StatusReceiver");//建议把它写一个公共的变量，这里方便阅读就不写了。
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                int bandStatus = intent.getIntExtra("status", 0);

                CacheUtils.putInt(context, Constants.STATUS, bandStatus);

            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }

    /**
     * 初始化数据库
     */
    private void initdao() {

        daoconfig = new DbManager.DaoConfig()
                .setDbName("CB_LIST.db")
//                .setDbDir(new File("/sdcard"))
                .setDbVersion(1);
        CommonApplication.dm = x.getDb(daoconfig);

    }

}
