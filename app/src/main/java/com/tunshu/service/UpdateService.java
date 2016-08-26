package com.tunshu.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.webkit.MimeTypeMap;

import com.basecore.util.core.OpenFiles;
import com.tunshu.R;
import com.tunshu.application.CommonApplication;
import com.tunshu.util.Constants;
import com.tunshu.util.LogUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by ShadowNight on 2015/9/7.
 */
public class UpdateService extends Service {
    private DownloadManager downloadManager;
    private UpdateManagerReceiver updateManagerReceiver=new UpdateManagerReceiver();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void onCreate() {
        super.onCreate();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        mFilter.addAction(Constants.UPDATE_DOWNLOAD);
        registerReceiver(updateManagerReceiver, mFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(updateManagerReceiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        startDownload(intent.getStringExtra("updateUrl"));
        return START_REDELIVER_INTENT;
    }

    private class UpdateManagerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //这里可以取得下载的id，这样就可以知道哪个文件下载完成了。适用与多个下载任务的监听
            LogUtil.getLogger().d("" + intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0));
            if(intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)){
                queryDownloadStatus();
            }
        }
    };
    public  void startDownload(String url) {
        //开始下载
        Uri resource = Uri.parse(encodeGB(url));
        DownloadManager.Request request = new DownloadManager.Request(resource);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);
        //设置文件类型
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
        request.setMimeType(mimeString);
        //在通知栏中显示
        request.setShowRunningNotification(true);
        request.setVisibleInDownloadsUi(true);
        //sdcard的目录下的download文件夹
        request.setDestinationInExternalPublicDir("/download/", getApplicationContext().getResources().getString(R.string.app_name) + ".apk");
        request.setTitle(getApplicationContext().getResources().getString(R.string.app_name));
        long id = downloadManager.enqueue(request);
        ((CommonApplication)getApplicationContext()).getPreferenceConfig().setLong(Constants.UPDATE_DOWNLOAD_ID, id);
    }

    public String encodeGB(String string) {
        //转换中文编码
        String split[] = string.split("/");
        for (int i = 1; i < split.length; i++) {
            try {
                split[i] = URLEncoder.encode(split[i], "GB2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            split[0] = split[0] + "/" + split[i];
        }
        split[0] = split[0].replaceAll("\\+", "%20");//处理空格
        return split[0];
    }


    private void queryDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(((CommonApplication)getApplicationContext()).getPreferenceConfig().getLong(Constants.UPDATE_DOWNLOAD_ID, 0L));
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    LogUtil.getLogger().d("STATUS_PAUSED");
                case DownloadManager.STATUS_PENDING:
                    LogUtil.getLogger().d("STATUS_PENDING");
                case DownloadManager.STATUS_RUNNING:
                    //正在下载，不做任何事情
                    LogUtil.getLogger().d("STATUS_RUNNING");
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    //完成
                    LogUtil.getLogger().d(c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME)));
                    File file = new File(c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME)));
                    LogUtil.getLogger().d(file.isFile());
                    if (file.isFile()) {
                        Intent intent = OpenFiles.getApkFileIntent(file);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);
                    }
                    ((CommonApplication)getApplicationContext()).getPreferenceConfig().setLong(Constants.UPDATE_DOWNLOAD_ID, 0L);
                    onDestroy();
                    break;
                case DownloadManager.STATUS_FAILED:
                    //清除已下载的内容，重新下载
                    LogUtil.getLogger().d("STATUS_FAILED");
                    LogUtil.getLogger().d(c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON)));
                    downloadManager.remove(((CommonApplication) getApplicationContext()).getPreferenceConfig().getLong(Constants.UPDATE_DOWNLOAD_ID, 0L));
                    ((CommonApplication)getApplicationContext()).getPreferenceConfig().setLong(Constants.UPDATE_DOWNLOAD_ID, 0L);
//                    onDestroy();
                    break;
            }
        }
    }
}
