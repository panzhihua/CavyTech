package com.cavytech.wear2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;

import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.cavytech.wear2.R;
import com.cavytech.wear2.util.Constants;
import com.cavytech.wear2.util.FileUtils;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏

        setContentView(R.layout.activity_wear2_main);

        onListener();
    }

    private void onListener() {

        TextView loginBtn = (TextView)findViewById(R.id.login);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, LoginActivity.class);

                startActivity(it);
            }
        });

        TextView registBtn = (TextView)findViewById(R.id.regist);
        registBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FileUtils.deleteFile(Constants.SERIALIZE_USERINFO);
                Intent it = new Intent(MainActivity.this, BandConnectActivity.class);

                startActivity(it);
            }
        });
    }
}
