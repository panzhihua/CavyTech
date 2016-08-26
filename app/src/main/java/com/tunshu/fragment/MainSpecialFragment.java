package com.tunshu.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.basecore.util.core.ListUtils;
import com.basecore.util.core.ScreenUtil;
import com.basecore.util.netstate.NetWorkUtil;
import com.basecore.widget.CustomToast;
import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tunshu.R;
import com.tunshu.entity.GameListEntity;
import com.tunshu.entity.PreListEntity;
import com.tunshu.entity.SpecialEntity;
import com.tunshu.http.MyHttpClient;
import com.tunshu.util.Constants;
import com.tunshu.util.LogUtil;
import com.tunshu.view.activity.SpecialActivity;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ShadowNight on 2015/8/19.
 */
public class MainSpecialFragment extends CommonFragment {
    private PullToRefreshGridView listView;
    private MyAdapter adapter;
    private int pageNo = 1;
    private boolean isPad = false;
    private List<PreListEntity> preList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_special_fragment, container, false);
        findView(rootView);
        return rootView;
    }


    private void findView(View v) {
        listView = (PullToRefreshGridView) v.findViewById(R.id.main_pull_refresh_view);
        isPad = v.findViewById(R.id.special_layout) != null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getData(false);
        addListener();

    }

    private void fillAdapter(List<PreListEntity> preList) {
        if (isPad) {
            if (preList.size() > 8) {
                listView.getRefreshableView().setNumColumns(2);
                listView.getRefreshableView().setHorizontalSpacing(ScreenUtil.dip2px(8));
            } else {
                listView.getRefreshableView().setNumColumns(1);
            }
        } else {
            listView.getRefreshableView().setNumColumns(1);
        }
        if (adapter != null) {
            adapter.refreshAdapter(preList);
        } else {
            adapter = new MyAdapter(getActivity(), preList);
            listView.setAdapter(adapter);
        }
    }

    private void addListener() {
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                new HeaderRefreshTask().execute();

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
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


    private class MyAdapter extends BaseAdapter {
        private static final int TYPE_GAME = 0;
        private static final int TYPE_END = 1;
        private LayoutInflater mInflater;
        private List<PreListEntity> preList;
        private int height;

        public MyAdapter(Context context, List<PreListEntity> preList) {
            this.mInflater = LayoutInflater.from(context);
            this.preList = preList;
            WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
            height = wm.getDefaultDisplay().getWidth();
        }

        public void refreshAdapter(List<PreListEntity> preList) {
            this.preList = preList;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return preList.size();
        }

        @Override
        public Object getItem(int position) {
            return preList.get(position);
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
            return preList.get(position).getType() == null ? "" : preList.get(position).getType();
        }


        public String getPreId(int position) {
            return preList.get(position).getPrefectureId() == null ? "" : preList.get(position).getPrefectureId();
        }

        public String getName(int position) {
            return preList.get(position).getTitle() == null ? "" : preList.get(position).getTitle();
        }

        public String getStyle(int position) {
            return preList.get(position).getStyle() == null ? "" : preList.get(position).getStyle();
        }

        public String getBannerPhone(int position) {
            return preList.get(position).getBannerphone() == null ? "" : preList.get(position).getBannerphone();
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                if (getItemViewType(position) == TYPE_END) {
                    convertView = mInflater.inflate(R.layout.public_listview_end, parent, false);
                } else {
                    convertView = mInflater.inflate(R.layout.activity_special_list_item, parent, false);
                }
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (getItemViewType(position) == TYPE_GAME) {
                if (isPad) {
                    if (preList.size() > 8) {
                        LinearLayout.LayoutParams adLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (height - ScreenUtil.dip2px(24)) * 3 / 14);
                        adLayoutParams.setMargins(0, ScreenUtil.dip2px(5), 0, ScreenUtil.dip2px(4));
                        holder.appimg.setLayoutParams(adLayoutParams);
                    } else {
                        LinearLayout.LayoutParams adLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (height - ScreenUtil.dip2px(16)) * 3 / 7);
                        adLayoutParams.setMargins(0, ScreenUtil.dip2px(5), 0, ScreenUtil.dip2px(4));
                        holder.appimg.setLayoutParams(adLayoutParams);
                    }
                } else {
                    LinearLayout.LayoutParams adLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (height - ScreenUtil.dip2px(10)) * 3 / 7);
                    adLayoutParams.setMargins(ScreenUtil.dip2px(5), ScreenUtil.dip2px(5), ScreenUtil.dip2px(5), ScreenUtil.dip2px(4));
                    holder.appimg.setLayoutParams(adLayoutParams);
                }
                holder.appimg.setImageURI(Uri.parse(getBannerPhone(position)));
                holder.appimg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), SpecialActivity.class);
                        intent.putExtra("preId", getPreId(position));
                        intent.putExtra("name", getName(position));
                        intent.putExtra("style", getStyle(position));
                        getActivity().startActivity(intent);
                    }
                });
            }
            return convertView;
        }

        public class ViewHolder {
            public final SimpleDraweeView appimg;
            public final View root;

            public ViewHolder(View root) {
                appimg = (SimpleDraweeView) root.findViewById(R.id.app_special_img);
                this.root = root;
            }
        }
    }

    private void getData(final boolean isMore) {
        if (!isMore) {
            showProgress();
        }
        RequestParams params = getCommonParams();
        params.put("ac", "prefecture");
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
                    SpecialEntity entity = com.alibaba.fastjson.JSONObject.parseObject(response.toString(), SpecialEntity.class);
                    if (entity.getCode().equals("1001")) {
                        if (!ListUtils.isEmpty(entity.getData().getPreList())) {
                            pageNo++;
                            if (isMore) {
                                preList.addAll(entity.getData().getPreList());
                            } else {
                                listView.setMode(PullToRefreshBase.Mode.BOTH);
                                preList = entity.getData().getPreList();
                            }
                            if (!ListUtils.isEmpty(preList)) {
                                fillAdapter(preList);
                            }
                        } else {
                            if (isMore) {
                                listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                PreListEntity gameListEntity=new PreListEntity();
                                gameListEntity.setType("end");
                                preList.add(gameListEntity);
                                fillAdapter(preList);
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
}
