package com.tunshu.fragment;


import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.effect.Effect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.basecore.util.bitmap.Options;
import com.basecore.util.core.FileUtils;
import com.basecore.util.core.ListUtils;
import com.basecore.util.core.ScreenUtil;
import com.basecore.util.core.StringUtils;
import com.basecore.util.netstate.NetWorkUtil;
import com.basecore.widget.CustomToast;
import com.basecore.widget.viewpager.AutoScrollViewPager;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.tunshu.R;
import com.tunshu.adapter.ImagePagerAdapter;
import com.tunshu.adapter.MyGameAdapter;
import com.tunshu.application.CommonApplication;
import com.tunshu.entity.GameListEntity;
import com.tunshu.entity.InfoEntity;
import com.tunshu.entity.SpecialEntity;
import com.tunshu.http.MyHttpClient;
import com.tunshu.util.Constants;
import com.tunshu.util.LogUtil;
import com.tunshu.view.activity.CommonActivity;
import com.tunshu.view.activity.GameDetailActivity;
import com.tunshu.view.activity.HomePageActivity;
import com.tunshu.view.activity.SpecialActivity;
import com.tunshu.view.activity.WebActivity;
import com.tunshu.widget.SquareLayout;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.service.PushService;


public class MainRecommendFragment extends CommonFragment implements BaseSliderView.OnSliderClickListener {
    private RelativeLayout advLayout;
    private SliderLayout sliderLayout;
    private PullToRefreshListView listView;
    private View header;
    private LinearLayout adLayout;
    private List<GameListEntity> infoEntityList = new ArrayList<>();
    private List<InfoEntity.DataEntity.BannerEntity> bannerEntityList = new ArrayList<>();
    private SimpleDraweeView game1Icon, game2Icon;
    private MyGameAdapter adapter;
    private InfoEntity.DataEntity dataEntity;
    private DownloadStateReceiver downloadStateReceiver = new DownloadStateReceiver();
    private DownloadStateResetReceiver downloadStateResetReceiver = new DownloadStateResetReceiver();
    private AppInstalledReceiver installedReceiver = new AppInstalledReceiver();
    private DownloadInfoReceiver downloadInfoReceiver = new DownloadInfoReceiver();
    private DownloadCountReceiver downloadCountReceiver = new DownloadCountReceiver();
    private int pageNo = 1;
    private int width;

    Build deviceInfo_;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        getData(false);
        addListener();
        getActivity().registerReceiver(downloadStateReceiver, new IntentFilter(Constants.DOWNLOAD_STATE_REFRESH));
        getActivity().registerReceiver(downloadStateResetReceiver, new IntentFilter(Constants.DOWNLOAD_STATE_RESET));
        getActivity().registerReceiver(downloadInfoReceiver, new IntentFilter(Constants.GET_ALL_DOWNLOAD_STATE_RESULT));
        getActivity().registerReceiver(downloadCountReceiver, new IntentFilter(Constants.DOWNLOAD_COUNT_ADD));

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_REMOVED");
        filter.addDataScheme("package");
        getActivity().registerReceiver(installedReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(downloadStateReceiver);
        getActivity().unregisterReceiver(downloadStateResetReceiver);
        getActivity().unregisterReceiver(installedReceiver);
        getActivity().unregisterReceiver(downloadInfoReceiver);
        getActivity().unregisterReceiver(downloadCountReceiver);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_charts_fragment, container, false);//关联布局文件
        findView(rootView);

        deviceInfo_ = new Build();
        return rootView;
    }

    @Override
    public void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        sliderLayout.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!((HomePageActivity) getActivity()).getHomePageSlidingMenu().isMenuShowing()) {
            sliderLayout.startAutoCycle();
        }
    }

    private void findView(View v) {
        listView = (PullToRefreshListView) v.findViewById(R.id.main_pull_refresh_view);
        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        header = getActivity().getLayoutInflater().inflate(R.layout.activity_recommend_advertisement, listView, false);
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        header.setLayoutParams(layoutParams);
        initViews(header);
    }

    private void initViews(View root) {
        sliderLayout = (SliderLayout) root.findViewById(R.id.slider);
        advLayout = (RelativeLayout) root.findViewById(R.id.adv_layout);
        game1Icon = (SimpleDraweeView) root.findViewById(R.id.game1_icon);
        game2Icon = (SimpleDraweeView) root.findViewById(R.id.game2_icon);
        adLayout = (LinearLayout) root.findViewById(R.id.ad_layout);
    }

    private void fillView(InfoEntity.DataEntity dataEntity) {
        if (listView.getRefreshableView().getHeaderViewsCount() > 1) {
            listView.getRefreshableView().removeHeaderView(header);
        }
        LogUtil.getLogger().d(listView.getRefreshableView().getHeaderViewsCount());
        LinearLayout.LayoutParams adLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (width - ScreenUtil.dip2px(15)) * 2 / 7);
        adLayoutParams.setMargins(0, ScreenUtil.dip2px(6), 0, 0);
        adLayout.setLayoutParams(adLayoutParams);
        if (dataEntity.getGame1() != null) {
            game1Icon.setImageURI(Uri.parse(dataEntity.getGame1().getImages()));
        }
        if (dataEntity.getGame2() != null) {
            game2Icon.setImageURI(Uri.parse(dataEntity.getGame2().getImages()));
        }
        if (sliderLayout.getSliderSize()==0) {
            fillAdLayout(dataEntity.getBanner());
        }

        listView.getRefreshableView().addHeaderView(header);
    }

    private void fillAdLayout(List<InfoEntity.DataEntity.BannerEntity> bannerEntityList) {
        if (!ListUtils.isEmpty(bannerEntityList)) {
            sliderLayout.removeAllSliders();
            for (int i = 0, size = bannerEntityList.size(); i < size; i++) {
                TextSliderView textSliderView = new TextSliderView(getActivity());
                // initialize a SliderLayout
                textSliderView
                        .description("")
                        .image(bannerEntityList.get(i).getBannerphone())
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .setOnSliderClickListener(MainRecommendFragment.this);

                //add your extra information
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle().putSerializable("bannerEntity",bannerEntityList.get(i));
                textSliderView.getBundle().putString("gameId", bannerEntityList.get(i).getGameid());
                sliderLayout.addSlider(textSliderView);
            }
            LinearLayout.LayoutParams fLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (width - ScreenUtil.dip2px(10)) * 3 / 7);
            fLayoutParams.setMargins(0, ScreenUtil.dip2px(5), 0, 0);
            advLayout.setLayoutParams(fLayoutParams);
            sliderLayout.setPresetTransformer(SliderLayout.Transformer.Default);
            sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            sliderLayout.setCustomAnimation(new DescriptionAnimation());
            sliderLayout.setDuration(4000);
            advLayout.setVisibility(View.VISIBLE);
        } else {
            advLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        InfoEntity.DataEntity.BannerEntity bannerEntity=(InfoEntity.DataEntity.BannerEntity)slider.getBundle().getSerializable("bannerEntity");
        if (bannerEntity.getType().equals("prefecture")) {
            Intent intent = new Intent(getActivity(), SpecialActivity.class);
            intent.putExtra("preId", bannerEntity.getGameid());
            intent.putExtra("style", bannerEntity.getStyle());
            startActivity(intent);
        } else if (bannerEntity.getType().equals("notice")) {
            Intent intent = new Intent(getActivity(), WebActivity.class);
            intent.putExtra("titleName",bannerEntity.getTitle());
            intent.putExtra("webUrl",bannerEntity.getUrl());
            startActivity(intent);
        }else
        {
            Intent intent = new Intent(getActivity(), GameDetailActivity.class);
            intent.putExtra("gameId", bannerEntity.getGameid());
            startActivity(intent);
        }
    }

    private class DownloadInfoReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("flag").equals("MainRecommendFragment")) {
                infoEntityList = (List<GameListEntity>) intent.getSerializableExtra("downloadInfoList");
                if (!ListUtils.isEmpty(infoEntityList)) {
                    setAdapter(infoEntityList);
                }
            }
        }
    }

    private void setAdapter(List<GameListEntity> infoList) {
        if (adapter != null) {
            adapter.refreshAdapter(infoList);
        } else {
            adapter = new MyGameAdapter(getActivity(), infoList);
            listView.setAdapter(adapter);
        }
    }

    private void addListener() {
        game1Icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataEntity != null && dataEntity.getGame1() != null) {
                    if (dataEntity.getGame1().getType().equals("prefecture")) {
                        Intent intent = new Intent(getActivity(), SpecialActivity.class);
                        intent.putExtra("preId", dataEntity.getGame1().getGameid());
                        intent.putExtra("name", dataEntity.getGame1().getName());
                        intent.putExtra("style", dataEntity.getGame1().getStyle());
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getActivity(), GameDetailActivity.class);
                        intent.putExtra("gameId", dataEntity.getGame1().getGameid());
                        startActivity(intent);
                    }
                }
            }
        });
        game2Icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataEntity != null && dataEntity.getGame2() != null) {
                    if (dataEntity.getGame2().getType().equals("prefecture")) {
                        Intent intent = new Intent(getActivity(), SpecialActivity.class);
                        intent.putExtra("preId", dataEntity.getGame2().getGameid());
                        intent.putExtra("name", dataEntity.getGame2().getName());
                        intent.putExtra("style", dataEntity.getGame2().getStyle());
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getActivity(), GameDetailActivity.class);
                        intent.putExtra("gameId", dataEntity.getGame2().getGameid());
                        startActivity(intent);
                    }
                }
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
        ((HomePageActivity) getActivity()).getHomePageSlidingMenu().setOnOpenListener(new SlidingMenu.OnOpenListener() {
            @Override
            public void onOpen() {
                sliderLayout.stopAutoCycle();
            }
        });
        ((HomePageActivity) getActivity()).getHomePageSlidingMenu().setOnCloseListener(new SlidingMenu.OnCloseListener() {
            @Override
            public void onClose() {
                sliderLayout.startAutoCycle();
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
                setAdapter(infoEntityList);
            }
        }
    }

    private class DownloadStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!ListUtils.isEmpty(infoEntityList)) {
                for (int i = 0, size = infoEntityList.size(); i < size; i++) {
                    long currentTaskId = getCoreApplication().getPreferenceConfig().getLong(new HashCodeFileNameGenerator().generate(infoEntityList.get(i).getDownurl()), -10L);
                    if (currentTaskId < 0) {
                        continue;
                    }
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
                            adapter.updateView(listView, infoEntityList, i, 2);
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
                            setAdapter(infoEntityList);
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
                            setAdapter(infoEntityList);
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
            if (NetWorkUtil.isNetworkConnected(getActivity())) {
                if (NetWorkUtil.isNetworkAvailable(getActivity())) {
                    getData(true);
                } else {
                    listView.onRefreshComplete();
                }
            } else {
                listView.onRefreshComplete();
            }
        }
    }

    private void getData(final boolean isMore) {
        if (!isMore) {
            showProgress();
        }
        RequestParams params = getCommonParams();
        params.put("ac", "recommend");
        params.put("pagenum", String.valueOf(pageNo));
        params.put("pagesize", Constants.PAGESIZE);
        MyHttpClient.get(getActivity(), "mobileIndex/index", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                listView.onRefreshComplete();
                hideProgress();
                try {
                    LogUtil.getLogger().d(response.toString());
                    InfoEntity entity = com.alibaba.fastjson.JSONObject.parseObject(response.toString(), InfoEntity.class);
                    if (entity.getCode().equals("1001")) {

                        if (isMore) {
                            // 加载更多时只会返回游戏列表，不返回banner的消息
                            InfoEntity.DataEntity dataEntityTmp;
                            dataEntityTmp = entity.getData();

                            dataEntity.setGameList(dataEntityTmp.getGameList());
                        }else{
                            dataEntity = entity.getData();
                        }

                        if (dataEntity != null) {
                            if (!ListUtils.isEmpty(dataEntity.getGameList())) {
                                pageNo++;
                            }
                            if (isMore) {
                                if (ListUtils.isEmpty(dataEntity.getGameList())) {
                                    GameListEntity gameListEntity=new GameListEntity();
                                    gameListEntity.setType("end");
                                    infoEntityList.add(gameListEntity);
                                    listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                } else {
                                    infoEntityList.addAll(dataEntity.getGameList());
                                }
                            } else {
                                listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                fillView(dataEntity);
                                infoEntityList = dataEntity.getGameList();
                            }
                            if (!ListUtils.isEmpty(infoEntityList)) {
                                Intent intent = new Intent(Constants.GET_ALL_DOWNLOAD_STATE);
                                intent.putExtra("downloadInfoList", (Serializable) infoEntityList);
                                intent.putExtra("flag", "MainRecommendFragment");
                                getActivity().sendBroadcast(intent);
//                                setAdapter(infoEntityList);
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
            }
        });


    }

    public class DownloadCountReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String gameId = intent.getStringExtra("gameId") == null ? "" : intent.getStringExtra("gameId");
            if (!StringUtils.isEmpty(gameId)) {
                addDownloadCount(gameId);
            }
        }
    }


    private void addDownloadCount(String gameId) {
        RequestParams params = getCommonParams();
        params.put("ac", "downcount");
        params.put("gameid", gameId);

         String devinfo = deviceInfo_.MODEL + ","+ deviceInfo_.ID + "," + deviceInfo_.DEVICE +  "," + deviceInfo_.PRODUCT;
        params.put("devinfo",devinfo);
        params.put("serial",deviceInfo_.SERIAL);

        MyHttpClient.get(getActivity(), "appIndex/index", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    com.basecore.util.log.LogUtil.getLogger().d(response.toString());
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

}
