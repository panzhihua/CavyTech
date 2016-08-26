package com.tunshu.fragment;

import android.app.Activity;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by longjining on 16/1/26.
 */
public interface IGamesListView {
    public void setActivity(Activity activity);
    public void setListView(PullToRefreshListView listView);
}
