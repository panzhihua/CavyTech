package com.cavytech.wear2.application;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class PhoneService extends Service{
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("TAG", "ServiceDemo------onBind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TAG", "ServiceDemo------onCreate");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.e("TAG", "ServiceDemo------onStart");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e("TAG", "ServiceDemo------onStartCommand");
        startForeground(1,new Notification());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        Log.e("TAG", "ServiceDemo------onDestroy");
    }
}
