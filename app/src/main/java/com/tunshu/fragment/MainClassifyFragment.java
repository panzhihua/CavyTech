package com.tunshu.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.basecore.util.core.ListUtils;
import com.basecore.util.netstate.NetWorkUtil;
import com.basecore.widget.CustomToast;
import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tunshu.R;
import com.tunshu.entity.GameClassesListEntity;
import com.tunshu.entity.GameClassesListEntity.DataEntity.GameClassEntity;
import com.tunshu.http.MyHttpClient;
import com.tunshu.util.Constants;
import com.tunshu.view.activity.MarkListActivity;
import android.graphics.Color;
import android.content.Intent;
import org.apache.http.Header;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainClassifyFragment extends CommonFragment {
    private PullToRefreshListView listView;
    private MyClassifyAdapter adapter;
    private List<GameClassesListEntity.DataEntity> typeList = new ArrayList<>();

    private  static int CLASS_CNT = 12;  // 最大类别子项数量(手机时为10)
    private  static int CLASS_LAYOUT_CNT = 4;  // 最大类布局数量

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getData();
        addListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_charts_fragment, container, false);//关联布局文件
        findView(rootView);
        return rootView;
    }


    private void findView(View v) {
        listView = (PullToRefreshListView) v.findViewById(R.id.main_pull_refresh_view);
        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
    }

    private void fillAdapter(List<GameClassesListEntity.DataEntity> list) {
        if (adapter != null) {
            adapter.refreshAdapter(list);
        } else {
            adapter = new MyClassifyAdapter(getActivity(), list);
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
                    getData();
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
                    getData();
                } else {
                    listView.onRefreshComplete();
                }
            }
        }
    }

    private class MyClassifyAdapter extends BaseAdapter {
        private static final int TYPE_GAME = 0;
        private static final int TYPE_END = 1;
        private LayoutInflater mInflater;
        private List<GameClassesListEntity.DataEntity> list;

        public MyClassifyAdapter(Context context, List<GameClassesListEntity.DataEntity> list) {
            context.setTheme(R.style.Special_Theme_0);
            this.mInflater = LayoutInflater.from(context);
            this.list = list;
        }

        public void refreshAdapter(List<GameClassesListEntity.DataEntity> list) {
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
            return TYPE_GAME;
        }

        public String getClassId(int position) {
            return "1";
           // return list.get(position).getClassification().getClassid() == null ? "" : list.get(position).getClassification().getClassid();
        }

        private View findView(ViewGroup parent, int itemCnt){

            String strViewID;
            View convertView;
            View curView;

            for(int i = 0; i < CLASS_LAYOUT_CNT; i++) {
                strViewID = "activity_classify_item" + (i + 1);
                convertView = mInflater.inflate(getResources().getIdentifier(strViewID, "layout", "com.tunshu"), parent, false);
                if(convertView != null){
                    strViewID = "item" + itemCnt;
                    curView = convertView.findViewById(getResources().getIdentifier(strViewID, "id", "com.tunshu"));
                    if(curView != null){
                        return convertView;
                    }
                }
            }
            return null;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ItemViewHolder holder;
            if (convertView == null) {
                int itemCnt = typeList.get(position).getMarkarr().size();//random.nextInt(CLASS_CNT - 1) + 1;

                if(itemCnt == 0){
                    return new View(getActivity());
                }
                if (getItemViewType(position) == TYPE_END) {
                    convertView = mInflater.inflate(R.layout.public_listview_end, parent, false);
                } else {
                    convertView = findView(parent, itemCnt);
                    if(convertView == null){
                        return new View(getActivity());
                    }
                 }
                holder = new ItemViewHolder(convertView, position);

                convertView.setTag(holder);
            } else {
                holder = (ItemViewHolder) convertView.getTag();
                if(holder == null){
                    return convertView;
                }
            }

             hideView(holder);

            fillListItem(holder, position);
            return convertView;
        }

        private void fillListItem(ItemViewHolder holder, int pos){

            Uri imgUrl = Uri.parse(typeList.get(pos).getClass_imgurl());
            holder.itemIcon.setImageURI(imgUrl);

            for(int i = 0; i < typeList.get(pos).getMarkarr().size(); i++){

                if(holder.itemList.size() < i + 1){
                    break;
                }
                try {
                    GameClassEntity classEntity = typeList.get(pos).getMarkarr().get(i);
                    imgUrl = Uri.parse(classEntity.getMark_imgurl());
                    String name = classEntity.getMark_name();
                    if(name == null || name.isEmpty()){
                        name = "null";
                    }
                    holder.itemList.get(i).itemImg.setImageURI(imgUrl);
                    holder.itemList.get(i).itemname.setText(name);
                    holder.itemList.get(i).itemname.setTextColor(Color.parseColor(classEntity.getMark_color()));

                } catch (Exception e) {
                    e.printStackTrace();
                }
             }
        }

        private void hideView(ItemViewHolder holder){

            for(int i = 0; i < CLASS_CNT; i++){
                if(i >= holder.itemList.size()){
                    break;
                }
                if(i < holder.itemCnt){
                    holder.itemList.get(i).setVisibility(View.VISIBLE);
                }else{
                    holder.itemList.get(i).setVisibility(View.GONE);
                }
            }
            int starHideIndex;

            if(holder.itemList.size()/2 == holder.spliteImg.size()){
                starHideIndex =  holder.itemCnt/2;
            }else{
                starHideIndex = holder.itemCnt - 1 - holder.itemCnt/4;
                if(holder.itemCnt%4 == 0){
                    starHideIndex += 1;
                }
            }
            for(int i = 0; i < holder.spliteImg.size(); i++) {

                if(i < starHideIndex){
                    holder.spliteImg.get(i).setVisibility(View.VISIBLE);
                }else{
                    holder.spliteImg.get(i).setVisibility(View.GONE);
                }
            }
        }

        public class ItemViewHolder {

            public final int itemCnt;
            public final ImageView itemIcon;
            public final List<ItemView> itemList = new ArrayList<>();
            public final List<ImageView> spliteImg = new ArrayList<>();

            public class ItemView {
                public final ImageView itemImg;
                public final TextView itemname;
                public final View root;

                public ItemView(View root){
                    itemImg = (ImageView) root.findViewById(R.id.classitem_img);
                    itemname = (TextView) root.findViewById(R.id.classitem_title);
                    this.root = root;
                 }
                public void setVisibility(int visibility){
                    itemImg.setVisibility(visibility);
                    itemname.setVisibility(visibility);
                    if(visibility == View.GONE){
                        this.root.setEnabled(false);
                    }else{
                        this.root.setEnabled(true);
                    }
                }
            }

            public ItemViewHolder(View root, int pos) {
                itemIcon = (SimpleDraweeView) root.findViewById(R.id.app_img);

                if(typeList.size() > pos){
                    itemCnt = typeList.get(pos).getMarkarr().size();
                }else{
                    itemCnt = 0;
                }

                String strViewID;
                View curView = null;
                for(int i = 0; i < CLASS_CNT; i++){
                    strViewID = "item" + (i+1);
                    curView = root.findViewById(getResources().getIdentifier(strViewID, "id","com.tunshu"));

                    if(curView == null){
                        break;
                    }
                    if(i < typeList.get(pos).getMarkarr().size()){
                        curView.setTag(typeList.get(pos).getMarkarr().get(i));
                    }
                     curView.setEnabled(true);

                    curView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    GameClassEntity curMark = (GameClassEntity)v.getTag();

                                    try {
                                        Intent intent = new Intent(getActivity(), MarkListActivity.class);
                                        intent.putExtra("markname", curMark.getMark_name());
                                        intent.putExtra("markid", "" + curMark.getMark_id());
                                        startActivity(intent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    ItemView iteView = null;
                    if(curView != null){
                        iteView = new ItemView(curView);
                        itemList.add(iteView);
                    }

                    ImageView spliteImgTmp = null;
                    strViewID = "centerline_img" + (i + 1);
                    spliteImgTmp = (ImageView)root.findViewById(getResources().getIdentifier(strViewID, "id","com.tunshu"));
                    if(spliteImgTmp != null){
                        spliteImg.add(spliteImgTmp);
                    }
                }
            }
        }
    }
    private void getData() {
        RequestParams params = getCommonParams();
        params.put("ac", "newclassification");

        MyHttpClient.get(getActivity(), "mobileIndex/index", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                listView.onRefreshComplete();

                try {
                    GameClassesListEntity entity = com.alibaba.fastjson.JSONObject.parseObject(response.toString(), GameClassesListEntity.class);
                    if (entity.getCode().equals("1001")) {
                        if (!ListUtils.isEmpty(entity.getData())) {
                            typeList = entity.getData();
                            fillAdapter(typeList);
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

}
