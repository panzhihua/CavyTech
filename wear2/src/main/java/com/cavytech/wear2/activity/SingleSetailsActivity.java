package com.cavytech.wear2.activity;

import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cavytech.wear2.R;
import com.cavytech.wear2.base.AppCompatActivityEx;
import com.cavytech.wear2.entity.FriendBean;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 好友信息详情
 */
public class SingleSetailsActivity extends AppCompatActivityEx {
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

    @ViewInject(R.id.tv_single_old)
    private TextView tv_single_old;

    @ViewInject(R.id.tv_single_sex)
    private TextView tv_single_sex;

    @ViewInject(R.id.tv_single_hight)
    private TextView tv_single_hight;

    @ViewInject(R.id.tv_single_wight)
    private TextView tv_single_wight;

    @ViewInject(R.id.tv_single_birthday)
    private TextView tv_single_birthday;

    @ViewInject(R.id.tv_single_jibu)
    private TextView tv_single_jibu;

    @ViewInject(R.id.tv_mingcheng)
    private TextView tv_mingcheng;

    @ViewInject(R.id.tv_name2)
    private TextView tv_name2;

    @ViewInject(R.id.iv_single_chengjiu)
    private ImageView iv_single_chengjiu;

    @ViewInject(R.id.tv_single_pk_into)
    private TextView tv_single_pk_into;

    private List list = new ArrayList();
    private String fid;
    private String userid;
    private Gson gson;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_setails);
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

        tv_single_pk_into.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SingleSetailsActivity.this, PkAtateActivity.class);
                intent.putExtra("fdid", fid);
                startActivity(intent);
            }
        });

        tv_mingcheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SingleSetailsActivity.this, BeizhuActivity.class);
                intent.putExtra("friendId", fid);
                startActivityForResult(intent, EXBEIZHU);

//                Toast.makeText(SingleSetailsActivity.this,"修改备注",Toast.LENGTH_SHORT).show();

            }
        });

        iv_single_chengjiu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SingleSetailsActivity.this, AchieveAlertDialogActivity.class));
            }
        });
    }


    private void getdatafromnet() {

        HttpUtils.getInstance().getFriendInfo(fid, new RequestCallback<FriendDetailBean>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(FriendDetailBean response) {
                if (response.isSuccess()) {
                    initdata(response);

                    Log.e("TAG","-------------------------"+response);
                    Log.e("TAG","-------------------------"+response.getBirthday());

                }
            }
        });
    }

    private void initdata(FriendDetailBean fb) {
        if (!fb.getAvatarUrl().equals("")) {
            Picasso.with(SingleSetailsActivity.this)
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
        Log.e("TAG","-------------------------"+fb.getAddress());
        Log.e("TAG","-------------------------"+fb.getSex());
        Log.e("TAG","-------------------------"+fb.getBirthday());
        Log.e("TAG","-------------------------"+fb.getWeight());
        Log.e("TAG","-------------------------"+fb.getHeight());
        tv_single_city.setText(fb.getAddress());
        tv_single_old.setText(calCulate(dateExchange(fb.getBirthday()))+"");
        tv_single_sex.setText(fb.getSex());
        tv_single_hight.setText(fb.getHeight());
        tv_single_wight.setText(fb.getWeight());
        tv_single_birthday.setText(fb.getBirthday());
        tv_single_jibu.setText(fb.getStepNum() + "");

        dateExchange(fb.getBirthday());
    }

    /**
     * 将日期转换为正确格式
     * @param birthday
     * @return
     */
    public String dateExchange(String birthday){
        if(birthday.length()>1){
            String data ="";
            String month ="";
            String day = "";
            String spStr[] = birthday.split("-");
            System.out.println(spStr[0]);
            if(spStr[1].length()<=1){
                month = "0"+spStr[1];
                System.out.println(month);
            }else{
                month = spStr[1];
            }

            if(spStr[2].length()<=1){
                day = "0"+spStr[2];
                System.out.println(day);
            }else{
                day =spStr[2];
            }

            data = spStr[0]+"-"+month+"-"+day;
            Log.e("TAG",data+"3333333333333333333333333");

            return data;
        }else{
            return "0";
        }
    }

    /**
     * 根据用户生日计算年龄
     */
    public int calCulate(String birthday) {
        if(birthday.length()>1){
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date birthdAY = null;
            try {
                birthdAY = sDateFormat.parse(birthday);

            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

            Calendar cal = Calendar.getInstance();

            if (cal.before(birthdAY)) {
                throw new IllegalArgumentException(
                        "The birthDay is before Now.It's unbelievable!");
            }

            int yearNow = cal.get(Calendar.YEAR);
            int monthNow = cal.get(Calendar.MONTH) + 1;
            int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

            cal.setTime(birthdAY);
            int yearBirth = cal.get(Calendar.YEAR);
            int monthBirth = cal.get(Calendar.MONTH) + 1;
            int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

            int age = yearNow - yearBirth;

            if (monthNow <= monthBirth) {
                if (monthNow == monthBirth) {
                    if (dayOfMonthNow < dayOfMonthBirth) {
                        age--;
                    }
                } else {
                    age--;
                }
            }

            Log.e("tag", age + "--------------------");
            return age;

        }else{
            return 0;
        }
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
        if (resultCode == RESULT_OK) {
            if (requestCode == EXBEIZHU) {
                String beizhu = data.getStringExtra("beizhu");
                tv_name.setText(beizhu);
                tv_name2.setText(name);

            }
        }
    }
}
