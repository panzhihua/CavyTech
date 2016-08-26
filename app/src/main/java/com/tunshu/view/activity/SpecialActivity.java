package com.tunshu.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.basecore.util.core.FileUtils;
import com.basecore.util.core.ListUtils;
import com.basecore.util.core.ScreenUtil;
import com.basecore.util.core.StringUtils;
import com.basecore.util.netstate.NetWorkUtil;
import com.basecore.widget.CustomToast;
import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.tunshu.R;
import com.tunshu.adapter.MyGameAdapter;
import com.tunshu.entity.GameListEntity;
import com.tunshu.entity.InfoEntity;
import com.tunshu.entity.SpecialEntity;
import com.tunshu.http.MyHttpClient;
import com.tunshu.util.Constants;
import com.tunshu.util.LogUtil;
import com.tunshu.widget.ScrimUtil;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ShadowNight on 2015/8/21.
 */
public class SpecialActivity extends CommonActivity {
    private PullToRefreshListView listView;
    private SimpleDraweeView appimg;
    private TextView introduce;
    private MyGameAdapter adapter;
    private int pageNo = 1;
    private String style;
    private String preId;
    private String name;
    private List<GameListEntity> gameList = new ArrayList<>();
    private DownloadStateReceiver downloadStateReceiver = new DownloadStateReceiver();
    private AppInstalledReceiver installedReceiver = new AppInstalledReceiver();
    private DownloadInfoReceiver downloadInfoReceiver = new DownloadInfoReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getStyle());
        setContentView(R.layout.activity_special);
        preId = getIntent().getStringExtra("preId") == null ? "" : getIntent().getStringExtra("preId");
        name = getIntent().getStringExtra("name") == null ? "" : getIntent().getStringExtra("name");
        findView();
        addListener();
        getData(false);
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

    private int getStyle() {
        if (StringUtils.isEmpty(style)) {
            style = getIntent().getStringExtra("style") == null ? "0" : getIntent().getStringExtra("style");
        }
        switch (style) {
            case "0":
                return R.style.Special_Theme_0;
            case "1":
                return R.style.Special_Theme_1;
            case "2":
                return R.style.Special_Theme_2;
            case "3":
                return R.style.Special_Theme_3;
            case "4":
                return R.style.Special_Theme_4;
            case "5":
                return R.style.Special_Theme_5;
            case "6":
                return R.style.Special_Theme_6;
            case "7":
                return R.style.Special_Theme_7;
            default:
                return R.style.Special_Theme_0;
        }
    }

    private void findView() {
        findTitle();
        listView = (PullToRefreshListView) findViewById(R.id.main_pull_refresh_view);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        View header = getLayoutInflater().inflate(R.layout.activity_special_top, listView, false);
        appimg = (SimpleDraweeView) header.findViewById(R.id.app_img);
        introduce=(TextView)header.findViewById(R.id.special_intro);
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getWidth();
        LinearLayout.LayoutParams adLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (height-ScreenUtil.dip2px(10))*3/7);
        adLayoutParams.setMargins(0, 0, 0, ScreenUtil.dip2px(5));
        appimg.setLayoutParams(adLayoutParams);
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        header.setLayoutParams(layoutParams);
        listView.getRefreshableView().addHeaderView(header);
        title.setText(name);
    }

    private void fillAd(String url,String intro,String titleName) {
        title.setText(titleName);
        appimg.setImageURI(Uri.parse(url));
        introduce.setText(intro);
    }

    private class DownloadInfoReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("flag").equals("SpecialActivity")) {
                gameList = (List<GameListEntity>) intent.getSerializableExtra("downloadInfoList");
                if (!ListUtils.isEmpty(gameList)) {
                    fillAdapter(gameList);
                }
            }
        }
    }


    private void fillAdapter(List<GameListEntity> infoList) {
        if (adapter != null) {
            adapter.refreshAdapter(infoList);
        } else {
            adapter = new MyGameAdapter(this, infoList, getStyle());
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
            if (NetWorkUtil.isNetworkConnected(SpecialActivity.this)) {
                if (NetWorkUtil.isNetworkAvailable(SpecialActivity.this)) {
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
            if (NetWorkUtil.isNetworkConnected(SpecialActivity.this)) {
                if (NetWorkUtil.isNetworkAvailable(SpecialActivity.this)) {
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
            if (!ListUtils.isEmpty(gameList)) {
                for (int i = 0, size = gameList.size(); i < size; i++) {
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
                        adapter.updateView(listView, gameList, i, 2);
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
                if (!ListUtils.isEmpty(gameList)) {
                    for (int i = 0, size = gameList.size(); i < size; i++) {
                        if (packageName.equals(gameList.get(i).getPackpagename())) {
                            gameList.get(i).setActionText(getString(R.string.open));
                            fillAdapter(gameList);
                            if (getCoreApplication().getPreferenceConfig().getBoolean(Constants.SETTING_DELETE_INSTALL_PACKAGE, true)) {//删除安装包
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
                if (!ListUtils.isEmpty(gameList)) {
                    for (int i = 0, size = gameList.size(); i < size; i++) {
                        if (packageName.equals(gameList.get(i).getPackpagename())) {
                            gameList.get(i).setActionText(getString(R.string.download_manager));
                            fillAdapter(gameList);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void getData(final boolean isMore) {
        if (!isMore) {
            showProgress();
        }
        RequestParams params = getCommonParams();
        params.put("ac", "getpergames");
        params.put("preId", preId);
        params.put("pagenum", String.valueOf(pageNo));
        params.put("pagesize", Constants.PAGESIZE);
        MyHttpClient.get(this, "appIndex/index", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                listView.onRefreshComplete();
                hideProgress();
                try {
                    LogUtil.getLogger().d(response.toString());
                    SpecialEntity entity = com.alibaba.fastjson.JSONObject.parseObject(response.toString(), SpecialEntity.class);
                    if (entity.getCode().equals("1001")) {
                        if (entity.getData() != null) {
                            if(!isMore) {
                                style = entity.getData().getStyle();
                                fillAd(entity.getData().getBannerphone(),entity.getData().getIntro(),entity.getData().getTitle());
                            }

                            if (!ListUtils.isEmpty(entity.getData().getGameList())) {
                                pageNo++;
                                if (isMore) {
                                    gameList.addAll(entity.getData().getGameList());
                                } else {
                                    listView.setMode(PullToRefreshBase.Mode.BOTH);
                                    gameList = entity.getData().getGameList();
                                }
                                if (!ListUtils.isEmpty(gameList)) {
                                    Intent intent = new Intent(Constants.GET_ALL_DOWNLOAD_STATE);
                                    intent.putExtra("downloadInfoList", (Serializable) gameList);
                                    intent.putExtra("flag", "SpecialActivity");
                                    sendBroadcast(intent);
//                                    fillAdapter(gameList);
                                }
                            }else{

                                if(isMore) {
                                    listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                    GameListEntity gameListEntity=new GameListEntity();
                                    gameListEntity.setType("end");
                                    gameList.add(gameListEntity);
                                }else{
                                    fillAdapter(gameList);
                                }
                            }
                        }
                    } else {
                        CustomToast.showToast(SpecialActivity.this, entity.getMsg());
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
