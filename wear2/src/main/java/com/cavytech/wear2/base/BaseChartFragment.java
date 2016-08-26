package com.cavytech.wear2.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by libin on 2016/5/10.
 * 邮箱：bin.li@tunshu.com
 */
public abstract class BaseChartFragment extends Fragment {
    /**
     * 上下文
     */
    public Activity mActivity;

    /**
     * 当Fragment被创建的时候回调
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();//MainActivity
    }

    /**
     * 当Fragment的View被创建的时候回调这个方法
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = initView();
        return  view;
    }

    /**
     * 强制子类实现该方法，达到特有的效果
     * @return
     */
    public abstract View initView() ;

    /**
     * 当Activity被创建的时候被回调
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    /**
     * 当孩子需要实例自己特有的数据的时候，重新该方法
     *
     */
    public void initData() {
    }
}
