package com.tunshu.view.activity;


import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.basecore.widget.switchbutton.SwitchButton;
import com.tunshu.R;
import com.tunshu.application.CommonApplication;
import com.tunshu.util.Constants;

public class SettingActivity extends CommonActivity {
    private SwitchButton settingDownloadTipsSwitch;
    private SwitchButton settingAutoInstallSwitch;
    private SwitchButton settingDeleteInstallPackageSwitch;
    private TextView settingDownloadPathTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        fillView();
        addListener();
    }

    private void initView() {
        findTitle();
        settingDownloadTipsSwitch = (SwitchButton) findViewById(R.id.setting_download_tips_switch);
        settingAutoInstallSwitch = (SwitchButton) findViewById(R.id.setting_auto_install_switch);
        settingDeleteInstallPackageSwitch = (SwitchButton) findViewById(R.id.setting_delete_install_package_switch);
        settingDownloadPathTitle = (TextView) findViewById(R.id.setting_download_path_title);
        title.setText(getString(R.string.setting));
    }

    private void fillView() {
        settingDownloadTipsSwitch.setChecked(getCoreApplication().getPreferenceConfig().getBoolean(Constants.SETTING_DOWNLOAD_TIPS, true));
        settingAutoInstallSwitch.setChecked(getCoreApplication().getPreferenceConfig().getBoolean(Constants.SETTING_AUTO_INSTALL, true));
        settingDeleteInstallPackageSwitch.setChecked(getCoreApplication().getPreferenceConfig().getBoolean(Constants.SETTING_DELETE_INSTALL_PACKAGE, true));
        settingDownloadPathTitle.setText(((CommonApplication) getCoreApplication()).getDownloadFilePath());
//        settingDownloadPathTitle.setText(FileInfoUtils.getDownloadDir(SettingActivity.this));
    }

    private void addListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        settingDownloadTipsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getCoreApplication().getPreferenceConfig().setBoolean(Constants.SETTING_DOWNLOAD_TIPS, isChecked);
            }

        });
        settingAutoInstallSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getCoreApplication().getPreferenceConfig().setBoolean(Constants.SETTING_AUTO_INSTALL, isChecked);
            }
        });
        settingDeleteInstallPackageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getCoreApplication().getPreferenceConfig().setBoolean(Constants.SETTING_DELETE_INSTALL_PACKAGE, isChecked);
            }
        });
    }
}
