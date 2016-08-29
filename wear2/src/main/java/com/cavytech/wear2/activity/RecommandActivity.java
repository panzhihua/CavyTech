package com.cavytech.wear2.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.basecore.widget.SystemBarTintManager;
import com.cavytech.wear2.R;
import com.cavytech.wear2.fragment.AddressListFriendFragment;
import com.cavytech.wear2.fragment.NearbyFriendFragment;
import com.cavytech.wear2.fragment.RecommendFriendFragment;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TabPageIndicator;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

public class RecommandActivity extends FragmentActivity {//页面列表
    private ArrayList<Fragment> fragmentList;
    private AddressListFriendFragment addressListFriendFragment;
    private RecommendFriendFragment recommendFriendFragment;
    private NearbyFriendFragment nearbyFriendFragment;

    @ViewInject(R.id.title)
    private TextView title;

    @ViewInject(R.id.back)
    private ImageView back;

    @ViewInject(R.id.vp_recommend_content)
    private ViewPager vp_recommend_content;

    @ViewInject(R.id.tpi_recomend_title)
    private TabPageIndicator tpi_recomend_title;

    private static final String[] tabName = {"通讯录好友", "推荐好友", "附近好友"};
    private static final int[] ICONS = new int[] {
            R.drawable.icon_tab_contect_normal_select,
            R.drawable.icon_tab_advice_normal_select,
            R.drawable.icon_tab_location_normal_select,
    };
    private ContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.top_bg_color);//通知栏所需颜色
            tintManager.setNavigationBarTintEnabled(true);
            tintManager.setNavigationBarTintResource(R.color.black);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommand);
        initData();

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

    private void initData() {
        x.view().inject(this);
        adapter = new ContactAdapter(getSupportFragmentManager());
        getDataFromnet();
        title.setText("联系人");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fragmentList = new ArrayList<>();
        addressListFriendFragment =new AddressListFriendFragment();
        recommendFriendFragment =new RecommendFriendFragment();
        nearbyFriendFragment =new NearbyFriendFragment();

        fragmentList.add(addressListFriendFragment);
        fragmentList.add(recommendFriendFragment);
        fragmentList.add(nearbyFriendFragment);
        vp_recommend_content.setAdapter(adapter);
        tpi_recomend_title.setViewPager(vp_recommend_content);
    }

    class ContactAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
        public ContactAdapter(FragmentManager fm) {
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

        @Override public int getIconResId(int index) {
            return ICONS[index];
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

