package com.basecore.util.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.DisplayMetrics;

import com.basecore.application.BaseApplication;

/**
 * 
 * ScreenUtil:[类说明] 用来做Screen不同像素的显示，
 * 
 */
public class ScreenUtil {
	/**
	 * 转换dip值
	 * 
	 * @param context
	 *            当前context
	 * @param dipValue
	 *            dip值
	 * @return dip值转成px值
	 * 
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int dip2px(int dipValue) {
		float reSize = BaseApplication.getApplication().getResources()
				.getDisplayMetrics().density;
		return (int) ((dipValue * reSize) + 0.5);
	}

	public static int px2dip(int pxValue) {
		float reSize =  BaseApplication.getApplication().getResources()
				.getDisplayMetrics().density;
		return (int) ((pxValue / reSize) + 0.5);
	}
	
	public static Bitmap getScreenBitmap(Context context, Bitmap bitmap) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();

		if(bitmap == null) return null;
		
		final int width = bitmap.getWidth();
		final int height = bitmap.getHeight();
		
		try {
			float widthScale = metrics.widthPixels / (float)bitmap.getWidth();
			Matrix matrix = new Matrix();
			matrix.postScale(widthScale, widthScale);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
					height, matrix, true);
		} catch (OutOfMemoryError error) {
			if (null != bitmap) {
				bitmap.recycle();
				bitmap = null;
			}
		}
		
		return bitmap;
	}

	public static Bitmap getScreenBitmap(Context context, Bitmap bitmap, int maxHeight) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();

		if(bitmap == null) return null;
		
		final int width = bitmap.getWidth();
		final int height = bitmap.getHeight();
		
		try {
			float widthScale = metrics.widthPixels / (float)bitmap.getWidth();
			Matrix matrix = new Matrix();
			matrix.postScale(widthScale, widthScale);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
					height > maxHeight ? maxHeight : height, matrix, true);
		} catch (OutOfMemoryError error) {
			if (null != bitmap) {
				bitmap.recycle();
				bitmap = null;
			}
		}
		
		return bitmap;
	}
}
