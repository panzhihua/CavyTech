package com.tunshu.view.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.basecore.util.core.ListUtils;
import com.basecore.widget.CustomToast;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tunshu.R;
import com.tunshu.adapter.MyGameAdapter;
import com.tunshu.entity.GameListEntity;
import com.tunshu.entity.HelpEntity;
import com.tunshu.entity.PreListEntity;
import com.tunshu.entity.SearchKeyEntity;
import com.tunshu.entity.TopListEntity;
import com.tunshu.entity.TypeListEntity;
import com.tunshu.http.MyHttpClient;
import com.tunshu.util.Constants;
import com.tunshu.util.LogUtil;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：yzb on 2015/7/10 10:02
 */
public class SearchActivity extends CommonActivity {
    private EditText searchContext;
    private ImageView searchButton;
    private ImageButton back;
    private TextView emptyText;
    private PullToRefreshListView listView;
    private List<GameListEntity> searchList = new ArrayList<>();
    private List<String> searchKeyList = new ArrayList<>();
    private MyGameAdapter adapter;
    private MyAdapter myadapter;
    private int pageNo = 1;
    private int pageKeyNo = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        findView();
        getSearchKey(false);
        addListener();
    }

    private void findView() {
        back = (ImageButton) findViewById(R.id.back);
        searchContext = (EditText) findViewById(R.id.search_context);
        searchButton = (ImageView) findViewById(R.id.home_page_search);
        emptyText = (TextView) findViewById(R.id.empty_text);
        listView = (PullToRefreshListView) findViewById(R.id.main_pull_refresh_view);
    }

    private void addListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchContext.getText().toString().trim().length() > 0) {
                    pageNo = 1;
                    getData(false);
                }
            }
        });
        searchContext.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {// 修改回车键功能
                    // 先隐藏键盘
                    try {
                        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(SearchActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        if (searchContext.getText().toString().trim().length() > 0) {
                            pageNo = 1;
                            getData(false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
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
            pageNo = 1;
            getData(false);
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
            getData(true);
        }
    }

    private void fillAdapter(List<GameListEntity> list) {
        if (adapter != null) {
            adapter.refreshAdapter(list);
        } else {
            adapter = new MyGameAdapter(this, list, false);
            listView.setAdapter(adapter);
        }

    }

    private void fillKeyAdapter(List<String> list) {
        if (myadapter != null) {
            myadapter.refreshAdapter(list);
        } else {
            myadapter = new MyAdapter(this, list);
            listView.setAdapter(myadapter);
        }

    }

    private class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater = null;
        private List<String> dataList;

        public MyAdapter(Context context, List<String> dataList) {
            this.mInflater = LayoutInflater.from(context);
            this.dataList = dataList;
        }

        public void refreshAdapter(List<String> dataList) {
            this.dataList = dataList;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.activity_searchkey_item, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.itemtitle.setText(dataList.get(position));
            holder.itemtitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchContext.setText(dataList.get(position));
                    pageNo = 1;
                    getData(false);
                }
            });
            return convertView;
        }

        public class ViewHolder {
            public final TextView itemtitle;
            public final View root;

            public ViewHolder(View root) {
                itemtitle = (TextView) root.findViewById(R.id.item_title);
                this.root = root;
            }
        }
    }

    private void getData(final boolean isMore) {
        RequestParams params = getCommonParams();
        params.put("ac", "gamesearch");
        params.put("pagenum", String.valueOf(pageNo));
        params.put("pagesize", Constants.PAGESIZE);
        params.put("gamekeyword", searchContext.getText().toString().trim());
        MyHttpClient.get(SearchActivity.this, "mobileIndex/index", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                listView.onRefreshComplete();
                try {
                    LogUtil.getLogger().d(response.toString());
                    TopListEntity entity = com.alibaba.fastjson.JSONObject.parseObject(response.toString(), TopListEntity.class);
                    if (entity.getCode().equals("1001")) {
                        if (!ListUtils.isEmpty(entity.getData().getGameList())) {
                            pageNo++;
                            if (isMore) {
                                searchList.addAll(entity.getData().getGameList());
                            } else {
                                listView.setMode(PullToRefreshBase.Mode.BOTH);
                                searchList = entity.getData().getGameList();
                            }
                        } else {
                            if(isMore) {
                                listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                GameListEntity gameListEntity=new GameListEntity();
                                gameListEntity.setType("end");
                                searchList.add(gameListEntity);
                                fillAdapter(searchList);
                            }else{
                                searchList.clear();
                            }
                        }
                        if (!ListUtils.isEmpty(searchList)) {
                            fillAdapter(searchList);
                            emptyText.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                        } else {
                            emptyText.setVisibility(View.VISIBLE);
                            emptyText.setText(getString(R.string.no_search_result));
                            listView.setVisibility(View.GONE);
                        }
                    } else {
                        CustomToast.showToast(SearchActivity.this, entity.getMsg());
                    }
                } catch (Exception e) {
                    emptyText.setVisibility(View.VISIBLE);
                    emptyText.setText(getString(R.string.no_search_result));
                    listView.setVisibility(View.GONE);
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

    private void getSearchKey(final boolean isMore) {
        RequestParams params = getCommonParams();
        params.put("ac", "hotkeyword");
        params.put("pagenum", String.valueOf(pageKeyNo));
        params.put("pagesize", Constants.PAGESIZE);
        MyHttpClient.get(SearchActivity.this, "appIndex/index", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                listView.onRefreshComplete();
                try {
                    LogUtil.getLogger().d(response.toString());
                    SearchKeyEntity entity = com.alibaba.fastjson.JSONObject.parseObject(response.toString(), SearchKeyEntity.class);
                    if (entity.getCode().equals("1001")) {
                        if (!ListUtils.isEmpty(entity.getData().getHotkey())) {
                            pageKeyNo++;
                            if (isMore) {
                                searchKeyList.addAll(entity.getData().getHotkey());
                            } else {
                                searchKeyList = entity.getData().getHotkey();
                            }
                            fillKeyAdapter(searchKeyList);
                        }
                    } else {
                        CustomToast.showToast(SearchActivity.this, entity.getMsg());
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

}
