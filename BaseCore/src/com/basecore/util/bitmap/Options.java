package com.basecore.util.bitmap;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.CircularBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class Options {
	public final static DisplayImageOptions options = new DisplayImageOptions.Builder()
			.cacheInMemory(true).bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
			.cacheOnDisk(true).displayer(new FadeInBitmapDisplayer(300)).postProcessor(null)
			.resetViewBeforeLoading(true).build();;
	public final static DisplayImageOptions circularOptions = new DisplayImageOptions.Builder()
			.cacheInMemory(true).bitmapConfig(Bitmap.Config.RGB_565)
			.cacheOnDisk(true).displayer(new CircularBitmapDisplayer()).build();
	public final static DisplayImageOptions roundOptions = new DisplayImageOptions.Builder()
			.cacheInMemory(true).bitmapConfig(Bitmap.Config.RGB_565)
			.cacheOnDisk(true).displayer(new RoundedBitmapDisplayer(26))// 图片圆角7度
			.build();
	public final static DisplayImageOptions roundOptions34 = new DisplayImageOptions.Builder()
			.cacheInMemory(true).bitmapConfig(Bitmap.Config.RGB_565)
			.cacheOnDisk(true).displayer(new RoundedBitmapDisplayer(30))// 图片圆角7度
			.build();
	public final static DisplayImageOptions roundOptionsBig = new DisplayImageOptions.Builder()
			.cacheInMemory(true).bitmapConfig(Bitmap.Config.RGB_565)
			.cacheOnDisk(true).displayer(new RoundedBitmapDisplayer(36))// 图片圆角7度
			.build();
}
