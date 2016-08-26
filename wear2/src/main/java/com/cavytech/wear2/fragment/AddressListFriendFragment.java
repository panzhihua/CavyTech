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
import android.widget.TextView;

import com.cavytech.wear2.R;
import com.cavytech.wear2.activity.SingleSetailsActivity;
import com.cavytech.wear2.activity.VerficatonActivity;
import com.cavytech.wear2.entity.FriendBean;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.RequestCallback;
import com.cavytech.wear2.util.CacheUtils;
import com.cavytech.wear2.util.CircleTransform;
import com.cavytech.wear2.util.Constants;
import com.cavytech.wear2.util.PhoneUtils;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by libin on 2016/4/6.
 * 邮箱：bin.li@tunshu.com
 * 通讯录好友
 */
public class AddressListFriendFragment extends Fragment{
    private MyPullToRefrshListAdapter mAdapter ;
    private ListView pull_refresh_list_address;
    private ArrayList<HashMap<String, String>> phonelist;
    private ArrayList<String> numlist = new ArrayList<String>();
    private String userid;
    private FriendBean frebean;
    private List<FriendBean.FriendInfosBean> frelist = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("TAG","通讯录好友 ====");
        userid = CacheUtils.getString(getActivity(), Constants.USERID);
        phonelist = PhoneUtils.getPeopleInPhone2(getActivity(), true);

        getlistfromnet();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    /**
     * 从后端获取好友列表
     */
    private void getlistfromnet() {

        if(phonelist != null){

            numlist.clear();
            for(int i=0; i<phonelist.size(); i++){
                numlist.add(phonelist.get(i).get("phoneNum"));
             }
        }else{
            return;
        }

        HttpUtils.getInstance().searchFriend(numlist, new RequestCallback<FriendBean>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(FriendBean response) {

                if(response.getCode() == HttpUtils.newSUCCESS){
                    frebean = response;
                    frelist = frebean.getFriendInfos(); //后台返回匹配好的list

                    if(frelist!=null && frelist.size() > 0){
                        String locPhone;
                        for(int i=0; i<frelist.size(); i++){
                            for(int j=0; j<phonelist.size(); j++){
                                locPhone = phonelist.get(j).get("phoneNum");
                                if(locPhone.contains(frelist.get(i).getPhoneNum())){
                                    frelist.get(i).setRemark(phonelist.get(j).get("peopleName"));
                                    break;
                                }
                            }
                         }
                        mAdapter.notifyDataSetChanged();
                    }
                }else{

                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.address_friend_fragment,null);
        // 得到控件

        pull_refresh_list_address = (ListView)view.findViewById(R.id.pull_refresh_list_address);

        //设置适配器
        mAdapter = new MyPullToRefrshListAdapter() ;
        pull_refresh_list_address.setAdapter(mAdapter);

        pull_refresh_list_address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), SingleSetailsActivity.class);
                i.putExtra("fid",frelist.get(position).getUserId());
                i.putExtra("name",frelist.get(position).getNickname());
                startActivity(i);
            }
        });

        return view;
    }

    /*private void initDatas() {
        // 初始化数据和数据源
        mListItems = new ArrayList< String >( ) ;

        for ( int i = 0 ; i < mItemCount ; i ++ )
        {
            mListItems . add ( "" + i ) ;
        }
    }*/

    public class MyPullToRefrshListAdapter extends BaseAdapter{
        @Override
        public int getCount() {

            if(null == frelist){
                return 0;
            }
            return frelist.size();
        }

        @Override
        public Object getItem(int position) {
            return frelist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null){
                convertView = View.inflate(getActivity(),R.layout.address_item_list,null);
                viewHolder = new ViewHolder();
                viewHolder.tv_count_head = (TextView) convertView.findViewById(R.id.tv_count_head);
                viewHolder.iv_address_icon = (ImageView) convertView.findViewById(R.id.iv_address_icon);
                viewHolder.atv_address_name = (TextView) convertView.findViewById(R.id.atv_address_name);
                viewHolder.atv_address_beizhu = (TextView) convertView.findViewById(R.id.atv_address_beizhu);
                viewHolder.ll_address_add = (LinearLayout) convertView.findViewById(R.id.ll_address_add);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tv_count_head.setVisibility(View.INVISIBLE);
            viewHolder.atv_address_beizhu.setText(frelist.get(position).getRemark());
            viewHolder.atv_address_name.setText(frelist.get(position).getNickname());
             //用户头像
            if (!"".equals(frelist.get(position).getAvatarUrl())) {
                Picasso.with(getActivity())
                        .load(frelist.get(position).getAvatarUrl())
                        .transform(new CircleTransform())
                        .placeholder(R.drawable.head_boy_normal)
                        .error(R.drawable.head_boy_normal)
                        .into(viewHolder.iv_address_icon);
             }
            viewHolder.ll_address_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String friendid = frelist.get(position).getUserId();
                    Intent intent = new Intent(getActivity(),VerficatonActivity.class);
                    intent.putExtra("userid",userid);
                    intent.putExtra("friendId",friendid);
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }
    public static class ViewHolder{
        TextView tv_count_head;
        ImageView iv_address_icon;
        TextView atv_address_name;
        TextView atv_address_beizhu;
        LinearLayout ll_address_add;
    }

}
