package com.tunshu.service;

import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import android.app.Notification;
import android.app.PendingIntent;

import com.basecore.util.core.ListUtils;
import com.basecore.util.core.StringUtils;
import com.basecore.util.netstate.NetWorkUtil;
import com.basecore.widget.CustomToast;
import com.basecore.widget.dialog.Effectstype;
import com.basecore.widget.dialog.NiftyDialogBuilder;
import com.golshadi.majid.core.DownloadManagerPro;
import com.golshadi.majid.core.enums.QueueSort;
import com.golshadi.majid.core.enums.TaskStates;
import com.golshadi.majid.report.ReportStructure;
import com.golshadi.majid.report.exceptions.QueueDownloadInProgressException;
import com.golshadi.majid.report.exceptions.QueueDownloadNotStartedException;
import com.golshadi.majid.report.listener.DownloadManagerListener;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.tunshu.R;
import com.tunshu.application.CommonApplication;
import com.tunshu.entity.GameDetailEntity;
import com.tunshu.entity.GameListEntity;
import com.tunshu.entity.TypeListEntity;
import com.tunshu.util.Constants;
import com.tunshu.util.LogUtil;
import com.basecore.util.core.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import com.tunshu.view.activity.HomePageActivity;

/**
 * Created by ShadowNight on 2015/7/24.
 */
public class DownloadService extends Service {
    private ConnectivityManager connectivityManager;
    private NetworkInfo info;
    private DownloadManagerListener downloadManagerListener;
    public DownloadManagerPro downloadManagerPro;

    public static  ReentrantLock downComplitelock = new ReentrantLock();
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mFilter.addAction(Constants.DOWNLOAD_STATE_TO_START);
        mFilter.addAction(Constants.DOWNLOAD_STATE_TO_PAUSE);
        mFilter.addAction(Constants.DOWNLOAD_STATE_TO_CANCEL);
        mFilter.addAction(Constants.DOWNLOAD_STATE_TO_RESTART);
        mFilter.addAction(Constants.GET_ALL_DOWNLOAD_STATE);
        mFilter.addAction(Constants.GET_SINGLE_DOWNLOAD_STATE);
        mFilter.addAction(Constants.DOWNLOAD_INFO_DELETE);
        registerReceiver(stateReceiver, mFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(stateReceiver);
    }

    private BroadcastReceiver stateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            final int taskToken = intent.getIntExtra("taskToken", -1);
            String packageName = intent.getStringExtra("packageName") == null ? "" : intent.getStringExtra("packageName");
            final String downloadUrl = intent.getStringExtra("downloadUrl") == null ? "" : intent.getStringExtra("downloadUrl");
            switch (action) {
                case ConnectivityManager.CONNECTIVITY_ACTION:
                    connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    info = connectivityManager.getActiveNetworkInfo();
                    if (info != null && info.isAvailable()) {
                        String name = info.getTypeName();
                        LogUtil.getLogger().d("当前网络名称：" + name);
                        if (!name.equals("WIFI") && downloadManagerPro.downloadTasksInSameState(TaskStates.DOWNLOADING).size() > 0) {
                            //
                        }
                    } else {
                        LogUtil.getLogger().d("没有可用网络,下载服务停止");
                    }
                    break;
                case Constants.DOWNLOAD_STATE_TO_START:
                    final String appName = intent.getStringExtra("appName") == null ? "last" : intent.getStringExtra("appName");
                    final boolean overwrite = intent.getBooleanExtra("overwrite", true);
                    final boolean priority = intent.getBooleanExtra("priority", true);
                    final boolean isUpdate = intent.getBooleanExtra("isUpdate", false);
                    final GameListEntity gameListEntity = (GameListEntity) intent.getSerializableExtra("gameListEntity");
                    startDownload(appName, downloadUrl, gameListEntity, overwrite, priority, isUpdate);

                    break;
                case Constants.DOWNLOAD_STATE_TO_PAUSE:
                    if (taskToken > -1) {
                        pauseDownload(taskToken);
                    }
                    break;
                case Constants.DOWNLOAD_STATE_TO_CANCEL:
                    if (taskToken > -1) {
                        cancelDownload(taskToken, downloadUrl, packageName);
                    }
                    break;
                case Constants.DOWNLOAD_STATE_TO_RESTART:
                    if (taskToken > -1) {
                        restartDownload(taskToken);
                    }
                    break;
                case Constants.GET_ALL_DOWNLOAD_STATE:
                    if (intent.getStringExtra("flag").equals("MainClassifyFragment")) {
                        Intent resultIntent = new Intent(Constants.GET_ALL_DOWNLOAD_STATE_RESULT);
                        resultIntent.putExtra("downloadInfoList", (Serializable) getTypeAllDownloadState((List<TypeListEntity.DataEntity>) intent.getSerializableExtra("downloadInfoList")));
                        resultIntent.putExtra("flag", intent.getStringExtra("flag"));
                        sendBroadcast(resultIntent);
                    } else {
                        Intent resultIntent = new Intent(Constants.GET_ALL_DOWNLOAD_STATE_RESULT);
                        resultIntent.putExtra("downloadInfoList", (Serializable) getAllDownloadState((List<GameListEntity>) intent.getSerializableExtra("downloadInfoList")));
                        resultIntent.putExtra("flag", intent.getStringExtra("flag"));
                        sendBroadcast(resultIntent);
                    }
                    break;
                case Constants.GET_SINGLE_DOWNLOAD_STATE:
                    String versionNo = intent.getStringExtra("versionNo") == null ? "" : intent.getStringExtra("versionNo");
                    Intent resultIntent = new Intent(Constants.GET_SINGLE_DOWNLOAD_STATE_RESULT);
                    resultIntent.putExtra("actionText", getSingleDownloadState(packageName, downloadUrl, versionNo));
                    sendBroadcast(resultIntent);
                    break;
                case Constants.DOWNLOAD_INFO_DELETE:
                    if (taskToken > -1) {
                        deleteDownloadHistory(taskToken);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        downloadManagerInit();
        resetAllDownloadState(((CommonApplication) getApplicationContext()).finalDb.findAll(GameListEntity.class));

        return START_REDELIVER_INTENT;
    }

    private void downloadManagerInit() {
        downloadManagerListener = new DownloadManagerListener() {
            @Override
            public void OnDownloadStarted(long taskId) {
                LogUtil.getLogger().w("OnDownloadStarted " + taskId);
//                Intent intent = new Intent(Constants.DOWNLOAD_STATE_REFRESH);
//                intent.putExtra("state", Constants.DOWNLOAD_STARTED);
//                intent.putExtra("taskId", taskId);
//                sendBroadcast(intent);
            }

            @Override
            public void OnDownloadPaused(long taskId) {
                LogUtil.getLogger().w("OnDownloadPaused " + taskId);
                Intent intent = new Intent(Constants.DOWNLOAD_STATE_REFRESH);
                intent.putExtra("state", Constants.DOWNLOAD_PAUSED);
                intent.putExtra("taskId", taskId);
                sendBroadcast(intent);
                List<ReportStructure> reportStructures = downloadManagerPro.downloadTasksInSameState(TaskStates.INIT_WAIT);
                if (!ListUtils.isEmpty(reportStructures)) {
                    for (int i = 0, size = reportStructures.size(); i < size; i++) {
                        LogUtil.getLogger().e(reportStructures.get(i).getName() + "------->" + reportStructures.get(i).getId());
                    }
                    restartDownload(reportStructures.get(0).getId());
                } else {
                    List<ReportStructure> report = downloadManagerPro.downloadTasksInSameState(TaskStates.PAUSE_WAIT);
                    if (!ListUtils.isEmpty(report)) {
                        for (int i = 0, size = report.size(); i < size; i++) {
                            LogUtil.getLogger().e(report.get(i).getName() + "------->" + report.get(i).getId());
                        }
                        restartDownload(report.get(0).getId());
                    }
                }
            }

            @Override
            public void onDownloadProcess(long taskId, double percent, long downloadedLength) {
              //  LogUtil.getLogger().w("onDownloadProcess " + taskId + "--" + percent + "--" + downloadedLength);
                if ((int) percent <= 100) {
                    Intent intent = new Intent(Constants.DOWNLOAD_STATE_REFRESH);
                    intent.putExtra("state", Constants.DOWNLOAD_PROCESS);
                    intent.putExtra("taskId", taskId);
                    intent.putExtra("percent", percent);
                    intent.putExtra("downloadedLength", downloadedLength);
                    sendBroadcast(intent);
                }
            }

            @Override
            public void OnDownloadFinished(long taskId) {
                LogUtil.getLogger().d("OnDownloadFinished " + taskId);
                Intent intent = new Intent(Constants.DOWNLOAD_STATE_REFRESH);
                intent.putExtra("state", Constants.DOWNLOAD_FINISHED);
                intent.putExtra("taskId", taskId);
                sendBroadcast(intent);
            }

            @Override
            public void OnDownloadRebuildStart(long taskId) {
                LogUtil.getLogger().d("OnDownloadRebuildStart " + taskId);
            }

            @Override
            public void OnDownloadRebuildFinished(long taskId) {
                LogUtil.getLogger().d("OnDownloadRebuildFinished " + taskId);
            }

            @Override
            public void OnDownloadCompleted(long taskId) {
                downComplitelock.lock();
                try {

                    LogUtil.getLogger().d("OnDownloadCompleted " + taskId);
                    Intent intent = new Intent(Constants.DOWNLOAD_STATE_REFRESH);
                    intent.putExtra("state", Constants.DOWNLOAD_COMPLETED);
                    intent.putExtra("taskId", taskId);
                    sendBroadcast(intent);
                    List<ReportStructure> reportStructures = downloadManagerPro.downloadTasksInSameState(TaskStates.INIT_WAIT);
                    if (!ListUtils.isEmpty(reportStructures)) {
                        for (int i = 0, size = reportStructures.size(); i < size; i++) {
                            LogUtil.getLogger().e(reportStructures.get(i).getName() + "------->" + reportStructures.get(i).getId());
                        }
                        restartDownload(reportStructures.get(0).getId());
                    } else {
                        List<ReportStructure> report = downloadManagerPro.downloadTasksInSameState(TaskStates.PAUSE_WAIT);
                        if (!ListUtils.isEmpty(report)) {
                            for (int i = 0, size = report.size(); i < size; i++) {
                                LogUtil.getLogger().e(report.get(i).getName() + "------->" + report.get(i).getId());
                            }
                            restartDownload(report.get(0).getId());
                        }
                    }
                    if (((CommonApplication) getApplicationContext()).getPreferenceConfig().getBoolean(Constants.SETTING_AUTO_INSTALL, true)
                            ||((CommonApplication) getApplicationContext()).getPreferenceConfig().getBoolean(taskId+"update", false)) {
                        try {
                            ((CommonApplication) getApplicationContext()).appInstall((int) taskId);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }catch (Exception e){

                    e.printStackTrace();
                }finally {
                    downComplitelock.unlock();
                }
            }

            @Override
            public void connectionLost(long taskId) {
                LogUtil.getLogger().d("connectionLost " + taskId);
                Intent intent = new Intent(Constants.DOWNLOAD_STATE_REFRESH);
                intent.putExtra("state", Constants.DOWNLOAD_CONNECTION_LOST);
                intent.putExtra("taskId", taskId);
                sendBroadcast(intent);
            }
        };
        downloadManagerPro = new DownloadManagerPro(this);
        downloadManagerPro.init(getDownloadFilePath(), 1, downloadManagerListener);
    }

    public String getDownloadFilePath() {
        String downloadPath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            downloadPath = Environment.getExternalStoragePublicDirectory("download").getAbsolutePath() + File.separator + "CavyTech";
        } else {
            downloadPath = Environment.getDataDirectory().getAbsolutePath() + File.separator + "CavyTech";
        }
        File downloadFile = new File(downloadPath);
        LogUtil.getLogger().d(Uri.fromFile(downloadFile).getPath());
        if (!downloadFile.isDirectory() && !downloadFile.mkdirs()) {
            throw new IllegalAccessError(" cannot create download folder");
        }
        return downloadPath;
    }

    public void startDownload(final String appName, final String downloadUrl, final GameListEntity gameListEntity, boolean overwrite, boolean priority, final boolean isUpdate) {
        LogUtil.getLogger().w(downloadUrl);
        int currentTaskId = (int) ((CommonApplication) getApplicationContext()).getPreferenceConfig().getLong(new HashCodeFileNameGenerator().generate(downloadUrl), -10L);
        ReportStructure structure = downloadManagerPro.singleDownloadStatus(currentTaskId);
        LogUtil.getLogger().w(currentTaskId);

        if(structure.getDownloadLength() == 0 && structure.getState() != TaskStates.INIT && currentTaskId > 0){
            downloadManagerPro.changeStateToInit(currentTaskId);
        }
        int currentTaskIdTmp = currentTaskId;

        //增加判断structure.getState()==TaskStates.END 下载的内容被删除后重新下载
        if (currentTaskId < 0 || structure.getState() == TaskStates.END) {
            currentTaskId = downloadManagerPro.addTask(appName, downloadUrl, overwrite, priority);
            LogUtil.getLogger().d(currentTaskId);
            final int taskId = currentTaskId;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ((CommonApplication) getApplicationContext()).getPreferenceConfig().setLong(new HashCodeFileNameGenerator().generate(downloadUrl), taskId);
                    ((CommonApplication) getApplicationContext()).getPreferenceConfig().setString(String.valueOf(taskId), getDownloadFilePath() + File.separator + appName + ".apk");
                    ((CommonApplication) getApplicationContext()).getPreferenceConfig().setBoolean(String.valueOf(taskId) + "update", isUpdate);
                    if (!isUpdate) {
                        gameListEntity.setTaskId(taskId);
                        ((CommonApplication) getApplicationContext()).saveInfoToDb(gameListEntity);
                    }
                }
            }).start();
        }
        boolean bCanDown = true;

        if (currentTaskIdTmp > 0){
        // currentTaskIdTmp > 0 说明是继续或者重试，这时要判断正在下载的数量
            if (downloadManagerPro.downloadTasksInSameState(TaskStates.DOWNLOADING).size() >= 3) {
                bCanDown = false;
            }
        }else{
            int downloading = downloadManagerPro.downloadTasksInSameState(TaskStates.DOWNLOADING).size();
            int intWait = downloadManagerPro.downloadTasksInSameState(TaskStates.INIT_WAIT).size();
            int pauseWait = downloadManagerPro.downloadTasksInSameState(TaskStates.PAUSE_WAIT).size();
            if ((downloading + intWait + pauseWait) >= 3) {

                bCanDown = false;
            }
        }

        if (bCanDown) {
            try {
                 LogUtil.getLogger().w("-------------start-------------");
                ReportStructure reportStructure = downloadManagerPro.singleDownloadStatus(currentTaskId);
                LogUtil.getLogger().w(reportStructure.getState());
                if (reportStructure.getState() == TaskStates.INIT || reportStructure.getState() == TaskStates.PAUSED) {

                    // fixBegin 下载完后出现解析包错误的BUG
                    // 如果下载长度是0，而且又有改文件存在则先删除
                    if(reportStructure.getState() == TaskStates.INIT && reportStructure.getDownloadLength() == 0){

                        String fileName = getDownloadFilePath() + File.separator + String.valueOf(currentTaskId);

                        try{
                            File file = new File(fileName, "");

                            if (file.exists()){
                                FileUtils.DeleteFile(file);
                            }

                        }catch (Exception e) {
                            // TODO: handle exceptio
                        }
                    }
                    // fix end

                    Intent intent = new Intent(Constants.DOWNLOAD_STATE_REFRESH);
                    intent.putExtra("state", Constants.DOWNLOAD_PROCESS);
                    intent.putExtra("taskId", (long) currentTaskId);
                    intent.putExtra("percent", reportStructure.getPercent());
                    intent.putExtra("downloadedLength", reportStructure.getDownloadLength());
                    sendBroadcast(intent);
                }

                // 开始下载前先把状态改成INIT_WAIT, 用于下次点击下载判断下载次数的准确性
                downloadManagerPro.waitInitDownload(currentTaskId);

                downloadManagerPro.startDownload(currentTaskId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ReportStructure reportStructure = downloadManagerPro.singleDownloadStatus(currentTaskId);
            LogUtil.getLogger().w(reportStructure.getState());
            if (reportStructure.getState() == TaskStates.INIT || reportStructure.getState() == TaskStates.PAUSED) {//如果当前状态为暂停加入等待队列中等待下载
                Intent intent = new Intent(Constants.DOWNLOAD_STATE_REFRESH);
                intent.putExtra("state", Constants.DOWNLOAD_WAIT);
                intent.putExtra("taskId", (long) currentTaskId);
                intent.putExtra("percent", reportStructure.getPercent());
                intent.putExtra("downloadedLength", reportStructure.getDownloadLength());
                sendBroadcast(intent);
                if (reportStructure.getState() == TaskStates.INIT) {
                    downloadManagerPro.waitInitDownload(currentTaskId);
                } else if (reportStructure.getState() == TaskStates.PAUSED) {
                    downloadManagerPro.waitPauseDownload(currentTaskId);
                }
            }
        }
    }

    public void restartDownload(int taskToken) {
        LogUtil.getLogger().w(downloadManagerPro.downloadTasksInSameState(TaskStates.DOWNLOADING).size());

        if (!NetWorkUtil.isNetworkConnected(this)) {
           // CustomToast.showToast(this, this.getString(R.string.network_not_available));

            return;
        }
        if (downloadManagerPro.downloadTasksInSameState(TaskStates.DOWNLOADING).size() < 3) {
            try {
                LogUtil.getLogger().w(taskToken);
                downloadManagerPro.startDownload(taskToken);
                downloadManagerPro.changeStateToDownloading(taskToken);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {//超过最大下载数显示 暂停
            ReportStructure reportStructure = downloadManagerPro.singleDownloadStatus(taskToken);
            LogUtil.getLogger().w(reportStructure.getState());
            if (reportStructure.getState() == TaskStates.INIT || reportStructure.getState() == TaskStates.PAUSED) {//如果当前状态为暂停加入等待队列中等待下载
                Intent intent = new Intent(Constants.DOWNLOAD_STATE_REFRESH);
                intent.putExtra("state", Constants.DOWNLOAD_WAIT);
                intent.putExtra("taskId", (long) taskToken);
                intent.putExtra("percent", reportStructure.getPercent());
                intent.putExtra("downloadedLength", reportStructure.getDownloadLength());
                sendBroadcast(intent);
                if (reportStructure.getState() == TaskStates.INIT) {
                    downloadManagerPro.waitInitDownload(taskToken);
                } else if (reportStructure.getState() == TaskStates.PAUSED) {
                    downloadManagerPro.waitPauseDownload(taskToken);
                }
            }
        }
    }

    public void pauseDownload(int taskToken) {
        try {
            ReportStructure report = downloadManagerPro.singleDownloadStatus(taskToken);
            LogUtil.getLogger().w(report.getState());
            if (report.getState() == TaskStates.INIT_WAIT || report.getState() == TaskStates.PAUSE_WAIT) {
                Intent intent = new Intent(Constants.DOWNLOAD_STATE_REFRESH);
                intent.putExtra("state", Constants.DOWNLOAD_PAUSED);
                intent.putExtra("taskId", (long) taskToken);
                intent.putExtra("percent", report.getPercent());
                intent.putExtra("downloadedLength", report.getDownloadLength());
                sendBroadcast(intent);
                if (report.getState() == TaskStates.INIT_WAIT) {
                    downloadManagerPro.changeStateToInit(taskToken);
                } else if (report.getState() == TaskStates.PAUSE_WAIT) {
                    downloadManagerPro.changeStateToPause(taskToken);
                }
            } else {
                downloadManagerPro.pauseDownload(taskToken);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelDownload(int taskToken, String downloadUrl, String packageName) {
        LogUtil.getLogger().d(taskToken);
        ReportStructure report = downloadManagerPro.singleDownloadStatus(taskToken);
        try {
            downloadManagerPro.pauseDownload(taskToken);
        } catch (Exception e) {
            if (downloadManagerPro.delete(taskToken, true) || report.getState() == TaskStates.INIT) {
                Intent intent = new Intent(Constants.DOWNLOAD_STATE_RESET);
                intent.putExtra("packageName", packageName);
                sendBroadcast(intent);
                ((CommonApplication) getApplicationContext()).getPreferenceConfig().setLong(new HashCodeFileNameGenerator().generate(downloadUrl), -10L);
                if(!packageName.equals("com.tunshu")) {
                    CustomToast.showToast(getApplicationContext(), getString(R.string.delete_download_task_success));
                }
            }
            e.printStackTrace();
        }
        if (downloadManagerPro.delete(taskToken, true) || report.getState() == TaskStates.INIT) {
            Intent intent = new Intent(Constants.DOWNLOAD_STATE_RESET);
            intent.putExtra("packageName", packageName);
            sendBroadcast(intent);
            ((CommonApplication) getApplicationContext()).getPreferenceConfig().setLong(new HashCodeFileNameGenerator().generate(downloadUrl), -10L);
            if(!packageName.equals("com.tunshu")) {
                CustomToast.showToast(getApplicationContext(), getString(R.string.delete_download_task_success));
            }
        }
    }

//    public void cancelWait(int taskToken) {
//        ReportStructure report = downloadManagerPro.singleDownloadStatus(taskToken);
//        if (report.getState() == TaskStates.INIT) {
//            if (downloadManagerPro.delete(taskToken, true)) {
//                Intent intent = new Intent(Constants.DOWNLOAD_STATE_REFRESH);
//                intent.putExtra("state", Constants.DOWNLOAD_PAUSED);
//                intent.putExtra("taskId", (long) taskToken);
//                sendBroadcast(intent);
//            }
//        } else if (report.getState() == TaskStates.PAUSED) {
//            Intent intent = new Intent(Constants.DOWNLOAD_STATE_REFRESH);
//            intent.putExtra("state", Constants.DOWNLOAD_PAUSED);
//            intent.putExtra("taskId", (long) taskToken);
//            sendBroadcast(intent);
//        }
//    }

    public void deleteDownloadHistory(int taskToken) {
        downloadManagerPro.delete(taskToken, true);
    }

    private List<GameListEntity> getAllDownloadState(List<GameListEntity> downloadInfoList) {
        if (!ListUtils.isEmpty(downloadInfoList)) {
            for (int i = 0, size = downloadInfoList.size(); i < size; i++) {
                LogUtil.getLogger().d(downloadInfoList.get(i).getGamename());
                LogUtil.getLogger().d(downloadInfoList.get(i).getTaskId());
                int currentTaskId = (int) ((CommonApplication) getApplicationContext()).getPreferenceConfig().getLong(new HashCodeFileNameGenerator().generate(downloadInfoList.get(i).getDownurl()), -10L);
                if (currentTaskId <= 0) {
                    continue;
                }
                ReportStructure report = downloadManagerPro.singleDownloadStatus(currentTaskId);
                if (report != null) {
                    LogUtil.getLogger().d(report.getName() + "-->" + report.getState() + "--->" + report.getId() + "-->" + report.getDownloadLength() + "-->" + report.getPercent() + "-->" + report.getTotalSize());
                    downloadInfoList.get(i).setState(String.valueOf(report.getState()));
                    downloadInfoList.get(i).setDownloadedLength(report.getDownloadLength());
                    downloadInfoList.get(i).setPercent(String.valueOf((int) report.getPercent()));
                    switch (report.getState()) {
                        case TaskStates.INIT_WAIT:
                        case TaskStates.PAUSE_WAIT:
                            downloadInfoList.get(i).setActionText(getString(R.string.pause));
                            downloadInfoList.get(i).setActionTag(Constants.WATTING_TAG);
                            break;
                        case TaskStates.INIT:
                            downloadInfoList.get(i).setActionText(getString(R.string.download_manager));
                            downloadInfoList.get(i).setActionTag(getString(R.string.pause));
                            break;
                        case TaskStates.READY:
                            downloadInfoList.get(i).setActionText(getString(R.string.download_manager));
                            break;
                        case TaskStates.DOWNLOADING:
                            downloadInfoList.get(i).setActionText((int)report.getPercent()+"%");
                            break;
                        case TaskStates.PAUSED:
                            downloadInfoList.get(i).setActionText(getString(R.string.go_on));
                            break;
                        case TaskStates.DOWNLOAD_FINISHED:
                            downloadInfoList.get(i).setActionText(getDownloadState(
                                    downloadInfoList.get(i).getPackpagename(),
                                    downloadInfoList.get(i).getDownurl(),
                                    downloadInfoList.get(i).getVersion()
                            ));
                            break;
                        case TaskStates.END:
                            downloadInfoList.get(i).setActionText(getDownloadState(
                                    downloadInfoList.get(i).getPackpagename(),
                                    downloadInfoList.get(i).getDownurl(),
                                    downloadInfoList.get(i).getVersion()
                            ));
                            break;
                        default:
                            break;
                    }
                }
            }
            return downloadInfoList;
        } else {
            return null;
        }
    }

    private String getDownloadState(String packageName, String downloadUrl, String versionNo) {
        PackageInfo packageInfo;
        try {
            packageInfo = getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
        }
        if (packageInfo == null) {
            if (getDownloadFile(downloadUrl).isFile()) {
                return getString(R.string.install);
            } else {
                return getString(R.string.download_manager);
            }
        } else {
            int version = packageInfo.versionCode;
            if (version < Integer.parseInt(versionNo)) {
                if (getDownloadFile(downloadUrl).isFile()) {
                    return getString(R.string.install);
                } else {
                    return getString(R.string.update);
                }
            } else {
                return getString(R.string.open);
            }
        }
    }

    private File getDownloadFile(String downloadUrl) {
        int taskToken = (int) ((CommonApplication) getApplicationContext()).getPreferenceConfig().getLong(new HashCodeFileNameGenerator().generate(downloadUrl), -10L);
        return new File(((CommonApplication) getApplicationContext()).getPreferenceConfig().getString(String.valueOf(taskToken), ""));
    }

    private List<TypeListEntity.DataEntity> getTypeAllDownloadState(List<TypeListEntity.DataEntity> downloadInfoList) {
        if (!ListUtils.isEmpty(downloadInfoList)) {
            for (int i = 0, size = downloadInfoList.size(); i < size; i++) {
                List<GameListEntity> gameList = downloadInfoList.get(i).getClassification().getGameList();
                if (!ListUtils.isEmpty(gameList)) {
                    for (int j = 0, length = gameList.size(); j < length; j++) {
                        int currentTaskId = (int) ((CommonApplication) getApplicationContext()).getPreferenceConfig().getLong(new HashCodeFileNameGenerator().generate(gameList.get(j).getDownurl()), -10L);
                        if (currentTaskId <= 0) {
                            continue;
                        }
                        ReportStructure report = downloadManagerPro.singleDownloadStatus(currentTaskId);
                        if (report != null) {
                            LogUtil.getLogger().d(report.getName() + "-->" + report.getState() + "--->" + report.getId() + "-->" + report.getDownloadLength() + "-->" + report.getPercent() + "-->" + report.getTotalSize());
                            gameList.get(j).setState(String.valueOf(report.getState()));
                            gameList.get(j).setDownloadedLength(report.getDownloadLength());
                            gameList.get(j).setPercent(String.valueOf((int) report.getPercent()));
                            switch (report.getState()) {
                                case TaskStates.INIT_WAIT:
                                case TaskStates.PAUSE_WAIT:
                                    gameList.get(j).setActionText(getString(R.string.pause));
                                    break;
                                case TaskStates.INIT:
                                    gameList.get(j).setActionText(getString(R.string.download_manager));
                                    break;
                                case TaskStates.READY:
                                    gameList.get(j).setActionText(getString(R.string.download_manager));
                                    break;
                                case TaskStates.DOWNLOADING:
                                    gameList.get(j).setActionText((int)report.getPercent()+"%");
                                    break;
                                case TaskStates.PAUSED:
                                    gameList.get(j).setActionText(getString(R.string.go_on));
                                    break;
                                case TaskStates.DOWNLOAD_FINISHED:
                                    gameList.get(j).setActionText(getDownloadState(
                                            gameList.get(j).getPackpagename(),
                                            gameList.get(j).getDownurl(),
                                            gameList.get(j).getVersion()
                                    ));
                                    break;
                                case TaskStates.END:
                                    gameList.get(j).setActionText(getDownloadState(
                                            gameList.get(j).getPackpagename(),
                                            gameList.get(j).getDownurl(),
                                            gameList.get(j).getVersion()
                                    ));
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
            }
            return downloadInfoList;
        } else {
            return null;
        }
    }

    private String getSingleDownloadState(String packageName, String downloadUrl, String versionNo) {
        int currentTaskId = (int) ((CommonApplication) getApplicationContext()).getPreferenceConfig().getLong(new HashCodeFileNameGenerator().generate(downloadUrl), -10L);
        if (currentTaskId > 0) {
            ReportStructure report = downloadManagerPro.singleDownloadStatus(currentTaskId);
            if (report != null) {
                String state = "";
                LogUtil.getLogger().d(report.getName() + "-->" + report.getState() + "--->" + report.getId() + "-->" + report.getDownloadLength() + "-->" + report.getPercent() + "-->" + report.getTotalSize());
                switch (report.getState()) {
                    case TaskStates.INIT_WAIT:
                    case TaskStates.PAUSE_WAIT:
                        state = getString(R.string.pause);
                        break;
                    case TaskStates.INIT:
                        state = getString(R.string.download_manager);
                        break;
                    case TaskStates.READY:
                        state = getString(R.string.download_manager);
                        break;
                    case TaskStates.DOWNLOADING:
                        state =(int)report.getPercent()+"%";
                        break;
                    case TaskStates.PAUSED:
                        state = getString(R.string.go_on);
                        break;
                    case TaskStates.DOWNLOAD_FINISHED:
                        state = getString(R.string.install);
                        break;
                    case TaskStates.END:
                        state = getString(R.string.install);
                        break;
                    default:
                        state = getDownloadState(packageName, downloadUrl, versionNo);
                        break;
                }
                return state;
            } else {
                return getDownloadState(packageName, downloadUrl, versionNo);
            }
        } else {
            return getDownloadState(packageName, downloadUrl, versionNo);
        }
    }

    /**
     * 重置下载异常退出的下载状态
     * TaskStates.DOWNLOADING-->TaskStates.PAUSED
     * TaskStates.PAUSE_WAIT-->TaskStates.PAUSED
     * TaskStates.INIT_WAIT-->TaskStates.INIT
     * TaskStates.INIT-->TaskStates.INIT
     *
     * @param downloadInfoList
     */
    private void resetAllDownloadState(List<GameListEntity> downloadInfoList) {
        if (!ListUtils.isEmpty(downloadInfoList)) {
            for (int i = 0, size = downloadInfoList.size(); i < size; i++) {
                int currentTaskId = (int) ((CommonApplication) getApplicationContext()).getPreferenceConfig().getLong(new HashCodeFileNameGenerator().generate(downloadInfoList.get(i).getDownurl()), -10L);
                if (currentTaskId <= 0) {
                    continue;
                }
                ReportStructure report = downloadManagerPro.singleDownloadStatus(currentTaskId);
                if (report != null) {
                    LogUtil.getLogger().d(report.getName() + "-->" + report.getState() + "--->" + report.getId() + "-->" + report.getDownloadLength() + "-->" + report.getPercent() + "-->" + report.getTotalSize());
                    switch (report.getState()) {
                        case TaskStates.INIT_WAIT:
                            downloadManagerPro.changeStateToInit(currentTaskId);
                            break;
                        case TaskStates.PAUSE_WAIT:
                            downloadManagerPro.changeStateToPause(currentTaskId);
                            break;
                        case TaskStates.INIT:
                            break;
                        case TaskStates.READY:
                            downloadManagerPro.changeStateToPause(currentTaskId);
                            break;
                        case TaskStates.DOWNLOADING:
                            downloadManagerPro.changeStateToPause(currentTaskId);
                            break;
                        case TaskStates.PAUSED:
                            break;
                        case TaskStates.DOWNLOAD_FINISHED:
                            break;
                        case TaskStates.END:
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

}
