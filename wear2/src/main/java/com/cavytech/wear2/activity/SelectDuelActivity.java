package com.cavytech.wear2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cavytech.wear2.R;
import com.cavytech.wear2.application.CommonApplication;
import com.cavytech.wear2.base.AppCompatActivityEx;
import com.cavytech.wear2.entity.DBfriendBean;
import com.cavytech.wear2.entity.FriendBean;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.RequestCallback;
import com.cavytech.wear2.util.CircleTransform;
import com.cavytech.wear2.util.PinYinUtils;
import com.cavytech.wear2.util.PinyinComparator2;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Picasso;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 选择对手
 */
public class SelectDuelActivity extends AppCompatActivityEx {
    @ViewInject(R.id.title)
    private TextView title;

    @ViewInject(R.id.back)
    private ImageView back;

    @ViewInject(R.id.go)
    private ImageView go;

    @ViewInject(R.id.lv_select_duel)
    private ListView lv_select_duel;

    private MyListAdapter mAdapter;

    private List<FriendBean.FriendInfosBean> haha;

    private int realposition;

    // private String utlImaget;

    private PinyinComparator2 pinyinComparator = new PinyinComparator2();
    private List<DBfriendBean> dao_haha;
    private DbManager.DaoConfig daoconfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_duel);
        x.view().inject(this);
        setToolBar();

        Intent intent = getIntent();
        realposition = intent.getIntExtra("realposition", -1);

        Log.d("TAG", realposition + "-========realposition");
        haha = new ArrayList<>();
        //初始化数据
//        initdao();

        initdata();

        go.setImageResource(R.drawable.icon_search_select);
        go.setOnClickListener(new MyClockOnClickListener());
        back.setOnClickListener(new MyClockOnClickListener());
        title.setText(R.string.select_duel);

        //设置适配器

        initlistener();
    }

    private void initdao() {
        daoconfig = new DbManager.DaoConfig()
                .setDbName("CB_LIST.db")
//                .setDbDir(new File("/sdcard"))
                .setDbVersion(1);
        CommonApplication.dm = x.getDb(daoconfig);
    }

    private void initlistener() {
        lv_select_duel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplication(),"选择对手",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SelectDuelActivity.this, InvitationDuelActivity.class);

                // Toast.makeText(getApplication(),"utlImaget"+utlImaget, Toast.LENGTH_SHORT).show();
                intent.putExtra("position", position);
                intent.putExtra("utlImaget", haha.get(position).getAvatarUrl());
                intent.putExtra("friendid", haha.get(position).getUserId());
                InvitationDuelActivity.isAdd = true;
                startActivity(intent);
                finish();
            }
        });
    }

    private void initdata() {
        getdatafromnet();
    }

    private void getdatafromnet() {
        HttpUtils.getInstance().getFriendList(new RequestCallback<FriendBean>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(FriendBean response) {

                haha.addAll(response.getFriendInfos());

                Log.e("TAG", "ceshi -----" + haha.size());

                try {
                    testdbdatabase();
                } catch (DbException e) {
                    e.printStackTrace();
                }

                if (haha != null) {
                    Collections.sort(haha, pinyinComparator);
                }
                mAdapter = new MyListAdapter();

                lv_select_duel.setAdapter(mAdapter);
            }
        });
    }


    class MyClockOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v == back) {
                finish();
            } else if (v == go) {
                startActivity(new Intent(SelectDuelActivity.this, SearchInaccurateActivity.class));
            }
        }
    }


    public class MyListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getApplication(), R.layout.select_duel_item_list, null);
                viewHolder = new ViewHolder();

                viewHolder.iv_select_duel_icon = (ImageView) convertView.findViewById(R.id.iv_select_duel_icon);
                viewHolder.tv_select_duel_count = (TextView) convertView.findViewById(R.id.tv_select_duel_count);
                viewHolder.tv_select_duel_name = (TextView) convertView.findViewById(R.id.tv_select_duel_name);
                viewHolder.tv_select_duel_selected = (TextView) convertView.findViewById(R.id.tv_select_duel_selected);
//                if(isCheck == true){
//                    viewHolder.tv_select_duel_selected.setText(R.string.hasselect);
//                }else{
//                    viewHolder.tv_select_duel_selected.setText("");
//                }
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            //utlImaget = haha.get(position).getAvatarUrl();
            if (!"".equals(haha.get(position).getAvatarUrl())) {
                Picasso.with(SelectDuelActivity.this)
                        .load(haha.get(position).getAvatarUrl())
                        .transform(new CircleTransform())
                        .placeholder(R.drawable.head_boy_normal)
                        .error(R.drawable.head_boy_normal)
                        .into(viewHolder.iv_select_duel_icon);
            }
            if (realposition == position) {
                viewHolder.tv_select_duel_selected.setVisibility(View.VISIBLE);
            }
            //Log.d("TAG",utlImaget+"-========utlImaget");

            viewHolder.tv_select_duel_name.setText(haha.get(position).getNickname());

            viewHolder.tv_select_duel_count.setText(PinYinUtils.getFirstLetter(PinYinUtils.getPinYin(haha.get(position).getNickname())));
            return convertView;
        }
    }

    public static class ViewHolder {
        ImageView iv_select_duel_icon;
        TextView tv_select_duel_count;
        TextView tv_select_duel_name;
        TextView tv_select_duel_selected;
    }


    private void testdbdatabase() throws DbException {
        DBfriendBean dd = null;
        for (int i = 0; i < haha.size(); i++) {
            Log.e("TAG", "hahahhaah--" + i);
            dd = new DBfriendBean();
            dd.setIsFollow(haha.get(i).getIsFollow());
            dd.setNickname(haha.get(i).getNickname());
            dd.setPhoneNum(haha.get(i).getPhoneNum());
            dd.setAvatarUrl(haha.get(i).getAvatarUrl());
            dd.setUserId(haha.get(i).getUserId());
            dd.setPinyin(PinYinUtils.getPinYin(haha.get(i).getNickname()));
            CommonApplication.dm.saveOrUpdate(dd);
        }

    }


}
