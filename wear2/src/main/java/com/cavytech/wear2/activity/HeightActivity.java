package com.cavytech.wear2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cavytech.wear2.R;
import com.cavytech.wear2.util.Constants;
import com.cavytech.wear2.util.SerializeUtils;
import com.cavytech.widget.TuneWheelVertical;

/**
 * Created by longjining on 16/3/17.
 */
public class HeightActivity extends GuideSetComActivity implements TuneWheelVertical.OnValueChangeListener {

    TuneWheelVertical heightWheel;
    TextView selHeight;

    private final int beginHeight  = 30;
    private final int defultHeight = 170;
    private final int maxHeight    = 240;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_height);

        setTitleText(getString(R.string.title_mymessage));
        hideRightTitleText();
        findView();

        onListener();
    }

    protected void findView() {
        super.findView();

        heightWheel= (TuneWheelVertical)findViewById(R.id.height_wheel);
        selHeight  = (TextView)findViewById(R.id.sel_height);

        selHeight.setText(defultHeight+"");

        if(isEdit ){
            heightWheel.initViewParam(beginHeight, (int) userInfo.getHeight(), maxHeight);

        }else{
            heightWheel.initViewParam(beginHeight, defultHeight, maxHeight);
        }

        heightWheel.setValueChangeListener(this);
    }

    @Override
    public void onValueChange(View wheel, float value) {

        selHeight.setText((int)value + "");
    }

    @Override
    public void onValueChangeEnd(View wheel, float value) {

    }

    @Override
    public void onClickNextBtn(){
        int a = Integer.parseInt(selHeight.getText().toString());

        if(a == 0){
            userInfo.setHeight(170);
        }else {
            userInfo.setHeight(a);
        }

        if(isEdit){
            saveEdit(userInfo);
        }else{
            Intent intent = new Intent(HeightActivity.this, WeightActivity.class);

            SerializeUtils.serialize(userInfo, Constants.SERIALIZE_USERINFO);

            startActivity(intent);
            finish();
        }
    }
}
