package com.cavytech.wear2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cavytech.wear2.R;
import com.cavytech.wear2.base.AppCompatActivityEx;
import com.cavytech.wear2.entity.CommonEntity;
import com.cavytech.wear2.entity.NewFriendBean;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.RequestCallback;
import com.cavytech.wear2.slidingmenu.FriendActivity;
import com.cavytech.wear2.util.CacheUtils;
import com.cavytech.wear2.util.CircleTransform;
import com.cavytech.wear2.util.Constants;
import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Picasso;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewFriendActivity extends AppCompatActivityEx {
    private static final int NEWFRIEND = 1;
    @ViewInject(R.id.back)
    private ImageView back;

    @ViewInject(R.id.title)
    private TextView title;

    @ViewInject(R.id.lv_new_friend)
    private ListView lv_new_friend;

    private MyNewFriendListAdapter adapter;
    private String userid;
    private Gson gson;
    private List<NewFriendBean.UserInfosBean> newlist;
    private String friendId;
    private ViewHolder viewHolder;

//    private List< String > mListItems ;
//    private int mItemCount = 9 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);
        x.view().inject(this);
        gson = new Gson();

        newlist = new ArrayList<NewFriendBean.UserInfosBean>();
        adapter = new MyNewFriendListAdapter();

//        if(newlist.size()!=0){


//        }


        setToolBar();
        userid = CacheUtils.getString(this, Constants.USERID);
        initData();
        
        setResult(NEWFRIEND);

    }

    private void initData() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        title.setText("新的朋友");

        HttpUtils.getInstance().getFriendReqList(new RequestCallback<NewFriendBean>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(NewFriendBean response) {
                if(response.isSuccess()){
                    newlist = response.getUserInfos();
                    lv_new_friend.setAdapter(adapter);
//                    adapter.notifyDataSetChanged();
                }else{

                }
            }
        });
    }

    public class MyNewFriendListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return newlist.size();
        }

        @Override
        public Object getItem(int position) {
            return newlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = View.inflate(getApplication(),R.layout.new_friend_item_list,null);
                viewHolder = new ViewHolder();
                viewHolder.iv_new_friend_icon = (ImageView) convertView.findViewById(R.id.iv_new_friend_icon);
                viewHolder.tv_new_friend_name = (TextView) convertView.findViewById(R.id.tv_new_friend_name);
//                viewHolder.tv_new_friend_yitongyi = (TextView) convertView.findViewById(R.id.tv_new_friend_yitongyi);
                viewHolder.tv_new_friend_beizhu = (TextView) convertView.findViewById(R.id.tv_new_friend_beizhu);
                viewHolder.ll_new_friend_add = (LinearLayout) convertView.findViewById(R.id.ll_new_friend_add);
                viewHolder.tv_tongyi = (TextView) convertView.findViewById(R.id.tv_tongyi);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            //用户头像
            if(newlist.get(position)!=null){
                if (!"".equals(newlist.get(position).getAvatarUrl())) {
                    Picasso.with(NewFriendActivity.this)
                            .load(newlist.get(position).getAvatarUrl())
                            .transform(new CircleTransform())
                            .placeholder(R.drawable.head_boy_normal)
                            .error(R.drawable.head_boy_normal)
                            .into(viewHolder.iv_new_friend_icon);
                }

                viewHolder.tv_new_friend_name.setText(newlist.get(position).getNickname());
                if(newlist.get(position).getVerifyMsg()!=null){
                    viewHolder.tv_new_friend_beizhu.setText(newlist.get(position).getVerifyMsg());
                }
                friendId= newlist.get(position).getUserId();
                viewHolder.ll_new_friend_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplication(),"同意",Toast.LENGTH_SHORT).show();
                        agren(friendId);
                    }
                });
            }

//            viewHolder.tv_new_friend_yitongyi.setVisibility(View.INVISIBLE);

            return convertView;
        }
    }

    private void agren(String fId) {

        HttpUtils.getInstance().addFriend(fId, HttpUtils.AddFriendType.ANSWERREQ, "", new RequestCallback<CommonEntity>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(CommonEntity response) {
                if(response.isSuccess()){
                    viewHolder.tv_tongyi.setText("已同意");
                }else{

                }
            }
        });
    }

    public static class ViewHolder{
        ImageView iv_new_friend_icon;
        TextView tv_new_friend_name;
//        TextView tv_new_friend_yitongyi;
        TextView tv_new_friend_beizhu;
        LinearLayout ll_new_friend_add;
        TextView tv_tongyi;
    }
}
