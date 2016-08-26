package com.cavytech.wear2.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.basecore.util.netstate.NetWorkUtil;
import com.basecore.widget.dialog.Effectstype;
import com.basecore.widget.dialog.NiftyDialogBuilder;
import com.cavytech.wear2.R;
import com.cavytech.wear2.activity.BandConnectActivity;
import com.cavytech.wear2.activity.GuideActivity;
import com.cavytech.wear2.activity.HomePager;
import com.cavytech.wear2.activity.ProgressbarAcitvity;
import com.cavytech.wear2.activity.SexActivity;
import com.cavytech.wear2.activity.SplashActivity;
import com.cavytech.wear2.application.CommonApplication;
import com.cavytech.wear2.entity.CheckVersionBean;
import com.cavytech.wear2.entity.UserEntity;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.RequestCallback;
import com.cavytech.widget.CustomDialog;
import com.squareup.okhttp.Request;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by longjining on 16/2/18.
 */
public class UpdateManger {
    // 应用程序Context
    private Context mContext;
    private ProgressBar progressBar;
    private TextView updateInfo, updateProgress;
    private String downloadUrl = "";
    private String fileSize = "";
    private NiftyDialogBuilder dialogBuilder;

    private String FILENAME = "wear";
    private String saveFileName = "";

    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private int progress;// 当前进度
    private Thread downLoadThread; // 下载线程
    private boolean interceptFlag = false;// 用户取消下载
    public int appsize;
    // 通知处理刷新界面的handler
    private Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    progressBar.setProgress(progress);
                    break;
                case DOWN_OVER:
                    installApk();
                    exitApp();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void exitApp() {
        MobclickAgent.onKillProcess(mContext);
        System.exit(0);
        ((CommonApplication) mContext.getApplicationContext()).getAppManager().AppExit(mContext, false);
    }

    public UpdateManger(Context context) {
        this.mContext = context;
    }

    public void checkVersion() {

        if (NetWorkUtil.isNetworkConnected(mContext)) {
            checkUpdateInfo();
//            CheckVerson.getInstance(mContext).checkversion(mContext);
//            nextToDo(1000);
        } else {
            nextToDo(1000);
        }
    }

    // 显示更新程序对话框，供主程序调用
    public void checkUpdateInfo() {

        HttpUtils.getInstance().getVersion(mContext, new RequestCallback<CheckVersionBean>() {

            @Override
            public void onError(Request request, Exception e) {

                LogUtil.getLogger().d(request == null ? "" : request.toString());
                nextToDo();

            }

            @Override
            public void onResponse(CheckVersionBean response) {

                Log.e("TAG", "检查版本更新---" + response.getData().getUrl() + "--" + response.getData().getDescription() + "--" + response.getData().getReversion());
                try {

                    int localVersion = 0;
                    try {
                        localVersion = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;

                    } catch (PackageManager.NameNotFoundException e1) {
                        e1.printStackTrace();
                    }
                    if(response!=null){
                        downloadUrl = response.getData().getUrl();
                        appsize = response.getData().getSize();
                        if(response.getData().getUrl()!=null){
                            if (response.getData().getUrl().length() > 0 && localVersion < response.getData().getReversion()) {
                                updateDialog(response.getData().isForce_update(), response.getData());
                            } else {
                                nextToDo(1500);
                            }
                        }else {
                            nextToDo(1500);
                        }

                    }else {
                        nextToDo(1500);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    nextToDo();
                }
            }
        });

    }

    private void nextToDo(int delay) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                nextToDo();
            }
        }, delay);
    }

    private void nextToDo() {
        boolean isFirstIn = false;

        // 读取SharedPreferences中需要的数据
        // 使用SharedPreferences来记录程序的使用次数
        SharedPreferences preferences = mContext.getSharedPreferences(
                Constants.SHAREDPREFERENCES_NAME, mContext.MODE_PRIVATE);

        // 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
        isFirstIn = preferences.getBoolean("isFirstIn", true);

        UserEntity.ProfileEntity profileEntity = (UserEntity.ProfileEntity) SerializeUtils.unserialize(Constants.SERIALIZE_USERINFO);


        // 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
        if (!isFirstIn) {
            if (CacheUtils.getString(mContext, Constants.PASSWORD).length() > 0) {
                if(CacheUtils.getMacAdress(mContext) == null){
                    Intent it = new Intent(mContext, BandConnectActivity.class);
                    mContext.startActivity(it);
                }else {
                    if(CacheUtils.getMacAdress(mContext).equals("") ){
                        Intent it = new Intent(mContext, BandConnectActivity.class);
                        mContext.startActivity(it);
                    }else if(profileEntity == null ){
                        // profileEntity.getWeight() == 0 || profileEntity.getHeight() == 0
                        Intent it = new Intent(mContext, SexActivity.class);
                        mContext.startActivity(it);
                    }else {
                        Intent intent = new Intent(mContext, HomePager.class);
                        mContext.startActivity(intent);
                    }
                }
                ((SplashActivity) mContext).finish();
            } else {
                Intent intent = new Intent(mContext, GuideActivity.class);
                mContext.startActivity(intent);
                ((SplashActivity) mContext).finish();
            }
        } else {
            Intent intent = new Intent(mContext, GuideActivity.class);
            mContext.startActivity(intent);
            ((SplashActivity) mContext).finish();
        }
    }

    public void updateDialog(final boolean mustUpdate, final CheckVersionBean.DataBean versionInfo) {
        CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
        builder.setMessage("1 当前版本 \n 2 当前版本");
        builder.setTitle("安装新APP版本" + versionInfo.getVersion());
        builder.setMessage(versionInfo.getDescription());
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //设置你的操作事项
                Intent intent = new Intent(mContext, ProgressbarAcitvity.class);
                intent.putExtra(Constants.HOWSHOW, Constants.APP);
                intent.putExtra(Constants.APPURL, versionInfo.getUrl());
                intent.putExtra(Constants.APPSIZE, appsize);
//                intent.putExtra( Constants.MAXSIZE,"14.91");
                mContext.startActivity(intent);
            }
        });

        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (mustUpdate) {
                            exitApp();
                        } else {
                            nextToDo();
                        }

                        dialog.dismiss();

                    }
                });

        builder.create().show();
/*        dialogBuilder = NiftyDialogBuilder.getInstance(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View layout = inflater.inflate(R.layout.public_update_dialog, null);
        progressBar = (ProgressBar) layout.findViewById(R.id.item_progress);
        updateInfo = (TextView) layout.findViewById(R.id.update_info);
        updateProgress = (TextView) layout.findViewById(R.id.update_progress);
        updateInfo.setText(versionInfo.getDescription());
        dialogBuilder.withTitle(mContext.getString(R.string.new_version) + versionInfo.getVersion()).withTitleColor("#000000")
                .withMessage(null)
                .setCustomView(layout, mContext)
                .withDividerColor("#efeff4")
                .isCancelableOnTouchOutside(false).isCancelable(false).withDuration(300)
                .withEffect(Effectstype.SlideBottom)
                .withButton1Text(mContext.getString(R.string.update_later)).withButton2Text(mContext.getString(R.string.update_now))
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (((Button) v).getText().equals(mContext.getString(R.string.update_cancel))) {

                            interceptFlag = true;
                        }
                        if (mustUpdate) {
                            exitApp();
                        } else {
                            nextToDo();
                        }
                        dialogBuilder.dismiss();
                    }
                }).setButton2Click(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (versionInfo.getUrl().length() > 0) {

//                    checkWifiDown(versionInfo);
                    updateApp(versionInfo);

                    dialogBuilder.withTitle(mContext.getString(R.string.updating)).withButton1Text(mContext.getString(R.string.update_cancel)).withButton2Text("");
                }
            }
        }).show();*/
    }

    private void updateApp(final CheckVersionBean.DataBean versionInfo) {

        downloadApk();
        updateInfo.setVisibility(View.GONE);
        updateProgress.setVisibility(View.VISIBLE);
    }

    private void checkWifiDown(final CheckVersionBean.DataBean versionInfo) {

        if (NetWorkUtil.isWifiConnected(mContext)) {
            updateApp(versionInfo);
        } else {
            final NiftyDialogBuilder wifiDialogBuilder = NiftyDialogBuilder.getInstance(mContext);
            wifiDialogBuilder.withTitle(null).withTitleColor("#000000").withMessage(mContext.getString(R.string.download_network_change))
                    .isCancelableOnTouchOutside(false).isCancelable(false).withDuration(300)
                    .withEffect(Effectstype.SlideBottom).withButton1Text(mContext.getString(R.string.cancel)).withButton2Text(mContext.getString(R.string.go_on)).setButton1Click(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    exitApp();
                    wifiDialogBuilder.dismiss();

                }
            }).setButton2Click(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    updateApp(versionInfo);
                    wifiDialogBuilder.dismiss();
                }
            });

            wifiDialogBuilder.show();
        }
    }

    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    protected void installApk() {
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");// File.toString()会返回路径信息
        mContext.startActivity(i);
    }

    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            URL url;
            try {
                url = new URL(downloadUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream ins = conn.getInputStream();

//                String savePath = ((CommonApplication) mContext.getApplicationContext()).getDownloadFilePath();
                String savePath = getDownloadFilePath();
                String saveFileNameTmp = savePath + File.separator + FILENAME + ".apk";
                saveFileName = savePath;

                File file = new File(saveFileNameTmp);
                int fileIndex = 1;
                while (file.exists()) {
                    saveFileNameTmp = saveFileName + File.separator + FILENAME + "(" + fileIndex + ")" + ".apk";
                    file = new File(saveFileNameTmp);
                    fileIndex++;
                }
                saveFileName = saveFileNameTmp;

                File ApkFile = new File(saveFileName);
                FileOutputStream outStream = new FileOutputStream(ApkFile);
                int count = 0;
                byte buf[] = new byte[1024];
                do {
                    int numread = ins.read(buf);
                    count += numread;
                    progress = (int) (((float) count / length) * 100);
                    // 下载进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        // 下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    outStream.write(buf, 0, numread);
                } while (!interceptFlag);// 点击取消停止下载
                outStream.close();
                ins.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public String getDownloadFilePath() {
        String downloadPath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            LogUtil.getLogger().d(Environment.getExternalStoragePublicDirectory("download"));
            downloadPath = Environment.getExternalStoragePublicDirectory("download").getAbsolutePath() + File.separator + "Wear";
        } else {
            downloadPath = Environment.getExternalStorageDirectory() + File.separator + "Wear";
        }
        File downloadFile = new File(downloadPath);
        if (!downloadFile.isDirectory() && !downloadFile.mkdirs()) {
            throw new IllegalAccessError(" cannot create download folder");
        }
        LogUtil.getLogger().d(downloadPath);
        return downloadPath;
    }
}
