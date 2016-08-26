package com.cavytech.wear2.activity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.cavytech.wear2.R;
import com.cavytech.wear2.entity.UserEntity;
import com.cavytech.wear2.util.Constants;
import com.cavytech.wear2.util.SerializeUtils;

/**
 * Created by longjining on 16/3/15.
 */
public class SexActivity extends GuideSetComActivity {

    ImageButton btn_boy;
    ImageButton btn_girl;

    int sex = Constants.SEX_BOY;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sex);

        setTitleText(getString(R.string.title_mymessage));
        hideRightTitleText();
        findView();
        onListener();
    }

    @Override
    protected void findView() {
        super.findView();

        btn_boy = (ImageButton) findViewById(R.id.sex_boy);
        btn_girl = (ImageButton) findViewById(R.id.sex_girl);

        if(isEdit){
            if(userInfo.getSex() == Constants.SEX_BOY){
                setBoy();
            }else{
                setGirl();
            }
        }
    }

    @Override
    protected void onListener() {
        super.onListener();

        btn_boy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setBoy();
            }
        });

        btn_girl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setGirl();
            }
        });
    }

    private void setBoy(){
        btn_boy.setImageDrawable(getResources().getDrawable(R.drawable.head_boy_chosen));
        btn_girl.setImageDrawable(getResources().getDrawable(R.drawable.head_girl_normal));

        sex = Constants.SEX_BOY;
    }

    private void setGirl(){
        btn_boy.setImageDrawable(getResources().getDrawable(R.drawable.head_boy_normal));
        btn_girl.setImageDrawable(getResources().getDrawable(R.drawable.head_girl_chosen));

        sex = Constants.SEX_GIRL;
    }
    @Override
    public void onClickNextBtn(){
        if(isEdit){
            userInfo.setSex(sex);
            saveEdit(userInfo);
        }else{
            Intent intent = new Intent(SexActivity.this, HeightActivity.class);

            if(userInfo == null ){
                userInfo = new UserEntity.ProfileEntity();
            }

            Log.e("TAG","sex"+"--------"+sex);

            userInfo.setSex(sex);

            intent.putExtra(Constants.SERIALIZE_USERINFO,userInfo);

            SerializeUtils.serialize(userInfo, Constants.SERIALIZE_USERINFO);
            startActivity(intent);

            finish();

        }
    }
}
