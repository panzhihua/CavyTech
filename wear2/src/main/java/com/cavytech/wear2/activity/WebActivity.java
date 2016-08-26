package com.cavytech.wear2.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.basecore.util.log.LogUtil;
import com.basecore.widget.CustomToast;
import com.cavytech.wear2.R;
import com.cavytech.wear2.base.AppCompatActivityEx;
import com.cavytech.wear2.util.Constants;

public class WebActivity extends AppCompatActivityEx {
	private WebView webView;
	private TextView progressText;
	private FrameLayout videoview;// 全屏时视频加载view
	private WebChromeClient.CustomViewCallback customViewCallback;
	private View mCustomView;
	private String webUrl;
	private ImageView back;
	private TextView title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		findView();
		fillView();
        addListener();

		setToolBar();
    }

	private void findView() {

		videoview = (FrameLayout) findViewById(R.id.video_view);
		webView = (WebView) findViewById(R.id.webview);
		back = (ImageView) findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);
		progressText = (TextView) findViewById(R.id.progress_text);
		webView.getSettings().setJavaScriptEnabled(true);
		// webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		// webView.getSettings().setSupportMultipleWindows(true);//
		// 链接中有goto的把这个打开
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setSupportZoom(true);

		webView.getSettings().setBuiltInZoomControls(false);
		webView.getSettings().setRenderPriority(RenderPriority.HIGH);
		webView.getSettings().setBlockNetworkImage(false);
		webView.getSettings().setAppCacheEnabled(true);
		webView.getSettings().setSaveFormData(true);

		// 如果使用 setWebChromeClient 会弹出异常对话框
		//mWebChromeClient = new myWebChromeClient();
		//webView.setWebChromeClient(mWebChromeClient);
	}

	private void fillView() {
		String titleName = getIntent().getStringExtra(Constants.INTENT_EXTRA_TITLE);
		webUrl = getIntent().getStringExtra(Constants.INTENT_EXTRA_WEBURL);
		title.setText(titleName);

		if(null == webUrl){
			return;
		}
		if (webUrl.length() > 0) {
			 webView.loadUrl(webUrl);
		}
	}

	private void addListener() {
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LogUtil.getLogger().d(webView.canGoBack());
				LogUtil.getLogger().d(webView.copyBackForwardList().getSize());
				LogUtil.getLogger().d(webView.copyBackForwardList().getCurrentIndex());
				if (webView.canGoBack() && webView.copyBackForwardList().getCurrentIndex() > 0) {
					webView.goBack();
				} else {
					WebActivity.this.finish();
				}

			}
		});

		webView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				LogUtil.getLogger().d(url);

				if (url.startsWith("tel:")) {
					TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					if (TelephonyManager.PHONE_TYPE_NONE == tm.getPhoneType()) {
						CustomToast.showToast(WebActivity.this, "您使用的设备无电话功能");
					} else {
						Uri uri = Uri.parse(url);
						Intent it = new Intent(Intent.ACTION_DIAL, uri);
						startActivity(it);
					}
					return true;
				} else if (url.startsWith("mailto:")) {
					Uri uri = Uri.parse(url);
					Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
					startActivity(intent);
					return true;
				} else {
					// 此处根据需要是否调用loadurl
					//webView.loadUrl(url);
				}

				return false;
			}

			@Override
			public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
				return super.shouldOverrideKeyEvent(view, event);
			}

			@Override
			public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
				super.doUpdateVisitedHistory(view, url, isReload);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}

			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				handler.proceed();// 接受证书
			}

		});
	}

	class myWebChromeClient extends WebChromeClient {
		private View mVideoProgressView;

		public void onProgressChanged(WebView view, int progress) {
			LogUtil.getLogger().d(progress);
			if (progress == 100) {
				webView.getSettings().setBlockNetworkImage(false);
				progressText.setVisibility(View.GONE);
			} else {
				webView.getSettings().setBlockNetworkImage(true);
				if (progressText.getVisibility() == View.GONE)
					progressText.setVisibility(View.VISIBLE);
				progressText.setText(progress + "%");
			}
			super.onProgressChanged(view, progress);
		}

		@Override
		public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
			onShowCustomView(view, callback); // To change body of overridden
												// methods use File | Settings |
												// File Templates.
		}

		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {

			// if a view already exists then immediately terminate the new one
			if (mCustomView != null) {
				callback.onCustomViewHidden();
				return;
			}
			mCustomView = view;
			webView.setVisibility(View.GONE);
			videoview.setVisibility(View.VISIBLE);
			videoview.addView(view);
			customViewCallback = callback;
		}

		@Override
		public View getVideoLoadingProgressView() {

			if (mVideoProgressView == null) {
				LayoutInflater inflater = LayoutInflater.from(WebActivity.this);
				mVideoProgressView = inflater.inflate(R.layout.activity_webview_loading_progress, null);
			}
			return mVideoProgressView;
		}

		@Override
		public void onHideCustomView() {
			super.onHideCustomView(); // To change body of overridden methods
										// use File | Settings | File Templates.
			if (mCustomView == null)
				return;

			webView.setVisibility(View.VISIBLE);
			videoview.setVisibility(View.GONE);

			// Hide the custom view.
			mCustomView.setVisibility(View.GONE);

			// Remove the custom view from its container.
			videoview.removeView(mCustomView);
			customViewCallback.onCustomViewHidden();

			mCustomView = null;
		}
	}

	private void removeWebViewCookie() {
		CookieSyncManager.createInstance(WebActivity.this);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.setAcceptCookie(true);
		cookieManager.removeSessionCookie();// 移除
		cookieManager.removeAllCookie();
		CookieSyncManager.getInstance().sync();

	}

	@Override
	public void onPause() {// 继承自Activity
		super.onPause();
		LogUtil.getLogger().d("onPause");
		webView.onPause();
	}

	@Override
	public void onResume() {// 继承自Activity
		super.onResume();
		LogUtil.getLogger().d("onResume");
		webView.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
		LogUtil.getLogger().d("onStop");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.getLogger().d("onDestroy");
		webView.clearCache(true);
		webView.clearHistory();
		removeWebViewCookie();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BREAK) {
			finish();
		}

		return super.onKeyDown(keyCode, event);
	}


}
