package com.cavytech.wear2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cavytech.wear2.R;
import com.cavytech.wear2.base.AppCompatActivityEx;
import com.cavytech.wear2.entity.FriendDetailBean;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.RequestCallback;
import com.cavytech.wear2.util.CacheUtils;
import com.cavytech.wear2.util.CircleTransform;
import com.cavytech.wear2.util.Constants;
import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Picasso;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 陌生人信息详情
 */
public class SingleSetailsMoShengActivity extends AppCompatActivityEx {
    private static final int EXBEIZHU = 1;//修改好友备注

    @ViewInject(R.id.back)
    private ImageView back;
    @ViewInject(R.id.title)
    private TextView title;

    @ViewInject(R.id.imageView)
    private ImageView imageView;

    @ViewInject(R.id.tv_name)
    private TextView tv_name;

    @ViewInject(R.id.tv_single_city)
    private TextView tv_single_city;


    @ViewInject(R.id.tv_single_sex)
    private TextView tv_single_sex;

    @ViewInject(R.id.tv_single_hight)
    private TextView tv_single_hight;


    @ViewInject(R.id.tv_mingcheng)
    private TextView tv_mingcheng;

    @ViewInject(R.id.tv_name2)
    private TextView tv_name2;

    @ViewInject(R.id.iv_single_chengjiu)
    private ImageView iv_single_chengjiu;

    @ViewInject(R.id.iv_rival_add)
    private ImageView iv_rival_add;

    private List list = new ArrayList();
    private String fid;
    private String userid;
    private Gson gson;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_setails_mosheng);
        x.view().inject(this);

        setToolBar();
        gson = new Gson();

        back.setOnClickListener(new MyOnClickListener());
        title.setText(R.string.contantperson);

        Intent i = getIntent();
        fid = i.getStringExtra("fid");
        name = i.getStringExtra("name");

        Log.e("TAG", "传递的fid----" + fid);
        userid = CacheUtils.getString(this, Constants.USERID);

        initlistener();

        getdatafromnet();


    }

    private void initlistener() {

       /* tv_single_pk_into.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SingleSetailsMoShengActivity.this,PkAtateActivity.class);
                intent.putExtra("fdid",fid);
                startActivity(intent);
            }
        });*/

/*        tv_mingcheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SingleSetailsMoShengActivity.this, BeizhuActivity.class);
                intent.putExtra("friendId", fid);
                startActivityForResult(intent, EXBEIZHU);

//                Toast.makeText(SingleSetailsActivity.this,"修改备注",Toast.LENGTH_SHORT).show();

            }
        });*/

        iv_rival_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SingleSetailsMoShengActivity.this,"点击了",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(SingleSetailsMoShengActivity.this,VerficatonActivity.class);
                intent.putExtra("userid",userid);
                intent.putExtra("friendId",fid);
                finish();
                startActivity(intent);

            }
        });

        iv_single_chengjiu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SingleSetailsMoShengActivity.this,ChengjiuActivity.class));
            }
        });
    }


    private void getdatafromnet() {

        HttpUtils.getInstance().getFriendInfo("56fb8eadd346350ad85c818d", new RequestCallback<FriendDetailBean>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(FriendDetailBean response) {
                Log.e("TAG","陌生人详情 ----"+response.getCode());
                if (response.isSuccess()) {
                    initdata(response);
                }
            }
        });
    }

    private void initdata(FriendDetailBean fb) {
        if (!fb.getAvatarUrl().equals("")) {
            Picasso.with(SingleSetailsMoShengActivity.this)
                    .load(fb.getAvatarUrl())
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.head_boy_normal)
                    .error(R.drawable.head_boy_normal)
                    .into(imageView);
        }
/**
 * 有备注  备注大  无备注 名字大
 */
        if (!fb.getRemark().equals("")) {//备注不为空,备注大
            tv_name.setText(fb.getRemark());
            if (name != null) {
                tv_name2.setText(name);
            }
        } else {//备注为空,名字大
            if (name != null) {
                tv_name.setText(name);
            }
        }

        tv_single_city.setText(fb.getAddress());
//        tv_single_old.setText(fb.get);
        tv_single_sex.setText(fb.getSex());
//        tv_single_hight.setText(fb.getHeight());
//        tv_single_wight.setText(fb.getWeight());
//        tv_single_birthday.setText(fb.getBirthday());
//        tv_single_jibu.setText(fb.getStepNum() + "");

    }


    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v == back) {
                finish();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EXBEIZHU) {
            String beizhu = data.getStringExtra("beizhu");
            tv_name.setText(beizhu);
            tv_name2.setText(name);

        }
    }
}
