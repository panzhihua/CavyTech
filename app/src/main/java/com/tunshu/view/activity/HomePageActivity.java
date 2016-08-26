package com.tunshu.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.basecore.widget.CustomToast;
import com.basecore.widget.SystemBarTintManager;
import com.basecore.widget.dialog.Effectstype;
import com.basecore.widget.dialog.NiftyDialogBuilder;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tunshu.R;
import com.tunshu.entity.GameListEntity;
import com.tunshu.entity.VersionEntity;
import com.tunshu.fragment.HomePageBehindFragment;
import com.tunshu.fragment.HomePageContentFragment;
import com.tunshu.http.MyHttpClient;
import com.tunshu.util.Constants;
import com.tunshu.util.LogUtil;
import com.tunshu.widget.ArrowDownloadButton;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.service.PushService;

public class HomePageActivity extends SlidingActivity {

    private SlidingMenu sm;
    private Fragment mContent;
    private LocaleChangedReceiver localeChangedReceiver = new LocaleChangedReceiver();
    int progress=0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        setUpMenu(savedInstanceState);
    }
    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(localeChangedReceiver, new IntentFilter(Intent.ACTION_LOCALE_CHANGED));
    }

    @Override
    public void onDestroy() {



        Intent sevice = new Intent(this, PushService.class);
        this.startService(sevice);

        super.onDestroy();

        unregisterReceiver(localeChangedReceiver);
    }

    public void setStatusBar(int color) {
        // create our manager instance after the content view is set
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(color);
    }
    private void setUpMenu(Bundle savedInstanceState) {
        if (findViewById(R.id.menu_frame) == null) {
            setBehindContentView(R.layout.menu_frame);
            getSlidingMenu().setSlidingEnabled(true);
            getSlidingMenu()
                    .setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        } else {
            // add a dummy view
            View v = new View(this);
            setBehindContentView(v);
            getSlidingMenu().setSlidingEnabled(false);
            getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }
        // set the Above View Fragment
        if (savedInstanceState != null) {
            mContent = getSupportFragmentManager().getFragment(
                    savedInstanceState, "mContent");
        }

        if (mContent == null) {
            mContent = new HomePageContentFragment();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mContent).commit();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth()/4;
        // set the Behind View Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.menu_frame, new HomePageBehindFragment()).commit();
        sm = getSlidingMenu();
        sm.setBehindOffset(width);
        sm.setFadeEnabled(false);
        sm.setBehindScrollScale(0.25f);
        sm.setFadeDegree(0.25f);

        sm.setBackgroundImage(R.drawable.menu_background);
        sm.setBehindCanvasTransformer(new SlidingMenu.CanvasTransformer() {
            @Override
            public void transformCanvas(Canvas canvas, float percentOpen) {
                float scale = (float) (percentOpen * 0.25 + 0.75);
                canvas.scale(scale, scale, -canvas.getWidth() / 2,
                        canvas.getHeight() / 2);
            }
        });

        sm.setAboveCanvasTransformer(new SlidingMenu.CanvasTransformer() {
            @Override
            public void transformCanvas(Canvas canvas, float percentOpen) {
                float scale = (float) (1 - percentOpen * 0.25);
                canvas.scale(scale, scale, 0,(float)( canvas.getHeight()*0.5));
            }
        });
        sm.setOnCloseListener(new SlidingMenu.OnCloseListener() {
            @Override
            public void onClose() {
                setStatusBar(R.color.com_titlebar);
            }
        });
        sm.setOnOpenListener(new SlidingMenu.OnOpenListener() {
            @Override
            public void onOpen() {
                setStatusBar(R.color.homepage_titlebar);
            }
        });
    }

    public SlidingMenu getHomePageSlidingMenu() {
        return sm;
    }


    public class LocaleChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().compareTo(Intent.ACTION_LOCALE_CHANGED) == 0) {
                getCoreApplication().getAppManager().AppExit(HomePageActivity.this, false);
            }
        }
    }
}
