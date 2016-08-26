package com.cavytech.wear2.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.cavytech.wear2.activity.ProgressbarAcitvity;
import com.cavytech.wear2.entity.CheckVersionBean;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.RequestCallback;
import com.cavytech.widget.CustomDialog;
import com.squareup.okhttp.Request;

/**
 * Created by LiBin on 2016/7/5.
 * <p/>
 * 检查app版本更新
 */
public class CheckVerson {

    private static CheckVerson mInstance;
    private static int versionCode;
    private static String versionName;
    private static String packageNames;

    /**
     * 1.联网获取最新版本号
     * 2.检查当前版本和最新版本
     * 3.不更新或更新
     * 4.更新判断强制更或者正常更
     * 5.下载新版本
     * 6.放到本地
     * 7.解析apk
     * 8.安装、替换
     */

    public static CheckVerson getInstance(Context context) {

        if (mInstance == null) {
            synchronized (HttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new CheckVerson();
                }
            }
        }
        return mInstance;
    }

    /**
     * 返回当前程序版本名
     */
    public static int getAppVersionName(Context context) {

        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            // 当前应用的版本名称
            versionName = info.versionName;
            // 当前版本的版本号
             versionCode = info.versionCode;
            // 当前版本的包名
            packageNames = info.packageName;
            Log.e("TAG","名称--"+versionName+"--版本号--"+versionCode+"--包名--"+packageNames);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return versionCode;
    }

    public void checkversion(final Context context) {

        final int nowversion = getAppVersionName(context);

        HttpUtils.getInstance().getVersion(context,new RequestCallback<CheckVersionBean>() {
            @Override
            public void onError(Request request, Exception e) {


            }

            @Override
            public void onResponse(CheckVersionBean response) {

                Log.e("TAG", "检查版本更新---" + response.toString());

                if(nowversion<response.getData().getReversion()){//当前版本<最新版本--需要更新

                    initDialog(context);

                }else {
                    Toast.makeText(context,"当前已经是最新版",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void initDialog(final Context context) {
        CustomDialog.Builder builder = new CustomDialog.Builder(context);
        builder.setMessage("1 当前版本 \n 2 当前版本");
        builder.setTitle("安装新APP版本1.0.1");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //设置你的操作事项
                Intent intent = new Intent(new Intent(context, ProgressbarAcitvity.class));
                intent.putExtra( Constants.HOWSHOW,Constants.APP);
                context.startActivity(intent);
            }
        });

        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();

    }
}
