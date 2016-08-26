package com.tunshu.fragment;


import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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
import com.tunshu.entity.InfoEntity;
import com.tunshu.entity.TopListEntity;
import com.tunshu.entity.TopTypeListEntity;

import com.tunshu.http.MyHttpClient;
import com.tunshu.util.Constants;
import com.tunshu.util.LogUtil;
import com.tunshu.view.activity.HomePageActivity;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class MainChartsFragment extends CommonFragment {
    private PullToRefreshListView listView;
    private MyGameAdapter adapter;
    private List<GameListEntity> infoEntityList = new ArrayList<>();
    private List<TopTypeListEntity.DataEntity> topTypeinfoEntityList = new ArrayList<>();

    private DownloadStateReceiver downloadStateReceiver = new DownloadStateReceiver();
    private DownloadStateResetReceiver downloadStateResetReceiver = new DownloadStateResetReceiver();
    private AppInstalledReceiver installedReceiver = new AppInstalledReceiver();
    private DownloadInfoReceiver downloadInfoReceiver = new DownloadInfoReceiver();

    private RadioGroup itemRadioGroup;
    private int indicatorWidth;
    private LayoutInflater mInflater;
    private RadioButton radiobt;

    private String curTopType = "";
    private int topTypeIndex = 0;

    private List<TopGameListInfo> topGameList = new ArrayList<>();

    private static boolean isFirstSetCheck = true;

    private class TopGameListInfo{
        public List<GameListEntity> infoEntityList = new ArrayList<>();
        public String topType = "";
        public int pageNo = 1;

        public TopGameListInfo(String topType, List<GameListEntity> infoEntityList)
        {
            this.topType = topType;
            this.infoEntityList = infoEntityList;
        }
    }

    public int getTopTypeCurPageNo(String topType){
        for(int i = 0; i < topGameList.size(); i++){

            if(topGameList.get(i).topType == topType){
                return topGameList.get(i).pageNo;
            }
        }

        return 1;
    }
    public void setTopTypeCurPageNo(String topType){
        for(int i = 0; i < topGameList.size(); i++){

            if(topGameList.get(i).topType == topType){
                topGameList.get(i).pageNo += 1;
                return;
            }
        }
    }

    public List<GameListEntity> getGameList(String topType){
        for(int i = 0; i < topGameList.size(); i++){

            if(topGameList.get(i).topType == topType){
                return topGameList.get(i).infoEntityList;
            }
        }

        infoEntityList.clear();
        return infoEntityList;
    }

    public void setGameList(String topType, List<GameListEntity> infoEntityList, boolean isAdd){

        for(int i = 0; i < topGameList.size(); i++){

            if(topGameList.get(i).topType == topType){
                 if(isAdd){
                     topGameList.get(i).infoEntityList.addAll(infoEntityList);
                }else{
                     topGameList.get(i).infoEntityList.clear();
                     topGameList.get(i).pageNo = 1;
                     topGameList.get(i).infoEntityList = infoEntityList;
                }
                return;
             }
        }
        TopGameListInfo gameList = new TopGameListInfo(topType, infoEntityList);

        topGameList.add(gameList);
    }
    public void setGameList(String topType, GameListEntity infoEntityList){

        for(int i = 0; i < topGameList.size(); i++){

            if(topGameList.get(i).topType == topType){
                topGameList.get(i).infoEntityList.add(infoEntityList);
                return;
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       // getData(false);
        addListener();
        getActivity().registerReceiver(downloadStateReceiver, new IntentFilter(Constants.DOWNLOAD_STATE_REFRESH));
        getActivity().registerReceiver(downloadStateResetReceiver, new IntentFilter(Constants.DOWNLOAD_STATE_RESET));
        getActivity().registerReceiver(downloadInfoReceiver, new IntentFilter(Constants.GET_ALL_DOWNLOAD_STATE_RESULT));
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_REMOVED");
        filter.addDataScheme("package");
        getActivity().registerReceiver(installedReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(downloadStateResetReceiver);
        getActivity().unregisterReceiver(downloadStateReceiver);
        getActivity().unregisterReceiver(installedReceiver);
        getActivity().unregisterReceiver(downloadInfoReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mInflater = inflater;
        View rootView = inflater.inflate(R.layout.activity_charts_fragment, container, false);//关联布局文件
        findView(rootView);

        getTypesData();
        return rootView;
    }

    private void findView(View v) {

        listView = (PullToRefreshListView) v.findViewById(R.id.main_pull_refresh_view);

        itemRadioGroup = (RadioGroup) v.findViewById(R.id.rg_nav_content);

        View tab_bar = (View) v.findViewById(R.id.tab_bar_layout);
        tab_bar.setVisibility(View.VISIBLE);

        View iv_shadow = (View) v.findViewById(R.id.tab_shadow);
        iv_shadow.setVisibility(View.VISIBLE);

        ImageView iv_nav_indicator = (ImageView) v.findViewById(R.id.iv_nav_indicator);
        iv_nav_indicator.setVisibility(View.GONE);
     }

    private void initNavigationHSV() {
        itemRadioGroup.removeAllViews();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        indicatorWidth = dm.widthPixels/ topTypeinfoEntityList.size();
        for (int i = 0, size = topTypeinfoEntityList.size(); i < size; i++) {
            RadioButton rb = (RadioButton) mInflater.inflate(R.layout.activity_nav_toptype_radiogroup_item, null);
            rb.setId(i);
            rb.setText(topTypeinfoEntityList.get(i).getRankname());
            rb.setBackgroundColor(getResources().getColor(R.color.background));
            rb.setLayoutParams(new ViewGroup.LayoutParams(indicatorWidth,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            if (i == 0) {
                radiobt = rb;
            }
            itemRadioGroup.addView(rb);
        }
        if(topTypeinfoEntityList.size() > 0){
            curTopType = topTypeinfoEntityList.get(0).getRanktype();
        }

        radiobt.performClick();
    }

    private class DownloadInfoReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("flag").equals("MainChartsFragment")) {
                infoEntityList = (List<GameListEntity>) intent.getSerializableExtra("downloadInfoList");
                if (!ListUtils.isEmpty(infoEntityList)) {
                    fillAdapter(infoEntityList);
                }
            }
        }
    }

    private void fillAdapter(List<GameListEntity> list) {
        if (adapter != null) {
            adapter.refreshAdapter(list);
        } else {
            adapter = new MyGameAdapter(getActivity(), list, true, true);
            adapter.showDivider1(false);

            listView.setAdapter(adapter);
        }
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

        itemRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(!isFirstSetCheck){
                    if (itemRadioGroup.getChildAt(checkedId) != null) {
                        if(topTypeinfoEntityList.size() > checkedId){
                            curTopType = topTypeinfoEntityList.get(checkedId).getRanktype();
                            infoEntityList = getGameList(curTopType);
                            topTypeIndex = checkedId;
                            if(infoEntityList.size() == 0){
                                getData(false);
                            }else{
                                fillAdapter(infoEntityList);
                            }
                        }
                    }
                }
            }
        });
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
                                infoEntityList.get(i).setActionText(getString(R.string.pause));
                                break;
                            case Constants.DOWNLOAD_PROCESS:
                                infoEntityList.get(i).setActionText((int) intent.getDoubleExtra("percent", 0) + "%");
                                break;
                            case Constants.DOWNLOAD_FINISHED:
                                break;
                            case Constants.DOWNLOAD_COMPLETED:
                                infoEntityList.get(i).setActionText(getString(R.string.install));
                                break;
                            case Constants.DOWNLOAD_PAUSED:
                                infoEntityList.get(i).setActionText(getString(R.string.go_on));
                                break;
                            case Constants.DOWNLOAD_REBUILD_FINISHED:
                                break;
                            case Constants.DOWNLOAD_CONNECTION_LOST:
                                infoEntityList.get(i).setActionText(getString(R.string.try_again));
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
                            infoEntityList.get(i).setActionText(getString(R.string.open));
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
                            infoEntityList.get(i).setActionText(getString(R.string.download_manager));
                            fillAdapter(infoEntityList);
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
            if (NetWorkUtil.isNetworkConnected(getActivity())) {
                if (NetWorkUtil.isNetworkAvailable(getActivity())) {

                    if(topTypeinfoEntityList.size() == 0){
                        getTypesData();
                    }else{

                        intPageNo(curTopType);
                        getData(false);
                    }
                } else {
                    listView.onRefreshComplete();
                }
            } else {
                listView.onRefreshComplete();
            }
        }
    }
    public void intPageNo(String topType){
        for(int i = 0; i < topGameList.size(); i++){

            if(topGameList.get(i).topType == topType){
               topGameList.get(i).pageNo = 1;
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
            if (NetWorkUtil.isNetworkConnected(getActivity())) {
                if (NetWorkUtil.isNetworkAvailable(getActivity())) {
                    if(topTypeinfoEntityList.size() == 0){
                        getTypesData();
                    }else{
                        getData(true);
                    }
                } else {
                    listView.onRefreshComplete();
                }
            } else {
                listView.onRefreshComplete();
            }
        }
    }

    private void getTypesData() {
        RequestParams params = getCommonParams();
        params.put("ac", "newranking");

        MyHttpClient.get(getActivity(), Constants.HTTP_PRE_MOBILEINDEX, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                listView.onRefreshComplete();

                 try {
                     TopTypeListEntity entity = com.alibaba.fastjson.JSONObject.parseObject(response.toString(), TopTypeListEntity.class);
                     if (entity.getCode().equals("1001")) {
                        if (!ListUtils.isEmpty(entity.getData())) {
                            topTypeinfoEntityList.clear();
                            topTypeinfoEntityList = entity.getData();
                            initNavigationHSV();
                            getData(false);
                        } else {
                            CustomToast.showToast(getActivity(), entity.getMsg());
                        }
                    } else {
                        CustomToast.showToast(getActivity(), entity.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                listView.onRefreshComplete();

            }
        });
    }
    private void getData(final boolean isMore) {
        showProgress();
        RequestParams params = getCommonParams();
        params.put("ac", "getrankgames");
        params.put("ranktype", curTopType);
        params.put("pagenum", String.valueOf(getTopTypeCurPageNo(curTopType)));
        params.put("pagesize", Constants.PAGESIZE);
        MyHttpClient.get(getActivity(), Constants.HTTP_PRE_APPINDEX, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                listView.onRefreshComplete();
                hideProgress();

                if(isFirstSetCheck){
                    isFirstSetCheck = false;
                }
                try {
                    LogUtil.getLogger().d(response.toString());
                    TopListEntity entity = com.alibaba.fastjson.JSONObject.parseObject(response.toString(), TopListEntity.class);
                    if (entity.getCode().equals("1001")) {
                        if (!ListUtils.isEmpty(entity.getData().getGameList())) {
                             //排行应用名前加排名数

                            infoEntityList = getGameList(curTopType);
                            int gameListSize = isMore ? ListUtils.isEmpty(infoEntityList) ? 0 : infoEntityList.size() : 0;
                            List<GameListEntity> tempList = entity.getData().getGameList();
                            for (int i = 0, size = tempList.size(); i < size; i++) {
                                tempList.get(i).setTopnum(gameListSize + i + 1 + ".");
                            }
                            if (!isMore) {
                                listView.setMode(PullToRefreshBase.Mode.BOTH);
                            }

                            setGameList(curTopType, tempList, isMore);
                            setTopTypeCurPageNo(curTopType);

                            infoEntityList = getGameList(curTopType);

                            Intent intent = new Intent(Constants.GET_ALL_DOWNLOAD_STATE);
                            intent.putExtra("downloadInfoList", (Serializable) infoEntityList);
                            intent.putExtra("flag", "MainChartsFragment");
                            getActivity().sendBroadcast(intent);
//                            fillAdapter(infoEntityList);
                        } else {
                            if(isMore) {
                                listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                GameListEntity gameListEntity=new GameListEntity();
                                gameListEntity.setType("end");
                                setGameList(curTopType, gameListEntity);

                                infoEntityList = getGameList(curTopType);
                                fillAdapter(infoEntityList);
                            }
                        }
                    } else {
                        CustomToast.showToast(getActivity(), entity.getMsg());
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

                if(isFirstSetCheck){
                    isFirstSetCheck = false;
                }
            }
        });


    }

}
