package com.cavytech.wear2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.cavytech.wear2.R;
import com.cavytech.wear2.util.Constants;
import com.cavytech.wear2.util.SerializeUtils;
import com.cavytech.widget.WeightView;

/**
 * Created by longjining on 16/3/18.
 */
public class WeightActivity extends GuideSetComActivity implements WeightView.OnValueChangeListener{

    WeightView viewWeight;

    String weight = "50";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_weight);

        setTitleText(getString(R.string.title_mymessage));
        hideRightTitleText();
        findView();
        onListener();
    }

    @Override
    protected void findView() {
        super.findView();

        viewWeight = (WeightView)findViewById(R.id.weight_view);

        if(userInfo.getWeight() == 0){
            weight =  50 +"";
        }else {
            weight =  userInfo.getWeight() +"";
        }

        if(isEdit && weight.length() > 0){

            viewWeight.initValue(Float.valueOf(weight));
        }
        viewWeight.setValueChangeListener(this);
    }

    @Override
    public void onValueChange(View wheel, float value) {

    }

    @Override
    public void onValueChangeEnd(View wheel, float value) {
        weight = value + "";
    }

    @Override
    public void onClickNextBtn(){
        userInfo.setWeight(Double.parseDouble(weight));
        if(isEdit){
             saveEdit(userInfo);
        }else{
            Intent intent = new Intent(WeightActivity.this, BirthDayAcivity.class);

            SerializeUtils.serialize(userInfo, Constants.SERIALIZE_USERINFO);

            startActivity(intent);

            finish();
        }
    }
}
