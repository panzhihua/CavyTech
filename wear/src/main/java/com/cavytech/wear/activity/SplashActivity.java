package com.cavytech.wear.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.cavytech.wear.R;


public class SplashActivity extends CommonActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);
        setStatusBar(R.color.com_titlebar);
        nextToDo();
    }

    private void nextToDo() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MatchActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        }, 2000);
    }
}
