package com.cavytech.wear2.base;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.basecore.widget.SystemBarTintManager;
import com.cavytech.wear2.R;

/**
 * Created by longjining on 16/3/31.
 */
public class AppCompatActivityEx extends AppCompatActivity {

    private Toolbar mToolbar;

    protected void setToolBar(){
        // 设定状态栏的颜色，当版本大于4.4时起作用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.com_titlebar2);
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
    }
}
