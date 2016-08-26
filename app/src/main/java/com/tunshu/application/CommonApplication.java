package com.tunshu.application;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Environment;

import com.basecore.application.BaseApplication;
import com.basecore.util.core.ListUtils;
import com.basecore.util.core.OpenFiles;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.tunshu.entity.GameListEntity;
import com.tunshu.service.DownloadService;
import com.tunshu.util.Constants;
import com.tunshu.util.LogUtil;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import net.tsz.afinal.FinalDb;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class  CommonApplication extends BaseApplication {
    public FinalDb finalDb;

    private LocationClient mLocationClient;
    public MyLocationListener mMyLocationListener;
    private LocationClientOption.LocationMode tempMode = LocationClientOption.LocationMode.Hight_Accuracy;
    private String lbs;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.getLogger().d("jpush");
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        Fresco.initialize(this);
        dbInit();
        Intent intent = new Intent(this, DownloadService.class);
        startService(intent);

        getLoctionAddress();
    }

    public String getDownloadFilePath() {
        String downloadPath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            LogUtil.getLogger().d(Environment.getExternalStoragePublicDirectory("download"));
            downloadPath = Environment.getExternalStoragePublicDirectory("download").getAbsolutePath() + File.separator + "CavyTech";
        } else {
            downloadPath = Environment.getExternalStorageDirectory() + File.separator + "CavyTech";
        }
        File downloadFile = new File(downloadPath);
        if (!downloadFile.isDirectory() && !downloadFile.mkdirs()) {
            throw new IllegalAccessError(" cannot create download folder");
        }
        LogUtil.getLogger().d(downloadPath);
        return downloadPath;
    }

    public void appInstall(final int taskToken) throws ActivityNotFoundException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.getLogger().d(getPreferenceConfig().getString(String.valueOf(taskToken), ""));
                File file = new File(getPreferenceConfig().getString(String.valueOf(taskToken), ""));
                LogUtil.getLogger().d(file.isFile());
                if (file.isFile()) {
                    Intent intent = OpenFiles.getApkFileIntent(file);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                }
            }
        }).start();
    }

    public void openApp(final String packageName) throws PackageManager.NameNotFoundException {

        try {
            PackageInfo pi = getPackageManager().getPackageInfo(packageName, 0);
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            resolveIntent.setPackage(pi.packageName);
            List<ResolveInfo> apps = getPackageManager().queryIntentActivities(resolveIntent, 0);
            if (apps != null) {
                ResolveInfo ri = apps.iterator().next();
                if (ri != null) {
                    String namePackage = ri.activityInfo.packageName;
                    String className = ri.activityInfo.name;
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    ComponentName cn = new ComponentName(namePackage, className);
                    intent.setComponent(cn);
                    startActivity(intent);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void dbInit() {
        finalDb = FinalDb.create(getApplicationContext(), "cavyTech", true,
                Constants.DB_VERSION, null);
    }

    public void saveInfoToDb(final GameListEntity item) {
        List<GameListEntity> downloadInfoList = finalDb.findAll(GameListEntity.class);
        if (ListUtils.isEmpty(downloadInfoList)) {
            downloadInfoList = new ArrayList<>();
            downloadInfoList.add(item);
        } else {
            boolean isNew = true;
            for (int i = 0, size = downloadInfoList.size(); i < size; i++) {
                LogUtil.getLogger().d(downloadInfoList.get(i).getPackpagename() + "-->" + item.getPackpagename());
                if (downloadInfoList.get(i).getPackpagename().equals( item.getPackpagename())) {
                    downloadInfoList.set(i, item);
                    isNew = false;
                }
            }
            if (isNew) {
                downloadInfoList.add(item);
            }
        }
        finalDb.deleteAll(GameListEntity.class);
        for (int i = 0, size = downloadInfoList.size(); i < size; i++) {
            finalDb.save(downloadInfoList.get(i));
        }
        List<GameListEntity> saveList = finalDb.findAll(GameListEntity.class);
        for (int i = 0, size = saveList.size(); i < size; i++) {
            LogUtil.getLogger().d(saveList.get(i).getGamename() + "--->" + saveList.get(i).getTaskId());
        }
    }
    public void deleteDownloadInfo(String taskId) {
        final List<GameListEntity> downloadInfoList = finalDb.findAll(GameListEntity.class);
        if (!ListUtils.isEmpty(downloadInfoList)) {
            for (int i = 0, size = downloadInfoList.size(); i < size; i++) {
                if (taskId.equals(downloadInfoList.get(i).getTaskId())) {
                    LogUtil.getLogger().d(taskId);
                    downloadInfoList.remove(i);
                    break;
                }
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    finalDb.deleteAll(GameListEntity.class);
                    for (int j = 0, length = downloadInfoList.size(); j < length; j++) {
                        finalDb.save(downloadInfoList.get(j));
                    }
                }
            }).start();
        }
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
