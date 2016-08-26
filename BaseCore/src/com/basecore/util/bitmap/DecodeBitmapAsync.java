package com.basecore.util.bitmap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

import com.basecore.R;
import com.basecore.application.BaseApplication;
import com.basecore.widget.CustomToast;

public class DecodeBitmapAsync extends AsyncTask<Uri, Void, Bitmap> implements
		OnCancelListener {

	private static final String TAG = "DecodeBitmapAsync";

	private Context mContext;

	private ImageView mImageView;

	/** 含有进度条的对话框 */
	private ProgressDialog mProgress;

	/** 图片的路徑 */
	private Uri mUri;

	private FinishListener mListener;

	public DecodeBitmapAsync(Context context, ImageView imageView,
			FinishListener listener) {
		mContext = context;
		mImageView = imageView;
		mListener = listener;
	}

	/*
	 * (non-Javadoc)加载之前的回调方法
	 * 
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgress = new ProgressDialog(mContext);
		mProgress.setIndeterminate(true);
		mProgress.setCancelable(true);
		mProgress.setMessage(mContext.getString(R.string.picture_loading));
		mProgress.setOnCancelListener(this);
		mProgress.show();
	}

	/*
	 * (non-Javadoc)后台执行的方法
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Bitmap doInBackground(Uri... params) {
		mUri = params[0];
		int[] size = BaseApplication.getApplication().getScreenSize();
		Bitmap bitmap = BitmapUtils.decode(mContext, mUri, size[0], size[1]);
		return bitmap;
	}

	/*
	 * (non-Javadoc)加载之后的回调方法
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);
		if (mProgress.getWindow() != null) {
			mProgress.dismiss();
		}
		if (result != null) {
			setImageURI(result);
		} else {
			CustomToast.showToast(mContext,
					mContext.getString(R.string.picture_loaded_failed),
					Gravity.BOTTOM, Toast.LENGTH_SHORT);
		}
		if (mListener != null) {
			mListener.onFinish(mUri, result);
		}
	}

	/*
	 * (non-Javadoc)取消加载的回调方法
	 * 
	 * @see
	 * android.content.DialogInterface.OnCancelListener#onCancel(android.content
	 * .DialogInterface)
	 */
	@Override
	public void onCancel(DialogInterface dialog) {
		cancel(true);
	}

	/*
	 * (non-Javadoc)加载取消之后的回调方法
	 * 
	 * @see android.os.AsyncTask#onCancelled()
	 */
	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	public interface FinishListener {
		public void onFinish(Uri uri, Bitmap bitmap);
	}

	private void setImageURI(final Bitmap bitmap) {
		if (mImageView != null) {
			mImageView.setImageBitmap(bitmap);
		}
	}

}