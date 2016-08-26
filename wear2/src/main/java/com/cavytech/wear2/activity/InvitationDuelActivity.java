package com.cavytech.wear2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cavytech.wear2.R;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.Req.LaunchPkReq;
import com.cavytech.wear2.http.RequestCallback;
import com.cavytech.wear2.util.CircleTransform;
import com.cavytech.widget.TextPick;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.callback.StringCallback;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 邀请对决
 */
public class InvitationDuelActivity extends AppCompatActivity implements TextPick.OnValueChangeListener{
    @ViewInject(R.id.back)
    private ImageView back;

    @ViewInject(R.id.title)
    private TextView title;

    @ViewInject(R.id.tp_select_splite)
    TextPick tp_select_splite;

    @ViewInject(R.id.iv_rival_add)
    ImageView iv_rival_add;

    @ViewInject(R.id.rg_invitation)
    RadioGroup rg_invitation;

    @ViewInject(R.id.rb_pk_detail_1)
    RadioButton rb_pk_detail_1;

    @ViewInject(R.id.rb_pk_detail_2)
    RadioButton rb_pk_detail_2;

    @ViewInject(R.id.iv_pk_next)
    ImageView iv_pk_next;

    private int position;

    public static boolean isAdd = false;

    private String utlImaget;

    private int pkTime;

    private String pkDuration = "3";
    
    private ArrayList<String> dayLone;//  发起PK时长

    private int isAllowWatch;//	是否好友可见 1: 好友可见，0:好友不可见
    private String friendid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation_duel);
        x.view().inject(this);

        Intent intent = getIntent();
        position = intent.getIntExtra("position",-1);
        utlImaget = intent.getStringExtra("utlImaget");
        friendid = intent.getStringExtra("friendid");

        if(position != -1){
//            if(isAdd){
                if (!"".equals(utlImaget)) {
                    Picasso.with(InvitationDuelActivity.this).load(utlImaget).transform(new CircleTransform()).into(iv_rival_add);

                    Picasso.with(InvitationDuelActivity.this)
                            .load(utlImaget)
                            .transform(new CircleTransform())
                            .placeholder(R.drawable.head_boy_normal)
                            .error(R.drawable.head_boy_normal)
                            .into(iv_rival_add);
                }else{
                    iv_rival_add.setImageResource(R.drawable.head_boy_normal);
                    Toast.makeText(InvitationDuelActivity.this,"获取头像失败",Toast.LENGTH_SHORT).show();
                }
        }

        initNumberPicker();

        isFriendVisiable();

        getDateFromnate();

        back.setOnClickListener(new MyonClickListener());
        iv_pk_next.setOnClickListener( new MyonClickListener());
        iv_rival_add.setOnClickListener(new MyonClickListener());
        title.setText(R.string.inviactionDuel);

    }

    /**
     * 是否好友可见
     */
    private void isFriendVisiable() {
        rb_pk_detail_1.setChecked(true);

        rg_invitation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == rb_pk_detail_1.getId()){
                    isAllowWatch = 0;
                }else if(checkedId == rb_pk_detail_2.getId()){
                    isAllowWatch = 1;
                }
            }
        });

        if(rb_pk_detail_1.isChecked()){
            isAllowWatch = 0;
        }else if(rb_pk_detail_2.isChecked()){
            isAllowWatch = 1;
        }
        //Toast.makeText(InvitationDuelActivity.this,""+isAllowWatch,Toast.LENGTH_SHORT).show();
       // Log.e("TAG",  "isAllowWatch---"+isAllowWatch);

    }

    void initNumberPicker(){

        dayLone = new ArrayList<>();

        dayLone.add("1");
        dayLone.add("3");
        dayLone.add("7");

        tp_select_splite.setTextAttrs(45,30,
                getResources().getColor(R.color.text_b3_color),
                getResources().getColor(R.color.text_4d_color),
                false);
        tp_select_splite.initViewParam(dayLone, dayLone.size() / 2);
        tp_select_splite.setValueChangeListener(this);
    }

    @Override
    public void onValueChange(View wheel, float value) {
       // Log.e("TAG",value + "");
    }

    @Override
    public void onValueChangeEnd(View wheel, float value) {
        //Toast.makeText(this, "pkTime-----"+pkTime, Toast.LENGTH_SHORT).show();

        pkTime = (int) value;
       // Log.e("TAG","pkTime-----"+pkTime);
        pkDuration = dayLone.get(pkTime);
       // Log.e("TAG"," dayLone.get(pkTime);-----"+pkDuration);
    }

    class MyonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(v == back){
                finish();
            }else if(v == iv_rival_add){
                Log.d("TAG",position+"-========");
                startActivity(new Intent(InvitationDuelActivity.this,SelectDuelActivity.class).putExtra("realposition",position));
                finish();
            }else if(v == iv_pk_next){
                if(utlImaget == null){
                    Toast.makeText(InvitationDuelActivity.this, "请选择对手~哦~", Toast.LENGTH_SHORT).show();
                }else{
                    Log.d("TAG","utlImaget--"+utlImaget+"============="+"pkDuration----"+pkDuration+"======="+"isAllowWatch"+isAllowWatch);
                    Toast.makeText(InvitationDuelActivity.this, "utlImaget--"+utlImaget+"============="
                            +"pkDuration----"+pkDuration+"======="+"isAllowWatch"+isAllowWatch, Toast.LENGTH_SHORT).show();

                    ArrayList<LaunchPkReq.PkListEntity> pelist = new ArrayList<LaunchPkReq.PkListEntity>();
                    LaunchPkReq.PkListEntity pkbean = new LaunchPkReq.PkListEntity(friendid,pkDuration,isAllowWatch);
                    pelist.add(pkbean);
                    Log.e("TAG","测试pklist----"+pelist.size());
                    post2net(pelist);
                    startActivity(new Intent(InvitationDuelActivity.this, PkAtateActivity.class));
                    finish();
                }


            }
        }
    }

    /**
     * 发起PK，数据传递给后台
     */
    private void post2net(ArrayList<LaunchPkReq.PkListEntity> pelist) {

        HttpUtils.getInstance().launchPK(pelist, new RequestCallback<Object>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(Object response) {
                Log.e("TAG","发起pk----"+response.toString());
                /**
                 * 这里需要code的判断
                 */
            }
        });

    }

    /**
     * 联网
     */
    private void getDateFromnate() {
//        HashMap<String, String> parms = HttpUtils.getcommon(HttpUtils.METHOD_LAUNCHPK);
//
//        parms.put(HttpUtils.Param_USERID, "57198c07d3463513589c0277");
//
//        String json = "{\\\"launchPkList\\\":[{\\\"friendId\\\":\\\"56fb8eadd346350ad85c818d\\\",\\\"launchTime\\\": \\\"2016-04-17 16:50:49\\\",\\\"pkDuration\\\": \\\"3\\\",\\\"isAllowWatch\\\": true}]}";
//
//        parms.put(HttpUtils.LAUNCHPKLIST,json);
//
//        parms.put(HttpUtils.Param_FRIENDID,"56d94c3dd346350e387cc2a6");
//
//        parms.put(HttpUtils.PKDURATION,"3");
//
//        parms.put(HttpUtils.ISALLOWWATCH,"1");
//
//        OkHttpUtils.post()
//                .url(HttpUtils.SERVICE)
//                .params(parms)
//                .build()
//                .execute(new InvitationCallback());
    }

    class InvitationCallback extends StringCallback {

        @Override
        public void onError(Request request, Exception e) {
            Log.e("TAG", "InvitationCallback----onError ----");
        }

        @Override
        public void onResponse(String response) {
            Log.e("TAG", "InvitationCallback" + response.toString());

        }
    }
}
