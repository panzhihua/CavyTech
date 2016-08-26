package com.cavytech.wear2.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by LiBin on 2016/5/27.
 */
public class StatusReceiver extends BroadcastReceiver {
    private int statu ;

    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra("status",-1);
        Log.e("TAg","StatusReceiver-----"+status+"--------------------------");
        statu = status;
    }
}
