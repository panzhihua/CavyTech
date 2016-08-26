/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.basecore.util.bitmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.FloatMath;
import android.util.Log;

import com.basecore.util.core.AssertUtils;
import com.basecore.util.core.CleanUtils;
import com.basecore.util.core.FileInfoUtils;
import com.basecore.util.core.FileUtils;
import com.basecore.util.core.IOUtils;
import com.basecore.util.core.Utils;
import com.nostra13.universalimageloader.utils.L;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class BitmapUtils {
	private static final String TAG = "BitmapUtils";
	public static final int UNCONSTRAINED = -1;

	public static final int DEFAULT_JPEG_QUALITY = 90;

	public final static int MAX_WIDTH = 600;
	public final static int MAX_HEIGHT = 600;

	private BitmapUtils() {
	}

	/*
	 * Compute the sample size as a function of minSideLength and
	 * maxNumOfPixels. minSideLength is used to specify that minimal width or
	 * height of a bitmap. maxNumOfPixels is used to specify the maximal size in
	 * pixels that is tolerable in terms of memory usage.
	 * 
	 * The function returns a sample size based on the constraints. Both size
	 * and minSideLength can be passed in as UNCONSTRAINED, which indicates no
	 * care of the corresponding constraint. The functions prefers returning a
	 * sample size that generates a smaller bitmap, unless minSideLength =
	 * UNCONSTRAINED.
	 * 
	 * Also, the function rounds up the sample size to a power of 2 or multiple
	 * of 8 because BitmapFactory only honors sample size this way. For example,
	 * BitmapFactory downsamples an image by 2 even though the request is 3. So
	 * we round up the sample size to avoid OOM.
	 */
	public static int computeSampleSize(int width, int height,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(width, height,
				minSideLength, maxNumOfPixels);

		return initialSize <= 8 ? Utils.nextPowerOf2(initialSize)
				: (initialSize + 7) / 8 * 8;
	}

	private static int computeInitialSampleSize(int w, int h,
			int minSideLength, int maxNumOfPixels) {
		if (maxNumOfPixels == UNCONSTRAINED && minSideLength == UNCONSTRAINED)
			return 1;

		int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1
				: (int) FloatMath.ceil(FloatMath.sqrt((float) (w * h)
						/ maxNumOfPixels));

		if (minSideLength == UNCONSTRAINED) {
			return lowerBound;
		} else {
			int sampleSize = Math.min(w / minSideLength, h / minSideLength);
			return Math.max(sampleSize, lowerBound);
		}
	}

	// This computes a sample size which makes the longer side at least
	// minSideLength long. If that's not possible, return 1.
	public static int computeSampleSizeLarger(int w, int h, int minSideLength) {
		int initialSize = Math.max(w / minSideLength, h / minSideLength);
		if (initialSize <= 1)
			return 1;

		return initialSize <= 8 ? Utils.prevPowerOf2(initialSize)
				: initialSize / 8 * 8;
	}

	// Find the min x that 1 / x >= scale
	public static int computeSampleSizeLarger(float scale) {
		int initialSize = (int) FloatMath.floor(1f / scale);
		if (initialSize <= 1)
			return 1;

		return initialSize <= 8 ? Utils.prevPowerOf2(initialSize)
				: initialSize / 8 * 8;
	}

	// Find the max x that 1 / x <= scale.
	public static int computeSampleSize(float scale) {
		Utils.assertTrue(scale > 0);
		int initialSize = Math.max(1, (int) FloatMath.ceil(1 / scale));
		return initialSize <= 8 ? Utils.nextPowerOf2(initialSize)
				: (initialSize + 7) / 8 * 8;
	}

	public static Bitmap resizeBitmapByScale(Bitmap bitmap, float scale,
			boolean recycle) {
		int width = Math.round(bitmap.getWidth() * scale);
		int height = Math.round(bitmap.getHeight() * scale);
		if (width == bitmap.getWidth() && height == bitmap.getHeight())
			return bitmap;
		Bitmap target = Bitmap.createBitmap(width, height, getConfig(bitmap));
		Canvas canvas = new Canvas(target);
		canvas.scale(scale, scale);
		Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
		canvas.drawBitmap(bitmap, 0, 0, paint);
		if (recycle)
			bitmap.recycle();
		return target;
	}

	private static Bitmap.Config getConfig(Bitmap bitmap) {
		Bitmap.Config config = bitmap.getConfig();
		if (config == null) {
			config = Bitmap.Config.ARGB_8888;
		}
		return config;
	}

	public static Bitmap resizeDownBySideLength(Bitmap bitmap, int maxLength,
			boolean recycle) {
		int srcWidth = bitmap.getWidth();
		int srcHeight = bitmap.getHeight();
		float scale = Math.min((float) maxLength / srcWidth, (float) maxLength
				/ srcHeight);
		if (scale >= 1.0f)
			return bitmap;
		return resizeBitmapByScale(bitmap, scale, recycle);
	}

	public static Bitmap resizeAndCropCenter(Bitmap bitmap, int size,
			boolean recycle) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		if (w == size && h == size)
			return bitmap;

		// scale the image so that the shorter side equals to the target;
		// the longer side will be center-cropped.
		float scale = (float) size / Math.min(w, h);

		Bitmap target = Bitmap.createBitmap(size, size, getConfig(bitmap));
		int width = Math.round(scale * bitmap.getWidth());
		int height = Math.round(scale * bitmap.getHeight());
		Canvas canvas = new Canvas(target);
		canvas.translate((size - width) / 2f, (size - height) / 2f);
		canvas.scale(scale, scale);
		Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
		canvas.drawBitmap(bitmap, 0, 0, paint);
		if (recycle)
			bitmap.recycle();
		return target;
	}

	public static void recycleSilently(Bitmap bitmap) {
		if (bitmap == null)
			return;
		try {
			bitmap.recycle();
		} catch (Throwable t) {
			Log.w(TAG, "unable recycle bitmap", t);
		}
	}

	public static Bitmap rotateBitmap(Bitmap source, int rotation,
			boolean recycle) {
		if (rotation == 0)
			return source;
		int w = source.getWidth();
		int h = source.getHeight();
		Matrix m = new Matrix();
		m.postRotate(rotation);
		Bitmap bitmap = Bitmap.createBitmap(source, 0, 0, w, h, m, true);
		if (recycle)
			source.recycle();
		return bitmap;
	}

	public static Bitmap createVideoThumbnail(String filePath) {
		// MediaMetadataRetriever is available on API Level 8
		// but is hidden until API Level 10
		Class<?> clazz = null;
		Object instance = null;
		try {
			clazz = Class.forName("android.media.MediaMetadataRetriever");
			instance = clazz.newInstance();

			Method method = clazz.getMethod("setDataSource", String.class);
			method.invoke(instance, filePath);

			// The method name changes between API Level 9 and 10.
			if (Build.VERSION.SDK_INT <= 9) {
				return (Bitmap) clazz.getMethod("captureFrame")
						.invoke(instance);
			} else {
				byte[] data = (byte[]) clazz.getMethod("getEmbeddedPicture")
						.invoke(instance);
				if (data != null) {
					Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
							data.length);
					if (bitmap != null)
						return bitmap;
				}
				return (Bitmap) clazz.getMethod("getFrameAtTime").invoke(
						instance);
			}
		} catch (IllegalArgumentException ex) {
			// Assume this is a corrupt video file
		} catch (RuntimeException ex) {
			// Assume this is a corrupt video file.
		} catch (InstantiationException e) {
			Log.e(TAG, "createVideoThumbnail", e);
		} catch (InvocationTargetException e) {
			Log.e(TAG, "createVideoThumbnail", e);
		} catch (ClassNotFoundException e) {
			Log.e(TAG, "createVideoThumbnail", e);
		} catch (NoSuchMethodException e) {
			Log.e(TAG, "createVideoThumbnail", e);
		} catch (IllegalAccessException e) {
			Log.e(TAG, "createVideoThumbnail", e);
		} finally {
			try {
				if (instance != null) {
					clazz.getMethod("release").invoke(instance);
				}
			} catch (Exception ignored) {
			}
		}
		return null;
	}

	public static byte[] compressToBytes(Bitmap bitmap) {
		return compressToBytes(bitmap, DEFAULT_JPEG_QUALITY);
	}

	public static byte[] compressToBytes(Bitmap bitmap, int quality) {
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream(65536);
			bitmap.compress(CompressFormat.PNG, quality, baos);
			return baos.toByteArray();
		} finally {
			try {
				baos.flush();
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean isSupportedByRegionDecoder(String mimeType) {
		if (mimeType == null)
			return false;
		mimeType = mimeType.toLowerCase();
		return mimeType.startsWith("image/")
				&& (!mimeType.equals("image/gif") && !mimeType.endsWith("bmp"));
	}

	public static boolean isRotationSupported(String mimeType) {
		if (mimeType == null)
			return false;
		mimeType = mimeType.toLowerCase();
		return mimeType.equals("image/jpeg");
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();
		L.i("drawableToBitmap w:" + w + " h:" + h);
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		drawable.draw(canvas);
		return bitmap;
	}

	public static boolean getZoomOutBitmapBound(File paramFile, int paramInt,
			Rect paramRect) {
		AssertUtils.checkNull(paramFile);
		AssertUtils.checkNull(paramRect);
		try {
			if (!FileUtils.doesExisted(paramFile))
				return false;
			boolean bool = getZoomOutBitmapBound(
					FileUtils.makeInputBuffered(new FileInputStream(paramFile)),
					paramInt, paramRect);
			return bool;
		} catch (FileNotFoundException localFileNotFoundException) {
			L.e(localFileNotFoundException);
		}
		return false;
	}

	public static boolean getZoomOutBitmapBound(InputStream paramInputStream,
			int paramInt, Rect paramRect) {
		AssertUtils.checkNull(paramInputStream);
		AssertUtils.checkNull(paramRect);
		BitmapFactory.Options localOptions = new BitmapFactory.Options();
		InputStream localInputStream = FileUtils
				.makeInputBuffered(paramInputStream);
		localOptions.inJustDecodeBounds = true;
		if (paramInt > 1)
			localOptions.inSampleSize = paramInt;
		BitmapFactory.decodeStream(localInputStream, null, localOptions);
		CleanUtils.closeStream(localInputStream);
		if ((localOptions.outWidth > 0) && (localOptions.outHeight > 0)) {
			paramRect.set(0, 0, localOptions.outWidth, localOptions.outHeight);
			return true;
		}
		return false;
	}

	public static boolean getZoomOutBitmapBound(String paramString,
			int paramInt, Rect paramRect) {
		AssertUtils.checkStringNullOrEmpty(paramString);
		return getZoomOutBitmapBound(new File(paramString), paramInt, paramRect);
	}

	public static boolean getZoomOutBitmapBound(byte[] paramArrayOfByte,
			int paramInt1, int paramInt2, int paramInt3, Rect paramRect) {
		AssertUtils.checkArrayNullOrEmpty(paramArrayOfByte);
		AssertUtils.checkNull(paramRect);
		BitmapFactory.Options localOptions = new BitmapFactory.Options();
		localOptions.inJustDecodeBounds = true;
		if (paramInt3 > 1)
			localOptions.inSampleSize = paramInt3;
		BitmapFactory.decodeByteArray(paramArrayOfByte, paramInt1, paramInt2,
				localOptions);
		if ((localOptions.outWidth > 0) && (localOptions.outHeight > 0)) {
			paramRect.set(0, 0, localOptions.outWidth, localOptions.outHeight);
			return true;
		}
		return false;
	}

	public static void configOptions(BitmapFactory.Options options,
			Bitmap.Config config) {
		options.inPreferredConfig = config;
	}

	public static void sampleOptions(BitmapFactory.Options options, int sample) {
		options.inSampleSize = sample;
	}

//	public static void sampleOptions(BitmapFactory.Options options,
//			Object object) {
//		int width = DecodeUtils.getWidth(object);
//		int sample = BitmapUtils.getSampleSize(width, MAX_WIDTH);
//		options.inSampleSize = sample;
//	}

	public static int getSampleSize(double width, double screenWidth) {
		return (int) Math.ceil(width / screenWidth);
	}

//	public static Bitmap[] decodeBitmaps(Object object, Options options) {
//		int width = DecodeUtils.getWidth(object);
//		int height = DecodeUtils.getHeight(object);
//
//		final Bitmap[] bitmaps;
//		if (height % 1024 == 0) {
//			bitmaps = new Bitmap[height / MAX_HEIGHT];
//		} else {
//			bitmaps = new Bitmap[height / MAX_HEIGHT + 1];
//		}
//
//		L.i("decodeBitmaps size:" + bitmaps);
//
//		int index = 0;
//		while (true) {
//			Bitmap localBitmap = null;
//			if (index < bitmaps.length - 1) {
//				localBitmap = DecodeUtils.decodeRegion(object, new Rect(0,
//						index * MAX_HEIGHT, width, MAX_HEIGHT * (index + 1)),
//						options);
//				bitmaps[index] = localBitmap;
//			} else if (index == bitmaps.length - 1) {
//				localBitmap = DecodeUtils.decodeRegion(object, new Rect(0,
//						index * MAX_HEIGHT, width, height), options);
//				bitmaps[index] = localBitmap;
//				break;
//			} else {
//				break;
//			}
//			++index;
//		}
//
//		return bitmaps;
//	}

	public static File getImageFile(Context context) {
		File dir = StorageUtils.getIndividualCacheDirectory(context);
		File out = new File(dir, FileInfoUtils.generalFileName());
		return out;
	}
	public static File getJseImageFile(Context context) {
		File file = new File(Environment.getExternalStorageDirectory().getPath(), "BaseCore/Images");
		if (!file.exists()) {
			file.mkdirs();
		}
		File out = new File(file, FileInfoUtils.generalFileName());
		return out;
	}

	public static Uri getJseImageFileUri(Context context) {
		File out = getJseImageFile(context);
		Uri uri = Uri.fromFile(out);
		return uri;
	}
	public static Uri getImageFileUri(Context context) {
		File out = getImageFile(context);
		Uri uri = Uri.fromFile(out);
		return uri;
	}

	public static File saveBitmap(Context context, Bitmap bitmap) {
		FileOutputStream fos = null;
		File file = getImageFile(context);
		try {
			fos = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG,
					BitmapUtils.DEFAULT_JPEG_QUALITY, fos);// 90为清晰度 0~100
			fos.flush();
			return file;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (fos != null) {
				try {
					fos.close();
					fos = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static Bitmap decode(Context context, Uri uri, int maxW, int maxH) {
		InputStream stream = openInputStream(context, uri);
		if (null == stream) {
			return null;
		}

		maxW = maxW > MAX_WIDTH ? MAX_WIDTH : maxW;
		maxH = maxH > MAX_HEIGHT ? MAX_HEIGHT : maxH;

		Bitmap bitmap = null;
		int[] imageSize = new int[2];

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		boolean decoded = decodeImageBounds(stream, imageSize, options);
		int orientation = defineExifOrientation(uri, options.outMimeType);
		IOUtils.closeSilently(stream);

		Log.v("dsd", "decode orientation:" + orientation + " decoded:"
				+ decoded);

		if (decoded) {
			int sampleSize = computeSampleSize(imageSize[0], imageSize[1],
					(int) (maxW * 1.2D), (int) (maxH * 1.2D), orientation);
			options = getDefaultOptions();

			float maxSampleW = maxW * 1.5F;
			float maxSampleH = maxH * 1.5F;

			if ((imageSize[1] < maxSampleW + 100.0F)
					&& (imageSize[1] < maxSampleH)) {
				sampleSize = 1;
			}

			options.inSampleSize = sampleSize;

			bitmap = decodeBitmap(context, uri, options, maxW, maxH,
					orientation, 0);
		}

		return bitmap;
	}

	static Bitmap decodeBitmap(Context context, Uri uri,
			BitmapFactory.Options options, int maxW, int maxH, int orientation,
			int pass) {
		Bitmap bitmap = null;
		Bitmap newBitmap = null;

		if (pass > 10) {
			return null;
		}

		InputStream stream = openInputStream(context, uri);
		if (null == stream)
			return null;

		L.d(TAG, "options.inSampleSize:" + options.inSampleSize);

		try {
			bitmap = BitmapFactory.decodeStream(stream, null, options);
			IOUtils.closeSilently(stream);

			if (bitmap != null) {
				L.d(TAG, "maxW:" + maxW + " maxH:" + maxH);
				newBitmap = BitmapUtils.resizeBitmap(bitmap, maxW, maxH,
						orientation);
				if (bitmap != newBitmap) {
					bitmap.recycle();
				}
				bitmap = newBitmap;
			}
		} catch (OutOfMemoryError error) {
			IOUtils.closeSilently(stream);
			if (null != bitmap) {
				bitmap.recycle();
				bitmap = null;
			}
			options.inSampleSize += 1;
			bitmap = decodeBitmap(context, uri, options, maxW, maxH,
					orientation, pass + 1);
		}
		return bitmap;
	}

	public static Bitmap resizeBitmap(Bitmap input, int destWidth,
			int destHeight, int rotation) throws OutOfMemoryError {
		int dstWidth = destWidth;
		int dstHeight = destHeight;
		int srcWidth = input.getWidth();
		int srcHeight = input.getHeight();

		if ((rotation == 90) || (rotation == 270)) {
			dstWidth = destHeight;
			dstHeight = destWidth;
		}

		boolean needsResize = false;

		if ((srcWidth > dstWidth) || (srcHeight > dstHeight)) {
			needsResize = true;

			float ratio1 = (float) srcWidth / dstWidth;
			float ratio2 = (float) srcHeight / dstHeight;
			Log.v("dsd", "ratio1:" + ratio1 + " ratio2:" + ratio2);

			if (ratio1 > ratio2) {
				float p = (float) dstWidth / srcWidth;
				dstHeight = (int) (srcHeight * p);
			} else {
				float p = (float) dstHeight / srcHeight;
				dstWidth = (int) (srcWidth * p);
			}
		} else {
			dstWidth = srcWidth;
			dstHeight = srcHeight;
		}

		Log.v("dsd", "dstWidth:" + dstWidth + " dstHeight:" + dstHeight
				+ " srcWidth:" + srcWidth + " srcHeight:" + srcHeight);
		if ((needsResize) || (rotation != 0)) {
			Bitmap output = null;
			if (rotation == 0) {
				output = Bitmap.createScaledBitmap(input, dstWidth, dstHeight,
						true);
			} else {
				Matrix matrix = new Matrix();
				matrix.postScale((float) dstWidth / srcWidth, (float) dstHeight
						/ srcHeight);
				matrix.postRotate(rotation);
				output = Bitmap.createBitmap(input, 0, 0, srcWidth, srcHeight,
						matrix, true);
			}
			return output;
		}
		return input;
	}

	public static BitmapFactory.Options getDefaultOptions() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inDither = false;
		options.inJustDecodeBounds = false;
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inTempStorage = new byte[16384];
		return options;
	}

	private static int computeSampleSize(int bitmapW, int bitmapH, int maxW,
			int maxH, int orientation) {
		double h = 0.0;
		double w = 0.0;
		if ((orientation == 0) || (orientation == 180)) {
			w = bitmapW;
			h = bitmapH;
		} else {
			w = bitmapH;
			h = bitmapW;
		}

		Log.v("dsd", "computeSampleSize w:" + w);
		Log.v("dsd", "computeSampleSize h:" + h);
		Log.v("dsd", "computeSampleSize bitmapW:" + bitmapW);
		Log.v("dsd", "computeSampleSize bitmapH:" + bitmapH);
		Log.v("dsd", "computeSampleSize w / maxW:" + w / maxW);
		Log.v("dsd", "computeSampleSize h / maxH:" + h / maxH);
		int sampleSize = (int) Math.ceil(Math.max(w / maxW, h / maxH));
		Log.v("dsd", "computeSampleSize sampleSize:" + sampleSize);
		return sampleSize;
	}

	public static boolean decodeImageBounds(InputStream stream, int[] outSize,
			BitmapFactory.Options options) {
		BitmapFactory.decodeStream(stream, null, options);
		if ((options.outHeight > 0) && (options.outWidth > 0)) {
			outSize[0] = options.outWidth;
			outSize[1] = options.outHeight;
			return true;
		}
		return false;
	}

	public static int defineExifOrientation(Uri imageUri, String mimeType) {
		int rotation = 0;
		if ("image/jpeg".equalsIgnoreCase(mimeType)
				&& "file".equals(imageUri.getScheme())) {
			try {
				ExifInterface exif = new ExifInterface(imageUri.getPath());
				int exifOrientation = exif.getAttributeInt(
						ExifInterface.TAG_ORIENTATION,
						ExifInterface.ORIENTATION_NORMAL);
				Log.e("dsd", "exifOrientation:" + exifOrientation);
				switch (exifOrientation) {
				case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
				case ExifInterface.ORIENTATION_NORMAL:
					rotation = 0;
					break;
				case ExifInterface.ORIENTATION_TRANSVERSE:
				case ExifInterface.ORIENTATION_ROTATE_90:
					rotation = 90;
					break;
				case ExifInterface.ORIENTATION_FLIP_VERTICAL:
				case ExifInterface.ORIENTATION_ROTATE_180:
					rotation = 180;
					break;
				case ExifInterface.ORIENTATION_TRANSPOSE:
				case ExifInterface.ORIENTATION_ROTATE_270:
					rotation = 270;
					break;
				}
			} catch (IOException e) {
				Log.e("dsd", "Can't read EXIF tags from file [%s]" + imageUri);
			}
		}
		return rotation;
	}

	public static InputStream openInputStream(Context context, Uri uri) {
		if (null == uri)
			return null;
		String scheme = uri.getScheme();
		InputStream stream = null;
		if ((scheme == null) || ("file".equals(scheme))) {
			stream = openFileInputStream(uri.getPath());
		} else if ("content".equals(scheme)) {
			stream = openContentInputStream(context, uri);
		} else if (("http".equals(scheme)) || ("https".equals(scheme))) {
			stream = openRemoteInputStream(uri);
		}
		return stream;
	}

	static InputStream openFileInputStream(String path) {
		try {
			return new FileInputStream(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	static InputStream openContentInputStream(Context context, Uri uri) {
		try {
			return context.getContentResolver().openInputStream(uri);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	static InputStream openRemoteInputStream(Uri uri) {
		URL finalUrl;
		try {
			finalUrl = new URL(uri.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		HttpURLConnection connection;
		try {
			connection = (HttpURLConnection) finalUrl.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		connection.setInstanceFollowRedirects(false);
		int code;
		try {
			code = connection.getResponseCode();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		if ((code == 301) || (code == 302) || (code == 303)) {
			String newLocation = connection.getHeaderField("Location");
			return openRemoteInputStream(Uri.parse(newLocation));
		}
		try {
			return (InputStream) finalUrl.getContent();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
