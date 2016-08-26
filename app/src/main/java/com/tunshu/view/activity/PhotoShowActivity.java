package com.tunshu.view.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.basecore.util.bitmap.Options;
import com.basecore.util.core.ListUtils;
import com.basecore.widget.HackyViewPager;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tunshu.R;
import com.tunshu.entity.ImageItemEntity;

import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoShowActivity extends CommonActivity {
    private HackyViewPager viewPager;
    private List<ImageItemEntity> imageList;
    private int position = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(android.R.color.black);
        setContentView(R.layout.activity_photo_show);
        imageList = (List<ImageItemEntity>) getIntent().getSerializableExtra("imageList");
        position = getIntent().getIntExtra("position", 0);
        viewPager = (HackyViewPager) findViewById(R.id.view_pager);
        if (!ListUtils.isEmpty(imageList)) {
            fillAdapter(imageList);
        }
    }

    private void fillAdapter(List<ImageItemEntity> imageList) {
        PhotoViewPagerAdapter adapter = new PhotoViewPagerAdapter(imageList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        viewPager.setOffscreenPageLimit(3);
    }


    private class PhotoViewPagerAdapter extends PagerAdapter {
        private List<ImageItemEntity> imageList;

        public PhotoViewPagerAdapter(List<ImageItemEntity> imageList) {
            this.imageList = imageList;
        }

        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            View imageLayout = LayoutInflater.from(PhotoShowActivity.this).inflate(R.layout.activity_photo_show_page_item, container, false);
            PhotoView photoView = (PhotoView) imageLayout.findViewById(R.id.photo_view);
            ImageAware imageAware = new ImageViewAware(photoView);
            mImageLoader.displayImage(imageList.get(position).getBigimage(), imageAware, R.drawable.game_screenshot,Options.options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    finish();
                }
            });
            photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    finish();
                }
            });
            container.addView(imageLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return imageLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }


}
