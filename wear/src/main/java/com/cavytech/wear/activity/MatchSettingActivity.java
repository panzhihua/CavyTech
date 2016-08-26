package com.cavytech.wear.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.basecore.widget.switchbutton.SwitchButton;
import com.cavytech.wear.R;
import com.cavytech.wear.util.Constants;
import com.taig.pmc.PopupMenuCompat;

public class MatchSettingActivity extends CommonActivity {
    private SwitchButton settingReconnectSwitch;
    private SwitchButton settingCallingVibrateSwitch;
    private SwitchButton settingBraceletVibrateSwitch;
    private SwitchButton settingDisconnectItem1;
    private SwitchButton settingDisconnectItem2;

    public ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_setting);
        initView();
        fillView();
        addListener();
    }

    private void initView() {
        back = (ImageButton) findViewById(R.id.back);

        findTitle();
        settingReconnectSwitch = (SwitchButton) findViewById(R.id.setting_reconnect_switch);
        settingCallingVibrateSwitch = (SwitchButton) findViewById(R.id.setting_calling_vibrate_switch);
        settingBraceletVibrateSwitch = (SwitchButton) findViewById(R.id.setting_bracelet_vibrate_switch);
        settingDisconnectItem1 = (SwitchButton) findViewById(R.id.setting_disconnect1);
        settingDisconnectItem2 = (SwitchButton) findViewById(R.id.setting_disconnect2);
        title.setText(getString(R.string.setting));
    }

    private void fillView() {
        settingReconnectSwitch.setChecked(getCoreApplication().getPreferenceConfig().getBoolean(Constants.SETTING_RECONNECT, true));
        settingCallingVibrateSwitch.setChecked(getCoreApplication().getPreferenceConfig().getBoolean(Constants.SETTING_CALLING_VIBRATE, true));
        settingBraceletVibrateSwitch.setChecked(getCoreApplication().getPreferenceConfig().getBoolean(Constants.SETTING_BRACELET_VIBRATE, true));
        String disconnectValue = getCoreApplication().getPreferenceConfig().getString(Constants.SETTING_DISCONNECT_VALUE, "0");
        if (disconnectValue.equals("0")) {
            settingDisconnectItem1.setChecked(true);
            settingDisconnectItem2.setChecked(true);
        } else if (disconnectValue.equals("1")) {
            settingDisconnectItem1.setChecked(true);
            settingDisconnectItem2.setChecked(false);
        } else if (disconnectValue.equals("2")) {
            settingDisconnectItem1.setChecked(false);
            settingDisconnectItem2.setChecked(true);
        } else if (disconnectValue.equals("-1")) {
            settingDisconnectItem1.setChecked(false);
            settingDisconnectItem2.setChecked(false);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("LINK_LOST", getCoreApplication().getPreferenceConfig().getBoolean(Constants.SETTING_BRACELET_VIBRATE, true) ? 1 : 0);
        setResult(1, intent);
        finish();
    }

    private void addListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("LINK_LOST", getCoreApplication().getPreferenceConfig().getBoolean(Constants.SETTING_BRACELET_VIBRATE, true) ? 1 : 0);
                setResult(1, intent);
                finish();
            }
        });
        settingReconnectSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getCoreApplication().getPreferenceConfig().setBoolean(Constants.SETTING_RECONNECT, isChecked);
            }
        });
        settingCallingVibrateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getCoreApplication().getPreferenceConfig().setBoolean(Constants.SETTING_CALLING_VIBRATE, isChecked);
            }
        });
        settingBraceletVibrateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getCoreApplication().getPreferenceConfig().setBoolean(Constants.SETTING_BRACELET_VIBRATE, isChecked);
            }
        });
        settingDisconnectItem1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(settingDisconnectItem2.isChecked()){
                        getCoreApplication().getPreferenceConfig().setString(Constants.SETTING_DISCONNECT_VALUE, "0");
                    }else{
                        getCoreApplication().getPreferenceConfig().setString(Constants.SETTING_DISCONNECT_VALUE, "1");
                    }
                }else{
                    if(settingDisconnectItem2.isChecked()){
                        getCoreApplication().getPreferenceConfig().setString(Constants.SETTING_DISCONNECT_VALUE, "2");
                    }else{
                        getCoreApplication().getPreferenceConfig().setString(Constants.SETTING_DISCONNECT_VALUE, "-1");
                    }
                }
            }
        });
        settingDisconnectItem2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(settingDisconnectItem1.isChecked()){
                        getCoreApplication().getPreferenceConfig().setString(Constants.SETTING_DISCONNECT_VALUE, "0");
                    }else{
                        getCoreApplication().getPreferenceConfig().setString(Constants.SETTING_DISCONNECT_VALUE, "1");
                    }
                }else{
                    if(settingDisconnectItem1.isChecked()){
                        getCoreApplication().getPreferenceConfig().setString(Constants.SETTING_DISCONNECT_VALUE, "2");
                    }else{
                        getCoreApplication().getPreferenceConfig().setString(Constants.SETTING_DISCONNECT_VALUE, "-1");
                    }
                }
            }
        });
    }
}
