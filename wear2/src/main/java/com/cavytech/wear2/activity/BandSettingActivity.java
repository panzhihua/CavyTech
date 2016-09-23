package com.cavytech.wear2.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cavytech.wear2.R;
import com.cavytech.wear2.application.CommonApplication;
import com.cavytech.wear2.entity.BandInfoEntity;
import com.cavytech.wear2.entity.CommonEntity;
import com.cavytech.wear2.entity.UserEntity;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.RequestCallback;
import com.cavytech.wear2.util.CacheUtils;
import com.cavytech.wear2.util.Constants;
import com.cavytech.wear2.util.LifeBandBLEUtil;
import com.cavytech.wear2.util.PhoneUtils;
import com.cavytech.wear2.util.SerializeUtils;
import com.squareup.okhttp.Request;
import com.umeng.analytics.MobclickAgent;
import com.zhy.android.percent.support.PercentLinearLayout;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;

/**
 * Created by longjining on 16/4/8.
 */
public class BandSettingActivity extends GuideSetComActivity {

    protected enum StepType {
        OPEN,
        CONNECTTING,
        SUCCESS,
        FAIL
    }


    @ViewInject(R.id.subtitle_text)
    protected TextView subtitleText;

    @ViewInject(R.id.small_subtitle_text)
    protected TextView smallSubtitleText;

    @ViewInject(R.id.small_tips_text)
    protected TextView smallTipsText;

    @ViewInject(R.id.band_open_img)
    protected ImageView bandOpenImg;

    @ViewInject(R.id.title_layout)
    protected PercentRelativeLayout titleLayout;

    @ViewInject(R.id.guidsetting_layout)
    protected PercentLinearLayout guidsettingLayout;

    @ViewInject(R.id.img_next)
    protected ImageButton img_next;

    protected StepType stepActivity = StepType.OPEN;

    private boolean isReConnectBand = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_band_setting);
        x.view().inject(this);

        setTitleText(getString(R.string.title_connect_band));
        hideRightTitleText();
        findView();
        onListener();

        intView();
        setBackground();
        setStatusBar(R.color.setting_green_background);

        isReConnectBand = getIntent().getBooleanExtra(Constants.INTENT_EXTRA_RECONNECTDEV, false);
    }

    void intView() {

        btn_next.setVisibility(View.VISIBLE);

        switch (stepActivity) {
            case OPEN: {
                subtitleText.setText(getString(R.string.openwear));
                smallSubtitleText.setText(getString(R.string.openwear_tips1));
                smallTipsText.setText(getString(R.string.openwear_tips2));
                bandOpenImg.setImageResource(R.drawable.banner_open);

                smallSubtitleText.setVisibility(View.VISIBLE);
                smallTipsText.setVisibility(View.VISIBLE);
                bandOpenImg.setVisibility(View.VISIBLE);
                btn_next.setVisibility(View.GONE);  // TODO
                break;
            }
            case CONNECTTING: {
                connectedFalse();
                subtitleText.setText(getString(R.string.connecting));
                smallSubtitleText.setVisibility(View.GONE);
                smallTipsText.setVisibility(View.GONE);
                bandOpenImg.setVisibility(View.GONE);
                btn_next.setVisibility(View.GONE); // TODO
                break;
            }
            case SUCCESS: {
                Log.e("TAG","测试绑定---");
                back.setVisibility(View.VISIBLE);
                subtitleText.setText(getString(R.string.pairsucess));
                smallSubtitleText.setText(getString(R.string.startband));
                bandOpenImg.setImageResource(R.drawable.banner_start);
                smallSubtitleText.setVisibility(View.VISIBLE);
                smallTipsText.setVisibility(View.GONE);
                bandOpenImg.setVisibility(View.VISIBLE);
                btn_next.setBackgroundResource(R.drawable.button_next);
                postactivity();
                break;
            }
            case FAIL: {
                subtitleText.setText(getString(R.string.pairfail));
                smallSubtitleText.setText(getString(R.string.pairfailtips));
                bandOpenImg.setImageResource(R.drawable.banner_flash);
                smallSubtitleText.setVisibility(View.VISIBLE);
                smallTipsText.setVisibility(View.GONE);
                bandOpenImg.setVisibility(View.VISIBLE);
                btn_next.setBackgroundResource(R.drawable.btn_flash_press);
                break;
            }
            default:
                break;
        }
    }

    /**
     * 登录后绑定手环
     */
    private void postactivity() {

        HashMap<String,String> map = new HashMap<String,String>();
        map.put("device_serial", PhoneUtils.getUDID(BandSettingActivity.this));
        map.put("device_model",android.os.Build.MODEL+"-"+Build.VERSION.SDK_INT);
        map.put("band_mac",CacheUtils.getMacAdress(BandSettingActivity.this));
        map.put("user_id", CommonApplication.userID);
        map.put("longitude",CacheUtils.getString(BandSettingActivity.this, Constants.LONGITUDE));
        map.put("latitude",CacheUtils.getString(BandSettingActivity.this, Constants.LATITUDE));

        MobclickAgent.onEvent(BandSettingActivity.this, Constants.BAND_CONNECT,map);

        HttpUtils.getInstance().activities(BandSettingActivity.this,CacheUtils.getString(BandSettingActivity.this, Constants.LONGITUDE),
                CacheUtils.getString(BandSettingActivity.this, Constants.LATITUDE),
                CacheUtils.getMacAdress(BandSettingActivity.this),""+ Build.VERSION.SDK_INT, "BAND_CONNECT",new RequestCallback<CommonEntity>(){

                    @Override
                    public void onError(Request request, Exception e) {
//                                Log.e("TAG","活动上传失败--BAND_CONNECT--"+e.getLocalizedMessage()+e.getMessage().toString());
                    }

                    @Override
                    public void onResponse(CommonEntity response) {
//                                Log.e("TAG","活动上传成功--BAND_CONNECT--"+response.getCode());
                    }
                });

    }

    void setBackground() {

        if (titleLayout != null) {
            titleLayout.setBackgroundColor(getResources().getColor(R.color.setting_green_background));
        }

        if (guidsettingLayout != null) {
            guidsettingLayout.setBackgroundColor(getResources().getColor(R.color.setting_green_background));
        }
    }

    private void connectedFalse() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (stepActivity == StepType.CONNECTTING) {
                    stepActivity = StepType.FAIL;
                    intView();
                }


            }
        }, 10000);
    }

    @Override
    public void onResume() {
        super.onResume();
/*
        if(stepActivity == StepType.CONNECTTING){
            stepActivity = StepType.OPEN;
        }*/
    }

    @Override
    public void onClickNextBtn() {
        if (stepActivity == StepType.OPEN) {

            stepActivity = StepType.CONNECTTING;

            Intent intent = new Intent(BandSettingActivity.this, BandConnectActivity.class);
            startActivity(intent);
        }
        if (stepActivity == StepType.CONNECTTING) {

            stepActivity = StepType.SUCCESS;

            intView();
        }
        /**
         * 登陆注册
         */

        UserEntity.ProfileEntity profile = (UserEntity.ProfileEntity) SerializeUtils.unserialize(Constants.SERIALIZE_USERINFO);

        if (stepActivity == StepType.SUCCESS) {

            if(profile!=null) {
                if (isReConnectBand) {
                    //startActivity(new Intent(BandSettingActivity.this, HomePager.class));
                    BandInfoEntity bandInfo = (BandInfoEntity) SerializeUtils.unserialize(Constants.SERIALIZE_BAND_INFO);
                    CacheUtils.saveMacAdress(BandSettingActivity.this, bandInfo.getAddress());
                    LifeBandBLEUtil.getInstance().DataSync(1, 0);
                    finish();
                } else if (profile.getWeight() == 0 && profile.getHeight() == 0) {
                    Intent intent = new Intent(BandSettingActivity.this, SexActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(BandSettingActivity.this, HomePager.class);
                    startActivity(intent);
                    finish();
                }
            }else{
                Intent intent = new Intent(BandSettingActivity.this, SexActivity.class);
                startActivity(intent);
                finish();
            }
            return;
        }
        if (stepActivity == StepType.FAIL) {
            stepActivity = StepType.OPEN;
            LifeBandBLEUtil.getInstance().Disconnect();
            LifeBandBLEUtil.getInstance().StopScanCavyBand();
            LifeBandBLEUtil.getInstance().StartScanCavyBand();
            intView();
        }
        intView();
    }
}
