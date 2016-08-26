package com.tunshu.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tunshu.R;

public class AboutActivity extends CommonActivity {

    private TextView tvversion;
    private RelativeLayout homeWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initView();
        fillView();
        addListener();
    }

    private void initView() {
        findTitle();
        tvversion = (TextView) findViewById(R.id.tv_version);
        homeWeb = (RelativeLayout) findViewById(R.id.home_web);
    }

    private void fillView() {
        String version= "V"+ getVersionCode(this);
        tvversion.setText(version);
        title.setText(getString(R.string.about));
    }

    private String getVersionCode(Context context) {
        String versionName = "";
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    private void addListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        homeWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://tunshu.com");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }


}
