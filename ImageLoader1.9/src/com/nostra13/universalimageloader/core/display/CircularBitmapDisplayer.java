package com.nostra13.universalimageloader.core.display;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

public class CircularBitmapDisplayer implements BitmapDisplayer {
	public Bitmap display(Bitmap bitmap, ImageView imageView,
			LoadedFrom loadedFrom) {
		Bitmap roundBitmap;
		try {
			roundBitmap = getCircularBitmap(bitmap);
		} catch (OutOfMemoryError e) {
			Log.e(ImageLoader.TAG,
					"Can't create bitmap with rounded corners. Not enough memory.",
					e);
			roundBitmap = bitmap;
		}
		imageView.setImageBitmap(roundBitmap);
		return roundBitmap;
	}

	private Bitmap getCircularBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.WHITE);
		canvas.drawOval(rectF, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	@Override
	public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
		Bitmap roundBitmap;
		try {
			roundBitmap = getCircularBitmap(bitmap);
		} catch (OutOfMemoryError e) {
			Log.e(ImageLoader.TAG,
					"Can't create bitmap with rounded corners. Not enough memory.",
					e);
			roundBitmap = bitmap;
		}
		imageAware.setImageBitmap(roundBitmap);
		
	}

}