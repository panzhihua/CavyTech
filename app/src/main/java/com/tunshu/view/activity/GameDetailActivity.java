package com.tunshu.view.activity;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
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
import com.basecore.widget.CustomToast;
import com.basecore.widget.dialog.Effectstype;
import com.basecore.widget.dialog.NiftyDialogBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.golshadi.majid.core.enums.TaskStates;
import com.golshadi.majid.report.ReportStructure;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.tunshu.R;
import com.tunshu.adapter.EmptyAdapter;
import com.tunshu.application.CommonApplication;
import com.tunshu.entity.CommentEntity;
import com.tunshu.entity.CommonEntity;
import com.tunshu.entity.GameDetailEntity;
import com.tunshu.entity.GameListEntity;
import com.tunshu.entity.ImageItemEntity;
import com.tunshu.http.MyHttpClient;
import com.tunshu.util.Constants;
import com.tunshu.util.LogUtil;
import com.tunshu.widget.ExpandableTextView;
import com.tunshu.widget.ShareDialog;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.CustomPlatform;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

/**
 * 作者：yzb on 2015/7/9 11:21
 */
public class GameDetailActivity extends CommonActivity {

    private SimpleDraweeView gameicon;
    private ImageView gamescreenshothor;
    private TextView gametitle;
    private TextView gamesize;
    private TextView gameinfo;
    private RecyclerView hzlistView;
    private TextView gamecompany;
    private TextView gametype;
    private TextView gameupdatetime;
    private TextView gameversionnum;
    private TextView gamecomment;
    private TextView gamedesc;
    private TextView download;
    private LinearLayout downloadLayout;
    private LinearLayout gamescreenlayout;
    //    private ExpandableTextView expandableTextView;
    private View header;
    private PullToRefreshListView listView;
    private String gameId;
    private int pageNo = 1;
    private int height;
    private int taskToken = -1;
    private GameDetailEntity entity;
    private List<CommentEntity.DataEntity> commentList = new ArrayList<>();
    private MyAdapter myAdapter;
    private DownloadStateReceiver downloadStateReceiver = new DownloadStateReceiver();
    private AppInstalledReceiver installedReceiver = new AppInstalledReceiver();
    private GetDownloadStateTextReceiver getDownloadStateTextReceiver = new GetDownloadStateTextReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamedetail);
        gameId = getIntent().getStringExtra("gameId") == null ? "" : getIntent().getStringExtra("gameId");
        findView();
        getDetail(gameId);
        getComment(gameId, false);
        addListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(downloadStateReceiver, new IntentFilter(Constants.DOWNLOAD_STATE_REFRESH));
        registerReceiver(getDownloadStateTextReceiver, new IntentFilter(Constants.GET_SINGLE_DOWNLOAD_STATE_RESULT));
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
        unregisterReceiver(getDownloadStateTextReceiver);
    }

    private void findView() {
        findTitle();
        listView = (PullToRefreshListView) findViewById(R.id.refresh_view);
        listView.setVisibility(View.INVISIBLE);
        download = (TextView) findViewById(R.id.download);
        downloadLayout = (LinearLayout) findViewById(R.id.bootom_layout);
        header = getLayoutInflater().inflate(R.layout.activity_gamedetail_header, listView, false);
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        header.setLayoutParams(layoutParams);
        initView(header);
        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        go.setVisibility(View.VISIBLE);
        go.setImageResource(R.drawable.icon_share);
        title.setText(getString(R.string.app_detail));
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
         height = wm.getDefaultDisplay().getWidth() - ScreenUtil.dip2px(40);
    }

    private void initView(View rootView) {
        gameicon = (SimpleDraweeView) rootView.findViewById(R.id.game_icon);
        gametitle = (TextView) rootView.findViewById(R.id.game_title);
        gamesize = (TextView) rootView.findViewById(R.id.game_size);
        gameinfo = (TextView) rootView.findViewById(R.id.game_info);
        hzlistView = (RecyclerView) rootView.findViewById(R.id.hz_listView);
        gamecompany = (TextView) rootView.findViewById(R.id.game_company);
        gametype = (TextView) rootView.findViewById(R.id.game_type);
        gameupdatetime = (TextView) rootView.findViewById(R.id.game_update_time);
        gameversionnum = (TextView) rootView.findViewById(R.id.game_version_num);
//        expandableTextView = (ExpandableTextView) rootView.findViewById(R.id.expand_text_view);
        gamedesc = (TextView) rootView.findViewById(R.id.game_desc);
        gamescreenshothor = (ImageView) rootView.findViewById(R.id.item_image);
        gamescreenlayout=(LinearLayout)rootView.findViewById(R.id.screenshot_layout);
        gamecomment = (TextView) rootView.findViewById(R.id.game_comment);
    }

    private void fillHeader(GameDetailEntity entity) {
        if (listView.getRefreshableView().getHeaderViewsCount() > 0) {
            listView.getRefreshableView().removeHeaderView(header);
        }
        gameicon.setImageURI(Uri.parse(entity.getData().getIcon()));
        gametitle.setText(entity.getData().getGamename());
        //应用详情需要修改一下，下面的描述性文字不要了，直接把多少M和下载次数分成两行显示
        gamesize.setText(entity.getData().getFilesize() + "M"/*/" + entity.getData().getDowncount() + getString(R.string.counts_down)*/);
        gameinfo.setText(entity.getData().getGamedesc());

//        gamesize.setText(entity.getData().getFilesize() + "M");
//        gameinfo.setText(getString(R.string.download_count)+entity.getData().getDowncount());
        //屏幕截图
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        hzlistView.setLayoutManager(layoutManager);
        if (!ListUtils.isEmpty(entity.getData().getViewimage())) {//横屏屏幕截图
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height * 485 / 970);
            layoutParams.setMargins(ScreenUtil.dip2px(20), ScreenUtil.dip2px(6), ScreenUtil.dip2px(20), 0);
            gamescreenlayout.setLayoutParams(layoutParams);
            gamescreenlayout.setVisibility(View.VISIBLE);
            hzlistView.setVisibility(View.GONE);
            gamescreenshothor.setImageURI(Uri.parse(entity.getData().getViewimage().get(0).getBigimage()));
        } else {
            gamescreenlayout.setVisibility(View.GONE);
            hzlistView.setVisibility(View.VISIBLE);
            if (!ListUtils.isEmpty(entity.getData().getVerimage())) {//竖屏屏幕截图
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ScreenUtil.dip2px(260));
                layoutParams.setMargins(ScreenUtil.dip2px(12), ScreenUtil.dip2px(6), ScreenUtil.dip2px(12), 0);
                hzlistView.setLayoutParams(layoutParams);
                hzlistView.setAdapter(new MyHorizonAdapter(entity.getData().getVerimage(), false));
            }
        }
        //游戏信息
        gamecompany.setText(entity.getData().getDevelopers());
        gametype.setText(entity.getData().getGametype());
        gameupdatetime.setText(entity.getData().getUpdateTime());
        gameversionnum.setText(entity.getData().getViewVersion());
//        ((ExpandableTextView) expandableTextView.findViewById(R.id.expand_text_view)).setText(Html.fromHtml(entity.getData().getGamedetial()));
        gamedesc.setText(Html.fromHtml(entity.getData().getGamedetial()));

        listView.getRefreshableView().addHeaderView(header);
        if (myAdapter == null) {
            listView.getRefreshableView().setAdapter(new EmptyAdapter(this, getString(R.string.comment_empty)));
        }

        try {
            downloadLayout.setVisibility(View.VISIBLE);
            checkApp(download, entity.getData().getPageName(), Integer.parseInt(entity.getData().getVersion()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        listView.setVisibility(View.VISIBLE);

        setThisUpdate(entity);
    }

    private void setThisUpdate(GameDetailEntity entity){

        TextView tvTitle = (TextView)findViewById(R.id.this_update_title);
        LinearLayout thisupdatecontent = (LinearLayout)findViewById(R.id.this_update_content);
        
        if(!entity.getData().getSituation().equals("")){
            TextView tvTime      = (TextView)findViewById(R.id.this_update_time);
            TextView tvSituation = (TextView)findViewById(R.id.this_update_situation);

            tvTime.setText(entity.getData().getUpdateTime());
            tvSituation.setText(Html.fromHtml(entity.getData().getSituation()));
        }else{

            tvTitle.setVisibility(View.GONE);
            thisupdatecontent.setVisibility(View.GONE);
        }
    }
    private void checkApp(TextView appInstall, String packageName, int versionCode) {
        PackageInfo packageInfo;
        try {
            packageInfo = getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
        }
        if (packageInfo == null) {
            taskToken = (int) getCoreApplication().getPreferenceConfig().getLong(new HashCodeFileNameGenerator().generate(entity.getData().getDownurl()), -10L);
            File file = new File(getCoreApplication().getPreferenceConfig().getString(String.valueOf(taskToken), ""));
            if (file.isFile()) {
                appInstall.setText(getString(R.string.install));
            } else {
                if (StringUtils.isEmpty(entity.getData().getDownurl())) {
                    appInstall.setText(getString(R.string.download_manager));
                } else {
                    Intent intent = new Intent(Constants.GET_SINGLE_DOWNLOAD_STATE);
                    intent.putExtra("downloadUrl", entity.getData().getDownurl());
                    intent.putExtra("packageName", packageName);
                    intent.putExtra("versionNo", String.valueOf(versionCode));
                    sendBroadcast(intent);
                }
            }
        } else {
            int version = packageInfo.versionCode;
            if (version < versionCode) {
                taskToken = (int) getCoreApplication().getPreferenceConfig().getLong(new HashCodeFileNameGenerator().generate(entity.getData().getDownurl()), -10L);
                File file = new File(getCoreApplication().getPreferenceConfig().getString(String.valueOf(taskToken), ""));
                if (file.isFile()) {
                    appInstall.setText(getString(R.string.install));
                }else{
                    appInstall.setText(getString(R.string.update));
                    Intent intent = new Intent(Constants.GET_SINGLE_DOWNLOAD_STATE);
                    intent.putExtra("downloadUrl", entity.getData().getDownurl());
                    intent.putExtra("packageName", packageName);
                    intent.putExtra("versionNo", String.valueOf(versionCode));
                    sendBroadcast(intent);
                }
            } else {
                appInstall.setText(getString(R.string.open));
            }
        }
    }

    private void fillAdapter(List<CommentEntity.DataEntity> commentList) {
        if (myAdapter != null) {
            myAdapter.refreshAdapter(commentList);
        } else {
            myAdapter = new MyAdapter(this, commentList);
            listView.setAdapter(myAdapter);
        }
    }

    private void addListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShareDialog();
            }
        });
        gamecomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentDialog();
            }
        });
        gamescreenshothor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(GameDetailActivity.this, PhotoShowActivity.class);
                    intent.putExtra("imageList", (Serializable) entity.getData().getViewimage());
                    intent.putExtra("position", 0);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appName = entity.getData().getGamename() == null ? "" : entity.getData().getGamename();
                String downloadUrl = entity.getData().getDownurl() == null ? "" : entity.getData().getDownurl();
                String gameId = entity.getData().getGameid() == null ? "" : entity.getData().getGameid();
                LogUtil.getLogger().d(downloadUrl);
                LogUtil.getLogger().d(new HashCodeFileNameGenerator().generate(downloadUrl));
                if (download.getText().equals(getString(R.string.download_manager)) || download.getText().equals(getString(R.string.update))) {
                    if (!StringUtils.isEmpty(appName) && !StringUtils.isEmpty(downloadUrl)) {
                        download.setEnabled(false);
                        checkWifiDown(true, download, appName, downloadUrl, gameId, saveDownloadInfo());
                    }
                } else if (download.getText().equals(getString(R.string.go_on)) || download.getText().equals(getString(R.string.try_again))) {
                    download.setEnabled(false);
                    checkWifiDown(false, download, appName, downloadUrl, gameId, saveDownloadInfo());
                } else if (download.getText().equals(getString(R.string.install))) {
                    download.setEnabled(false);
                    taskToken = (int) getCoreApplication().getPreferenceConfig().getLong(new HashCodeFileNameGenerator().generate(downloadUrl), -10L);
                    try {
                        ((CommonApplication) getCoreApplication()).appInstall(taskToken);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                } else if (download.getText().equals(getString(R.string.open))) {
                    download.setEnabled(false);
                    try {
                        ((CommonApplication) getCoreApplication()).openApp(entity.getData().getPageName());
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    download.setEnabled(false);
                    taskToken = (int) getCoreApplication().getPreferenceConfig().getLong(new HashCodeFileNameGenerator().generate(downloadUrl), -10L);
                    Intent intent = new Intent(Constants.DOWNLOAD_STATE_TO_PAUSE);
                    intent.putExtra("taskToken", taskToken);
                    sendBroadcast(intent);
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
    }

    private class GetDownloadStateTextReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String actionText = intent.getStringExtra("actionText") == null ? "" : intent.getStringExtra("actionText");
            if (!StringUtils.isEmpty(actionText)) {
                download.setText(actionText);
                download.setEnabled(true);
            }
        }
    }

    private class DownloadStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String downUrl;
            if (entity != null) {
                downUrl = StringUtils.isEmpty(entity.getData().getDownurl()) ? "" : entity.getData().getDownurl();
            } else {
                return;
            }
            long currentTaskId = getCoreApplication().getPreferenceConfig().getLong(new HashCodeFileNameGenerator().generate(downUrl), -10L);
            if (currentTaskId == intent.getLongExtra("taskId", -1)) {
                download.setEnabled(true);
                String state = intent.getStringExtra("state");
                switch (state) {
                    case Constants.DOWNLOAD_STARTED:
                        download.setText(getString(R.string.download_manager));
                        //取消下载任务重新变为下载，将下载taskToken置位
                        getCoreApplication().getPreferenceConfig().setLong(new HashCodeFileNameGenerator().generate(downUrl), -10L);
                        break;
                    case Constants.DOWNLOAD_PROCESS:
                        download.setText((int) intent.getDoubleExtra("percent", 0) + "%");
                        break;
                    case Constants.DOWNLOAD_FINISHED:
                        break;
                    case Constants.DOWNLOAD_COMPLETED:
                        download.setText(getString(R.string.install));
                        break;
                    case Constants.DOWNLOAD_PAUSED:
                        download.setText(getString(R.string.go_on));
                        break;
                    case Constants.DOWNLOAD_REBUILD_FINISHED:
                        break;
                    case Constants.DOWNLOAD_CONNECTION_LOST:
                        download.setText(getString(R.string.try_again));
                        break;
                    default:
                        break;
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
                download.setText(getString(R.string.open));
                if (getCoreApplication().getPreferenceConfig().getBoolean(Constants.SETTING_DELETE_INSTALL_PACKAGE, true)) {//删除安装包
                    File file = new File(getCoreApplication().getPreferenceConfig().getString(String.valueOf(taskToken), ""));
                    FileUtils.DeleteFile(file);
                }
                deleteDownloadInfo(packageName);
            }
            // 接收卸载广播
            if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
                String packageName = intent.getDataString().replace("package:", "");
                LogUtil.getLogger().d(packageName);
                download.setText(getString(R.string.download_manager));
            }
        }
    }

    private GameListEntity saveDownloadInfo() {
        GameListEntity item = new GameListEntity();
        item.setGamename(entity.getData().getGamename());
        item.setDownurl(entity.getData().getDownurl());
        item.setFilesize(entity.getData().getFilesize());
        item.setGameid(entity.getData().getGameid());
        item.setIcon(entity.getData().getIcon());
        item.setPackpagename(entity.getData().getPageName());
        item.setVersion(entity.getData().getVersion());
        return item;
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
            pageNo = 1;
            getComment(gameId, false);
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
            getComment(gameId, true);
        }
    }

    private void showCommentDialog() {
        final LayoutInflater inflater = LayoutInflater.from(GameDetailActivity.this);
        View layout = inflater.inflate(R.layout.activity_gamedetail_comment, null);
        final EditText commentInput = (EditText) layout.findViewById(R.id.comment_input);
        commentInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(GameDetailActivity.this);
        dialogBuilder.withTitle(null).withTitleColor("#ffffff").withMessage(null).isCancelableOnTouchOutside(true).isCancelable(true).withDuration(300).withEffect(Effectstype.Fadein)
                .setCustomView(layout, GameDetailActivity.this).withButton1Text(getString(R.string.cancel)).withButton2Text(getString(R.string.ok)).setButton1Click(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeInputMethod();
                dialogBuilder.dismiss();
            }
        }).setButton2Click(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = commentInput.getText().toString().replace(" ", "");
                if (StringUtils.count(comment) > 2 && StringUtils.count(comment) < 201) {
                    sendComment(gameId, comment);
                    dialogBuilder.dismiss();
                } else {
                    CustomToast.showToast(GameDetailActivity.this, getString(R.string.comment_length_tips));
                }
            }
        }).show();
    }
    public void closeInputMethod() {
        try {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class MyAdapter extends BaseAdapter {
        private static final int TYPE_GAME = 0;
        private static final int TYPE_END = 1;
        private LayoutInflater mInflater;
        List<CommentEntity.DataEntity> list;

        public MyAdapter(Context context, List<CommentEntity.DataEntity> list) {
            context.setTheme(R.style.Special_Theme_0);
            this.list = list;
            this.mInflater = LayoutInflater.from(context);
        }

        public void refreshAdapter(List<CommentEntity.DataEntity> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            if (getType(position).equals("end")) {
                return TYPE_END;
            } else {
                return TYPE_GAME;
            }
        }
        public String getType(int position) {
            return list.get(position).getType() == null ? "" : list.get(position).getType();
        }

        public String getUserName(int position) {
            return list.get(position).getNickname() == null ? "" : list.get(position).getNickname();
        }

        public String getCommentTime(int position) {
            return list.get(position).getCom_datetime() == null ? "" : list.get(position).getCom_datetime();
        }

        public String getCommentContent(int position) {
            return list.get(position).getCom_content() == null ? "" : list.get(position).getCom_content();
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                if (getItemViewType(position) == TYPE_END) {
                    convertView = mInflater.inflate(R.layout.public_listview_end, parent, false);
                } else {
                    convertView = mInflater.inflate(R.layout.activity_item_comment, parent, false);
                }
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if(getItemViewType(position)==TYPE_GAME) {
                holder.userimage.setImageURI(Uri.parse(list.get(position).getAvatar()));
                holder.username.setText(getUserName(position));
                holder.commenttime.setText(getCommentTime(position));
                holder.commentcontent.setText(getCommentContent(position));
            }
            return convertView;
        }

        public class ViewHolder {
            public final SimpleDraweeView userimage;
            public final TextView username;
            public final TextView commenttime;
            public final TextView commentcontent;
            public final View root;

            public ViewHolder(View root) {
                userimage = (SimpleDraweeView) root.findViewById(R.id.user_image);
                username = (TextView) root.findViewById(R.id.user_name);
                commenttime = (TextView) root.findViewById(R.id.comment_time);
                commentcontent = (TextView) root.findViewById(R.id.comment_content);
                this.root = root;
            }
        }
    }

    public class MyHorizonAdapter extends RecyclerView.Adapter<MyHorizonAdapter.ViewHolder> {
        private List<ImageItemEntity> imageList;
        private boolean isHorizontal;

        public MyHorizonAdapter(List<ImageItemEntity> imageList, boolean isHorizontal) {
            super();
            this.imageList = imageList;
            this.isHorizontal = isHorizontal;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            View view;
            if (isHorizontal) {
                view = View.inflate(viewGroup.getContext(), R.layout.activity_item_screenshot_horizontal, null);
            } else {
                view = View.inflate(viewGroup.getContext(), R.layout.activity_item_screenshot_vertical, null);
            }
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            viewHolder.itemImage.setImageURI(Uri.parse(imageList.get(position).getBigimage()));
            viewHolder.itemImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(GameDetailActivity.this, PhotoShowActivity.class);
                    intent.putExtra("imageList", (Serializable) imageList);
                    intent.putExtra("position", position);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return imageList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public SimpleDraweeView itemImage;
            public TextView lineColor;

            public ViewHolder(View itemView) {
                super(itemView);
                itemImage = (SimpleDraweeView) itemView.findViewById(R.id.item_image);
            }
        }
    }

    private void showShare(GameDetailEntity entity) {
        ShareSDK.initSDK(getApplicationContext());
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.app_name));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(entity.getData().getShareUrl());
        // text是分享文本，所有平台都需要这个字段
        oks.setText(entity.getData().getShareUrl());
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//		oks.setImagePath(entity.getData().getIcon());//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setShareContentCustomizeCallback(new ShareContentCustomize(entity));
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(entity.getData().getShareUrl());

        // 启动分享GUI
        oks.show(getApplicationContext());
    }

    public class ShareContentCustomize implements ShareContentCustomizeCallback {
        private GameDetailEntity entity;

        public ShareContentCustomize(GameDetailEntity entity) {
            this.entity = entity;
        }

        public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
            if (platform instanceof CustomPlatform) {
                return;
            }
            if ("Wechat".equals(platform.getName()) || "WechatFavorite".equals(platform.getName())) {
                paramsToShare.setImageUrl(entity.getData().getIcon());
                paramsToShare.setUrl(entity.getData().getShareUrl());
                paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                paramsToShare.setTitle(entity.getData().getGamename());
                paramsToShare.setText(entity.getData().getGamedesc());
            } else if ("WechatMoments".equals(platform.getName())) {
                paramsToShare.setImageUrl(entity.getData().getIcon());
                paramsToShare.setUrl(entity.getData().getShareUrl());
                paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                paramsToShare.setTitle(entity.getData().getGamedesc());
            }
        }

    }

    private void getDetail(final String gameId) {
        showProgress();
        RequestParams params = getCommonParams();
        params.put("ac", "getgameview");
        params.put("gameid", gameId);
        MyHttpClient.get(GameDetailActivity.this, "appIndex/index", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                hideProgress();
                try {
                    LogUtil.getLogger().d(response.toString());
                    entity = com.alibaba.fastjson.JSONObject.parseObject(response.toString(), GameDetailEntity.class);
                    if (entity.getCode().equals("1001")) {
                        if (entity != null) {
                            fillHeader(entity);
                        }
                    } else {
                        CustomToast.showToast(GameDetailActivity.this, entity.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                hideProgress();
            }
        });
    }

    private void getComment(String gameId, final boolean isMore) {
        RequestParams params = getCommonParams();
        params.put("ac", "getcomment");
        params.put("pagenum", String.valueOf(pageNo));
        params.put("pagesize", Constants.PAGESIZE);
        params.put("gameid", gameId);
        MyHttpClient.get(GameDetailActivity.this, "common/index", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                listView.onRefreshComplete();
                try {
                    LogUtil.getLogger().d(response.toString());
                    CommentEntity entity = com.alibaba.fastjson.JSONObject.parseObject(response.toString(), CommentEntity.class);
                    if (entity.getCode().equals("1001")) {
                        if (!ListUtils.isEmpty(entity.getData())) {
                            pageNo++;
                            if (isMore) {
                                commentList.addAll(entity.getData());
                            } else {
                                commentList = entity.getData();
                            }
                            fillAdapter(commentList);
                        }else{
                            if(isMore) {
                                listView.setMode(PullToRefreshBase.Mode.DISABLED);
                                CommentEntity.DataEntity dataEntity=new CommentEntity().new DataEntity();
                                dataEntity.setType("end");
                                commentList.add(dataEntity);
                                fillAdapter(commentList);
                            }
                        }
                    } else {
                        CustomToast.showToast(GameDetailActivity.this, entity.getMsg());
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

    private void sendComment(final String gameId, String commentContent) {
        RequestParams params = getCommonParams();
        params.put("ac", "gamecomment");
        params.put("gameid", gameId);
        params.put("com_content", commentContent);
        MyHttpClient.get(GameDetailActivity.this, "common/index", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                listView.onRefreshComplete();
                try {
                    LogUtil.getLogger().d(response.toString());
                    CommonEntity entity = com.alibaba.fastjson.JSONObject.parseObject(response.toString(), CommonEntity.class);
                    if (entity.getCode().equals("1001")) {
                        //发送评论成功，刷新评论
                        pageNo = 1;
                        getComment(gameId, false);
                    }
                    CustomToast.showToast(GameDetailActivity.this, entity.getMsg());
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

    private void showShare(GameDetailEntity entity,String platform) {

        ShareSDK.initSDK(getApplicationContext());
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.app_name));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(entity.getData().getShareUrl());
        // text是分享文本，所有平台都需要这个字段
        oks.setText(entity.getData().getShareUrl());
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//		oks.setImagePath(entity.getData().getIcon());//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setShareContentCustomizeCallback(new ShareContentCustomize(entity));
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(entity.getData().getShareUrl());
        oks.setDialogMode();
        if (platform != null) {
            oks.setPlatform(platform);
        }
        // 启动分享GUI
        oks.show(this);
    }


    private  void showShareDialog(){
        int[] screenPoint = new int[2];
        listView.getLocationOnScreen(screenPoint);
        ShareDialog shareDialog = new ShareDialog(this,R.style.shareDilaogStyle,screenPoint[1]-getStatusBarHeight());
        shareDialog.setViewListener(new ShareDialog.IShareThing() {
            @Override
            public void shareDetails(int index, String platform) {
                try {
                    showShare(entity, platform);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        shareDialog.show();
    }


    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
