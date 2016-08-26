package com.tunshu.adapter;


import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import com.tunshu.R;

import java.util.HashMap;

public class GuideAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ISplashAdapter iSplashAdapter;
    private static View[] arrView;
    private int[] imageList;
    private HashMap<Integer, View> imgViewMap = new HashMap<Integer, View>();

    public GuideAdapter(Context context, ISplashAdapter iSplashAdapter) {
        this.context = context;
        this.iSplashAdapter = iSplashAdapter;
        inflater = LayoutInflater.from(context);
    }

    public GuideAdapter(Context context, int[] imageList) {
        this.context = context;
        this.imageList = imageList;
        inflater = LayoutInflater.from(context);
        arrView = new View[imageList.length];
    }

    @Override
    public void destroyItem(View v, int position, Object obj) {
    }

    @Override
    public void finishUpdate(View arg0) {
    }

    @Override
    public int getCount() {
        if (arrView != null)
            return arrView.length;
        else
            return 0;
    }

    public void clearHashMap() {

        if (imgViewMap != null) {
            imgViewMap.clear();
        }
    }

    @Override
    public Object instantiateItem(View v, int position) {
        try {
            if (!imgViewMap.containsKey(position)) {

                arrView[position] = inflater.inflate(R.layout.activity_help_detail_item, null);
                final ImageView imgView = (ImageView) arrView[position].findViewById(R.id.splash_item_imageview);
                final Button button = (Button) arrView[position].findViewById(R.id.splash_item_btn);
                imgView.setBackgroundResource(imageList[position]);
                if (position < 2) {
                    button.setVisibility(View.INVISIBLE);
                } else {
                    button.setVisibility(View.VISIBLE);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            if (iSplashAdapter != null) {
                                iSplashAdapter.doOnClick();
                            }
                        }
                    });
                }
                ((ViewPager) v).addView(arrView[position]);
                imgViewMap.put(position, arrView[position]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return arrView[position];
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(View arg0) {
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public interface ISplashAdapter {
        public void doOnClick();
    }
}
