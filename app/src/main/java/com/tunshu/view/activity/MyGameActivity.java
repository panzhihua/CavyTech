package com.tunshu.view.activity;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.net.Uri;

import com.basecore.util.core.FileInfoUtils;
import com.basecore.util.core.ListUtils;
import com.basecore.util.core.StringUtils;
import com.basecore.util.netstate.NetWorkUtil;
import com.basecore.widget.CustomToast;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.tunshu.R;
import com.tunshu.application.CommonApplication;
import com.tunshu.entity.AppEntity;
import com.tunshu.entity.GameClassesListEntity;
import com.tunshu.entity.GameListEntity;
import com.tunshu.entity.TopListEntity;
import com.tunshu.http.MyHttpClient;
import com.tunshu.util.Constants;
import com.tunshu.util.LogUtil;
import com.tunshu.widget.RoundedDrawable;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ShadowNight on 2015/7/21.
 */
public class MyGameActivity extends CommonActivity {

    private ListView listView;
    private TextView emptyText;
    private List<AppEntity> appList = new ArrayList<AppEntity>();
    private MyAdapter adapter;
    private AppInstalledReceiver installedReceiver = new AppInstalledReceiver();
    private DownloadStateReceiver downloadStateReceiver = new DownloadStateReceiver();

    private List<GameListEntity> infoEntityList = new ArrayList<>();

    private String appPackname = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_manager_list);
        initView();
        getData();
      //  fillView();
        addListener();
        registerRec();
    }

    private void registerRec(){
        registerReceiver(downloadStateReceiver, new IntentFilter(Constants.DOWNLOAD_STATE_REFRESH));
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_REMOVED");
        filter.addDataScheme("package");
        registerReceiver(installedReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(installedReceiver);
        unregisterReceiver(downloadStateReceiver);
    }

    private void initView() {
        findTitle();
        listView = (ListView) findViewById(R.id.example_lv_list);
        emptyText = (TextView) findViewById(R.id.empty_text);
        title.setText(getString(R.string.my_game));

        appList = getAppList();
    }

    private void fillView() {
        if (!ListUtils.isEmpty(appList)) {
            fillAdapter(appList);
            emptyText.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }else{
            emptyText.setText(getString(R.string.no_my_game));
            emptyText.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
    }

    private void addListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void fillAdapter(List<AppEntity> list) {
        if (adapter != null) {
            adapter.refreshAdapter(list);
        } else {
            adapter = new MyAdapter(MyGameActivity.this, list, infoEntityList);
            listView.setAdapter(adapter);
        }
    }

    public List<AppEntity> getAppList() {
        appList.clear();
        // 获取手机已安装的所有应用package的信息(其中包括用户自己安装的，还有系统自带的)
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        for (int i = 0, size = packages.size(); i < size; i++) {
            PackageInfo packageInfo = packages.get(i);
            // 如果属于非系统程序，则添加到列表显示
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && packageInfo.packageName.contains("cavy")) {
                AppEntity appEntity = new AppEntity();
                appEntity.setAppIcon(packageInfo.applicationInfo.loadIcon(getPackageManager()));
                appEntity.setAppName(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
                LogUtil.getLogger().d(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
                appEntity.setPackageName(packageInfo.packageName);
                appEntity.setVersionCode(packageInfo.versionCode);
                appEntity.setVersionName(packageInfo.versionName);

                if(appPackname.isEmpty()){
                    appPackname = packageInfo.packageName;
                }else{
                    appPackname += "," + packageInfo.packageName;
                }
                appList.add(appEntity);
            }
        }
        return appList;
    }
    public void setActionText(){
        for (int i = 0; i < appList.size(); i++){

            GameListEntity gameinfo = getGameentity(appList.get(i).getPackageName());

            if(isNeedUpdate(appList.get(i).getPackageName(), appList.get(i).getVersionCode())){
                // 如果本地已下载版本和服务器版本一致 且比已安装版本高 则显示安装
                String archiveFilePath = Environment.getExternalStoragePublicDirectory("download").getAbsolutePath() + File.separator +
                        "CavyTech"+ File.separator + appList.get(i).getAppName() + ".apk";//安装包路径

                PackageManager pm = getPackageManager();
                PackageInfo info = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES);

                if(info != null){
                    int verionNo = Integer.parseInt(gameinfo.getVersion());
                    if(info.versionCode == verionNo && info.versionCode > appList.get(i).versionCode){
                        gameinfo.setActionText(getString(R.string.install));
                    }else{
                        gameinfo.setActionText(getString(R.string.uninstall));
                    }
                }else{
                    gameinfo.setActionText(getString(R.string.uninstall));
                 }
            }else{
                gameinfo.setActionText(getString(R.string.uninstall));
            }
        }
    }

    // 获取已下载本地版本
    public int getLoacationVersio(){

        return 0;
    }
    public boolean isNeedUpdate(String packName, int versioCode){

        if(infoEntityList == null){
            return false;
        }
        for (int i = 0; i < infoEntityList.size(); i++){
            if(infoEntityList.get(i).getPackpagename().equals(packName)){

                int verionNo = Integer.parseInt(infoEntityList.get(i).getVersion());
                if(versioCode < verionNo){
                    return true;
                }else{
                    return false;
                }
            }
        }
        return false;
    }
    private void getData() {
        if(appPackname.isEmpty()){
            fillView();
            return;
        }
        RequestParams params = getCommonParams();
        params.put("ac", "getdownloadgameinfo");
        params.put("packagename", appPackname);

        MyHttpClient.get(MyGameActivity.this, Constants.HTTP_PRE_APPINDEX, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    TopListEntity entity = com.alibaba.fastjson.JSONObject.parseObject(response.toString(), TopListEntity.class);
                    if (entity.getCode().equals("1001")) {
                        if (entity.getData() != null) {
                           // typeList = entity.getData();
                          //  fillAdapter(typeList);
                            infoEntityList = entity.getData().getGameList();

                            setActionText();
                            fillView();
                        }
                    } else {
                        fillView();
                        CustomToast.showToast(MyGameActivity.this, entity.getMsg());
                    }
                } catch (Exception e) {
                    fillView();
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                fillView();
            }
        });
    }

    private GameListEntity getGameentity(String packName){
        for(int i = 0; i < infoEntityList.size(); i++){
            if(infoEntityList.get(i).getPackpagename().equals(packName)){
                return infoEntityList.get(i);
            }
        }

        return null;
    }

    private int getTaskId(String downurl){
        int currentTaskId = (int)getCoreApplication().getPreferenceConfig().getLong(new HashCodeFileNameGenerator().generate(downurl), -10L);

        return currentTaskId;
    }

    private class DownloadStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!ListUtils.isEmpty(appList)) {
                for (int i = 0, size = appList.size(); i < size; i++) {
                    GameListEntity gameinfo = getGameentity(appList.get(i).getPackageName());
                    long currentTaskId = getTaskId(gameinfo.getDownurl());
                    if (currentTaskId < 0) {
                        continue;
                    }
                    if (currentTaskId == intent.getLongExtra("taskId", -1L)) {
                          String state = intent.getStringExtra("state");
                        switch (state) {
                            case Constants.DOWNLOAD_WAIT:
                                gameinfo.setActionText(getString(R.string.pause));
                                gameinfo.setActionTag(Constants.WATTING_TAG);
                                gameinfo.setPercent(String.valueOf((int) intent.getDoubleExtra("percent", 0)));
                                gameinfo.setDownloadedLength(intent.getLongExtra("downloadedLength", 0L));
                                break;
                            case Constants.DOWNLOAD_PROCESS:
                                gameinfo.setActionText((int) intent.getDoubleExtra("percent", 0) + "%");
                                gameinfo.setActionTag(getString(R.string.pause));
                                gameinfo.setPercent(String.valueOf((int) intent.getDoubleExtra("percent", 0)));
                                gameinfo.setDownloadedLength(intent.getLongExtra("downloadedLength", 0L));
                                break;
                            case Constants.DOWNLOAD_FINISHED:
                                break;
                            case Constants.DOWNLOAD_COMPLETED:
                                gameinfo.setActionText(getString(R.string.install));
                                break;
                            case Constants.DOWNLOAD_PAUSED:
                                gameinfo.setActionText(getString(R.string.go_on));
                                break;
                            case Constants.DOWNLOAD_REBUILD_FINISHED:
                                break;
                            case Constants.DOWNLOAD_CONNECTION_LOST:
                                gameinfo.setActionText(getString(R.string.try_again));
                                break;
                            default:
                                break;
                        }
                        if (adapter != null) {
                            adapter.updateView(listView, appList, i);
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
                fillView();

            }
            // 接收卸载广播
            if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
                String packageName = intent.getDataString().replace("package:", "");
                LogUtil.getLogger().d(packageName);
                appList = getAppList();
                fillView();
            }
        }
    }

    public class MyAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater mInflater;
        private String emptyInfo;
        private List<AppEntity> appList;
        private List<GameListEntity> gameInfoEntityList = null;

        public MyAdapter(Context context, List<AppEntity> appList, List<GameListEntity> gameInfoEntityList) {
            this.mInflater = LayoutInflater.from(context);
            this.context = context;
            this.appList = appList;
            this.gameInfoEntityList = gameInfoEntityList;
        }

        public void refreshAdapter(List<AppEntity> appList) {
            this.appList = appList;
            notifyDataSetChanged();
        }

        @Override
        public Object getItem(int position) {
            return appList.get(position);
        }

        @Override
        public int getCount() {
            return appList.size();
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public String getPackageName(int position) {
            return appList.get(position).getPackageName();
        }

        public String getActionText(String packName){

            for(int i = 0; i < gameInfoEntityList.size(); i++){
                if(gameInfoEntityList.get(i).getPackpagename().equals(packName)){
                    return gameInfoEntityList.get(i).getActionText();
                }
            }

            return getString(R.string.uninstall);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.activity_my_app_list_item, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.appdownload.setText(getActionText(appList.get(position).getPackageName()));

            holder.appdownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GameListEntity gameinfo = getGameentity(appList.get(position).getPackageName());

                    if (((TextView) view).getText().equals(getString(R.string.pause))) {
                        setActionTextEnable(view);
                        Intent intent = new Intent(Constants.DOWNLOAD_STATE_TO_PAUSE);
                        intent.putExtra("taskToken", getTaskId(gameinfo.getDownurl()));
                        sendBroadcast(intent);
                    } else if (((TextView) view).getText().equals(getString(R.string.download_manager)) ||
                            ((TextView) view).getText().equals(getString(R.string.try_again)) ||
                            ((TextView) view).getText().equals(getString(R.string.go_on)) ||
                            ((TextView) view).getText().equals(getString(R.string.update))) {
                        setActionTextEnable(view);
                        checkWifiDown(false, view, gameinfo.getGamename(), gameinfo.getDownurl(), "", gameinfo);
                    } else if (((TextView) view).getText().equals(getString(R.string.install))) {
                        try {
                            ((CommonApplication) getCoreApplication()).appInstall(getTaskId(gameinfo.getDownurl()));
                        } catch (ActivityNotFoundException e) {
                            CustomToast.showToast(MyGameActivity.this, getString(R.string.install_failed));
                            e.printStackTrace();
                        }
                    } else if (((TextView) view).getText().equals(getString(R.string.uninstall))) {
                        uninstall(gameinfo.getPackpagename());
                    }else {
                        setActionTextEnable(view);
                        Intent intent = new Intent(Constants.DOWNLOAD_STATE_TO_PAUSE);
                        intent.putExtra("taskToken", getTaskId(gameinfo.getDownurl()));
                        sendBroadcast(intent);
                    }
/*
                    if (((TextView) view).getText().equals(context.getString(R.string.download_manager)) || ((TextView) view).getText().equals(context.getString(R.string.update))) {
                        setActionTextEnable(view);
                        checkWifiDown(false, view, gameinfo.getGamename(), gameinfo.getDownurl(), "", gameinfo);
                    } else if (((TextView) view).getText().equals(context.getString(R.string.go_on)) || ((TextView) view).getText().equals(context.getString(R.string.try_again))) {
                        setActionTextEnable(view);
                        checkWifiDown(false, view, gameinfo.getGamename(), gameinfo.getDownurl(), "", gameinfo);
                    } else if (((TextView) view).getText().equals(context.getString(R.string.install))) {
                        setActionTextEnable(view);
                        try {
                            ((CommonApplication) context.getApplicationContext()).appInstall(getTaskToken(position));
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else if (((TextView) view).getText().equals(context.getString(R.string.open))) {
                        setActionTextEnable(view);
                        try {
                            ((CommonApplication) context.getApplicationContext()).openApp(getPackageName(position));
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else if (((TextView) view).getText().equals(context.getResources().getString(R.string.pause))) {
                        setActionTextEnable(view);
                        Intent intent = new Intent(Constants.DOWNLOAD_STATE_TO_PAUSE);
                        intent.putExtra("taskToken", getTaskToken(position));
                        context.sendBroadcast(intent);
                    } else {
                        setActionTextEnable(view);
                        Intent intent = new Intent(Constants.DOWNLOAD_STATE_TO_PAUSE);
                        intent.putExtra("taskToken", getTaskToken(position));
                        context.sendBroadcast(intent);
                    }*/
                }
            });

            holder.appimg.setImageDrawable(appList.get(position).getAppIcon());
            holder.appname.setText(appList.get(position).getAppName());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        ((CommonApplication) getCoreApplication()).openApp(getPackageName(position));
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
            return convertView;
        }

        public void setActionTextEnable(View v){
            if (NetWorkUtil.isNetworkConnected(context)) {
                v.setEnabled(false);
            }else{
                v.setEnabled(true);
            }
        }
        private void uninstall(String packagename){
            String packname = "package:" + packagename;
            Uri packageURI = Uri.parse(packname);
            //创建Intent意图
            Intent intent = new Intent(Intent.ACTION_DELETE,packageURI);
            //执行卸载程序
            startActivity(intent);
        }
        public void updateView(ListView listView, List<AppEntity> appList, int position) {
            this.appList = appList;
            int firstVisiblePosition = listView.getFirstVisiblePosition();
            int lastVisiblePosition = listView.getLastVisiblePosition();
            int offset = position - firstVisiblePosition;

            if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
                View view = listView.getChildAt(offset);
                if (view == null) {
                    return;
                }
                LogUtil.getLogger().d(view.getRootView().getTag());
                ViewHolder holder = (ViewHolder) view.getTag();
                holder.appdownload.setText(getActionText(appList.get(position).getPackageName()));

                holder.appdownload.setEnabled(true);
                if (holder.appdownload.getText().equals(getString(R.string.go_on))) {
                    holder.appdownload.setBackgroundResource(R.drawable.down_button_white);
                    holder.appdownload.setTextAppearance(context, R.style.download_button_text_0_1);
                } else if (holder.appdownload.getText().equals(getString(R.string.install))) {
                    holder.appdownload.setBackgroundResource(R.drawable.down_button_blue);
                    holder.appdownload.setTextAppearance(context, R.style.download_button_text_0_3);
                } else if (holder.appdownload.getText().equals(getString(R.string.try_again))) {
                    holder.appdownload.setBackgroundResource(R.drawable.down_pause);
                    holder.appdownload.setTextAppearance(context, R.style.download_button_text_0_2);
                } else if (holder.appdownload.getText().equals(getString(R.string.pause))) {
                    holder.appdownload.setBackgroundResource(R.drawable.down_pause);
                    holder.appdownload.setTextAppearance(context, R.style.download_button_text_0_2);
                }else {
                    holder.appdownload.setTextAppearance(context, R.style.download_button_text_0_1);
                    holder.appdownload.setBackgroundResource(R.drawable.down_button_white);
                }
            }
        }

        public class ViewHolder {
            public final ImageView appimg;
            public final TextView appname;
            public final TextView appdownload;
            public final View root;

            public ViewHolder(View root) {
                appimg = (ImageView) root.findViewById(R.id.app_img);
                appname = (TextView) root.findViewById(R.id.app_name);
                appdownload = (TextView) root.findViewById(R.id.app_download);
                this.root = root;
            }
        }

        public Bitmap drawable2Bitmap(Drawable drawable) {
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            } else if (drawable instanceof NinePatchDrawable) {
                Bitmap bitmap = Bitmap
                        .createBitmap(
                                drawable.getIntrinsicWidth(),
                                drawable.getIntrinsicHeight(),
                                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                        : Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight());
                drawable.draw(canvas);
                return bitmap;
            } else {
                return null;
            }
        }

        public void saveMyBitmap(String bitName, Bitmap mBitmap) {
            File f = new File("/sdcard/" + bitName + ".png");
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            try {
                fOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
