package com.cavytech.wear2.slidingmenu;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.basecore.widget.SystemBarTintManager;
import com.cavytech.wear2.R;
import com.cavytech.wear2.application.CommonApplication;
import com.cavytech.wear2.util.CacheUtils;
import com.cavytech.wear2.util.Constants;
import com.cavytech.widget.SwitchButtonEx;
import com.cavytech.widget.TextPick;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

public class NoticeActivity extends Activity implements TextPick.OnValueChangeListener{
    @ViewInject(R.id.title)
    private TextView title;

    @ViewInject(R.id.tv_notice_time)
    private TextView tv_notice_time;

    @ViewInject(R.id.back)
    private ImageView back;

    @ViewInject(R.id.ll_text_picker)
    private LinearLayout ll_text_picker;

    @ViewInject(R.id.rl_123)
    private RelativeLayout rl_123;

    @ViewInject(R.id.sbe_clock_item_call)
    private SwitchButtonEx sbe_clock_item_call;

    @ViewInject(R.id.textpicker_wheel)
    TextPick textpicker_wheel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.top_bg_color);//通知栏所需颜色
            tintManager.setNavigationBarTintEnabled(true);
            tintManager.setNavigationBarTintResource(R.color.black);
        }
        CommonApplication.isLogin = true;

        setContentView(R.layout.activity_notice);
        x.view().inject(this);

        tv_notice_time.setText(CacheUtils.getInt(NoticeActivity.this,Constants.PHONENOTICE)+"秒未接提醒");

        /**
         * 保存选中状态
         */
        sbe_clock_item_call.setChecked(CacheUtils.getBoolean(NoticeActivity.this, Constants.PHONEISCHECKED));

        if(sbe_clock_item_call.isChecked()){
            ll_text_picker.setVisibility(View.VISIBLE);


        }else {
            ll_text_picker.setVisibility(View.GONE);
        }

        title.setText("提醒");

        back.setOnClickListener(new MyClockOnClickListener());

        sbe_clock_item_call.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("TAG",isChecked+"=-=-=-=-=-=-=-=-=-");

                if(CacheUtils.getBoolean(NoticeActivity.this, Constants.PHONEISCHECKED)){
                    ll_text_picker.setVisibility(View.GONE);
                    rl_123.setBackgroundResource(R.drawable.radius);
                }else {
                    ll_text_picker.setVisibility(View.VISIBLE);
                    rl_123.setBackgroundResource(R.drawable.notice_bg);

                }

                CacheUtils.putBoolean(NoticeActivity.this, Constants.PHONEISCHECKED,isChecked);

            }
        });

        initNumberPicker();
     }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    void initNumberPicker(){

        ArrayList<String> text = new ArrayList<>();

        for (int i = 0; i <= 4; i++){
            int a = i*5;
            text.add(a + "");
        }

        textpicker_wheel.setTextAttrs(45,30,
                getResources().getColor(R.color.text_b3_color),
                getResources().getColor(R.color.text_4d_color),
               false);
//        textpicker_wheel.initViewParam(text, text.size() / 2);
        if(CacheUtils.getInt(NoticeActivity.this,Constants.SAVENOTICE)!=-1){
            textpicker_wheel.initViewParam(text, CacheUtils.getInt(NoticeActivity.this,Constants.SAVENOTICE));
        }else {
            textpicker_wheel.initViewParam(text, 0);
        }

        textpicker_wheel.setValueChangeListener(this);
    }

    @Override
    public void onValueChange(View wheel, float value) {

    }

    @Override
    public void onValueChangeEnd(View wheel, float value) {
        CacheUtils.putInt(NoticeActivity.this,Constants.SAVENOTICE,(int)value);
        switch ((int) value){
            case 0:
                CacheUtils.putInt(NoticeActivity.this, Constants.PHONENOTICE,0);
                Log.e("TAG",0+"=-=-=-=-=-=-=-=-=-");
                tv_notice_time.setText("0秒未接提醒");
                break;
            case 1:
                CacheUtils.putInt(NoticeActivity.this, Constants.PHONENOTICE,5);
                Log.e("TAG",5+"=-=-=-=-=-=-=-=-=-");
                tv_notice_time.setText("5秒未接提醒");
                break;
            case 2:
                CacheUtils.putInt(NoticeActivity.this, Constants.PHONENOTICE,10);
                Log.e("TAG",10+"=-=-=-=-=-=-=-=-=-");
                tv_notice_time.setText("10秒未接提醒");
                break;
            case 3:
                CacheUtils.putInt(NoticeActivity.this, Constants.PHONENOTICE,15);
                Log.e("TAG",15+"=-=-=-=-=-=-=-=-=-");
                tv_notice_time.setText("15秒未接提醒");
                break;
            case 4:
                CacheUtils.putInt(NoticeActivity.this, Constants.PHONENOTICE,20);
                Log.e("TAG",20+"=-=-=-=-=-=-=-=-=-");
                tv_notice_time.setText("20秒未接提醒");
                break;
        }

    }

    class MyClockOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v == back) {
                finish();
            }
        }
    }


}
