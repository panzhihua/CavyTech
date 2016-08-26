package com.basecore.window;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.basecore.application.BaseApplication;
import com.basecore.util.bitmap.BitmapUtils;
import com.basecore.util.bitmap.DecodeBitmapAsync;
import com.basecore.util.bitmap.DecodeBitmapAsync.FinishListener;
import com.basecore.util.bitmap.ImageLoadingUtils;
import com.basecore.util.config.IConfig;
import com.basecore.util.crop.CropImage;
import com.basecore.util.crop.CropImageActivity;
import com.basecore.util.log.LogUtil;
import com.basecore.widget.CustomToast;

public class WindowWrapper {

	private final static String TAG = "WindowWrapper";

	private final static String TEMP_FILENAME = "temp_filename";
	private final static String TEMP_CROP = "temp_crop";
	private final static String TEMP_WIDTH = "temp_width";
	private final static String TEMP_HEIGHT = "temp_height";
	private int defaultWidth = 480;
	private int defaultHeight = 800;

	public final static int REQUEST_CODE_CAMERA = 111;
	public final static int REQUEST_CODE_CROP = 222;
	public final static int REQUEST_CODE_GALLERY = 333;
	public final static int REQUEST_CODE_GALLERY_KITKAT = 444;


	private ImageLoadingUtils utils;

	private IWindow mWindow;

	private IConfig mConfig;

	/** Parent view of the window decoration (action bar, mode, etc.). */
	private ViewGroup mDecor;
	/** Parent view of the activity content. */
	private ViewGroup mContentParent;

	private Context context;

	public WindowWrapper(IWindow window) {
		this.mWindow = window;
	}

	public BaseApplication getCoreApplication() {
		return BaseApplication.getApplication();
	}

	public void doActivity(Context context, Class<?> cls) {
		this.context = context;
		doActivity(context, cls, new Bundle());
	}

	public void doActivity(Context context, Class<?> cls, Bundle bundle) {
		this.context = context;
		Intent intent = new Intent();
		intent.setClass(context, cls);
		intent.putExtras(bundle);
		mWindow.startActivity(intent);
	}

	public void doCamera(Context context) {
		this.context = context;
		doCamera(context, false);
	}

	public void doCamera(Context context, int width, int height) {
		this.context = context;
		doCamera(context, false, width, height);
	}

	public void doCamera(Context context, boolean isCrop) {
		this.context = context;
		Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Uri uri = BitmapUtils.getJseImageFileUri(context);
		getConfig().setString(TEMP_FILENAME, String.valueOf(uri));
		getConfig().setBoolean(TEMP_CROP, isCrop);
		LogUtil.getLogger().d("doCamera uri:" + uri + " isCrop:" + isCrop);

		imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

		imageCaptureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

		mWindow.startActivityForResult(imageCaptureIntent, REQUEST_CODE_CAMERA);
	}

	public void doCamera(Context context, boolean isCrop, int width, int height) {
		this.context = context;
		Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Uri uri = BitmapUtils.getJseImageFileUri(context);
		getConfig().setString(TEMP_FILENAME, String.valueOf(uri));
		getConfig().setBoolean(TEMP_CROP, isCrop);
		getConfig().setInt(TEMP_WIDTH, width);
		getConfig().setInt(TEMP_HEIGHT, height);
		LogUtil.getLogger().d("doCamera uri:" + uri + " isCrop:" + isCrop);

		imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

		imageCaptureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

		mWindow.startActivityForResult(imageCaptureIntent, REQUEST_CODE_CAMERA);
	}

	public void doGallery(Context context) {
		this.context = context;
		doGallery(context, false);
	}

	public void doGallery(Context context, int width, int height) {
		this.context = context;
		doGallery(context, false, width, height);
	}

	public void doGallery(Context context, boolean isCrop) {
		this.context = context;
		getConfig().setBoolean(TEMP_CROP, isCrop);
		// 打开系统自带的图片库
		Intent intent = new Intent();
		// 开启Pictures画面Type设定为image
		intent.setType("image/*");
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
			// 使用Intent. ACTION_OPEN_DOCUMENT这个Action
			intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
			// 取得相片后返回本画面
			mWindow.startActivityForResult(intent, REQUEST_CODE_GALLERY_KITKAT);
		} else {
			// 使用Intent. ACTION_GET_CONTENT这个Action
			intent.setAction(Intent.ACTION_GET_CONTENT);
			// 取得相片后返回本画面
			mWindow.startActivityForResult(intent, REQUEST_CODE_GALLERY);
		}
	}

	public void doGallery(Context context, boolean isCrop, int width, int height) {
		this.context = context;
		getConfig().setBoolean(TEMP_CROP, isCrop);
		getConfig().setInt(TEMP_WIDTH, width);
		getConfig().setInt(TEMP_HEIGHT, height);
		// 打开系统自带的图片库
		Intent intent = new Intent();
		// 开启Pictures画面Type设定为image
		intent.setType("image/*");

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
			// 使用Intent. ACTION_OPEN_DOCUMENT这个Action
			intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
			// 取得相片后返回本画面
			try {
				mWindow.startActivityForResult(intent, REQUEST_CODE_GALLERY_KITKAT);
			} catch (Exception e) {//fix 金立U3无法调用到相册的问题
				intent.setAction(Intent.ACTION_GET_CONTENT);
				mWindow.startActivityForResult(intent, REQUEST_CODE_GALLERY_KITKAT);
			}
		} else {
			// 使用Intent. ACTION_GET_CONTENT这个Action
			intent.setAction(Intent.ACTION_GET_CONTENT);
			// 取得相片后返回本画面
			mWindow.startActivityForResult(intent, REQUEST_CODE_GALLERY);
		}

	}

	public void startPhotoCrop(Context context, String sourceUri, String desUri) {
		Intent intent = new Intent(context, CropImageActivity.class);
		intent.putExtra("crop", "true");
		intent.putExtra("isSquare", getConfig().getBoolean(TEMP_CROP, false));
		intent.putExtra("aspectX", defaultWidth);
		intent.putExtra("aspectY", defaultHeight);
		intent.putExtra("outputX", defaultWidth);
		intent.putExtra("outputY", defaultHeight);
		intent.putExtra("scale", true);
		intent.putExtra("image-path", sourceUri);
		intent.putExtra("output", desUri);
		intent.putExtra("return-data", false);
		intent.putExtra("scaleUpIfNeeded", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		mWindow.startActivityForResult(intent, REQUEST_CODE_CROP);
	}

	public void startPhotoCrop(Context context, String sourceUri, String desUri, int width, int height) {
		Intent intent = new Intent(context, CropImageActivity.class);
		intent.putExtra("crop", "true");
		intent.putExtra("isSquare", getConfig().getBoolean(TEMP_CROP, false));
		intent.putExtra("aspectX", width);
		intent.putExtra("aspectY", height);
		intent.putExtra("outputX", width);
		intent.putExtra("outputY", height);
		intent.putExtra("scale", true);
		intent.putExtra("image-path", sourceUri);
		intent.putExtra("output", desUri);
		intent.putExtra("return-data", false);
		intent.putExtra("scaleUpIfNeeded", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		mWindow.startActivityForResult(intent, REQUEST_CODE_CROP);
	}

	public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
		LogUtil.getLogger().d("onActivityResult resultCode:" + resultCode);
		if (resultCode != Activity.RESULT_OK)
			return;
		final String strUri;
		final Boolean boolCrop;
		final int width;
		final int height;
		switch (requestCode) {
		case REQUEST_CODE_CAMERA:
			strUri = getConfig().getString(TEMP_FILENAME, "");
			boolCrop = getConfig().getBoolean(TEMP_CROP, false);
			width = getConfig().getInt(TEMP_WIDTH, defaultWidth);
			height = getConfig().getInt(TEMP_HEIGHT, defaultHeight);
			LogUtil.getLogger().d("onActivityResult uri:" + strUri + " boolCrop:" + boolCrop);
//			if (boolCrop) {
				startPhotoCrop(context, strUri, strUri, width, height);
//			} else {
//				new ImageCompressionAsyncTask(false).execute(strUri);
//			}
			break;
		case REQUEST_CODE_CROP:
			strUri = getConfig().getString(TEMP_FILENAME, "");
			LogUtil.getLogger().d("onActivityResult crop:" + strUri);
			if(strUri.length()>0){
				new ImageCompressionAsyncTask(false).execute(strUri);
			}else {
				mWindow.onReturnImageUri(strUri);
			}
			break;
		case REQUEST_CODE_GALLERY:
			if (data != null) {
				Uri uriImage = data.getData();
				LogUtil.getLogger().d(uriImage);
				boolCrop = getConfig().getBoolean(TEMP_CROP, false);
				width = getConfig().getInt(TEMP_WIDTH, defaultWidth);
				height = getConfig().getInt(TEMP_HEIGHT, defaultHeight);
//				if (boolCrop) {
					Uri uri = BitmapUtils.getImageFileUri(context);
					getConfig().setString(TEMP_FILENAME, String.valueOf(uri));
					startPhotoCrop(context, uriImage.toString(), uri.toString(), width, height);
//				} else {
//					new ImageCompressionAsyncTask(false).execute(uriImage.toString());
//				}
			}
			break;
		case REQUEST_CODE_GALLERY_KITKAT:
			if (data != null) {
				Uri uriImage = data.getData();
				LogUtil.getLogger().d(uriImage);
				boolCrop = getConfig().getBoolean(TEMP_CROP, false);
				width = getConfig().getInt(TEMP_WIDTH, defaultWidth);
				height = getConfig().getInt(TEMP_HEIGHT, defaultHeight);
//				if (boolCrop) {
					Uri uri = BitmapUtils.getImageFileUri(context);
					getConfig().setString(TEMP_FILENAME, String.valueOf(uri));
					startPhotoCrop(context, uriImage.toString(), uri.toString(), width, height);
//				} else {
//					new ImageCompressionAsyncTask(false).execute(uriImage.toString());
//				}
			}
			break;
		default:
			break;
		}
	}

	class ImageCompressionAsyncTask extends AsyncTask<String, Void, Uri> {
		private boolean fromGallery;

		public ImageCompressionAsyncTask(boolean fromGallery) {
			this.fromGallery = fromGallery;
		}

		@Override
		protected Uri doInBackground(String... params) {
			Uri filePath = compressImage(params[0]);
			return filePath;
		}

		public Uri compressImage(String imageUri) {

			String filePath = getRealPathFromURI(context, imageUri);
			Bitmap scaledBitmap = null;

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

			int actualHeight = options.outHeight;
			int actualWidth = options.outWidth;
			// float maxHeight = 960.0f;
			// float maxWidth = 640.0f;
			float maxHeight = getConfig().getInt(TEMP_WIDTH, 640);
			float maxWidth = getConfig().getInt(TEMP_HEIGHT, 960);
			float imgRatio = actualWidth / actualHeight;
			float maxRatio = maxWidth / maxHeight;

			if (actualHeight > maxHeight || actualWidth > maxWidth) {
				if (imgRatio < maxRatio) {
					imgRatio = maxHeight / actualHeight;
					actualWidth = (int) (imgRatio * actualWidth);
					actualHeight = (int) maxHeight;
				} else if (imgRatio > maxRatio) {
					imgRatio = maxWidth / actualWidth;
					actualHeight = (int) (imgRatio * actualHeight);
					actualWidth = (int) maxWidth;
				} else {
					actualHeight = (int) maxHeight;
					actualWidth = (int) maxWidth;

				}
			}
			utils = new ImageLoadingUtils(getCoreApplication());
			options.inSampleSize = utils.calculateInSampleSize(options, actualWidth, actualHeight);
			options.inJustDecodeBounds = false;
			options.inDither = false;
			options.inPurgeable = true;
			options.inInputShareable = true;
			options.inTempStorage = new byte[16 * 1024];

			try {
				bmp = BitmapFactory.decodeFile(filePath, options);
			} catch (OutOfMemoryError exception) {
				exception.printStackTrace();

			}
			try {
				if (actualWidth > 0 && actualHeight > 0) {
					scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
				} else {
					return null;
				}
			} catch (OutOfMemoryError exception) {
				exception.printStackTrace();
			} catch (Exception ex) {
				LogUtil.getLogger().e(ex);
			}

			float ratioX = actualWidth / (float) options.outWidth;
			float ratioY = actualHeight / (float) options.outHeight;
			float middleX = actualWidth / 2.0f;
			float middleY = actualHeight / 2.0f;

			Matrix scaleMatrix = new Matrix();
			scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

			Canvas canvas = new Canvas(scaledBitmap);
			canvas.setMatrix(scaleMatrix);
			canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

			ExifInterface exif;
			try {
				exif = new ExifInterface(filePath);

				int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
				Log.d("EXIF", "Exif: " + orientation);
				Matrix matrix = new Matrix();
				if (orientation == 6) {
					matrix.postRotate(90);
					Log.d("EXIF", "Exif: " + orientation);
				} else if (orientation == 3) {
					matrix.postRotate(180);
					Log.d("EXIF", "Exif: " + orientation);
				} else if (orientation == 8) {
					matrix.postRotate(270);
					Log.d("EXIF", "Exif: " + orientation);
				}
				scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			FileOutputStream out = null;
			Uri filename = getFilename();
			try {
				out = new FileOutputStream(filename.getPath());
				scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return filename;

		}

		private String getRealPathFromURI(Context context, String contentURI) {
			Uri uri = Uri.parse(contentURI);
			// Cursor cursor =
			// getCoreApplication().getContentResolver().query(contentUri, null,
			// null, null, null);
			// if (cursor == null) {
			// return contentUri.getPath();
			// } else {
			// cursor.moveToFirst();
			// LogUtil.getLogger().d(cursor.getColumnCount());
			// int idx =
			// cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
			// return cursor.getString(idx);
			// }

			final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
			// DocumentProvider
			if (isKitKat) {
				final boolean isDocumentUri = DocumentsContract.isDocumentUri(context, uri);
				if (isDocumentUri) {
					// ExternalStorageProvider
					if (isExternalStorageDocument(uri)) {
						final String docId = DocumentsContract.getDocumentId(uri);
						final String[] split = docId.split(":");
						final String type = split[0];

						if ("primary".equalsIgnoreCase(type)) {
							return Environment.getExternalStorageDirectory() + "/" + split[1];
						}

						// TODO handle non-primary volumes
					}
					// DownloadsProvider
					else if (isDownloadsDocument(uri)) {

						final String id = DocumentsContract.getDocumentId(uri);
						final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

						return getDataColumn(context, contentUri, null, null);
					}
					// MediaProvider
					else if (isMediaDocument(uri)) {
						final String docId = DocumentsContract.getDocumentId(uri);
						final String[] split = docId.split(":");
						final String type = split[0];

						Uri contentUri = null;
						if ("image".equals(type)) {
							contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
						} else if ("video".equals(type)) {
							contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
						} else if ("audio".equals(type)) {
							contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
						}

						final String selection = "_id=?";
						final String[] selectionArgs = new String[] { split[1] };

						return getDataColumn(context, contentUri, selection, selectionArgs);
					}
				} else {
					return selectImage(context, uri);
				}
			}
			// MediaStore (and general)
			else if ("content".equalsIgnoreCase(uri.getScheme())) {

				// Return the remote address
				if (isGooglePhotosUri(uri))
					return uri.getLastPathSegment();

				return getDataColumn(context, uri, null, null);
			}
			// File
			else if ("file".equalsIgnoreCase(uri.getScheme())) {
				return uri.getPath();
			}

			return null;

		}

		/**
		 * Get the value of the data column for this Uri. This is useful for
		 * MediaStore Uris, and other file-based ContentProviders.
		 * 
		 * @param context
		 *            The context.
		 * @param uri
		 *            The Uri to query.
		 * @param selection
		 *            (Optional) Filter used in the query.
		 * @param selectionArgs
		 *            (Optional) Selection arguments used in the query.
		 * @return The value of the _data column, which is typically a file
		 *         path.
		 */
		public String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

			Cursor cursor = null;
			final String column = "_data";
			final String[] projection = { column };

			try {
				cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
				if (cursor != null && cursor.moveToFirst()) {
					final int index = cursor.getColumnIndexOrThrow(column);
					return cursor.getString(index);
				}
			} finally {
				if (cursor != null)
					cursor.close();
			}
			return null;
		}

		/**
		 * @param uri
		 *            The Uri to check.
		 * @return Whether the Uri authority is ExternalStorageProvider.
		 */
		public boolean isExternalStorageDocument(Uri uri) {
			return "com.android.externalstorage.documents".equals(uri.getAuthority());
		}

		/**
		 * @param uri
		 *            The Uri to check.
		 * @return Whether the Uri authority is DownloadsProvider.
		 */
		public boolean isDownloadsDocument(Uri uri) {
			return "com.android.providers.downloads.documents".equals(uri.getAuthority());
		}

		/**
		 * @param uri
		 *            The Uri to check.
		 * @return Whether the Uri authority is MediaProvider.
		 */
		public boolean isMediaDocument(Uri uri) {
			return "com.android.providers.media.documents".equals(uri.getAuthority());
		}

		/**
		 * @param uri
		 *            The Uri to check.
		 * @return Whether the Uri authority is Google Photos.
		 */
		public boolean isGooglePhotosUri(Uri uri) {
			return "com.google.android.apps.photos.content".equals(uri.getAuthority());
		}

		public String selectImage(Context context, Uri selectedImage) {
			LogUtil.getLogger().d(selectedImage);
			if (selectedImage != null) {
				String uriStr = selectedImage.toString();
				String path = uriStr.substring(10, uriStr.length());
				if (path.startsWith("com.sec.android.gallery3d")) {
					Log.e(TAG, "It's auto backup pic path:" + selectedImage.toString());
					return null;
				}
			}
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = context.getContentResolver().query(selectedImage, null, null, null, null);
			if (cursor == null) {
				return selectedImage.getPath();
			} else {
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();
				return picturePath;
			}

		}

		public Uri getFilename() {
			File file = BitmapUtils.getImageFile(getCoreApplication());
			return Uri.fromFile(file);

		}

		@Override
		protected void onPostExecute(Uri result) {
			super.onPostExecute(result);
			if (result != null) {
				mWindow.onReturnImageUri(result.toString());
			} else {
				CustomToast.showToast(getCoreApplication(), "请上传图片文件");
			}
		}

	}

	public void decodeBitmap(final Context context, final String strUri, final ImageView imageView) {
		DecodeBitmapAsync task = new DecodeBitmapAsync(context, imageView, new FinishListener() {
			@Override
			public void onFinish(Uri uri, Bitmap bitmap) {
				LogUtil.getLogger().d("loadAsync uri:" + uri + " bitmap:" + bitmap);
				File file = null;
				if (bitmap != null)
					file = BitmapUtils.saveBitmap(context, bitmap);
				mWindow.onReturnBitmap(strUri, imageView, bitmap, file);
			}
		});
		Uri uri = Uri.parse(strUri);
		task.execute(uri);
	}

	public IConfig getConfig() {
		if (mConfig != null)
			return mConfig;
		return getCoreApplication().getPreferenceConfig();
	}

	public void showProgress() {
		mWindow.showProgress();
	}

	public void hideProgress() {
		mWindow.hideProgress();
	}

}
