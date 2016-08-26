package com.tunshu.view.activity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.TextView;

import com.basecore.activity.BaseFragmentActivity;
import com.basecore.util.core.ListUtils;
import com.basecore.util.core.OpenFiles;
import com.basecore.util.core.StringUtils;
import com.basecore.util.netstate.NetWorkUtil;
import com.basecore.widget.CustomToast;
import com.basecore.widget.SystemBarTintManager;
import com.basecore.widget.dialog.Effectstype;
import com.basecore.widget.dialog.NiftyDialogBuilder;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.tunshu.R;
import com.tunshu.application.CommonApplication;
import com.tunshu.entity.CommonEntity;
import com.tunshu.entity.GameListEntity;
import com.tunshu.entity.VersionEntity;
import com.tunshu.http.MyHttpClient;
import com.tunshu.util.Constants;
import com.tunshu.util.LogUtil;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

public class CommonActivity extends BaseFragmentActivity {
    public ImageButton back;
    public TextView title, goText;
    public ImageButton go;

    private LocaleChangedReceiver localeChangedReceiver = new LocaleChangedReceiver();
    // 把下载统计放到 MainRecommendFragment中处理 否则推荐，排行，分类页面的下载无法统计
  //  private DownloadCountReceiver downloadCountReceiver = new DownloadCountReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(R.color.com_titlebar);
    }

    public void setStatusBar(int color) {
        // create our manager instance after the content view is set
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(color);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(localeChangedReceiver, new IntentFilter(Intent.ACTION_LOCALE_CHANGED));
     //   registerReceiver(downloadCountReceiver, new IntentFilter(Constants.DOWNLOAD_COUNT_ADD));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(localeChangedReceiver);
      //  unregisterReceiver(downloadCountReceiver);
    }

    public void findTitle() {
        back = (ImageButton) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        goText = (TextView) findViewById(R.id.go_text);
        go = (ImageButton) findViewById(R.id.go);
    }

    public RequestParams getCommonParams() {
        RequestParams params = new RequestParams();
        params.put("phonetype", "ANDROID");
        params.put("userid", getCoreApplication().getPreferenceConfig().getString(Constants.USERID, "-1"));
        params.put("token", getCoreApplication().getPreferenceConfig().getString(Constants.TOKEN, ""));
        params.put("language", getLanguage());
        return params;
    }

    public String getLanguage() {
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();

        if (language.endsWith("en")){
            language = "en";
        }else{
            String cnt = locale.getCountry();

            if(!"".equals(cnt)){
                language = language + "_" + locale.getCountry();
            }
        }

        return language;
    }

    public class LocaleChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().compareTo(Intent.ACTION_LOCALE_CHANGED) == 0) {
                getCoreApplication().getAppManager().AppExit(CommonActivity.this, false);
            }
        }
    }
/*  把下载统计放到 MainRecommendFragment中处理 否则推荐，排行，分类页面的下载无法统计
    public class DownloadCountReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String gameId = intent.getStringExtra("gameId") == null ? "" : intent.getStringExtra("gameId");
            if (!StringUtils.isEmpty(gameId)) {
                addDownloadCount(gameId);
            }
        }
    }
*/
    public void deleteDownloadInfo(String packageName) {
        final List<GameListEntity> downloadInfoList = ((CommonApplication) getCoreApplication()).finalDb.findAll(GameListEntity.class);
        if (!ListUtils.isEmpty(downloadInfoList)) {
            for (int i = 0, size = downloadInfoList.size(); i < size; i++) {
                if (packageName.equals(downloadInfoList.get(i).getPackpagename())) {
                    LogUtil.getLogger().d(downloadInfoList.get(i).getTaskId());
                    getCoreApplication().getPreferenceConfig().setLong(new HashCodeFileNameGenerator().generate(downloadInfoList.get(i).getDownurl()), -10L);
                    Intent intent = new Intent(Constants.DOWNLOAD_INFO_DELETE);
                    intent.putExtra("taskToken", downloadInfoList.get(i).getTaskId());
                    startActivity(intent);
                    downloadInfoList.remove(i);
                    break;
                }
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ((CommonApplication) getCoreApplication()).finalDb.deleteAll(GameListEntity.class);
                    for (int j = 0, length = downloadInfoList.size(); j < length; j++) {
                        ((CommonApplication) getCoreApplication()).finalDb.save(downloadInfoList.get(j));
                    }
                }
            }).start();
        }
    }
/*   把下载统计放到 MainRecommendFragment中处理 否则推荐，排行，分类页面的下载无法统计
    private void addDownloadCount(String gameId) {
        RequestParams params = getCommonParams();
        params.put("ac", "downcount");
        params.put("gameid", gameId);
        MyHttpClient.get(CommonActivity.this, "appIndex/index", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    LogUtil.getLogger().d(response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

*/

    public void uploadInfo(String bandinfo,String errorcode){
        Build bd = new Build();
        String devinfo = bd.MODEL + ","+ bd.ID + "," + bd.DEVICE +  "," + bd.PRODUCT;
        RequestParams params=new RequestParams();
        params.put("ac","onlineFailureLog");
        params.put("serial",bd.SERIAL);
        params.put("devinfo",devinfo);
        params.put("bandinfo",bandinfo);
        params.put("errorcode",errorcode);
        params.put("errormsg",getString(R.string.open_app));

        params.put("LBS",((CommonApplication)getApplicationContext()).getLBS());
        MyHttpClient.get(this, "common/index", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public void checkWifiDown(final boolean isNeedDownCount, final View textView,
                               final String appName, final String downloadUrl,
                               final String gameId,
                               final GameListEntity listEntity){

        if (!NetWorkUtil.isNetworkConnected(this)) {
            CustomToast.showToast(this, this.getString(R.string.network_not_available));
            textView.setEnabled(true);
            return;
        }

        if (NetWorkUtil.isWifiConnected(this) || !isDownLoadTips()) {
            downSendBroadcast(appName, downloadUrl, listEntity);

            if(isNeedDownCount){
                downCountSendBroadcast(gameId);
            }
        }else{
            final NiftyDialogBuilder wifiDialogBuilder = NiftyDialogBuilder.getInstance(this);
            wifiDialogBuilder.withTitle(null).withTitleColor("#000000").withMessage(this.getString(R.string.download_network_change))
                    .isCancelableOnTouchOutside(false).isCancelable(false).withDuration(300)
                    .withEffect(Effectstype.SlideBottom).withButton1Text(this.getString(R.string.cancel)).withButton2Text(this.getString(R.string.go_on)).setButton1Click(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    textView.setEnabled(true);
                    wifiDialogBuilder.dismiss();

                }
            }).setButton2Click(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    downSendBroadcast(appName, downloadUrl, listEntity);

                    if(isNeedDownCount){
                        downCountSendBroadcast(gameId);
                    }
                    wifiDialogBuilder.dismiss();
                }
            });

            wifiDialogBuilder.show();
        }
    }

    private  void downSendBroadcast(String appName, String downloadUrl, GameListEntity listEntity){

        Intent intent = new Intent(Constants.DOWNLOAD_STATE_TO_START);
        intent.putExtra("appName", appName);
        intent.putExtra("downloadUrl", downloadUrl);
        intent.putExtra("gameListEntity", listEntity);
        sendBroadcast(intent);
    }

    private  void downCountSendBroadcast(String gameId) {

        //下载计数
        Intent countIntent = new Intent(Constants.DOWNLOAD_COUNT_ADD);
        countIntent.putExtra("gameId", gameId);
        sendBroadcast(countIntent);
    }

    private  boolean isDownLoadTips(){

        return ((CommonApplication)getApplicationContext()).getPreferenceConfig().getBoolean(Constants.SETTING_DOWNLOAD_TIPS, true);
    }
}
