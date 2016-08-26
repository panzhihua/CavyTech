package com.tunshu.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.basecore.util.bitmap.Options;
import com.basecore.util.core.ListUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tunshu.entity.InfoEntity;
import com.tunshu.view.activity.GameDetailActivity;

import java.util.List;

public class ImagePagerAdapter extends RecyclingPagerAdapter {

	private Context context;
	private List<InfoEntity.DataEntity.BannerEntity> imageIdList;
	private int size;
	private boolean isInfiniteLoop;

	public ImagePagerAdapter(Context context, List<InfoEntity.DataEntity.BannerEntity> imageIdList) {
		this.context = context;
		this.imageIdList = imageIdList;
		this.size = ListUtils.getSize(imageIdList);
		isInfiniteLoop = false;
	}

	@Override
	public int getCount() {
		// Infinite loop
		return isInfiniteLoop ? Integer.MAX_VALUE : ListUtils.getSize(imageIdList);
	}

	/**
	 * get really position
	 * 
	 * @param position
	 * @return
	 */
	private int getPosition(int position) {
		return isInfiniteLoop ? position % size : position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup container) {
		ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = holder.imageView = new ImageView(context);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		ImageLoader.getInstance().displayImage(imageIdList.get(getPosition(position)).getBannerphone(), holder.imageView, Options.options);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, GameDetailActivity.class);
				intent.putExtra("gameId", imageIdList.get(getPosition(position)).getGameid());
				context.startActivity(intent);
			}
		});
		return view;
	}

	private static class ViewHolder {

		ImageView imageView;
	}

	/**
	 * @return the isInfiniteLoop
	 */
	public boolean isInfiniteLoop() {
		return isInfiniteLoop;
	}

	/**
	 * @param isInfiniteLoop
	 *            the isInfiniteLoop to set
	 */
	public ImagePagerAdapter setInfiniteLoop(boolean isInfiniteLoop) {
		this.isInfiniteLoop = isInfiniteLoop;
		return this;
	}
}
