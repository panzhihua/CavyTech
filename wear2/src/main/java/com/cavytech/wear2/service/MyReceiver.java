/*
package com.cavytech.wear2.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cavytech.wear2.application.CommonApplication;

import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

*/
/**
 * Created by Tong on 2015/11/23.
 *//*

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver(){

    }
    public void onReceive(Context context, Intent intent) {
        if(JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())){
            Bundle bunild=intent.getExtras();
            String extras = bunild.getString(JPushInterface.EXTRA_EXTRA);
            try{
                JSONObject pushObject=new JSONObject(extras);
                if(pushObject.get("pushType").equals("updateApp")){
                    Log.d("jpush", pushObject.getString("pushType"));
                    ((CommonApplication) context.getApplicationContext()).openApp("com.tunshu");
                }
                if (pushObject.get("pushType").equals("game")){
                    Log.d("jpush", pushObject.getString("gameId"));
                    */
/*
                    Intent gameIntent = new Intent(context, GameDetailActivity.class);
                    gameIntent.putExtra("gameId", pushObject.getString("gameId"));
                    gameIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(gameIntent);*//*

                }
                if(pushObject.get("pushType").equals("sysMessage")){
                    Log.d("jpush", pushObject.getString("url"));
                    */
/*
                    Intent noticeIntent = new Intent(context, WebActivity.class);
                    noticeIntent.putExtra("titleName",pushObject.getString("title"));
                    noticeIntent.putExtra("webUrl",pushObject.getString("url"));
                    noticeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(noticeIntent);*//*

                }
                if(pushObject.get("pushType").equals("prefecture")){
                    Log.d("jpush", pushObject.getString("title"));
                    */
/*
                    Intent specialIntent = new Intent(context, SpecialActivity.class);
                    specialIntent.putExtra("preId",pushObject.getString("prefectureId"));
                    specialIntent.putExtra("name", pushObject.getString("title"));
                    specialIntent.putExtra("style", pushObject.getString("style"));
                    specialIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(specialIntent);*//*

                }
            }catch(Exception e){
                e.printStackTrace();
            }


            Log.d("jpush",extras);

        }
    }



}
*/
