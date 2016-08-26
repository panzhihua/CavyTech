package com.cavytech.wear.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.basecore.activity.BaseFragmentActivity;
import com.basecore.widget.SystemBarTintManager;
import com.cavytech.wear.R;

import java.util.Locale;

public class CommonActivity extends BaseFragmentActivity {

    public TextView title;
    private LocaleChangedReceiver localeChangedReceiver = new LocaleChangedReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(R.color.com_titlebar);
    }

    public void setStatusBar(int color) {
        // create our manager instance after the content view is set
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(color);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(localeChangedReceiver, new IntentFilter(Intent.ACTION_LOCALE_CHANGED));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(localeChangedReceiver);
    }

    public void findTitle() {
        title = (TextView) findViewById(R.id.title);
    }


    public String getLanguage() {
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh")) {
            return "zh";
        } else {
            return "en";
        }
    }

    public class LocaleChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().compareTo(Intent.ACTION_LOCALE_CHANGED) == 0) {
                getCoreApplication().getAppManager().AppExit(CommonActivity.this, false);
            }
        }
    }


}
