package com.tunshu.view.activity;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tunshu.R;
import com.tunshu.adapter.GuideAdapter;

import java.util.ArrayList;
import java.util.List;

public class HelpDetailActivity extends CommonActivity {
    private ViewPager splashViewpager;
    private LinearLayout dotLayout;
    private RelativeLayout background;
    private List<View> dotViewsList = new ArrayList<View>();
    private String type = "1";
    private String titleText = "1";
    private int [] imageList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_detail);
        type = getIntent().getStringExtra("type") == null ? "1" : getIntent().getStringExtra("type");
        titleText = getIntent().getStringExtra("title") == null ? "" : getIntent().getStringExtra("title");
        imageList=getIntent().getIntArrayExtra("imageList");
        initView();
        initGuidePager();
        addListener();
    }

    private void initView() {
        findTitle();
        splashViewpager = (ViewPager) findViewById(R.id.splash_viewpager);
        dotLayout = (LinearLayout) findViewById(R.id.dotLayout);
        background = (RelativeLayout) findViewById(R.id.background);
        if(type.equals("1")){
            background.setBackgroundColor(Color.parseColor("#14cdfe"));
        }else if(type.equals("2")){
            background.setBackgroundColor(Color.parseColor("#575757"));
        }else if(type.equals("3")){
            background.setBackgroundColor(Color.parseColor("#1fd57e"));
        }
        title.setText(titleText);
    }
    private void addListener(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initGuidePager() {
        if (Build.VERSION.SDK_INT >= 11) {
//            splashViewpager.setPageTransformer(true, new AccordionTransformer());
        }
        splashViewpager.setAdapter(new GuideAdapter(this, imageList));
        for (int i = 0, size = imageList.length; i < size; i++) {
            ImageView dotView = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 4;
            params.rightMargin = 4;
            dotLayout.addView(dotView, params);
            dotViewsList.add(dotView);
        }
        for (int i = 0, size = dotViewsList.size(); i < size; i++) {
            if (i == 0) {
                dotViewsList.get(0).setBackgroundResource(R.drawable.dot_normal);
            } else {
                dotViewsList.get(i).setBackgroundResource(R.drawable.dot_select);
            }
        }
        splashViewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0, size = dotViewsList.size(); i < size; i++) {
                    if (i == position) {
                        dotViewsList.get(position).setBackgroundResource(R.drawable.dot_normal);
                    } else {
                        dotViewsList.get(i).setBackgroundResource(R.drawable.dot_select);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


}
