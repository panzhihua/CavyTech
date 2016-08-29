package com.cavytech.wear2.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.basecore.widget.SystemBarTintManager;
import com.cavytech.wear2.R;
import com.cavytech.wear2.entity.PklistBean;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.RequestCallback;
import com.cavytech.wear2.util.CircleTransform;
import com.cavytech.widget.NoScrollListview;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Picasso;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * PK列表
 */
public class PkAtateActivity extends Activity {
    private List<PklistBean.WaitListInfo> waitList;
    private List<PklistBean.DueListInfo> goingList;
    private List<PklistBean.FinishListInfo> successList;

    @ViewInject(R.id.title)
    private TextView title;

    @ViewInject(R.id.back)
    private ImageView back;

    @ViewInject(R.id.go)
    private ImageView go;

    @ViewInject(R.id.lv_wait_pk_state)
    private NoScrollListview lv_wait_pk_state;

    @ViewInject(R.id.lv_going_pk_state)
    private NoScrollListview lv_going_pk_state;

    @ViewInject(R.id.lv_success_pk_state)
    private NoScrollListview lv_success_pk_state;

    @ViewInject(R.id.iv_start_pk)
    private LinearLayout iv_start_pk;


    private MyWaitAdapter waitadapter;
    public MyGoingAdapter goingadapter;
    private MySuccessAdapter successadapter;
    private String fdid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.top_bg_color);//通知栏所需颜色
            tintManager.setNavigationBarTintEnabled(true);
            tintManager.setNavigationBarTintResource(R.color.black);
        }
        setContentView(R.layout.activity_pk_atate);
        x.view().inject(this);

        title.setText(R.string.pk);
        go.setImageResource(R.drawable.icon_attention);
        back.setOnClickListener(new MyClockOnClickListener());
        go.setOnClickListener(new MyClockOnClickListener());

        initView();

        initData();

        initlistener();

        lv_wait_pk_state.addHeaderView(View.inflate(PkAtateActivity.this, R.layout.daihuiying, null));
//        lv_wait_pk_state.setAdapter(new MyWaitAdapter());
        lv_going_pk_state.addHeaderView(View.inflate(PkAtateActivity.this, R.layout.jinxinzhong, null));
//        lv_going_pk_state.setAdapter(new MyGoingAdapter());
        lv_success_pk_state.addHeaderView(View.inflate(PkAtateActivity.this, R.layout.complete, null));
//        lv_success_pk_state.setAdapter(new MySuccessAdapter());

        //待回应item点击事件
        lv_wait_pk_state.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    return;
                }
                Intent intent = new Intent(PkAtateActivity.this, PkStateDaiHuiYingActivity.class);
                intent.putExtra("pkId",waitList.get(position-1).getPkId());
                intent.putExtra("friendicon",waitList.get(position-1).getAvatarUrl());
                intent.putExtra("friendname",waitList.get(position-1).getNickname());
                startActivity(intent);
            }
        });

        //进行中待回应item点击事件
        lv_going_pk_state.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PkAtateActivity.this, PkStateDaijingxinActivity.class);
                intent.putExtra("pkId",goingList.get(position-1).getPkId());
                intent.putExtra("friendicon",goingList.get(position-1).getAvatarUrl());
                intent.putExtra("friendname",goingList.get(position-1).getNickname());
                startActivity(intent);
            }
        });

        //已完成item点击事件
        lv_success_pk_state.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(PkAtateActivity.this, PkStateSuccessYingActivity.class));
            }
        });

    }

    private void initlistener() {

        iv_start_pk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(PkAtateActivity.this,InvitationDuelActivity.class));
            }
        });
    }

    private void initData() {

        waitList = new ArrayList();
        goingList = new ArrayList();
        successList = new ArrayList();

        Intent intent = getIntent();
        fdid = intent.getStringExtra("fdid");

        if(fdid!=null){
            iv_start_pk.setVisibility(View.INVISIBLE);
            getfriendpklist();
        }else {
            getpklist();
        }
    }

    private void getfriendpklist() {
        HttpUtils.getInstance().getfriendPkList(fdid,new RequestCallback<PklistBean>() {
            @Override
            public void onError(Request request, Exception e) {
                Log.e("TAG","Pk------++++onError");
            }

            @Override
            public void onResponse(PklistBean pkbean) {

                if(pkbean.getDueList()!=null){
                    goingList.addAll(pkbean.getDueList());
                    goingadapter = new MyGoingAdapter();
                    lv_going_pk_state.setAdapter(goingadapter);
                }
            }
        });
    }

    private void getpklist() {
        HttpUtils.getInstance().getPkList(new RequestCallback<PklistBean>() {

            @Override
            public void onError(Request request, Exception e) {
                Log.e("TAG","Pk------++++onError");
            }

            @Override
            public void onResponse(PklistBean pkbean) {
                if(waitList.size()!=0){
                    waitList.clear();
                }
                if(goingList.size()!=0){
                    goingList.clear();
                }
                if(successList.size()!=0){
                    successList.clear();
                }

                waitList.addAll(pkbean.getWaitList());
                goingList.addAll(pkbean.getDueList());
                successList.addAll(pkbean.getFinishList());

//                if(waitList.size()==0&&goingList.size()==0&&successList.size()==0){
//                    startActivity(new Intent(PkAtateActivity.this, PkActivity.class));
//                }


//                goingList = new ArrayList();
//                goingList.addAll(pkbean.getWaitList());

                waitadapter = new MyWaitAdapter();
                lv_wait_pk_state.setAdapter(waitadapter);
                goingadapter = new MyGoingAdapter();
                lv_going_pk_state.setAdapter(goingadapter);
                successadapter = new MySuccessAdapter();
                lv_success_pk_state.setAdapter(successadapter);
                if(waitList.size()==0){
                    lv_wait_pk_state.setVisibility(View.GONE);
                }
                if(goingList.size()==0){
                    lv_going_pk_state.setVisibility(View.GONE);
                }
                if(successList.size()==0){
                    lv_success_pk_state.setVisibility(View.GONE);
                }


            }
        });
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void initView() {
        lv_wait_pk_state.setDivider(null);
        lv_going_pk_state.setDivider(null);
        lv_success_pk_state.setDivider(null);
    }

    class MyClockOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v == back) {
                finish();
            } else if (v == go) {
                finish();
                startActivity(new Intent(PkAtateActivity.this, PkHelpActivity.class));
            }
        }
    }

    /**
     * 待回应
     */
    class MyWaitAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return waitList.size();
        }

        @Override
        public Object getItem(int position) {
            return waitList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolderWait viewHolderWait = null;
            if (convertView == null) {
                convertView = View.inflate(PkAtateActivity.this, R.layout.pk_state_daihuiying, null);

                viewHolderWait = new ViewHolderWait();

                viewHolderWait.ll_new_pk_state_chexiao = (LinearLayout) convertView.findViewById(R.id.ll_new_pk_state_chexiao);
                viewHolderWait.tv_state_huiyin_name = (TextView) convertView.findViewById(R.id.tv_state_huiyin_name);
                viewHolderWait.tv_pk_state_huiyin_beizhu = (TextView) convertView.findViewById(R.id.tv_pk_state_huiyin_beizhu);
                viewHolderWait.tv_pk_daihuiying = (TextView) convertView.findViewById(R.id.tv_pk_daihuiying);
                viewHolderWait.iv_state_huiyin_icon = (ImageView)convertView.findViewById(R.id.iv_state_huiyin_icon);

                convertView.setTag(viewHolderWait);
            } else {
                viewHolderWait = (ViewHolderWait) convertView.getTag();
            }
            int type = waitList.get(position).getType();
            if(type==0){//等待对方接受，撤销
                viewHolderWait.tv_pk_daihuiying.setText("撤销");
            }else if(type==1){//对方等你接受，同意
                viewHolderWait.tv_pk_daihuiying.setText("接受");
            }

            if (!"".equals(waitList.get(position).getAvatarUrl())) {
                Picasso.with(PkAtateActivity.this)
                        .load(waitList.get(position).getAvatarUrl())
                        .transform(new CircleTransform())
                        .placeholder(R.drawable.head_boy_normal)
                        .error(R.drawable.head_boy_normal)
                        .into(viewHolderWait.iv_state_huiyin_icon);
            }

            if(waitList.get(position).getNickname()!=null){
                viewHolderWait.tv_state_huiyin_name.setText(waitList.get(position).getNickname());
            }
            viewHolderWait.tv_pk_state_huiyin_beizhu.setText(waitList.get(position).getPkDuration());

            viewHolderWait.ll_new_pk_state_chexiao.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int type = waitList.get(position).getType();
                    if(type==0){//撤销
                        String[] p = {waitList.get(position).getPkId()};
                        post2chexiao(p,position);
                    }else if(type==1){//接受
                        post2jieshou(waitList.get(position).getPkId(),position);
                    }
                }
            });

            return convertView;
        }
    }

    /**
     * 接受Pk
     */
    private void post2jieshou(String pkid, final int position) {
        HttpUtils.getInstance().acceptPk(pkid, new RequestCallback<Object>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(Object response) {
                Log.e("TAG","PK接受----"+response.toString());
               getpklist();
            }
        });

    }

    /**
     * 撤销
     */
    private void post2chexiao(String[] pkid, final int position) {
        HttpUtils.getInstance().undoPk(pkid, new RequestCallback<Object>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(Object response) {
                Log.e("TAG","PK撤销----"+response.toString());
                waitList.remove(position);
                waitadapter.notifyDataSetChanged();
            }
        });

    }

    static class ViewHolderWait {
        private ImageView iv_state_huiyin_icon;
        private TextView tv_state_huiyin_name;
        private TextView tv_pk_state_huiyin_beizhu;
        private LinearLayout ll_new_pk_state_chexiao;
        private TextView tv_pk_daihuiying;

    }

    /**
     * 进行中
     */
    class MyGoingAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return goingList.size();
        }

        @Override
        public Object getItem(int position) {
            return goingList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolderGoing viewHolderGoing = null;
            if (convertView == null) {
                convertView = View.inflate(PkAtateActivity.this, R.layout.pk_state_jinxinzhong, null);
                viewHolderGoing = new ViewHolderGoing();
                viewHolderGoing.tv_pk_state_jinxing_name = (TextView) convertView.findViewById(R.id.tv_pk_state_jinxing_name);
                viewHolderGoing.tv_pk_state_jinxing_beizhu = (TextView) convertView.findViewById(R.id.tv_pk_state_jinxing_beizhu);
                viewHolderGoing.iv_pk_state_jinxing_icon = (ImageView) convertView.findViewById(R.id.iv_pk_state_jinxing_icon);
                convertView.setTag(viewHolderGoing);
            } else {
                viewHolderGoing = (ViewHolderGoing) convertView.getTag();
            }

            if (!"".equals(goingList.get(position).getAvatarUrl())) {
                Picasso.with(PkAtateActivity.this)
                        .load(goingList.get(position).getAvatarUrl())
                        .transform(new CircleTransform())
                        .placeholder(R.drawable.head_boy_normal)
                        .error(R.drawable.head_boy_normal)
                        .into(viewHolderGoing.iv_pk_state_jinxing_icon);
            }

            if(goingList.get(position).getNickname()!=null){
                viewHolderGoing.tv_pk_state_jinxing_name.setText(goingList.get(position).getNickname());
            }
            viewHolderGoing.tv_pk_state_jinxing_beizhu.setText(goingList.get(position).getPkDuration());

            return convertView;
        }
    }


    static class ViewHolderGoing {
        private ImageView iv_pk_state_jinxing_icon;
        private TextView tv_pk_state_jinxing_name;
        private TextView tv_pk_state_jinxing_beizhu;
    }


    /**
     * 完成
     */
    class MySuccessAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return successList.size();
        }

        @Override
        public Object getItem(int position) {
            return successList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolderSucccess viewHolderSucccess = null;
            if (convertView == null) {
                convertView = View.inflate(PkAtateActivity.this, R.layout.pk_state_complete, null);
                viewHolderSucccess = new ViewHolderSucccess();
                convertView.setTag(viewHolderSucccess);
                viewHolderSucccess.ll_new_pk_state_chexiao = (LinearLayout) convertView.findViewById(R.id.ll_new_pk_state_chexiao);
                viewHolderSucccess.iv_state_complete_icon = (ImageView) convertView.findViewById(R.id.iv_state_complete_icon);
                viewHolderSucccess.tv_pk_state_complete_beizhu = (TextView) convertView.findViewById(R.id.tv_pk_state_complete_beizhu);
                viewHolderSucccess.tv_state_complete_name = (TextView) convertView.findViewById(R.id.tv_state_complete_name);
            } else {
                viewHolderSucccess = (ViewHolderSucccess) convertView.getTag();
            }

            if (!"".equals(successList.get(position).getAvatarUrl())) {
                Picasso.with(PkAtateActivity.this)
                        .load(successList.get(position).getAvatarUrl())
                        .transform(new CircleTransform())
                        .placeholder(R.drawable.head_boy_normal)
                        .error(R.drawable.head_boy_normal)
                        .into(viewHolderSucccess.iv_state_complete_icon);
            }

            if(successList.get(position).getNickname()!=null){
                viewHolderSucccess.tv_state_complete_name.setText(successList.get(position).getNickname());
            }
            viewHolderSucccess.tv_pk_state_complete_beizhu.setText(successList.get(position).getPkDuration());


            viewHolderSucccess.ll_new_pk_state_chexiao.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(PkAtateActivity.this, "再战", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PkAtateActivity.this,InvitationDuelActivity.class);
                    intent.putExtra("utlImaget",successList.get(position).getAvatarUrl());
                    intent.putExtra("friendid",successList.get(position).getUserId());
                    startActivity(intent);
                }
            });

            return convertView;
        }
    }

    static class ViewHolderSucccess {
        private ImageView iv_state_complete_icon;
        private TextView tv_state_complete_name;
        private TextView tv_pk_state_complete_beizhu;
        private LinearLayout ll_new_pk_state_chexiao;
    }
}
