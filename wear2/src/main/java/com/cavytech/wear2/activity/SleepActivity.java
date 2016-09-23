package com.cavytech.wear2.activity;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.basecore.widget.SystemBarTintManager;
import com.cavytech.wear2.R;
import com.cavytech.wear2.base.AppCompatActivityEx;
import com.cavytech.wear2.fragment.DaySleepFragmrnt;
import com.cavytech.wear2.fragment.MonthSleepFragmrnt;
import com.cavytech.wear2.fragment.WeekSleepFragmrnt;
import com.cavytech.wear2.util.DateHelper;
import com.cavytech.wear2.util.FileUtils;
import com.viewpagerindicator.TabPageIndicator;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class SleepActivity extends AppCompatActivityEx {
    private String[] tabName;
    private ArrayList<Fragment> fragmentList;
    private DaySleepFragmrnt daySleepFragment;
    private WeekSleepFragmrnt yearSleepFragment;
    private MonthSleepFragmrnt monthSleepFragment;

    @ViewInject(R.id.go)
    private ImageView go;

    @ViewInject(R.id.back)
    private ImageView back;

    @ViewInject(R.id.title)
    private TextView title;

    @ViewInject(R.id.nsp_sleep)
    private ViewPager nsp_sleep;

    @ViewInject(R.id.tpi_sleep)
    private TabPageIndicator tpi_sleep;

    private FragmentPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.top_bg_color);//通知栏所需颜色
        }

        setContentView(R.layout.activity_sleep);
        x.view().inject(this);

//        setToolBar();
        tabName = new String[] { this.getString(R.string.day),this.getString(R.string.weeks), this.getString(R.string.month)};
        back.setOnClickListener(new MyonClickListener());
        go.setBackgroundResource(R.drawable.btn_share);
        title.setText(R.string.sleep_first);
        title.setTextColor(Color.WHITE);

        nsp_sleep.setOffscreenPageLimit(2);

        adapter = new SleepAdapter(getSupportFragmentManager());

        getDataFromnet();

        initData();

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScreenDrawable();

                showShare();
            }
        });

    }

    /**
     * 截图
     */
    private void ScreenDrawable() {

        FileUtils.saveMyBitmap(DateHelper.getSystemDateString("yyyy-MM-dd HH:mm"),FileUtils.myShot(SleepActivity.this));
        Log.e("TAG",DateHelper.getSystemDateString("yyyy-MM-dd HH:mm:ss")+"-=-=");

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

    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用

        //oks.setTitle("fengxinag");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        //oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        //oks.setText("我是分享文本");

        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath(FileUtils.getDrawableFilePath()+"/"+DateHelper.getSystemDateString("yyyy-MM-dd HH:mm")+".png");//确保SDcard下面存在此张图片
        //oks.setImagePath(FileUtils.getDrawableFilePath()+"/"+DateHelper.getSystemDateString("yyyy-MM-dd HH:mm:ss")+".png");//确保SDcard下面存在此张图片
        Log.e("TAG",DateHelper.getSystemDateString("yyyy-MM-dd HH:mm")+"-=-=");
        // url仅在微信（包括好友和朋友圈）中使用
        //oks.setUrl("http://www.tunshu.com/");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        //oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        //oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        //oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(this);
    }


    private void initData() {
        fragmentList = new ArrayList<>();
        daySleepFragment =new DaySleepFragmrnt();
        yearSleepFragment =new WeekSleepFragmrnt();
        monthSleepFragment =new MonthSleepFragmrnt();

        fragmentList.add(daySleepFragment);
        fragmentList.add(yearSleepFragment);
        fragmentList.add(monthSleepFragment);

        nsp_sleep.setAdapter(adapter);

        tpi_sleep.setViewPager(nsp_sleep);
    }

    class MyonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v == go) {
                Toast.makeText(SleepActivity.this, "fenxiang", Toast.LENGTH_SHORT).show();
            } else if (v == back) {
                finish();
            }
        }
    }

    class SleepAdapter extends FragmentPagerAdapter{
        public SleepAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabName[position % tabName.length].toUpperCase();
        }

        @Override
        public int getCount() {
            if(fragmentList.size() != 0){
                return fragmentList.size();
            }
            return 0;
        }
    }

    /**
     * 从网络获取数据
     */
    private void getDataFromnet() {

    }
}
