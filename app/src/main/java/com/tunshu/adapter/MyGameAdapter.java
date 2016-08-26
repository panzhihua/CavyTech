package com.tunshu.adapter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.basecore.util.core.ScreenUtil;
import com.basecore.util.core.StringUtils;
import com.basecore.util.netstate.NetWorkUtil;
import com.basecore.widget.CustomToast;
import com.basecore.widget.dialog.Effectstype;
import com.basecore.widget.dialog.NiftyDialogBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.tunshu.R;
import com.tunshu.application.CommonApplication;
import com.tunshu.entity.GameListEntity;
import com.tunshu.entity.VersionEntity;
import com.tunshu.util.Constants;
import com.tunshu.util.LogUtil;
import com.tunshu.view.activity.GameDetailActivity;
import com.tunshu.view.activity.SpecialActivity;

import java.io.File;
import java.util.List;

/**
 * 作者：yzb on 2015/7/10 13:26
 */
public class MyGameAdapter extends BaseAdapter {
    private static final int TYPE_PREFECTURE = 0;
    private static final int TYPE_GAME = 1;
    private static final int TYPE_END = 2;

    public static class MyView {
        public TextView gameName, gameDesc, gameFilesize, gameDownload;
        public SimpleDraweeView appimg, gameIcon;
        public View dividerView1, dividerView2, divider1, divider2;
    }

    private Context context;
    private List<GameListEntity> list;
    private LayoutInflater mInflater;
    private String countsDown;
    private boolean downloadEnable = true;
    private boolean showTopNum = false;
    private boolean showDivider = true;
    private boolean showDivider1 = true;
    private int height;
    private int buttonStyle1;
    private int buttonBackground1;
    private int buttonBackground2;
    private int buttonBackground3;
    private int buttonStyle2;
    private int buttonStyle3;

    private ConnectivityManager connectivityManager;
    private NetworkInfo info;

    public MyGameAdapter(Context context, List<GameListEntity> list) {
        context.setTheme(R.style.Special_Theme_0);
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        countsDown = context.getResources().getString(R.string.counts_down);
        this.list = list;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        height = wm.getDefaultDisplay().getWidth();
        getThemeStyle();
    }

    public MyGameAdapter(Context context, List<GameListEntity> list, int themeId) {
        context.setTheme(themeId);
        this.showDivider = false;
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        countsDown = context.getResources().getString(R.string.counts_down);
        this.list = list;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        height = wm.getDefaultDisplay().getWidth();
        getThemeStyle();
    }

    public MyGameAdapter(Context context, List<GameListEntity> list, boolean downloadEnable) {
        context.setTheme(R.style.Special_Theme_0);
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        countsDown = context.getResources().getString(R.string.counts_down);
        this.downloadEnable = downloadEnable;
        this.list = list;
        getThemeStyle();
    }

    public MyGameAdapter(Context context, List<GameListEntity> list, boolean downloadEnable, boolean showTopNum) {
        context.setTheme(R.style.Special_Theme_0);
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        countsDown = context.getResources().getString(R.string.counts_down);
        this.showTopNum = showTopNum;
        this.list = list;
        getThemeStyle();
    }

    public void showDivider1(boolean isshow){
        // 如果是排行，第一行分隔线不显示
        showDivider1 = isshow;
    }
    public void refreshAdapter(List<GameListEntity> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
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
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        if (getType(position).equals("prefecture")) {
            return TYPE_PREFECTURE;
        } else if (getType(position).equals("end")) {
            return TYPE_END;
        } else {
            return TYPE_GAME;
        }
    }

    public void getThemeStyle() {
        TypedArray typedArray = context.obtainStyledAttributes(null, R.styleable.SpecialTheme, 0, 0);
        buttonStyle1 = typedArray.getResourceId(R.styleable.SpecialTheme_special_button_text_1, 0);
        buttonStyle2 = typedArray.getResourceId(R.styleable.SpecialTheme_special_button_text_2, 0);
        buttonStyle3 = typedArray.getResourceId(R.styleable.SpecialTheme_special_button_text_3, 0);
        buttonBackground1 = typedArray.getResourceId(R.styleable.SpecialTheme_special_button_bg_1, 0);
        buttonBackground2 = typedArray.getResourceId(R.styleable.SpecialTheme_special_button_bg_2, 0);
        buttonBackground3 = typedArray.getResourceId(R.styleable.SpecialTheme_special_button_bg_3, 0);
        typedArray.recycle();
    }

    public String getType(int position) {
        return list.get(position).getType() == null ? "" : list.get(position).getType();
    }

    public String getIcon(int position) {
        return list.get(position).getIcon() == null ? "" : list.get(position).getIcon();
    }

    public String getGameName(int position) {
        return list.get(position).getGamename() == null ? "" : list.get(position).getGamename();
    }

    public String getTopNum(int position) {
        return list.get(position).getTopnum() == null ? "" : list.get(position).getTopnum();
    }

    public String getGameId(int position) {
        return list.get(position).getGameid() == null ? "" : list.get(position).getGameid();
    }

    public String getGameDesc(int position) {
        return list.get(position).getGamedesc() == null ? "" : list.get(position).getGamedesc();
    }

    public String getFileSize(int position) {
        return list.get(position).getFilesize() == null ? "" : list.get(position).getFilesize();
    }

    public String getDownCount(int position) {
        return list.get(position).getDowncount() == null ? "" : list.get(position).getDowncount();
    }

    public String getDownUrl(int position) {
        return list.get(position).getDownurl() == null ? "" : list.get(position).getDownurl();
    }

    public String getVersion(int position) {
        return list.get(position).getVersion() == null ? "1" : list.get(position).getVersion();
    }

    public String getActionText(int position) {
        return list.get(position).getActionText() == null ? "" : list.get(position).getActionText();
    }

    public String getPackageName(int position) {
        return list.get(position).getPackpagename() == null ? "" : list.get(position).getPackpagename();
    }

    public int getTaskToken(int position) {
        return (int) ((CommonApplication) context.getApplicationContext()).getPreferenceConfig().getLong(new HashCodeFileNameGenerator().generate(getDownUrl(position)), -10L);
    }

    public String getBannerPhone(int position) {
        return list.get(position).getBannerphone() == null ? "" : list.get(position).getBannerphone();
    }

    public String getPreId(int position) {
        return list.get(position).getPrefectureId() == null ? "" : list.get(position).getPrefectureId();
    }

    public String getName(int position) {
        return list.get(position).getTitle() == null ? "" : list.get(position).getTitle();
    }

    public String getStyle(int position) {
        return list.get(position).getStyle() == null ? "" : list.get(position).getStyle();
    }

    private void checkApp(TextView appInstall, String packageName, String downUrl, int versionCode) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
        }
        if (packageInfo == null) {
            int taskToken = (int) ((CommonApplication) context.getApplicationContext()).getPreferenceConfig().getLong(new HashCodeFileNameGenerator().generate(downUrl), -10L);
            File file = new File(((CommonApplication) context.getApplicationContext()).getPreferenceConfig().getString(String.valueOf(taskToken), ""));
            if (file.isFile()) {
                appInstall.setText(context.getString(R.string.install));
                appInstall.setTextAppearance(context, buttonStyle3);
                appInstall.setBackgroundResource(buttonBackground3);
            } else {
                appInstall.setText(context.getString(R.string.download_manager));
                appInstall.setTextAppearance(context, buttonStyle1);
                appInstall.setBackgroundResource(buttonBackground1);
            }
        } else {
            int version = packageInfo.versionCode;
            if (version < versionCode) {
                int taskToken = (int) ((CommonApplication) context.getApplicationContext()).getPreferenceConfig().getLong(new HashCodeFileNameGenerator().generate(downUrl), -10L);
                File file = new File(((CommonApplication) context.getApplicationContext()).getPreferenceConfig().getString(String.valueOf(taskToken), ""));
                if (file.isFile()) {
                    appInstall.setText(context.getString(R.string.install));
                    appInstall.setTextAppearance(context, buttonStyle3);
                    appInstall.setBackgroundResource(buttonBackground3);
                } else {
                    appInstall.setText(context.getString(R.string.update));
                    appInstall.setTextAppearance(context, buttonStyle3);
                    appInstall.setBackgroundResource(buttonBackground3);
                }
            } else {
                appInstall.setText(context.getString(R.string.open));
                appInstall.setTextAppearance(context, buttonStyle3);
                appInstall.setBackgroundResource(buttonBackground3);
            }
        }
    }

    public void setActionTextStyle(TextView appInstall, String actionText) {
        appInstall.setText(actionText);
        if (actionText.equals(context.getResources().getString(R.string.install)) || actionText.equals(context.getResources().getString(R.string.open))) {
            appInstall.setTextAppearance(context, buttonStyle3);
            appInstall.setBackgroundResource(buttonBackground3);
        } else if (actionText.equals(context.getResources().getString(R.string.pause))) {
            appInstall.setTextAppearance(context, buttonStyle2);
            appInstall.setBackgroundResource(buttonBackground2);
        } else {
            appInstall.setTextAppearance(context, buttonStyle1);
            appInstall.setBackgroundResource(buttonBackground1);
        }
    }

    public void setActionTextEnable(View appInstall){
        // 防止用户快速点击按钮
        if (NetWorkUtil.isNetworkConnected(context))
        {
            appInstall.setEnabled(false);
        }else{
            appInstall.setEnabled(true);
        }
    }

    public  boolean isDownLoadTips(){

        return ((CommonApplication) context.getApplicationContext()).getPreferenceConfig().getBoolean(Constants.SETTING_DOWNLOAD_TIPS, true);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MyView holder;
        if (convertView == null) {
            holder = new MyView();
            if (getItemViewType(position) == TYPE_GAME) {
                convertView = mInflater.inflate(R.layout.activity_app_list_item, parent, false);
            } else if (getItemViewType(position) == TYPE_END) {
                convertView = mInflater.inflate(R.layout.public_listview_end, parent, false);
            } else {
                convertView = mInflater.inflate(R.layout.activity_special_list_item, parent, false);
            }
            holder.gameIcon = (SimpleDraweeView) convertView.findViewById(R.id.app_img);
            holder.gameDesc = (TextView) convertView.findViewById(R.id.app_desc);
            holder.gameName = (TextView) convertView.findViewById(R.id.app_name);
            holder.gameFilesize = (TextView) convertView.findViewById(R.id.app_size);
            holder.gameDownload = (TextView) convertView.findViewById(R.id.app_download);
            holder.appimg = (SimpleDraweeView) convertView.findViewById(R.id.app_special_img);
            holder.dividerView1 = convertView.findViewById(R.id.divider_layout1);
            holder.dividerView2 = convertView.findViewById(R.id.divider_layout2);
            holder.divider1 = convertView.findViewById(R.id.divider_1);
            holder.divider2 = convertView.findViewById(R.id.divider_2);
            convertView.setTag(holder);
        } else {
            holder = (MyView) convertView.getTag();
        }
        if (getItemViewType(position) == TYPE_PREFECTURE) {
            LinearLayout.LayoutParams adLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (height - ScreenUtil.dip2px(10)) * 3 / 7);
            adLayoutParams.setMargins(ScreenUtil.dip2px(5), ScreenUtil.dip2px(5), ScreenUtil.dip2px(5), ScreenUtil.dip2px(3));
            holder.appimg.setLayoutParams(adLayoutParams);
            holder.appimg.setImageURI(Uri.parse(getBannerPhone(position)));
            holder.appimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SpecialActivity.class);
                    intent.putExtra("preId", getPreId(position));
                    intent.putExtra("name", getName(position));
                    intent.putExtra("style", getStyle(position));
                    context.startActivity(intent);
                }
            });
        } else if (getItemViewType(position) == TYPE_GAME) {
            if (showDivider) {
                if(position == 0){
                    if(showDivider1){
                        holder.dividerView1.setVisibility(View.VISIBLE);

                    }else{
                        holder.dividerView1.setVisibility(View.GONE);
                    }
                }else{
                    holder.dividerView1.setVisibility(View.VISIBLE);
                }

                holder.dividerView2.setVisibility(View.VISIBLE);
                holder.divider1.setVisibility(View.VISIBLE);
                holder.divider2.setVisibility(View.GONE);
            } else {
                holder.dividerView1.setVisibility(View.GONE);
                holder.dividerView2.setVisibility(View.GONE);
                holder.divider1.setVisibility(View.GONE);
                holder.divider2.setVisibility(View.VISIBLE);
            }
            holder.gameIcon.setImageURI(Uri.parse(getIcon(position)));
            holder.gameName.setText(showTopNum ? getTopNum(position) + getGameName(position) : getGameName(position));
            holder.gameDesc.setText(getGameDesc(position));
            holder.gameFilesize.setText(getFileSize(position) + "M"); // /" + getDownCount(position) + countsDown);
            if (downloadEnable) {
                holder.gameDownload.setVisibility(View.VISIBLE);
            } else {
                holder.gameDownload.setVisibility(View.GONE);
            }
            if (StringUtils.isEmpty(getActionText(position))) {
                try {
                    checkApp(holder.gameDownload, getPackageName(position), getDownUrl(position), Integer.parseInt(getVersion(position)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                setActionTextStyle(holder.gameDownload, getActionText(position));
            }

            holder.gameDownload.setEnabled(true);
            final TextView download = holder.gameDownload;
            holder.gameDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (download.getText().equals(context.getString(R.string.download_manager)) || download.getText().equals(context.getString(R.string.update))) {
                        if (!StringUtils.isEmpty(getGameName(position)) && !StringUtils.isEmpty(getDownUrl(position))) {
                            setActionTextEnable(v);
                            checkWifiDown(position, true, v);
                        }
                    } else if (download.getText().equals(context.getString(R.string.go_on)) || download.getText().equals(context.getString(R.string.try_again))) {
                        setActionTextEnable(v);
                        checkWifiDown(position, false, v);
                    } else if (download.getText().equals(context.getString(R.string.install))) {
                        setActionTextEnable(v);
                        try {
                            ((CommonApplication) context.getApplicationContext()).appInstall(getTaskToken(position));
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else if (download.getText().equals(context.getString(R.string.open))) {
                        setActionTextEnable(v);
                        try {
                            ((CommonApplication) context.getApplicationContext()).openApp(getPackageName(position));
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else if (download.getText().equals(context.getResources().getString(R.string.pause))) {
                        setActionTextEnable(v);
                        Intent intent = new Intent(Constants.DOWNLOAD_STATE_TO_PAUSE);
                        intent.putExtra("taskToken", getTaskToken(position));
                        context.sendBroadcast(intent);
                    } else {
                        setActionTextEnable(v);
                        Intent intent = new Intent(Constants.DOWNLOAD_STATE_TO_PAUSE);
                        intent.putExtra("taskToken", getTaskToken(position));
                        context.sendBroadcast(intent);
                    }
                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, GameDetailActivity.class);
                    intent.putExtra("gameId", getGameId(position));
                    context.startActivity(intent);
                }
            });
        }
        return convertView;
    }

    private void checkWifiDown(final int position, final boolean isNeedDownCount, final View textView){

        if (!NetWorkUtil.isNetworkConnected(context)) {
            CustomToast.showToast(context, context.getString(R.string.network_not_available));
            textView.setEnabled(true);
            return;
        }

        if (NetWorkUtil.isWifiConnected(context) || !isDownLoadTips()) {
            downSendBroadcast(position);

            if(isNeedDownCount){
                downCountSendBroadcast(position);
            }
        }else{
            final NiftyDialogBuilder wifiDialogBuilder = NiftyDialogBuilder.getInstance(context);
            wifiDialogBuilder.withTitle(null).withTitleColor("#000000").withMessage(context.getString(R.string.download_network_change))
                    .isCancelableOnTouchOutside(false).isCancelable(false).withDuration(300)
                    .withEffect(Effectstype.SlideBottom).withButton1Text(context.getString(R.string.cancel)).withButton2Text(context.getString(R.string.go_on)).setButton1Click(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    textView.setEnabled(true);
                    wifiDialogBuilder.dismiss();

                }
            }).setButton2Click(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    downSendBroadcast(position);

                    if(isNeedDownCount){
                        downCountSendBroadcast(position);
                    }
                    wifiDialogBuilder.dismiss();
                }
            });

            wifiDialogBuilder.show();
        }
    }

    private  void downSendBroadcast(final int position){
        Intent intent = new Intent(Constants.DOWNLOAD_STATE_TO_START);
        intent.putExtra("appName", getGameName(position));
        intent.putExtra("downloadUrl", getDownUrl(position));
        intent.putExtra("gameListEntity", list.get(position));
        context.sendBroadcast(intent);
    }

    private  void downCountSendBroadcast(final int position) {

        //下载计数
        Intent countIntent = new Intent(Constants.DOWNLOAD_COUNT_ADD);
        countIntent.putExtra("gameId", getGameId(position));
        context.sendBroadcast(countIntent);
    }

    public void updateView(PullToRefreshListView listView, List<GameListEntity> list, final int index, int headerNo) {
        this.list = list;
        int firstVisiblePosition = listView.getRefreshableView().getFirstVisiblePosition();
        int lastVisiblePosition = listView.getRefreshableView().getLastVisiblePosition();
        int offset = index - firstVisiblePosition + headerNo;
        /*
        LogUtil.getLogger().d("index-->" + index);
        LogUtil.getLogger().d("headerNo-->" + headerNo);
        LogUtil.getLogger().d("firstVisiblePosition-->" + firstVisiblePosition);
        LogUtil.getLogger().d("lastVisiblePosition-->" + lastVisiblePosition);
        LogUtil.getLogger().d("offset-->" + offset);*/
        if (index <= lastVisiblePosition) {
            View view = listView.getRefreshableView().getChildAt(offset);
            if (view == null) {
                return;
            }
            final MyView holder = (MyView) view.getTag();
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (StringUtils.isEmpty(getActionText(index))) {
                        checkApp(holder.gameDownload, getPackageName(index), getDownUrl(index), Integer.parseInt(getVersion(index)));
                    } else {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setActionTextStyle(holder.gameDownload, getActionText(index));
                            }
                        });
                    }
                }
            });
            holder.gameDownload.setEnabled(true);
        }

    }
}


