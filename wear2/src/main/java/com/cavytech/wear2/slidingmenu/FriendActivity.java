package com.cavytech.wear2.slidingmenu;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.BaseSwipListAdapter;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.cavytech.wear2.R;
import com.cavytech.wear2.activity.NewFriendActivity;
import com.cavytech.wear2.activity.RecommandActivity;
import com.cavytech.wear2.activity.SearchInaccurateActivity;
import com.cavytech.wear2.activity.SingleSetailsActivity;
import com.cavytech.wear2.application.CommonApplication;
import com.cavytech.wear2.base.AppCompatActivityEx;
import com.cavytech.wear2.entity.CommonEntity;
import com.cavytech.wear2.entity.DBfriendBean;
import com.cavytech.wear2.entity.FriendBean;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.RequestCallback;
import com.cavytech.wear2.util.CircleTransform;
import com.cavytech.wear2.util.DensityUtil;
import com.cavytech.wear2.util.PinYinUtils;
import com.cavytech.wear2.util.PinyinComparator;
import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Picasso;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FriendActivity extends AppCompatActivityEx {
    private static final int NEWFRIEND = 1;
    private TextView title;
    private ImageView go;
    private ImageView back;
    private MySwipeMenuAdapter mAdapter;
    private SwipeMenuListView smlv_content_list;
    private Gson gson;
    private List<FriendBean.FriendInfosBean> fblist = new ArrayList<FriendBean.FriendInfosBean>();
    private DbManager.DaoConfig daoconfig;
    private PinyinComparator pinyinComparator = new PinyinComparator();
    private List<DBfriendBean> haha;
    private RelativeLayout rl_add_friend;
    private RelativeLayout rl_new_friend;
    private RelativeLayout rl_head_life;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        gson = new Gson();
        setToolBar();

        findView();
        initView();
        mAdapter = new MySwipeMenuAdapter();

//        initdao();

        initDb();

    }

    /**
     * 设置数据库
     */
    private void initDb() {

        try {
            haha = CommonApplication.dm.findAll(DBfriendBean.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (haha != null) {
            Collections.sort(haha, pinyinComparator);
            smlv_content_list.setAdapter(mAdapter);
        } else {
            smlv_content_list.setAdapter(mAdapter);
        }
        getDataFromNet();

        initData();

    }

    private void initdao() {
        daoconfig = new DbManager.DaoConfig()
                .setDbName("CB_LIST.db")
//                .setDbDir(new File("/sdcard"))
                .setDbVersion(1);
        CommonApplication.dm = x.getDb(daoconfig);
    }

    private void findView() {
        smlv_content_list = (SwipeMenuListView) findViewById(R.id.smlv_content_list);
        title = (TextView) findViewById(R.id.title);
        back = (ImageView) findViewById(R.id.back);
        go = (ImageView) findViewById(R.id.go);
//        ll_add_friend_no_data = (LinearLayout) findViewById(R.id.ll_add_friend_no_data);
//        ll_new_friend_no_data = (LinearLayout) findViewById(R.id.ll_new_friend_no_data);
//        ll_tunshu_no_data = (LinearLayout) findViewById(R.id.ll_tunshu_no_data);
//        ll_no_data_layout = (LinearLayout) findViewById(R.id.ll_no_data_layout);

    }

    /**
     * 初始化数据
     */
    private void initData() {
        go.setImageResource(R.drawable.icon_search_select);
        title.setText("联系人");
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FriendActivity.this, SearchInaccurateActivity.class));
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

       /* ll_add_friend_no_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FriendActivity.this, RecommandActivity.class);
                startActivity(i);
            }
        });
        ll_new_friend_no_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FriendActivity.this, RecommandActivity.class);
                startActivity(i);
            }
        });*/
    }

    /**
     * 初始化视图
     */
    private void initView() {
        smlv_content_list.setDivider(null);
        View v1 = View.inflate(FriendActivity.this, R.layout.swipe_item_head_add, null);
        smlv_content_list.addHeaderView(v1);
        rl_add_friend = (RelativeLayout) v1.findViewById(R.id.rl_add_friend);
        rl_add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in1 = new Intent(FriendActivity.this, RecommandActivity.class);
                startActivity(in1);
            }
        });
        View v2 = View.inflate(FriendActivity.this, R.layout.swipe_item_head_new, null);
        smlv_content_list.addHeaderView(v2);
        rl_new_friend = (RelativeLayout) v2.findViewById(R.id.rl_new_friend);
        rl_new_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                Intent in2 = new Intent(FriendActivity.this, NewFriendActivity.class);
//                startActivity(in2);
                startActivityForResult(in2,NEWFRIEND);
            }
        });

        View v3 = View.inflate(FriendActivity.this, R.layout.swipe_item_head_life, null);
        smlv_content_list.addHeaderView(v3);
        rl_head_life = (RelativeLayout) v3.findViewById(R.id.rl_head_life);
        rl_head_life.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FriendActivity.this, "生活豚鼠", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 设置SwipelistView
     */
    private void setSwipeLisrView(final int ifollow) {
        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "pk" item
                SwipeMenuItem pkItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                pkItem.setBackground(new ColorDrawable(Color.rgb(0xff, 0x9f,
                        0x22)));
                // set item width
                pkItem.setWidth(DensityUtil.dip2px(FriendActivity.this, 66));
                // set item title
                pkItem.setTitle("PK");
                pkItem.setTitleColor(Color.parseColor("#ffffff"));
                // set item title fontsize
                pkItem.setTitleSize(16);
                // set item title font color
                pkItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(pkItem);
                // create "attention" item
                SwipeMenuItem attentionItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                attentionItem.setBackground(new ColorDrawable(Color.rgb(0xff,
                        0x56, 0x56)));

                if (ifollow == 0) {
                    attentionItem.setTitle("关注");
                } else {
                    attentionItem.setTitle("取消关注");
                }
                attentionItem.setTitleColor(Color.parseColor("#ffffff"));

                attentionItem.setTitleSize(16);
                // set item width
                attentionItem.setWidth(DensityUtil.dip2px(FriendActivity.this, 100));
                // set a icon
                //attentionItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(attentionItem);

                // create "deleteItem" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xdb,
                        0xdb, 0xdb)));
                deleteItem.setTitle("删除");

                deleteItem.setTitleSize(16);

                deleteItem.setTitleColor(Color.parseColor("#ffffff"));
                // set item width
                deleteItem.setWidth(DensityUtil.dip2px(FriendActivity.this, 66));
                // set a icon
                //attentionItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        // set creator
        smlv_content_list.setMenuCreator(creator);

        // Left
        smlv_content_list.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        //侧滑出的PK 删除等按钮点击
        smlv_content_list.setOnMenuItemClickListener(new MyOnMenuItemClickListener());

        //状态改变
        smlv_content_list.setOnMenuStateChangeListener(new SwipeMenuListView.OnMenuStateChangeListener() {
            @Override
            public void onMenuOpen(int position) {
            }

            @Override
            public void onMenuClose(int position) {
            }
        });

        smlv_content_list.setOnItemClickListener(new MyOnItemClickListener());
    }

    /**
     * 联网请求数据
     */
    private void getDataFromNet() {

        HttpUtils.getInstance().getFriendList(new RequestCallback<FriendBean>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(FriendBean response) {

                if(response.isSuccess()){
                    fblist.addAll(response.getFriendInfos());
                }else{

                }
                try {
                    testdbdatabase();
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 测试数据库
     */
    private void testdbdatabase() throws DbException {
        DBfriendBean dd = null;
        for (int i = 0; i < fblist.size(); i++) {
            Log.e("TAG", "hahahhaah--" + i);
            dd = new DBfriendBean();
            dd.setIsFollow(fblist.get(i).getIsFollow());
            dd.setNickname(fblist.get(i).getNickname());
            dd.setPhoneNum(fblist.get(i).getPhoneNum());
            dd.setAvatarUrl(fblist.get(i).getAvatarUrl());
            dd.setUserId(fblist.get(i).getUserId());
            dd.setRemark(fblist.get(i).getRemark());
            dd.setPinyin(PinYinUtils.getPinYin(fblist.get(i).getNickname()));
            CommonApplication.dm.saveOrUpdate(dd);
        }
        /**
         * 测试数据库数据
         */
        haha = CommonApplication.dm.findAll(DBfriendBean.class);
//        Log.e("TAG","测试haha----"+haha.get(0).getPinyin());
        if (haha != null) {
            Collections.sort(haha, pinyinComparator);
        }

//        mAdapter.notifyDataSetChanged();
        smlv_content_list.setAdapter(mAdapter);
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent;
            if (position == 0) {
                intent = new Intent(FriendActivity.this, RecommandActivity.class);
                startActivity(intent);
            } else if (position == 1) {
                intent = new Intent(FriendActivity.this, NewFriendActivity.class);
                startActivity(intent);
                //Toast.makeText(FriendActivity.this,"新的朋友",Toast.LENGTH_SHORT).show();
            } else if (position == 2) {
                Toast.makeText(FriendActivity.this, "生活豚鼠", Toast.LENGTH_SHORT).show();
            } /*else {
                Intent i = new Intent();
                i.setClass(FriendActivity.this, SingleSetailsActivity.class);
                startActivity(i);
            }*/
        }
    }

    class MySwipeMenuAdapter extends BaseSwipListAdapter {
        @Override
        public int getCount() {
            if (haha == null) {
                return 0;
            }
            return haha.size();
        }

        @Override
        public Object getItem(int position) {
            return haha.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(FriendActivity.this, R.layout.swipe_item_layout, null);

                new ViewHolder(convertView);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();

           /* if(!haha.get(position).getRemark().equals("")){
                holder.tv_swipe_name.setText(haha.get(position).getRemark());
                holder.tv_friend_swipe_count.setText(PinYinUtils.getFirstLetter(PinYinUtils.getPinYin(haha.get(position).getRemark())));
            }else {
                holder.tv_swipe_name.setText(haha.get(position).getNickname());
                holder.tv_friend_swipe_count.setText(PinYinUtils.getFirstLetter(PinYinUtils.getPinYin(haha.get(position).getNickname())));
            }*/

            holder.tv_swipe_name.setText(haha.get(position).getNickname());
            holder.tv_friend_swipe_count.setText(PinYinUtils.getFirstLetter(PinYinUtils.getPinYin(haha.get(position).getNickname())));

            //用户头像
            if (!"".equals(haha.get(position).getAvatarUrl())) {
                Picasso.with(FriendActivity.this)
                        .load(haha.get(position).getAvatarUrl())
                        .transform(new CircleTransform())
                        .placeholder(R.drawable.head_boy_normal)
                        .error(R.drawable.head_boy_normal)
                        .into(holder.iv_swipe_icon);
            }


            holder.rl_swipe_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(FriendActivity.this, SingleSetailsActivity.class);
                    i.putExtra("fid",haha.get(position).getUserId());
                    i.putExtra("name",haha.get(position).getNickname());
                    startActivity(i);
                }
            });



            /**
             * 是否关注
             */
            int isfollow = haha.get(position).getIsFollow();

            if (isfollow == 0) {//未关注
                holder.iv_friend_care.setVisibility(View.GONE);
            } else if (isfollow == 1) {
                holder.iv_friend_care.setVisibility(View.VISIBLE);
            }

         //   fdid = haha.get(fdposition).getUserId();

            setSwipeLisrView(isfollow);

            return convertView;
        }

        class ViewHolder {

            TextView tv_swipe_name;
            ImageView iv_swipe_icon;
            ImageView iv_friend_care;
            TextView tv_friend_swipe_count;
            RelativeLayout rl_swipe_item;

            public ViewHolder(View view) {

                tv_swipe_name = (TextView) view.findViewById(R.id.tv_swipe_name);
                tv_friend_swipe_count = (TextView) view.findViewById(R.id.tv_friend_swipe_count);
                iv_swipe_icon = (ImageView) view.findViewById(R.id.iv_swipe_icon);
                iv_friend_care = (ImageView) view.findViewById(R.id.iv_friend_care);
                rl_swipe_item = (RelativeLayout) view.findViewById(R.id.rl_swipe_item);

                view.setTag(this);
            }
        }

        /**
         * 是否能移动
         *
         * @param position
         * @return
         */
        @Override
        public boolean getSwipEnableByPosition(int position) {
            return super.getSwipEnableByPosition(position);
        }
    }


    class MyOnMenuItemClickListener implements SwipeMenuListView.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {

            String fdid = haha.get(position).getUserId();
            switch (index) {
                case 0:
                    // pk
                    Toast.makeText(FriendActivity.this, "PK", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    boolean isFollow = haha.get(position).getIsFollow() == 0 ? true : false;

                    /**
                     * 取消关注  或关注
                     */
                    HttpUtils.getInstance().followFriend(fdid, isFollow, new RequestCallback<CommonEntity>() {
                        @Override
                        public void onError(Request request, Exception e) {

                        }

                        @Override
                        public void onResponse(CommonEntity response) {

                            if(response.isSuccess()){

                            }else{
                                Toast.makeText(FriendActivity.this, response.getMsg(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    break;
                case 2:
                    //删除
                    HttpUtils.getInstance().deleteFriend(fdid, new RequestCallback<CommonEntity>() {
                        @Override
                        public void onError(Request request, Exception e) {

                        }

                        @Override
                        public void onResponse(CommonEntity response) {

                            if(response.isSuccess()){
                                try {
                                    DBfriendBean dbf = CommonApplication.dm.selector(DBfriendBean.class).where("id", "=", haha.get(position).getUserId()).findFirst();
                                    CommonApplication.dm.delete(dbf);
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                                haha.remove(position);
                                mAdapter.notifyDataSetChanged();
                            }else{
                                Toast.makeText(FriendActivity.this, response.getMsg(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    break;
            }
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==NEWFRIEND){
//            Toast.makeText(FriendActivity.this,"回调啦--",Toast.LENGTH_SHORT).show();
            getDataFromNet();
        }
    }
}
