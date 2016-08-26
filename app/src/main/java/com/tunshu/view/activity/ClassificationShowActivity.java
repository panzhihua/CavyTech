package com.tunshu.view.activity;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.basecore.util.core.FileUtils;
import com.basecore.util.core.ListUtils;
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
import com.tunshu.entity.TypeListEntity;
import com.tunshu.http.MyHttpClient;
import com.tunshu.util.Constants;
import com.tunshu.util.LogUtil;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：yzb on 2015/7/10 13:37
 */
public class ClassificationShowActivity extends CommonActivity {
    private PullToRefreshListView listView;
    private MyGameAdapter adapter;
    private List<GameListEntity> gameList = new ArrayList<>();
    private int pageNo=1;
    private String classId;
    private String className;
    private DownloadStateReceiver downloadStateReceiver = new DownloadStateReceiver();
    private AppInstalledReceiver installedReceiver = new AppInstalledReceiver();
    private DownloadInfoReceiver downloadInfoReceiver = new DownloadInfoReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify_show);
        classId=getIntent().getStringExtra("classId")==null?"":getIntent().getStringExtra("classId");
        className=getIntent().getStringExtra("className")==null?"":getIntent().getStringExtra("className");
        findView();
        getData(classId,false);
        addListener();
    }
    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(downloadStateReceiver, new IntentFilter(Constants.DOWNLOAD_STATE_REFRESH));
        registerReceiver(downloadInfoReceiver, new IntentFilter(Constants.GET_ALL_DOWNLOAD_STATE_RESULT));
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_REMOVED");
        filter.addDataScheme("package");
        registerReceiver(installedReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(downloadStateReceiver);
        unregisterReceiver(installedReceiver);
        unregisterReceiver(downloadInfoReceiver);
    }

    private void findView() {
        findTitle();
        listView = (PullToRefreshListView) findViewById(R.id.main_pull_refresh_view);
        title.setText(className);
    }
    private class DownloadInfoReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("flag").equals("ClassificationShowActivity")) {
                gameList = (List<GameListEntity>) intent.getSerializableExtra("downloadInfoList");
                if (!ListUtils.isEmpty(gameList)) {
                    fillAdapter(gameList);
                }
            }
        }
    }

    private void fillAdapter(List<GameListEntity> list) {
        if (adapter != null) {
            adapter.refreshAdapter(list);
        } else {
            adapter = new MyGameAdapter(ClassificationShowActivity.this, list);
            listView.setAdapter(adapter);
        }

    }
    private void addListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
    private class DownloadStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!ListUtils.isEmpty(gameList)){
                for (int i = 0,size=gameList.size(); i <size ; i++) {
                    long currentTaskId = getCoreApplication().getPreferenceConfig().getLong(new HashCodeFileNameGenerator().generate(gameList.get(i).getDownurl()), -10L);
                    if (currentTaskId == intent.getLongExtra("taskId", -1L)) {
                        String state = intent.getStringExtra("state");
                        switch (state) {
                            case Constants.DOWNLOAD_WAIT:
                                gameList.get(i).setActionText(getString(R.string.pause));
                                break;
                            case Constants.DOWNLOAD_PROCESS:
                                gameList.get(i).setActionText((int) intent.getDoubleExtra("percent", 0) + "%");
                                break;
                            case Constants.DOWNLOAD_FINISHED:
                                break;
                            case Constants.DOWNLOAD_COMPLETED:
                                gameList.get(i).setActionText(getString(R.string.install));
                                break;
                            case Constants.DOWNLOAD_PAUSED:
                                gameList.get(i).setActionText(getString(R.string.go_on));
                                break;
                            case Constants.DOWNLOAD_REBUILD_FINISHED:
                                break;
                            case Constants.DOWNLOAD_CONNECTION_LOST:
                                gameList.get(i).setActionText(getString(R.string.try_again));
                                break;
                            default:
                                break;
                        }
//                        fillAdapter(gameList);
                        adapter.updateView(listView, gameList, i, 1);
                        break;
                    }
                }
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
                if(!ListUtils.isEmpty(gameList)){
                    for (int i = 0,size=gameList.size(); i <size ; i++) {
                        if(packageName.equals(gameList.get(i).getPackpagename())){
                            gameList.get(i).setActionText(getString(R.string.open));
                            fillAdapter(gameList);
                            if(getCoreApplication().getPreferenceConfig().getBoolean(Constants.SETTING_DELETE_INSTALL_PACKAGE, true)) {//删除安装包
                                long currentTaskId = getCoreApplication().getPreferenceConfig().getLong(new HashCodeFileNameGenerator().generate(gameList.get(i).getDownurl()), -10L);
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
                if(!ListUtils.isEmpty(gameList)){
                    for (int i = 0,size=gameList.size(); i <size ; i++) {
                        if(packageName.equals(gameList.get(i).getPackpagename())){
                            gameList.get(i).setActionText(getString(R.string.download_manager));
                            fillAdapter(gameList);
                            break;
                        }
                    }
                }
            }
        }
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
            pageNo=1;
            getData(classId,false);
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
            getData(classId,true);
        }
    }
    private void getData(String classId,final boolean isMore) {
        if(!isMore){
            showProgress();
        }
        RequestParams params = getCommonParams();
        params.put("ac", "getclassificationmore");
        params.put("pagenum", String.valueOf(pageNo));
        params.put("pagesize", Constants.PAGESIZE);
        params.put("classid", classId);
        MyHttpClient.get(ClassificationShowActivity.this,"appIndex/index", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                listView.onRefreshComplete();
                hideProgress();
                try {
                    LogUtil.getLogger().d(response.toString());
                    TopListEntity entity = com.alibaba.fastjson.JSONObject.parseObject(response.toString(), TopListEntity.class);
                    if (entity.getCode().equals("1001")) {
                        if (!ListUtils.isEmpty(entity.getData().getGameList())) {
                            pageNo++;
                            if (isMore) {
                                gameList.addAll(entity.getData().getGameList());
                            } else {
                                listView.setMode(PullToRefreshBase.Mode.BOTH);
                                gameList = entity.getData().getGameList();
                            }
                            Intent intent = new Intent(Constants.GET_ALL_DOWNLOAD_STATE);
                            intent.putExtra("downloadInfoList", (Serializable) gameList);
                            intent.putExtra("flag","ClassificationShowActivity");
                            sendBroadcast(intent);
//                            fillAdapter(gameList);
                        }else{
                            if(isMore) {
                                listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                GameListEntity gameListEntity=new GameListEntity();
                                gameListEntity.setType("end");
                                gameList.add(gameListEntity);
                                fillAdapter(gameList);
                            }
                        }
                    } else {
                        CustomToast.showToast(ClassificationShowActivity.this, entity.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                listView.onRefreshComplete();
                hideProgress();
            }
        });


    }
}
