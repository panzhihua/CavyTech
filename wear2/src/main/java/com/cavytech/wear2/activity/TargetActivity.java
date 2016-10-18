package com.cavytech.wear2.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cavytech.wear2.R;
import com.cavytech.wear2.entity.CommonEntity;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.RequestCallback;
import com.cavytech.wear2.util.Constants;
import com.cavytech.wear2.util.SerializeUtils;
import com.squareup.okhttp.Request;
import com.zhy.android.percent.support.PercentLinearLayout;

import org.json.JSONObject;

import app.minimize.com.seek_bar_compat.SeekBarCompat;

/**
 * Created by longjining on 16/3/24.
 */
public class TargetActivity extends GuideSetComActivity implements SeekBarCompat.OnSeekBarChangeListener{

    final int STEP_PER   = 100;    // 颗粒度100步
    final int MAX_STEP   = 20000 / STEP_PER;
    final int INIT_STEP  = 8000 / STEP_PER;
    final int INIT_AVGSTEP  = 6000 / STEP_PER;
    final int SLEEP_PER   = 10;   // 颗粒度10分钟
    final int MAX_SLEEP  = 12 * 60 / SLEEP_PER;  // min
    final int INIT_SLEEP = 8 * 60 / SLEEP_PER;
    final int INIT_AVGSLEEP  = 7 * 60 / SLEEP_PER;

    SeekBarCompat seekBarTarget1;
    SeekBarCompat seekBarTarget2;

    TextView target1Value1;
    TextView target1Value2;
    TextView target2Value1;
    TextView target2Value2;

    PercentLinearLayout target1_linear_average;
    PercentLinearLayout target1_linear_recommend;

    PercentLinearLayout target2_linear_average;
    PercentLinearLayout target2_linear_recommend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_target);

        View targetView = findViewById(R.id.target1);
        target1Value1 = (TextView)targetView.findViewById(R.id.target_value1);
        target1Value2 = (TextView)targetView.findViewById(R.id.target_value2);
        seekBarTarget1 = (SeekBarCompat)targetView.findViewById(R.id.seekBar);
        seekBarTarget1.setOnSeekBarChangeListener(this);
        seekBarTarget1.setMax(MAX_STEP);
        if(isEdit){
            seekBarTarget1.setProgress(userInfo.getSteps_goal()/STEP_PER);
        }else{
            seekBarTarget1.setProgress(INIT_STEP);
        }
        target1Value1.setText(String.format("%d", INIT_STEP));
        TextView target1Unit = (TextView)targetView.findViewById(R.id.target_value1_unit);
        target1Unit.setVisibility(View.GONE);
        target1Value1.setVisibility(View.GONE);

        target1_linear_average = (PercentLinearLayout)targetView.findViewById(R.id.linear_average);
        target1_linear_recommend = (PercentLinearLayout)targetView.findViewById(R.id.linear_recommend);

        targetView = findViewById(R.id.target2);
        target2Value1 = (TextView)targetView.findViewById(R.id.target_value1);
        target2Value2 = (TextView)targetView.findViewById(R.id.target_value2);
        seekBarTarget2 = (SeekBarCompat)targetView.findViewById(R.id.seekBar);
        seekBarTarget2.setOnSeekBarChangeListener(this);
        seekBarTarget2.setMax(MAX_SLEEP);

        Log.e("TAG","jibu目标--"+userInfo.getSteps_goal());
        Log.e("TAG","shuimian目标--"+userInfo.getSleep_time_goal());

        if(isEdit){
            String[] sleepTime = new String[]{"0","0"};
            if(sleepTime.length == 2){
                //int iSleeppTime = Integer.parseInt(sleepTime[0]) * 60 + Integer.parseInt(sleepTime[1]);
                int iSleeppTime = userInfo.getSleep_time_goal();

                sleepTime[0] = iSleeppTime/60 + "";
                sleepTime[1] = iSleeppTime%60 + "";

                seekBarTarget2.setProgress(iSleeppTime/SLEEP_PER);
                target2Value1.setText(sleepTime[0]);
                target2Value2.setText(sleepTime[1]);
            }
        }else{
            seekBarTarget2.setProgress(INIT_SLEEP);
            target2Value1.setText(String.format("%d", INIT_SLEEP * SLEEP_PER / 60));
            target2Value2.setText("0");
        }

        target2_linear_average = (PercentLinearLayout)targetView.findViewById(R.id.linear_average);
        target2_linear_recommend = (PercentLinearLayout)targetView.findViewById(R.id.linear_recommend);

        TextView targetTitle = (TextView)targetView.findViewById(R.id.target_title);
        targetTitle.setText(getString(R.string.sleeping));
        TextView target2Unit = (TextView)targetView.findViewById(R.id.target_value2_unit);
        target2Unit.setText("min");

        setTitleText(getString(R.string.title_mymessage));
        hideRightTitleText();
        findView();
        onListener();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
        {
            initPos();
        }
    }

    private void initPos(){

        if(target1_linear_recommend == null || target1_linear_recommend.getPaddingLeft() > 0){
            return;
        }
        int thumbWidth = seekBarTarget1.getThumb().getBounds().width();

        int left = 0;

        left = seekBarTarget1.getWidth() * INIT_STEP / MAX_STEP - thumbWidth/2 + 5;
        target1_linear_recommend.setPadding(left,0,0,0);

        left = seekBarTarget1.getWidth() * INIT_AVGSTEP / MAX_STEP - thumbWidth/2 + 5;
        target1_linear_average.setPadding(left,0,0,0);

        left = seekBarTarget2.getWidth() * INIT_SLEEP / MAX_SLEEP - thumbWidth/2 - 10;

        target2_linear_recommend.setPadding(left,0,0,0);

        left = seekBarTarget2.getWidth() * INIT_AVGSLEEP / MAX_SLEEP - thumbWidth/2 - 10;
        target2_linear_average.setPadding(left,0,0,0);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if(seekBarTarget1 == seekBar){
            target1Value2.setText(String.format("%d", progress * STEP_PER));
        }else{
            progress *= SLEEP_PER;
            int min = progress;
            int hour = progress / 60;
            target2Value1.setText(String.format("%d", hour));
            target2Value2.setText(String.format("%d", min - hour * 60));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClickNextBtn(){

        int strTmp = Integer.parseInt(target1Value2.getText().toString());
        userInfo.setSteps_goal(strTmp);

        Log.e("TAG",strTmp+"-=-=-=记步目标");

        strTmp = Integer.parseInt(target2Value1.getText().toString())*60 +  Integer.parseInt(target2Value2.getText().toString());
        userInfo.setSleep_time_goal(strTmp);

        Log.e("TAG",strTmp+"-=-=-=睡眠目标");

        if(isEdit){
            saveEdit(userInfo);
        }else{
            SerializeUtils.serialize(userInfo, Constants.SERIALIZE_USERINFO);
            HttpUtils.getInstance().setUserInfo(userInfo, TargetActivity.this, new RequestCallback<CommonEntity>() {
                @Override
                public void onError(Request request, Exception e) {

                    try {
                        JSONObject jsonObj = new JSONObject(e.getLocalizedMessage());
                        int code = jsonObj.optInt("code");
                        if(HttpUtils.CODE_ACCOUNT_NOT_LOGIN==code){
                            AlertDialog.Builder dialog = new AlertDialog.Builder(TargetActivity.this);
                            dialog.setCancelable(false);
                            dialog.setMessage(R.string.not_login);
                            dialog.setPositiveButton(getString(R.string.ALERT_DLG_BTN_OK), new android.content.DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    arg0.dismiss();
                                    startActivity(new Intent(TargetActivity.this,LoginActivity.class));
                                    finish();
                                }
                            });
                            dialog.setNeutralButton(getString(R.string.ALERT_DLG_BTN_CANCEL), new android.content.DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    arg0.dismiss();
                                    startActivity(new Intent(TargetActivity.this, GuideActivity.class));
                                    finish();
                                }
                            } );
                            dialog.show();
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                }

                @Override
                public void onResponse(CommonEntity response) {
                    if(response.getCode() == 1000){
                        Intent intent;

//                        if(CommonApplication.isLogin){
                            // 先登陆后完善信息流程
                            intent = new Intent(TargetActivity.this, HomePager.class);

                            startActivity(intent);
//                        }else{
//                            intent = new Intent(TargetActivity.this, GuideActivity.class);
//
//                            startActivity(intent);
//                        }
//                        finish();
                    }
                }
            });

        }
    }
}
