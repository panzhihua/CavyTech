package com.tunshu.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.tunshu.R;
import com.tunshu.adapter.MyFragmentAdapter;
import com.tunshu.application.CommonApplication;
import com.tunshu.entity.FragmentEntity;
import com.tunshu.view.activity.HomePageActivity;
import com.tunshu.view.activity.SearchActivity;
import com.tunshu.widget.SyncHorizontalScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomePageContentFragment extends CommonFragment {
    private ImageView homePageMenu;
    private ImageView homePageTitle;
    private ImageView homePageSearch;
    private ViewPager pager;
    private SyncHorizontalScrollView titleHorizontalScrollView;
    private RadioButton radiobt;
    private ImageView iv_nav_indicator;
    private RadioGroup itemRadioGroup;
    private int indicatorWidth;
    private LayoutInflater mInflater;
    private ArrayList<String> title = new ArrayList<String>();
    private int currentIndicatorLeft = 0;


    public HomePageContentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home_page_content, container, false);
        assignViews(rootView);
        return rootView;
    }

    private void assignViews(View rootView) {
        homePageMenu = (ImageView) rootView.findViewById(R.id.home_page_menu);
        homePageTitle = (ImageView) rootView.findViewById(R.id.home_page_title);
        homePageSearch = (ImageView) rootView.findViewById(R.id.home_page_search);
        pager = (ViewPager) rootView.findViewById(R.id.pager);
        titleHorizontalScrollView = (SyncHorizontalScrollView) rootView.findViewById(R.id.tab_bar_layout);
        itemRadioGroup = (RadioGroup) rootView.findViewById(R.id.rg_nav_content);
        iv_nav_indicator = (ImageView) rootView.findViewById(R.id.iv_nav_indicator);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fillView();
        addListener();
    }

    private void fillView() {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        indicatorWidth = dm.widthPixels / 4;
        ViewGroup.LayoutParams cursor_Params = iv_nav_indicator.getLayoutParams();
        cursor_Params.width = indicatorWidth;
        iv_nav_indicator.setLayoutParams(cursor_Params);
        mInflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        initNavigationHSV();

        List<FragmentEntity> mListFragmentEntity = new ArrayList<FragmentEntity>();
        FragmentEntity fe1 = new FragmentEntity();
        fe1.setFragmentLabel("1");
        fe1.setmFragment(new MainRecommendFragment());
        FragmentEntity fe2 = new FragmentEntity();
        fe2.setFragmentLabel("2");
        fe2.setmFragment(new MainChartsFragment());
        FragmentEntity fe3 = new FragmentEntity();
        fe3.setFragmentLabel("3");
        fe3.setmFragment(new MainClassifyFragment());
        FragmentEntity fe4 = new FragmentEntity();
        fe4.setFragmentLabel("4");
        fe4.setmFragment(new MainSpecialFragment());

        mListFragmentEntity.add(fe1);
        mListFragmentEntity.add(fe2);
        mListFragmentEntity.add(fe3);
        mListFragmentEntity.add(fe4);
        pager.setAdapter(new MyFragmentAdapter(getActivity(), mListFragmentEntity));
        pager.setOffscreenPageLimit(1);
        pager.setCurrentItem(0);
        ((HomePageActivity) getActivity()).getHomePageSlidingMenu().addIgnoredView(pager);
    }

    private void initNavigationHSV() {
        itemRadioGroup.removeAllViews();
        title.add(getString(R.string.recommend));
        title.add(getString(R.string.charts));
        title.add(getString(R.string.classify));
        title.add(getString(R.string.special));
        for (int i = 0, size = title.size(); i < size; i++) {
            RadioButton rb = (RadioButton) mInflater.inflate(R.layout.activity_nav_radiogroup_item, null);
            rb.setId(i);
            rb.setText(title.get(i));
            rb.setLayoutParams(new ViewGroup.LayoutParams(indicatorWidth,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            if (i == 0) {
                radiobt = rb;
            }
            itemRadioGroup.addView(rb);
        }
        radiobt.performClick();

    }

    private void addListener() {
        homePageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomePageActivity) getActivity()).getHomePageSlidingMenu().showMenu();
            }
        });
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                if (itemRadioGroup != null && itemRadioGroup.getChildCount() > position) {
                    itemRadioGroup.getChildAt(position).performClick();
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        itemRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (itemRadioGroup.getChildAt(checkedId) != null) {
                    TranslateAnimation animation = new TranslateAnimation(
                            currentIndicatorLeft,
                            itemRadioGroup.getChildAt(checkedId).getLeft(), 0f, 0f);
                    animation.setInterpolator(new LinearInterpolator());
                    animation.setDuration(100);
                    animation.setFillAfter(true);
                    iv_nav_indicator.startAnimation(animation);
                    pager.setCurrentItem(checkedId);
                    currentIndicatorLeft = itemRadioGroup.getChildAt(checkedId).getLeft();
                    titleHorizontalScrollView.smoothScrollTo(
                            (checkedId > 1 ? itemRadioGroup.getChildAt(checkedId).getLeft() : 0) - itemRadioGroup.getChildAt(2).getLeft(), 0);
                }
            }
        });
        homePageSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), SearchActivity.class);
                startActivity(it);
            }
        });
    }


}
