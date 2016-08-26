package com.cavytech.wear2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
 * 待回应----pk
 */
public class PkStateDaiHuiYingActivity extends AppCompatActivity {

    private String pkId;
    private TextView textView8;
    private String friendicon;
    private ImageView iv_other_huiying_state;
    private TextView tv_pk_daihuiying_fdicon;
    private String friendname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pk_state_dai_hui_ying);

        textView8 = (TextView) findViewById(R.id.textView8);
        iv_other_huiying_state = (ImageView)findViewById(R.id.iv_other_huiying_state);
        tv_pk_daihuiying_fdicon = (TextView)findViewById(R.id.tv_pk_daihuiying_fdicon);

        Intent intent = getIntent();
        pkId = intent.getStringExtra("pkId");
        friendicon = intent.getStringExtra("friendicon");
        friendname = intent.getStringExtra("friendname");

        getdatafromnet();
    }

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
                    Picasso.with(PkStateDaiHuiYingActivity.this)
                            .load(friendicon)
                            .transform(new CircleTransform())
                            .placeholder(R.drawable.head_boy_normal)
                            .error(R.drawable.head_boy_normal)
                            .into(iv_other_huiying_state);
                }
                tv_pk_daihuiying_fdicon.setText(friendname);
            }
        });


    }
}
