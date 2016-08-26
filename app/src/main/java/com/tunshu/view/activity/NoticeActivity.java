package com.tunshu.view.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.basecore.util.core.ListUtils;
import com.basecore.widget.CustomToast;
import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tunshu.R;
import com.tunshu.entity.NoticeEntity;
import com.tunshu.http.MyHttpClient;
import com.tunshu.util.Constants;
import com.tunshu.util.LogUtil;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NoticeActivity extends CommonActivity {
    private PullToRefreshListView listView;
    private MyAdapter myAdapter;
    private List<NoticeEntity.DataEntity> noticeList = new ArrayList<>();
    private int pageNo=1;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_notice_list);
        initView();
        getData(false);
        addListener();
    }


    private void initView() {
        findTitle();
        listView = (PullToRefreshListView) findViewById(R.id.main_pull_refresh_view);
        title.setText(getString(R.string.notice));
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
            // Simulates a background job.
            try {
                Thread.sleep(Constants.REFRESH_SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            pageNo=1;
            getData(false);
            super.onPostExecute(result);
        }
    }

    private class FooterRefreshTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            // Simulates a background job.
            try {
                Thread.sleep(Constants.REFRESH_SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            getData(true);
            super.onPostExecute(result);
        }
    }

    private void fillAdapter(List<NoticeEntity.DataEntity> noticeList) {
        if(myAdapter!=null){
            myAdapter.refreshAdapter(noticeList);
        }else{
            myAdapter = new MyAdapter(this, noticeList);
            listView.setAdapter(myAdapter);
        }
    }



    private class MyAdapter extends BaseAdapter {
        private static final int TYPE_GAME = 0;
        private static final int TYPE_END = 1;
        private LayoutInflater mInflater;
        private List<NoticeEntity.DataEntity> noticeList;

        public MyAdapter(Context context, List<NoticeEntity.DataEntity> noticeList) {
            context.setTheme(R.style.Special_Theme_0);
            this.mInflater = LayoutInflater.from(context);
            this.noticeList = noticeList;
        }
        public void refreshAdapter(List<NoticeEntity.DataEntity> noticeList){
            this.noticeList = noticeList;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return noticeList.size();
        }

        @Override
        public Object getItem(int position) {
            return noticeList.get(position);
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
            return noticeList.get(position).getType() == null ? "" : noticeList.get(position).getType();
        }

        public String getTitle(int position){
            return noticeList.get(position).getTitle()==null?"":noticeList.get(position).getTitle();
        }
        public String getUrl(int position){
            return noticeList.get(position).getDetailUrl()==null?"":noticeList.get(position).getDetailUrl();
        }
        public String getIcon(int position){
            return noticeList.get(position).getMsgicon()==null?"":noticeList.get(position).getMsgicon();
        }



        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                if (getItemViewType(position) == TYPE_END) {
                    convertView = mInflater.inflate(R.layout.public_listview_end, parent, false);
                } else {
                    convertView = mInflater.inflate(R.layout.activity_help_item, parent, false);
                }
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (getItemViewType(position) == TYPE_GAME) {
                holder.itemimage.setImageURI(Uri.parse(getIcon(position)));
                holder.itemtitle.setText(noticeList.get(position).getTitle());
                holder.itemtime.setText(noticeList.get(position).getCreate_time());
                holder.itemcontent.setText(noticeList.get(position).getContent());
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(NoticeActivity.this, WebActivity.class);
                        intent.putExtra("titleName",myAdapter.getTitle(position));
                        intent.putExtra("webUrl",myAdapter.getUrl(position));
                        startActivity(intent);
                    }
                });
            }
            return convertView;
        }

        public class ViewHolder {
            public final SimpleDraweeView itemimage;
            public final TextView itemtitle;
            public final TextView itemtime;
            public final TextView itemcontent;
            public final View root;

            public ViewHolder(View root) {
                itemimage = (SimpleDraweeView) root.findViewById(R.id.item_image);
                itemtitle = (TextView) root.findViewById(R.id.item_title);
                itemtime = (TextView) root.findViewById(R.id.item_time);
                itemcontent = (TextView) root.findViewById(R.id.item_content);
                this.root = root;
            }
        }
    }

    private void getData(final boolean isMore) {
        if (!isMore) {
            showProgress();
        }
        RequestParams params = getCommonParams();
        params.put("ac", "sysmessage");
        params.put("pagenum", String.valueOf(pageNo));
        params.put("pagesize", Constants.PAGESIZE);
        MyHttpClient.get(this, "common/index", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                hideProgress();
                listView.onRefreshComplete();
                try {
                    LogUtil.getLogger().d(response.toString());
                    NoticeEntity entity = com.alibaba.fastjson.JSONObject.parseObject(response.toString(), NoticeEntity.class);
                    if (entity.getCode().equals("1001") || entity.getCode().equals("1002")) {
                        if (!ListUtils.isEmpty(entity.getData())) {
                            pageNo++;
                            if (isMore) {
                                noticeList.addAll(entity.getData());
                            } else {
                                noticeList = entity.getData();
                                listView.setMode(PullToRefreshBase.Mode.BOTH);
                            }
                            fillAdapter(noticeList);
                        } else {
                            if (isMore) {
                                listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                NoticeEntity.DataEntity dataEntity = new NoticeEntity().new DataEntity();
                                dataEntity.setType("end");
                                noticeList.add(dataEntity);
                                fillAdapter(noticeList);
                            }
                        }
                    } else {
                        CustomToast.showToast(NoticeActivity.this, entity.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                hideProgress();
                listView.onRefreshComplete();
            }
        });


    }


}
