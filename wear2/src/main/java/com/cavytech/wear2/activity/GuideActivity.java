package com.cavytech.wear2.activity;

/**
 * Created by longjining on 16/2/25.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cavytech.wear2.R;
import com.cavytech.wear2.adapter.ViewPagerAdapter;
import com.cavytech.wear2.util.Constants;
import com.cavytech.wear2.util.FileUtils;

import java.util.ArrayList;
import java.util.List;

/**
*
* @{# GuideActivity.java Create on 2013-5-2 下午10:59:08
*
*     class desc: 引导界面
*
*     <p>
*     Copyright: Copyright(c) 2013
*     </p>
* @Version 1.0
*
*
*/
public class GuideActivity extends Activity implements OnPageChangeListener {

    private ViewPager vp;
    private ViewPagerAdapter vpAdapter;
    private List<View> views;

    // 底部小点图片
    private ImageView[] dots;

    // 记录当前选中位置
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        // 初始化页面
        initViews();

        // 初始化底部小点
        initDots();

        onListener();
    }

   /* public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/

    private void onListener() {

        TextView loginBtn = (TextView)findViewById(R.id.login);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(GuideActivity.this, LoginActivity.class);
                setGuided();
                startActivity(it);
                finish();
            }
        });

        TextView registBtn = (TextView)findViewById(R.id.regist);
        registBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * 此处macAddress应该清空
                 */
                FileUtils.deleteFile(Constants.SERIALIZE_USERINFO);
           /*     FileUtils.deleteFile(Constants.SERIALIZE_BAND_INFO);
                FileUtils.deleteFile(Constants.SERIALIZE_USERBAND_INFO);*/
                Intent it = new Intent(GuideActivity.this, RegisterActivity.class);
                setGuided();
                startActivity(it);
                finish();
            }
        });
    }

    /**
     *
     * method desc：设置已经引导过了，下次启动不用再次引导
     */
    private void setGuided() {
        SharedPreferences preferences = getSharedPreferences(
                Constants.SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        // 存入数据
        editor.putBoolean("isFirstIn", false);
        // 提交修改
        editor.commit();
    }
    private void initViews() {
        LayoutInflater inflater = LayoutInflater.from(this);

        views = new ArrayList<View>();
        // 初始化引导图片列表
        views.add(inflater.inflate(R.layout.guide_one, null));
        views.add(inflater.inflate(R.layout.guide_two, null));
        views.add(inflater.inflate(R.layout.activity_three, null));
        views.add(inflater.inflate(R.layout.guild_four, null));

//        TextView v2 = (TextView) findViewById(R.id.tv_guild1);
//        v2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent it = new Intent(GuideActivity.this, LoginActivity.class);
//
//                startActivity(it);
//            }
//        });

//        View v1 = View.inflate(GuideActivity.this,R.layout.guide_one,null);
//        TextView tv1 = (TextView) v1.findViewById(R.id.tv_guild1);
//        tv1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent it = new Intent(GuideActivity.this, LoginActivity.class);
//
//                startActivity(it);
//            }
//        });

        // 初始化Adapter
        vpAdapter = new ViewPagerAdapter(views, this);

        vp = (ViewPager) findViewById(R.id.viewpager);
        vp.setAdapter(vpAdapter);
        // 绑定回调
        vp.setOnPageChangeListener(this);
    }

    private void initDots() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);

        dots = new ImageView[views.size()];

        // 循环取得小点图片
        for (int i = 0; i < views.size(); i++) {
            dots[i] = (ImageView) ll.getChildAt(i);
            dots[i].setEnabled(true);// 都设为灰色
        }

        currentIndex = 0;
        dots[currentIndex].setEnabled(false);// 设置为白色，即选中状态
    }

    private void setCurrentDot(int position) {
        if (position < 0 || position > views.size() - 1
                || currentIndex == position) {
            return;
        }

        dots[position].setEnabled(false);
        dots[currentIndex].setEnabled(true);

        currentIndex = position;
    }

    // 当滑动状态改变时调用
    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    // 当当前页面被滑动时调用
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    // 当新的页面被选中时调用
    @Override
    public void onPageSelected(int arg0) {
        // 设置底部小点选中状态
        setCurrentDot(arg0);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK){
//            android.os.Process.killProcess(android.os.Process.myPid());
//            System.exit(1);
//            return false;
//        }else {
//            return super.onKeyDown(keyCode, event);
//        }
//
//    }

}