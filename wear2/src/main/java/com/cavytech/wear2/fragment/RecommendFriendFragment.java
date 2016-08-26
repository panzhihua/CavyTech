package com.cavytech.wear2.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cavytech.wear2.R;
import com.cavytech.wear2.activity.ContaxtSearchActivity;
import com.cavytech.wear2.activity.SingleSetailsActivity;
import com.cavytech.wear2.activity.SingleSetailsMoShengActivity;
import com.cavytech.wear2.activity.VerficatonActivity;
import com.cavytech.wear2.entity.RecommendBean;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.RequestCallback;
import com.cavytech.wear2.util.CacheUtils;
import com.cavytech.wear2.util.CircleTransform;
import com.cavytech.wear2.util.Constants;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by libin on 2016/4/6.
 * 邮箱：bin.li@tunshu.com
 * 推荐好友
 */
public class RecommendFriendFragment extends Fragment{
    private MyPullToRefrshListAdapter mAdapter ;
    private PullToRefreshListView pull_refresh_list_recommend;
    private LinearLayout ll_search;
    private String userid;
    private Gson gson;
    private List<RecommendBean.FriendInfosBean> rblist = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("TAG","推荐好友 ====");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = View.inflate(getActivity(), R.layout.recommend_friend_fragment,null);
        // 得到控件
        pull_refresh_list_recommend = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list_recommend);
        ll_search = (LinearLayout) view.findViewById(R.id.ll_search);

        //设置适配器
        mAdapter = new MyPullToRefrshListAdapter() ;

        pull_refresh_list_recommend.setAdapter(mAdapter);

        return view;
    }

    private void initlistener() {
        pull_refresh_list_recommend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), SingleSetailsMoShengActivity.class);
                i.putExtra("fid",rblist.get(position-1).getUserId());
                i.putExtra("name",rblist.get(position-1).getNickname());
                Toast.makeText(getActivity(),"-------------name-----"+rblist.get(position).getNickname(),Toast.LENGTH_SHORT).show();
                for (int j =0 ;j<rblist.size();j++){

                   Log.e("TAg",rblist.get(j).getNickname()+"=====");

                }

                Log.e("TAg",rblist.get(position).getNickname()+"++++++++++++++++++");
                Log.e("TAg",position+"++++++++++++++++++");
                startActivity(i);
            }
        });

        // 设置监听事件
        pull_refresh_list_recommend.setOnRefreshListener ( new PullToRefreshBase.OnRefreshListener<ListView>( )
        {
            @ Override
            public void onRefresh (PullToRefreshBase < ListView > refreshView ){

                getdatafromnet();
            }
        }) ;
    }

    /**
     * 下拉刷新
     */
    private void getdatafromnet() {

        HttpUtils.getInstance().searchFriend(
                new RequestCallback<RecommendBean>() {
                    @Override
                    public void onError(Request request, Exception e) {

                    }

                    @Override
                    public void onResponse(RecommendBean response) {

                        rblist = response.getFriendInfos();
                        mAdapter.notifyDataSetChanged();
                        pull_refresh_list_recommend.onRefreshComplete();
                    }
                });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userid = CacheUtils.getString(getActivity(), Constants.USERID);
        gson = new Gson();
        getdatafromnet();
        //initlistener();
        ll_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ContaxtSearchActivity.class));
            }
        });
    }

    public class MyPullToRefrshListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if(null == rblist){
                return 0;
            }
            return rblist.size();
        }

        @Override
        public Object getItem(int position) {
            return rblist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null){
                convertView = View.inflate(getActivity(),R.layout.recommend_item_list,null);
                viewHolder = new ViewHolder();
                viewHolder.iv_recommend_icon = (ImageView) convertView.findViewById(R.id.iv_recommend_icon);
                viewHolder.tv_recommend_name = (TextView) convertView.findViewById(R.id.tv_recommend_name);
                viewHolder.ll_recommend_add = (LinearLayout) convertView.findViewById(R.id.ll_recommend_add);
                viewHolder.rl_swipe_item = (RelativeLayout) convertView.findViewById(R.id.rl_swipe_item);

                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tv_recommend_name.setText(rblist.get(position).getNickname());

            if (!"".equals(rblist.get(position).getAvatarUrl())) {
                Picasso.with(getActivity())
                        .load(rblist.get(position).getAvatarUrl())
                        .transform(new CircleTransform())
                        .placeholder(R.drawable.head_boy_normal)
                        .error(R.drawable.head_boy_normal)
                        .into(viewHolder.iv_recommend_icon);
            }

//            viewHolder.tv_recommend_name.setText(rblist.get(position).getNickname());
//            viewHolder.tv_recommend_name.setText(rblist.get(position).getNickname());
            viewHolder.rl_swipe_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), SingleSetailsMoShengActivity.class);
                    i.putExtra("fid",rblist.get(position).getUserId());
                    i.putExtra("name",rblist.get(position).getNickname());
                    Toast.makeText(getActivity(),"-------------name-----"+rblist.get(position).getNickname(),Toast.LENGTH_SHORT).show();
                    for (int j =0 ;j<rblist.size();j++){

                        Log.e("TAg",rblist.get(j).getNickname()+"=====");

                    }

                    Log.e("TAg",rblist.get(position).getNickname()+"++++++++++++++++++");
                    Log.e("TAg",position+"++++++++++++++++++");
                    startActivity(i);
                }
            });


            viewHolder.ll_recommend_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),VerficatonActivity.class);
                    intent.putExtra("userid",userid);
                    intent.putExtra("friendId",rblist.get(position).getUserId());

                    Log.e("TAG","-------------传过去的id-----"+userid);
                    Log.e("TAG","-----------好友的id-----"+rblist.get(position).getUserId());
                    startActivity(intent);
                }
            });

            return convertView;
        }
    }

    public static class ViewHolder{
        ImageView iv_recommend_icon;
        TextView tv_recommend_name;
        LinearLayout ll_recommend_add;
        RelativeLayout rl_swipe_item;
    }

}
