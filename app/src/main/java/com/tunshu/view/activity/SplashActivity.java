package com.tunshu.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.basecore.util.core.FileInfoUtils;
import com.basecore.util.netstate.NetWorkUtil;
import com.basecore.widget.dialog.Effectstype;
import com.basecore.widget.dialog.NiftyDialogBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.tunshu.R;
import com.tunshu.entity.GameListEntity;
import com.tunshu.entity.VersionEntity;
import com.tunshu.http.MyHttpClient;
import com.tunshu.util.Constants;
import com.tunshu.util.LogUtil;

import org.apache.http.Header;
import org.json.JSONObject;


public class SplashActivity extends CommonActivity {
    private ProgressBar progressBar;
    private TextView updateInfo, updateProgress;
    private String downloadUrl = "";
    private String fileSize = "";
    private NiftyDialogBuilder dialogBuilder;
    private DownloadStateReceiver downloadStateReceiver = new DownloadStateReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(R.color.com_titlebar);
        if (NetWorkUtil.isNetworkConnected(this)) {
            checkVersion();
        } else {
            nextToDo(1000);
        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                uploadInfo();
            }
        }, 10000);

    }

    private void uploadInfo() {

        uploadInfo("", "0");
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(downloadStateReceiver, new IntentFilter(Constants.DOWNLOAD_STATE_REFRESH));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(downloadStateReceiver);
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
        Intent intent = new Intent(SplashActivity.this, HomePageActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();
    }

    public void checkVersion() {
        RequestParams params = getCommonParams();
        params.put("ac", "cavySystemManage");
        MyHttpClient.get(this, "common/index", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    LogUtil.getLogger().d(response == null ? "" : response.toString());
                    VersionEntity versionInfo = com.alibaba.fastjson.JSONObject.parseObject(response.toString(), VersionEntity.class);
                    String build = versionInfo.getBuild();
                    int localVersion = 0;
                    try {
                        localVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
                    } catch (PackageManager.NameNotFoundException e1) {
                        e1.printStackTrace();
                    }
                    downloadUrl = versionInfo.getUrl();
                    fileSize = versionInfo.getSize() + "M";
                    if (versionInfo.getUrl().length() > 0 && localVersion < Integer.parseInt(build)) {
                        updateDialog(versionInfo.getNeedupdate().equals("1"), versionInfo);
                    } else {
                        nextToDo(1500);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    nextToDo();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                LogUtil.getLogger().d(errorResponse == null ? "" : errorResponse.toString());
                nextToDo();
            }

        });
    }

    public void updateDialog(final boolean mustUpdate, final VersionEntity versionInfo) {
        dialogBuilder = NiftyDialogBuilder.getInstance(this);
        LayoutInflater inflater = LayoutInflater.from(SplashActivity.this);
        View layout = inflater.inflate(R.layout.public_update_dialog, null);
        progressBar = (ProgressBar) layout.findViewById(R.id.item_progress);
        updateInfo = (TextView) layout.findViewById(R.id.update_info);
        updateProgress = (TextView) layout.findViewById(R.id.update_progress);
        updateInfo.setText(versionInfo.getIntro());
        dialogBuilder.withTitle(getString(R.string.new_version) + versionInfo.getVersion()).withTitleColor("#000000")
                .withMessage(null)
                .setCustomView(layout, SplashActivity.this)
                .withDividerColor("#efeff4")
                .isCancelableOnTouchOutside(false).isCancelable(false).withDuration(300)
                .withEffect(Effectstype.SlideBottom)
                .withButton1Text(getString(R.string.update_later)).withButton2Text(getString(R.string.update_now))
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (((Button) v).getText().equals(getString(R.string.update_cancel))) {
                            Intent intentCancel = new Intent(Constants.DOWNLOAD_STATE_TO_CANCEL);
                            intentCancel.putExtra("taskToken", (int) (getCoreApplication().getPreferenceConfig().getLong(new HashCodeFileNameGenerator().generate(downloadUrl), -10L)));
                            intentCancel.putExtra("downloadUrl", downloadUrl);
                            intentCancel.putExtra("packageName", "com.tunshu");
                            sendBroadcast(intentCancel);
                        }
                        if (mustUpdate) {
                            getCoreApplication().getAppManager().AppExit(getCoreApplication(), true);
                        } else {
                            nextToDo();
                        }
                        dialogBuilder.dismiss();
                    }
                }).setButton2Click(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (versionInfo.getUrl().length() > 0) {

                    checkWifiDown(versionInfo);

                    dialogBuilder.withTitle(getString(R.string.updating)).withButton1Text(getString(R.string.update_cancel)).withButton2Text("");
                }
            }
        }).show();
    }

    private void updateApp(final VersionEntity versionInfo){
        GameListEntity gameListEntity = new GameListEntity();
        gameListEntity.setPackpagename("com.tunshu");
        gameListEntity.setDownurl(versionInfo.getUrl());
        Intent intent = new Intent(Constants.DOWNLOAD_STATE_TO_START);
        intent.putExtra("appName", getString(R.string.app_name));
        intent.putExtra("downloadUrl", versionInfo.getUrl());
        intent.putExtra("gameListEntity", gameListEntity);
        intent.putExtra("isUpdate", true);
        sendBroadcast(intent);
        updateInfo.setVisibility(View.GONE);
        updateProgress.setVisibility(View.VISIBLE);
    }

    private void checkWifiDown(final VersionEntity versionInfo){

        if (NetWorkUtil.isWifiConnected(SplashActivity.this)) {
            updateApp(versionInfo);
        }else{
            final NiftyDialogBuilder wifiDialogBuilder = NiftyDialogBuilder.getInstance(SplashActivity.this);
            wifiDialogBuilder.withTitle(null).withTitleColor("#000000").withMessage(getString(R.string.download_network_change))
                    .isCancelableOnTouchOutside(false).isCancelable(false).withDuration(300)
                    .withEffect(Effectstype.SlideBottom).withButton1Text(getString(R.string.cancel)).withButton2Text(getString(R.string.go_on)).setButton1Click(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getCoreApplication().getAppManager().AppExit(getCoreApplication(), true);
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

    private class DownloadStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            long currentTaskId = getCoreApplication().getPreferenceConfig().getLong(new HashCodeFileNameGenerator().generate(downloadUrl), -10L);
            if (currentTaskId == intent.getLongExtra("taskId", -1)) {
                String state = intent.getStringExtra("state");
                long downloadLength = intent.getLongExtra("downloadedLength", 0L);
                switch (state) {
                    case Constants.DOWNLOAD_STARTED:
                        break;
                    case Constants.DOWNLOAD_PROCESS:
                        String percent = "<font color=#3e77db>" + FileInfoUtils.FormetFileSize(downloadLength) + "</font>" + "/" + fileSize;
                        updateProgress.setText(Html.fromHtml(percent));
                        progressBar.setProgress((int) intent.getDoubleExtra("percent", 0));
                        break;
                    case Constants.DOWNLOAD_FINISHED:
                        break;
                    case Constants.DOWNLOAD_COMPLETED:
                        break;
                    case Constants.DOWNLOAD_PAUSED:
                        break;
                    case Constants.DOWNLOAD_REBUILD_FINISHED:
                        break;
                    case Constants.DOWNLOAD_CONNECTION_LOST:
                        break;
                    default:
                        break;
                }
            }
        }
    }


}
