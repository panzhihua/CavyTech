package com.tunshu.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.widget.ListView;

import com.basecore.application.BaseApplication;
import com.basecore.util.core.FileUtils;
import com.basecore.util.core.ListUtils;
import com.basecore.util.netstate.NetWorkUtil;
import com.basecore.widget.CustomToast;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.tunshu.R;
import com.tunshu.adapter.MyGameAdapter;
import com.tunshu.application.CommonApplication;
import com.tunshu.entity.GameListEntity;
import com.tunshu.entity.TopListEntity;
import com.tunshu.http.MyHttpClient;
import com.tunshu.util.Constants;
import com.tunshu.util.LogUtil;
import com.tunshu.view.activity.CommonActivity;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by longjining on 16/1/26.
 */
public abstract class GamesListView implements IGamesListView{
    protected PullToRefreshListView listView;
    protected Activity activity;
    protected int pageNo = 1;

    private MyGameAdapter adapter;
    private List<GameListEntity> infoEntityList = new ArrayList<>();
    private DownloadStateReceiver downloadStateReceiver = new DownloadStateReceiver();
    private DownloadStateResetReceiver downloadStateResetReceiver = new DownloadStateResetReceiver();
    private AppInstalledReceiver installedReceiver = new AppInstalledReceiver();
    private DownloadInfoReceiver downloadInfoReceiver = new DownloadInfoReceiver();

    private String listViewTag = "";

    abstract public void setListView(PullToRefreshListView listView);
    abstract public void setActivity(Activity activity);
    abstract public void getData(final boolean isMore);

    public void setListViewTag(String listViewTag){
        this.listViewTag = listViewTag;
    }

    protected void getData(RequestParams params, String preHttp, final boolean isMore, final CommonActivity activity){

        MyHttpClient.get(activity, preHttp, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                listView.onRefreshComplete();
                if(activity != null){

                    activity.hideProgress();
                }
                try {
                    LogUtil.getLogger().d(response.toString());
                    TopListEntity entity = com.alibaba.fastjson.JSONObject.parseObject(response.toString(), TopListEntity.class);
                    if (entity.getCode().equals("1001")) {
                        if (!ListUtils.isEmpty(entity.getData().getGameList())) {
                            pageNo++;
                            //排行应用名前加排名数
                            int gameListSize = isMore ? ListUtils.isEmpty(infoEntityList) ? 0 : infoEntityList.size() : 0;
                            List<GameListEntity> tempList = entity.getData().getGameList();
                            /*
                            for (int i = 0, size = tempList.size(); i < size; i++) {
                                tempList.get(i).setTopnum(gameListSize + i + 1 + ".");
                            }*/
                            if (isMore) {
                                infoEntityList.addAll(tempList);
                            } else {
                                listView.setMode(PullToRefreshBase.Mode.BOTH);
                                infoEntityList = tempList;
                            }
                            if(!listViewTag.isEmpty()){
                                Intent intent = new Intent(Constants.GET_ALL_DOWNLOAD_STATE);
                                intent.putExtra("downloadInfoList", (Serializable) infoEntityList);
                                intent.putExtra("flag", listViewTag);
                                activity.sendBroadcast(intent);
                            }
                        } else {
                            if (isMore) {
                                listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                GameListEntity gameListEntity = new GameListEntity();
                                gameListEntity.setType("end");
                                infoEntityList.add(gameListEntity);
                                fillAdapter(infoEntityList);
                            }
                        }
                    } else {
                        CustomToast.showToast(activity, entity.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                listView.onRefreshComplete();
                if(activity != null){

                    activity.hideProgress();
                }
            }
        });
    };

    private BaseApplication getCoreApplication(){
        return BaseApplication.getApplication();
    }

    protected void setReceiver(){
        addListener();
        activity.registerReceiver(downloadStateReceiver, new IntentFilter(Constants.DOWNLOAD_STATE_REFRESH));
        activity.registerReceiver(downloadStateResetReceiver, new IntentFilter(Constants.DOWNLOAD_STATE_RESET));
        activity.registerReceiver(downloadInfoReceiver, new IntentFilter(Constants.GET_ALL_DOWNLOAD_STATE_RESULT));
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_REMOVED");
        filter.addDataScheme("package");
        activity.registerReceiver(installedReceiver, filter);
    }
    private void addListener() {
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                new HeaderRefreshTask().execute();

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                new FooterRefreshTask().execute();

            }
        });
    }
    private class HeaderRefreshTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            try {
                Thread.sleep(Constants.REFRESH_SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            if (NetWorkUtil.isNetworkConnected(activity)) {
                if (NetWorkUtil.isNetworkAvailable(activity)) {
                    pageNo = 1;
                    getData(false);
                } else {
                    listView.onRefreshComplete();
                }
            } else {
                listView.onRefreshComplete();
            }
        }
    }

    private class FooterRefreshTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            try {
                Thread.sleep(Constants.REFRESH_SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            if (NetWorkUtil.isNetworkConnected(activity)) {
                if (NetWorkUtil.isNetworkAvailable(activity)) {
                    getData(true);
                } else {
                    listView.onRefreshComplete();
                }
            } else {
                listView.onRefreshComplete();
            }
        }
    }
    private class DownloadStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!ListUtils.isEmpty(infoEntityList)) {
                for (int i = 0, size = infoEntityList.size(); i < size; i++) {

                    long currentTaskId = getCoreApplication().getPreferenceConfig().getLong(new HashCodeFileNameGenerator().generate(infoEntityList.get(i).getDownurl()), -10L);
                    if (currentTaskId == intent.getLongExtra("taskId", -1L)) {
                        String state = intent.getStringExtra("state");
                        switch (state) {
                            case Constants.DOWNLOAD_WAIT:
                                infoEntityList.get(i).setActionText(activity.getString(R.string.pause));
                                break;
                            case Constants.DOWNLOAD_PROCESS:
                                infoEntityList.get(i).setActionText((int) intent.getDoubleExtra("percent", 0) + "%");
                                break;
                            case Constants.DOWNLOAD_FINISHED:
                                break;
                            case Constants.DOWNLOAD_COMPLETED:
                                infoEntityList.get(i).setActionText(activity.getString(R.string.install));
                                break;
                            case Constants.DOWNLOAD_PAUSED:
                                infoEntityList.get(i).setActionText(activity.getString(R.string.go_on));
                                break;
                            case Constants.DOWNLOAD_REBUILD_FINISHED:
                                break;
                            case Constants.DOWNLOAD_CONNECTION_LOST:
                                infoEntityList.get(i).setActionText(activity.getString(R.string.try_again));
                                break;
                            default:
                                break;
                        }
                        if(adapter != null){
                            adapter.updateView(listView, infoEntityList, i, 1);
                        }

                        break;
                    }
                }
            }

        }
    }

    private class DownloadStateResetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String packageName = intent.getStringExtra("packageName") == null ? "" : intent.getStringExtra("packageName");
            if (!ListUtils.isEmpty(infoEntityList)) {
                for (int i = 0, size = infoEntityList.size(); i < size; i++) {
                    if (packageName.equals(infoEntityList.get(i).getPackpagename())) {
                        infoEntityList.get(i).setActionText("");
                        break;
                    }
                }
                fillAdapter(infoEntityList);
            }
        }
    }

    public class AppInstalledReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 接收安装广播
            if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
                String packageName = intent.getDataString().replace("package:", "");
                LogUtil.getLogger().d(packageName);
                if (!ListUtils.isEmpty(infoEntityList)) {
                    for (int i = 0, size = infoEntityList.size(); i < size; i++) {
                        if (packageName.equals(infoEntityList.get(i).getPackpagename())) {
                            infoEntityList.get(i).setActionText(activity.getString(R.string.open));
                            fillAdapter(infoEntityList);
                            if (getCoreApplication().getPreferenceConfig().getBoolean(Constants.SETTING_DELETE_INSTALL_PACKAGE, true)) {//删除安装包
                                long currentTaskId = getCoreApplication().getPreferenceConfig().getLong(new HashCodeFileNameGenerator().generate(infoEntityList.get(i).getDownurl()), -10L);
                                File file = new File(getCoreApplication().getPreferenceConfig().getString(String.valueOf(currentTaskId), ""));
                                FileUtils.DeleteFile(file);
                            }
                            break;
                        }
                    }
                }
                deleteDownloadInfo(packageName);

            }
            // 接收卸载广播
            if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
                String packageName = intent.getDataString().replace("package:", "");
                LogUtil.getLogger().d(packageName);
                if (!ListUtils.isEmpty(infoEntityList)) {
                    for (int i = 0, size = infoEntityList.size(); i < size; i++) {
                        if (packageName.equals(infoEntityList.get(i).getPackpagename())) {
                            infoEntityList.get(i).setActionText(activity.getString(R.string.download_manager));
                            fillAdapter(infoEntityList);
                            break;
                        }
                    }
                }
            }
        }
    }

    private class DownloadInfoReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!listViewTag.isEmpty()){
                if (intent.getStringExtra("flag").equals(listViewTag)) {
                    infoEntityList = (List<GameListEntity>) intent.getSerializableExtra("downloadInfoList");
                    if (!ListUtils.isEmpty(infoEntityList)) {
                        fillAdapter(infoEntityList);
                    }
                }
            }
        }
    }
    private void fillAdapter(List<GameListEntity> list) {
        if (adapter != null) {
            adapter.refreshAdapter(list);
        } else {
            adapter = new MyGameAdapter(activity, list, true, true);
            listView.setAdapter(adapter);
        }
    }

    public void deleteDownloadInfo(String packageName) {
        final List<GameListEntity> downloadInfoList = ((CommonApplication) getCoreApplication()).finalDb.findAll(GameListEntity.class);
        if (!ListUtils.isEmpty(downloadInfoList)) {
            for (int i = 0, size = downloadInfoList.size(); i < size; i++) {
                if (packageName.equals(downloadInfoList.get(i).getPackpagename())) {
                    LogUtil.getLogger().d(downloadInfoList.get(i).getTaskId());
                    getCoreApplication().getPreferenceConfig().setLong(new HashCodeFileNameGenerator().generate(downloadInfoList.get(i).getDownurl()), -10L);
                    Intent intent = new Intent(Constants.DOWNLOAD_INFO_DELETE);
                    intent.putExtra("taskToken", downloadInfoList.get(i).getTaskId());
                    downloadInfoList.remove(i);
                    break;
                }
            }
            ((CommonApplication) getCoreApplication()).finalDb.deleteAll(GameListEntity.class);
            for (int j = 0, length = downloadInfoList.size(); j < length; j++) {
                ((CommonApplication) getCoreApplication()).finalDb.save(downloadInfoList.get(j));
            }
        }
    }

}
