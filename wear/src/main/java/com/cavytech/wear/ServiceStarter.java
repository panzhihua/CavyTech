package com.cavytech.wear;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ServiceStarter extends BroadcastReceiver {

    public final static String INVOKE_WEAR_SERVICE = "com.cavytech.wear.invoke_service";

    @Override
    public void onReceive( Context context, Intent intent ) {
        if ( intent.getAction().equals( INVOKE_WEAR_SERVICE ) ) {
            context.startService(new Intent(context, BraceletService.class));
        } else if ( intent.getAction().equals( Intent.ACTION_USER_PRESENT ) || intent.getAction().equals( Intent.ACTION_BOOT_COMPLETED ) ) {
            context.startService(new Intent(context, BraceletService.class));
        }
    }
}
