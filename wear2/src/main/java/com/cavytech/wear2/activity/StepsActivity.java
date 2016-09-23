package com.cavytech.wear2.activity;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.basecore.widget.SystemBarTintManager;
import com.cavytech.wear2.R;
import com.cavytech.wear2.fragment.DayFragmrnt;
import com.cavytech.wear2.fragment.MonthFragmrnt;
import com.cavytech.wear2.fragment.WeekFragmrnt;
import com.cavytech.wear2.util.DateHelper;
import com.cavytech.wear2.util.FileUtils;
import com.viewpagerindicator.TabPageIndicator;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class StepsActivity extends FragmentActivity {
    private String[] tabName;
    private ArrayList<Fragment> fragmentList;
    private DayFragmrnt dayFriendFragment;
    private MonthFragmrnt monthFriendFragment;
    private WeekFragmrnt yearFriendFragment;

    @ViewInject(R.id.title)
    private TextView title;

    @ViewInject(R.id.back)
    private ImageView back;

    @ViewInject(R.id.go)
    private ImageView go;

    @ViewInject(R.id.nsp_staps)
    private ViewPager nsp_staps;

    @ViewInject(R.id.tpi_staps)
    private TabPageIndicator tpi_staps;

    private FragmentPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.top_bg_color);//通知栏所需颜色
            tintManager.setNavigationBarTintEnabled(true);
            tintManager.setNavigationBarTintResource(R.color.black);
        }

        setContentView(R.layout.activity_steps);
        x.view().inject(this);
        tabName = new String[] { this.getString(R.string.day),this.getString(R.string.weeks), this.getString(R.string.month)};
        title.setText(R.string.steps);

        back.setOnClickListener(new MyOnClickListener());
        go.setBackgroundResource(R.drawable.btn_share);

        adapter = new StepsAdapter(getSupportFragmentManager());

        getDataFromnet();

        initData();

        nsp_staps.setOffscreenPageLimit(2);

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScreenDrawable();

                showShare();
            }
        });

        /*FragmentPagerAdapter adapter = new GoogleMusicAdapter(getSupportFragmentManager());

        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);

        TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(pager);*/
    }

    /**
     * 截图
     */
    private void ScreenDrawable() {

        FileUtils.saveMyBitmap(DateHelper.getSystemDateString("yyyy-MM-dd HH:mm"),FileUtils.myShot(StepsActivity.this));
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

    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(v == back){
                finish();
            }
        }
    }

    private void initData() {
        fragmentList = new ArrayList<>();
        dayFriendFragment =new DayFragmrnt();
        yearFriendFragment =new WeekFragmrnt();
        monthFriendFragment =new MonthFragmrnt();

        fragmentList.add(dayFriendFragment);
        fragmentList.add(yearFriendFragment);
        fragmentList.add(monthFriendFragment);

        nsp_staps.setAdapter(adapter);

        tpi_staps.setViewPager(nsp_staps);
    }

    class StepsAdapter extends FragmentPagerAdapter{
        public StepsAdapter(FragmentManager fm) {
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
