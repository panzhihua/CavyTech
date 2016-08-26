package com.cavytech.wear2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cavytech.wear2.R;
import com.cavytech.wear2.entity.PkInfo;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.RequestCallback;
import com.cavytech.wear2.util.CircleTransform;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Picasso;

/**
 * 进行中-----pk
 */
public class PkStateDaijingxinActivity extends AppCompatActivity {

    private String pkId;
    private String friendicon;
    private String friendname;
    private TextView textView8;
    private ImageView iv_other_huiying_state;
    private TextView tv_pk_daijingxin_name;
    private TextView tv_pk_daijingxin_me;
    private TextView tv_pk_daijingxin_you;
    private ImageView iv_myself_huiying_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pk_state_daijingxin);

        textView8 = (TextView) findViewById(R.id.textView8);
        iv_other_huiying_state = (ImageView) findViewById(R.id.iv_other_huiying_state);
        tv_pk_daijingxin_name = (TextView) findViewById(R.id.tv_pk_daijingxin_name);
        tv_pk_daijingxin_me = (TextView) findViewById(R.id.tv_pk_daijingxin_me);
        tv_pk_daijingxin_you = (TextView) findViewById(R.id.tv_pk_daijingxin_you);
        iv_myself_huiying_state = (ImageView)findViewById(R.id.iv_myself_huiying_state);

        Intent intent = getIntent();
        pkId = intent.getStringExtra("pkId");
        friendicon = intent.getStringExtra("friendicon");
        friendname = intent.getStringExtra("friendname");

        getdatafromnet();
    }

    /**
     *
     */
    private void getdatafromnet() {

        HttpUtils.getInstance().getPKInfo(pkId, new RequestCallback<PkInfo>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(PkInfo response) {
                int isallow = response.getIsAllowWatch();

                if (isallow == 0) {
                    textView8.setText("好友不可见");
                } else if (isallow == 1) {
                    textView8.setText("好友可见");
                }
                if (!"".equals(friendicon)) {
                    Picasso.with(PkStateDaijingxinActivity.this)
                            .load(friendicon)
                            .transform(new CircleTransform())
                            .placeholder(R.drawable.head_boy_normal)
                            .error(R.drawable.head_boy_normal)
                            .into(iv_other_huiying_state);
                }
                tv_pk_daijingxin_name.setText(friendname);
                tv_pk_daijingxin_me.setText(response.getUserStepCount() + "");
                tv_pk_daijingxin_you.setText(response.getFriendStepCount() + "");

                ViewGroup.LayoutParams para = iv_myself_huiying_state.getLayoutParams();
                ViewGroup.LayoutParams para_you = iv_other_huiying_state.getLayoutParams();

                //判断谁胜利
                if(response.getUserStepCount()>response.getFriendStepCount()){
                    para.height=140;
                    para.width=140;
                    iv_myself_huiying_state.setLayoutParams(para);
                }else if(response.getUserStepCount()==response.getFriendStepCount()){
//                    para.height=140;
//                    para.width=140;
                }else if(response.getUserStepCount()<response.getFriendStepCount()){
                    para_you.height=140;
                    para_you.width=140;
                    iv_other_huiying_state.setLayoutParams(para_you);
                }

            }
        });


    }
}
