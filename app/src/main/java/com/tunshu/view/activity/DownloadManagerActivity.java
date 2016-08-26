package com.tunshu.view.activity;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.basecore.util.core.FileInfoUtils;
import com.basecore.util.core.FileUtils;
import com.basecore.util.core.ListUtils;
import com.basecore.util.core.StringUtils;
import com.basecore.util.netstate.NetWorkUtil;
import com.basecore.widget.CustomToast;
import com.basecore.widget.dialog.Effectstype;
import com.basecore.widget.dialog.NiftyDialogBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.tunshu.R;
import com.tunshu.application.CommonApplication;
import com.tunshu.entity.GameListEntity;
import com.tunshu.util.Constants;
import com.tunshu.util.LogUtil;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DownloadManagerActivity extends CommonActivity {
    private TextView emptyText;
    private ListView listView;
    private MyAdapter myAdapter;
    private List<GameListEntity> downloadInfoList = new ArrayList<>();
    private DownloadStateReceiver downloadStateReceiver = new DownloadStateReceiver();
    private AppInstalledReceiver installedReceiver = new AppInstalledReceiver();
    private DownloadInfoReceiver downloadInfoReceiver = new DownloadInfoReceiver();

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_download_manager_list);
        initView();
        initData();
        addListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerRec();
    }

    private void registerRec(){
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

    private void initView() {
        findTitle();
        listView = (ListView) findViewById(R.id.example_lv_list);
        emptyText = (TextView) findViewById(R.id.empty_text);
        title.setText(getString(R.string.download_manager));
    }

    private void initData() {
        downloadInfoList = ((CommonApplication) getCoreApplication()).finalDb.findAll(GameListEntity.class);
        Intent intent = new Intent(Constants.GET_ALL_DOWNLOAD_STATE);
        intent.putExtra("downloadInfoList", (Serializable) downloadInfoList);
        intent.putExtra("flag", "DownloadManagerActivity");
        sendBroadcast(intent);
    }


    private void addListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showDeleteDailog(final int position, final int taskToken, final String downloadUrl, final String packageName) {
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(DownloadManagerActivity.this);
        dialogBuilder.withTitle(null).withTitleColor("#ffffff").withMessage(getString(R.string.delete_download_task)).isCancelableOnTouchOutside(true).isCancelable(true).withDuration(300).withEffect(Effectstype.Fadein)
                .withButton1Text(getString(R.string.cancel)).withButton2Text(getString(R.string.ok))
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                    }
                }).setButton2Click(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //在当前界面中移除
                downloadInfoList.remove(position);
                fillAdapter(downloadInfoList);
                //在数据库中移除
                deleteDownloadInfo(packageName);
                //在下载任务中先暂停后取消
                Intent intentCancel = new Intent(Constants.DOWNLOAD_STATE_TO_CANCEL);
                intentCancel.putExtra("taskToken", taskToken);
                intentCancel.putExtra("downloadUrl", downloadUrl);
                intentCancel.putExtra("packageName", packageName);
                sendBroadcast(intentCancel);
                dialogBuilder.dismiss();
            }
        }).show();
    }

    public void deleteDownloadInfo(String packageName) {
        final List<GameListEntity> downloadInfoList = ((CommonApplication) getCoreApplication()).finalDb.findAll(GameListEntity.class);
        if (!ListUtils.isEmpty(downloadInfoList)) {
            for (int i = 0, size = downloadInfoList.size(); i < size; i++) {
                if (packageName.equals(downloadInfoList.get(i).getPackpagename())) {
                    LogUtil.getLogger().d(downloadInfoList.get(i).getTaskId());
                    downloadInfoList.remove(i);
                    break;
                }
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ((CommonApplication) getCoreApplication()).finalDb.deleteAll(GameListEntity.class);
                    for (int j = 0, length = downloadInfoList.size(); j < length; j++) {
                        ((CommonApplication) getCoreApplication()).finalDb.save(downloadInfoList.get(j));
                    }
                }
            }).start();
        }
    }


    private void fillAdapter(List<GameListEntity> list) {
        if (ListUtils.isEmpty(list)) {
            emptyText.setText(R.string.no_download_task);
            emptyText.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            if (myAdapter != null) {
                myAdapter.refreshAdapter(list);
            } else {
                myAdapter = new MyAdapter(this, list);
                listView.setAdapter(myAdapter);
            }
            emptyText.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    private class DownloadStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!ListUtils.isEmpty(downloadInfoList)) {
                for (int i = 0, size = downloadInfoList.size(); i < size; i++) {
                    long currentTaskId = getCoreApplication().getPreferenceConfig().getLong(new HashCodeFileNameGenerator().generate(downloadInfoList.get(i).getDownurl()), -10L);
                    if (currentTaskId < 0) {
                        continue;
                    }
                    if (currentTaskId == intent.getLongExtra("taskId", -1L)) {
                        String state = intent.getStringExtra("state");
                        switch (state) {
                            case Constants.DOWNLOAD_WAIT:
                                downloadInfoList.get(i).setActionText(getString(R.string.pause));
                                downloadInfoList.get(i).setActionTag(Constants.WATTING_TAG);
                                downloadInfoList.get(i).setPercent(String.valueOf((int) intent.getDoubleExtra("percent", 0)));
                                downloadInfoList.get(i).setDownloadedLength(intent.getLongExtra("downloadedLength", 0L));
                                break;
                            case Constants.DOWNLOAD_PROCESS:
                                downloadInfoList.get(i).setActionText(getString(R.string.pause));
                                downloadInfoList.get(i).setActionTag(getString(R.string.pause));
                                downloadInfoList.get(i).setPercent(String.valueOf((int) intent.getDoubleExtra("percent", 0)));
                                downloadInfoList.get(i).setDownloadedLength(intent.getLongExtra("downloadedLength", 0L));
                                break;
                            case Constants.DOWNLOAD_FINISHED:
                                break;
                            case Constants.DOWNLOAD_COMPLETED:
                                downloadInfoList.get(i).setActionText(getString(R.string.install));
                                break;
                            case Constants.DOWNLOAD_PAUSED:
                                downloadInfoList.get(i).setActionText(getString(R.string.go_on));
                                break;
                            case Constants.DOWNLOAD_REBUILD_FINISHED:
                                break;
                            case Constants.DOWNLOAD_CONNECTION_LOST:
                                downloadInfoList.get(i).setActionText(getString(R.string.try_again));
                                break;
                            default:
                                break;
                        }
                        if (myAdapter != null) {
                            myAdapter.updateView(listView, downloadInfoList, i, 0);
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
                if (!ListUtils.isEmpty(downloadInfoList)) {
                    for (int i = 0, size = downloadInfoList.size(); i < size; i++) {
                        if (packageName.equals(downloadInfoList.get(i).getPackpagename())) {
                            if (getCoreApplication().getPreferenceConfig().getBoolean(Constants.SETTING_DELETE_INSTALL_PACKAGE, true)) {//删除安装包
                                long currentTaskId = getCoreApplication().getPreferenceConfig().getLong(new HashCodeFileNameGenerator().generate(downloadInfoList.get(i).getDownurl()), -10L);
                                File file = new File(getCoreApplication().getPreferenceConfig().getString(String.valueOf(currentTaskId), ""));
                                FileUtils.DeleteFile(file);
                            }
                            downloadInfoList.remove(i);
                            fillAdapter(downloadInfoList);
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
                if (!ListUtils.isEmpty(downloadInfoList)) {
                    for (int i = 0, size = downloadInfoList.size(); i < size; i++) {
                        if (packageName.equals(downloadInfoList.get(i).getPackpagename())) {
                            downloadInfoList.remove(i);
                            fillAdapter(downloadInfoList);
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
            if (intent.getStringExtra("flag").equals("DownloadManagerActivity")) {
                downloadInfoList = (List<GameListEntity>) intent.getSerializableExtra("downloadInfoList");
                fillAdapter(downloadInfoList);
            }
        }
    }


    private class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private List<GameListEntity> list;
        private Context context;

        public MyAdapter(Context context, List<GameListEntity> list) {
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
            this.list = list;
        }

        public void refreshAdapter(List<GameListEntity> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public String getActionText(int position) {
            return list.get(position).getActionText() == null ? "" : list.get(position).getActionText();
        }

        public String getActionTag(int position) {
            return list.get(position).getActionTag() == null ? "" : list.get(position).getActionTag();
        }

        public int getPercent(int position) {
            try {
                return StringUtils.isEmpty(list.get(position).getPercent()) ? 0 : Integer.parseInt(list.get(position).getPercent());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return 0;
            }
        }

        public long getDownloadLength(int position) {
            return list.get(position).getDownloadedLength();
        }

        public String getFileSize(int position) {
            return list.get(position).getFilesize() == null ? "" : list.get(position).getFilesize() + "M";
        }

        public int getTaskId(int position) {
            return (int) (getCoreApplication().getPreferenceConfig().getLong(new HashCodeFileNameGenerator().generate(getDownurl(position)), -10L));
        }

        public String getGameId(int position) {
            return list.get(position).getGameid() == null ? "" : list.get(position).getGameid();
        }

        public String getDownurl(int position) {
            return list.get(position).getDownurl() == null ? "" : list.get(position).getDownurl();
        }

        public String getGameName(int position) {
            return list.get(position).getGamename() == null ? "" : list.get(position).getGamename();
        }

        public String getPackageName(int position) {
            return list.get(position).getPackpagename() == null ? "" : list.get(position).getPackpagename();
        }
        public String getDownUrl(int position) {
            return list.get(position).getDownurl() == null ? "" : list.get(position).getDownurl();
        }
        public String getIcon(int position) {
            return list.get(position).getIcon()== null ? "" : list.get(position).getIcon();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.activity_download_manager_item, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.itemimage.setImageURI(Uri.parse(getIcon(position)));
            holder.itemtitle.setText(list.get(position).getGamename());
            holder.itemaction.setText(getActionText(position));
            holder.itemprogress.setVisibility(View.GONE);
            holder.itempercent.setVisibility(View.GONE);
            holder.itemstateinfo.setVisibility(View.GONE);
            holder.itemspeed.setVisibility(View.GONE);
            holder.itemaction.setEnabled(true);
            if (holder.itemaction.getText().equals(getString(R.string.go_on))) {
                holder.itemaction.setBackgroundResource(R.drawable.down_button_white);
                holder.itemaction.setTextAppearance(context, R.style.download_button_text_0_1);
                holder.itemprogress.setVisibility(View.VISIBLE);
                holder.itemprogress.setProgress(getPercent(position));
                holder.itemstateinfo.setText(getString(R.string.already_pause));
                holder.itemstateinfo.setVisibility(View.VISIBLE);
            } else if (holder.itemaction.getText().equals(getString(R.string.install))) {
                holder.itemaction.setBackgroundResource(R.drawable.down_button_blue);
                holder.itemaction.setTextAppearance(context, R.style.download_button_text_0_3);
                holder.itemstateinfo.setText(getString(R.string.wait_install));
                holder.itemstateinfo.setVisibility(View.VISIBLE);
            } else if (holder.itemaction.getText().equals(getString(R.string.try_again))) {
                holder.itemaction.setBackgroundResource(R.drawable.down_pause);
                holder.itemaction.setTextAppearance(context, R.style.download_button_text_0_2);
                holder.itemstateinfo.setText(getString(R.string.download_failed));
                holder.itemstateinfo.setVisibility(View.VISIBLE);
            } else if (holder.itemaction.getText().equals(getString(R.string.pause))) {
                holder.itemaction.setBackgroundResource(R.drawable.down_pause);
                holder.itemaction.setTextAppearance(context, R.style.download_button_text_0_2);
//                if(getPercent(position)>0){
                holder.itemprogress.setProgress(getPercent(position));
                holder.itemprogress.setVisibility(View.VISIBLE);
//                }else{
//                    holder.itemprogress.setVisibility(View.GONE);
//                }
                holder.itempercent.setVisibility(View.VISIBLE);
                String percent;
                if (getActionTag(position).equals(Constants.WATTING_TAG)) {
                    percent = getString(R.string.download_wait);
                } else {
                    percent = "<font color=#6bc501>" + FileInfoUtils.FormetFileSize(getDownloadLength(position)) + "</font>" + "/" + getFileSize(position);
                }
                holder.itempercent.setText(Html.fromHtml(percent));
            }else{
                String percent;
                percent = getString(R.string.download_wait);

                holder.itempercent.setText(Html.fromHtml(percent));
                holder.itempercent.setVisibility(View.VISIBLE);

                holder.itemprogress.setProgress(getPercent(position));
                holder.itemprogress.setVisibility(View.VISIBLE);

                holder.itemaction.setBackgroundResource(R.drawable.down_pause);
                holder.itemaction.setTextAppearance(context, R.style.download_button_text_0_2);
                holder.itemaction.setText(R.string.pause);
            }
            holder.itemimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DownloadManagerActivity.this, GameDetailActivity.class);
                    intent.putExtra("gameId", myAdapter.getGameId(position));
                    startActivity(intent);
                }
            });
            holder.itemaction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (((TextView) view).getText().equals(getString(R.string.pause))) {
                        setActionTextEnable(view);
                        Intent intent = new Intent(Constants.DOWNLOAD_STATE_TO_PAUSE);
                        intent.putExtra("taskToken", getTaskId(position));
                        sendBroadcast(intent);
                    } else if (((TextView) view).getText().equals(getString(R.string.download_manager)) ||
                            ((TextView) view).getText().equals(getString(R.string.try_again)) ||
                            ((TextView) view).getText().equals(getString(R.string.go_on))) {
                        setActionTextEnable(view);
                        checkWifiDown(false, view, getGameName(position), getDownUrl(position), "", list.get(position));
                    } else if (((TextView) view).getText().equals(getString(R.string.install))) {
                        try {
                            ((CommonApplication) getCoreApplication()).appInstall(getTaskId(position));
                        } catch (ActivityNotFoundException e) {
                            CustomToast.showToast(DownloadManagerActivity.this, getString(R.string.install_failed));
                            e.printStackTrace();
                        }
                    }else{

                    }
                }
            });
            holder.itemdelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteDailog(position, getTaskId(position), getDownurl(position), getPackageName(position));
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

        public void updateView(ListView listView, List<GameListEntity> list, int position, int headerNo) {
            this.list = list;
            int firstVisiblePosition = listView.getFirstVisiblePosition();
            int lastVisiblePosition = listView.getLastVisiblePosition();
            int offset = position - firstVisiblePosition;
            /*
            LogUtil.getLogger().d("index-->" + position);
            LogUtil.getLogger().d("headerNo-->" + headerNo);
            LogUtil.getLogger().d("firstVisiblePosition-->" + firstVisiblePosition);
            LogUtil.getLogger().d("lastVisiblePosition-->" + lastVisiblePosition);
            LogUtil.getLogger().d("offset-->" + offset);*/
            if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
                View view = listView.getChildAt(offset);
                if (view == null) {
                    return;
                }
                LogUtil.getLogger().d(view.getRootView().getTag());
                ViewHolder holder = (ViewHolder) view.getTag();
                holder.itemaction.setText(getActionText(position));
                holder.itemprogress.setVisibility(View.GONE);
                holder.itempercent.setVisibility(View.GONE);
                holder.itemstateinfo.setVisibility(View.GONE);
                holder.itemspeed.setVisibility(View.GONE);
                holder.itemaction.setEnabled(true);
                if (holder.itemaction.getText().equals(getString(R.string.go_on))) {
                    holder.itemaction.setBackgroundResource(R.drawable.down_button_white);
                    holder.itemaction.setTextAppearance(context, R.style.download_button_text_0_1);
                    holder.itemprogress.setVisibility(View.VISIBLE);
                    holder.itemprogress.setProgress(getPercent(position));
                    holder.itemstateinfo.setText(getString(R.string.already_pause));
                    holder.itemstateinfo.setVisibility(View.VISIBLE);
                } else if (holder.itemaction.getText().equals(getString(R.string.install))) {
                    holder.itemaction.setBackgroundResource(R.drawable.down_button_blue);
                    holder.itemaction.setTextAppearance(context, R.style.download_button_text_0_3);
                    holder.itemstateinfo.setText(getString(R.string.wait_install));
                    holder.itemstateinfo.setVisibility(View.VISIBLE);
                } else if (holder.itemaction.getText().equals(getString(R.string.try_again))) {
                    holder.itemaction.setBackgroundResource(R.drawable.down_pause);
                    holder.itemaction.setTextAppearance(context, R.style.download_button_text_0_2);
                    holder.itemstateinfo.setText(getString(R.string.download_failed));
                    holder.itemstateinfo.setVisibility(View.VISIBLE);
                } else if (holder.itemaction.getText().equals(getString(R.string.pause))) {
                    holder.itemaction.setBackgroundResource(R.drawable.down_pause);
                    holder.itemaction.setTextAppearance(context, R.style.download_button_text_0_2);
                    holder.itemprogress.setVisibility(View.VISIBLE);
                    holder.itemprogress.setProgress(getPercent(position));
                    holder.itempercent.setVisibility(View.VISIBLE);
                    String percent;
                    if (getActionTag(position).equals(Constants.WATTING_TAG)) {
                        percent = getString(R.string.download_wait);
                    } else {
                        percent = "<font color=#6bc501>" + FileInfoUtils.FormetFileSize(getDownloadLength(position)) + "</font>" + "/" + getFileSize(position);
                    }
                    holder.itempercent.setText(Html.fromHtml(percent));
                }
            }

        }


        public class ViewHolder {
            public SimpleDraweeView itemimage;
            public TextView itemtitle;
            public TextView itemstateinfo;
            public TextView itemspeed;
            public TextView itempercent;
            public LinearLayout itemspeedlayout;
            public ProgressBar itemprogress;
            public TextView itemaction;
            public ImageView itemdelete;
            public View root;

            public ViewHolder(View root) {
                itemimage = (SimpleDraweeView) root.findViewById(R.id.item_image);
                itemtitle = (TextView) root.findViewById(R.id.item_title);
                itemstateinfo = (TextView) root.findViewById(R.id.item_state_info);
                itemspeed = (TextView) root.findViewById(R.id.item_speed);
                itempercent = (TextView) root.findViewById(R.id.item_percent);
                itemspeedlayout = (LinearLayout) root.findViewById(R.id.item_speed_layout);
                itemprogress = (ProgressBar) root.findViewById(R.id.item_progress);
                itemaction = (TextView) root.findViewById(R.id.item_action);
                itemdelete = (ImageView) root.findViewById(R.id.item_delete);
                this.root = root;
            }
        }
    }


}
