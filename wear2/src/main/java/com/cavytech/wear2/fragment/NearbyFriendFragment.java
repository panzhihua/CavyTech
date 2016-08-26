package com.cavytech.wear2.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cavytech.wear2.R;
import com.cavytech.wear2.activity.SingleSetailsActivity;
import com.cavytech.wear2.activity.SingleSetailsMoShengActivity;
import com.cavytech.wear2.activity.VerficatonActivity;
import com.cavytech.wear2.application.CommonApplication;
import com.cavytech.wear2.entity.FriendBean;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.RequestCallback;
import com.cavytech.wear2.util.CircleTransform;
import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hf on 2016/4/6.
 */
public class NearbyFriendFragment extends Fragment {
    private MyListAdapter mAdapter ;
    private ListView list_nearby;
    private Gson gson;
    private FriendBean friendBean;
    private List<FriendBean.FriendInfosBean> infolist  = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new Gson();
        mAdapter = new MyListAdapter();
        getdatafromnet();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.nearby_friend_fragment,null);
        list_nearby = (ListView) view.findViewById(R.id.pull_refresh_list_nearby);
        list_nearby.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initdata();
    }
    private void getdatafromnet() {
        HttpUtils.getInstance().searchFriend(CommonApplication.Longitude, CommonApplication.Latitude,
                new RequestCallback<FriendBean>() {
                    @Override
                    public void onError(Request request, Exception e) {

                    }

                    @Override
                    public void onResponse(FriendBean response) {
                        friendBean = response;
                        infolist = friendBean.getFriendInfos();
                        if(infolist != null){
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void initdata() {
        list_nearby.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(),"你点击了--"+infolist.get(position).getNickname(),Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getActivity(), SingleSetailsMoShengActivity.class);
                i.putExtra("fid",infolist.get(position).getUserId());
                i.putExtra("name",infolist.get(position).getNickname());
                startActivity(i);
            }
        });
    }

    public class MyListAdapter extends BaseAdapter {
        @Override
        public int getCount() {

            if(null == infolist){
                return 0;
            }
            return infolist.size();
        }

        @Override
        public Object getItem(int position) {
            return infolist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null){
                convertView = View.inflate(getActivity(),R.layout.nearby_item_list,null);
                viewHolder = new ViewHolder();
                viewHolder.iv_nearby_icon = (ImageView) convertView.findViewById(R.id.iv_nearby_icon);
                viewHolder.tv_nearby_name = (TextView) convertView.findViewById(R.id.tv_nearby_name);
                viewHolder.tv_nearby_juli = (TextView) convertView.findViewById(R.id.tv_nearby_juli);
                viewHolder.ll_nearby_add = (LinearLayout) convertView.findViewById(R.id.ll_nearby_add);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tv_nearby_name.setText(infolist.get(position).getNickname());
            if (!"".equals(infolist.get(position).getAvatarUrl())) {
                Picasso.with(getActivity())
                        .load(infolist.get(position).getAvatarUrl())
                        .transform(new CircleTransform())
                        .placeholder(R.drawable.head_boy_normal)
                        .error(R.drawable.head_boy_normal)
                        .into(viewHolder.iv_nearby_icon);
            }
            viewHolder.tv_nearby_juli.setText(infolist.get(position).getDistance()+" 米");

            viewHolder.ll_nearby_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String friendid = infolist.get(position).getUserId();
                    Intent intent = new Intent(getActivity(),VerficatonActivity.class);
                    intent.putExtra("userid", CommonApplication.userID);
                    intent.putExtra("friendId",friendid);
                    startActivity(intent);
                }
            });

            return convertView;
        }
    }

    public static class ViewHolder{
        ImageView iv_nearby_icon;
        TextView tv_nearby_name;
        TextView tv_nearby_juli;
        LinearLayout ll_nearby_add;
    }

}
