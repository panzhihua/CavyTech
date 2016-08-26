/*
 * Copyright (C) 2013
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
package com.basecore.application;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.DisplayMetrics;

import com.basecore.R;
import com.basecore.util.cache.FileCache;
import com.basecore.util.config.IConfig;
import com.basecore.util.config.PreferenceConfig;
import com.basecore.util.config.PropertiesConfig;
import com.basecore.util.log.LogUtil;
import com.basecore.util.netstate.NetChangeObserver;
import com.basecore.util.netstate.NetWorkUtil.NetType;
import com.basecore.widget.CustomProgressDialog;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public abstract class BaseApplication extends Application {

	/** 配置器 为Preference */
	public final static int PREFERENCECONFIG = 0;
	/** 配置器 为PROPERTIESCONFIG */
	public final static int PROPERTIESCONFIG = 1;

	/** 配置器 */
	private IConfig mCurrentConfig;
	/** App异常崩溃处理器 */
	private static BaseApplication application;

	/** 文件缓存 */
	private FileCache mFileCache;
	/** 应用程序运行Activity管理器 */
	private AppManager mAppManager;
	private Boolean networkAvailable = false;
	private NetChangeObserver taNetChangeObserver;
	private int[] screenSize;
	private CustomProgressDialog mProgressDialog;
	@Override
	public void onCreate() {
		onPreCreateApplication();
		super.onCreate();
		doOncreate();
		getAppManager();
		onAfterCreateApplication();
	}

	private void doOncreate() {
		application = this;
		// 注册activity启动控制控制器
		initScreenSize();
		initImageLoader();

	}

	public int[] getScreenSize() {
		return this.screenSize;
	}

	private void initScreenSize() {
		final DisplayMetrics metrics = getResources().getDisplayMetrics();
		screenSize = new int[] { metrics.widthPixels, metrics.heightPixels };
	}

	private void initImageLoader() {
		final int[] size = screenSize;
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCacheSize(2 * 1024 * 1024).threadPoolSize(3)
				.discCacheSize(60 * 1024 * 1024)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.memoryCacheExtraOptions(size[0], size[1]).build();
		ImageLoader.getInstance().init(config);
	}
	

	/**
	 * 当前没有网络连接
	 */
	public void onDisConnect() {
		LogUtil.getLogger().d("--onDisConnect--");
		networkAvailable = false;

	}

	/**
	 * 网络连接连接时调用
	 */
	protected void onConnect(NetType type) {
		LogUtil.getLogger().d( "--onConnect--");
		networkAvailable = true;

	}

	/**
	 * 获取Application
	 * 
	 * @return
	 */
	public static BaseApplication getApplication() {
		return application;
	}

	protected void onAfterCreateApplication() {
		// TODO Auto-generated method stub

	}

	protected void onPreCreateApplication() {
		// TODO Auto-generated method stub

	}

	public IConfig getPreferenceConfig() {
		return getConfig(PREFERENCECONFIG);
	}

	public IConfig getPropertiesConfig() {
		return getConfig(PROPERTIESCONFIG);
	}

	public IConfig getConfig(int confingType) {
		if (confingType == PREFERENCECONFIG) {
			mCurrentConfig = PreferenceConfig.getPreferenceConfig(this);

		} else if (confingType == PROPERTIESCONFIG) {
			mCurrentConfig = PropertiesConfig.getPropertiesConfig(this);
		} else {
			mCurrentConfig = PropertiesConfig.getPropertiesConfig(this);
		}
		if (!mCurrentConfig.isLoadConfig()) {
			mCurrentConfig.loadConfig();
		}
		return mCurrentConfig;
	}

	public IConfig getCurrentConfig() {
		if (mCurrentConfig == null) {
			getPreferenceConfig();
		}
		return mCurrentConfig;
	}


	
	public AppManager getAppManager() {
		if (mAppManager == null) {
			mAppManager = AppManager.getAppManager();
		}
		return mAppManager;
	}

	/**
	 * 退出应用程序
	 * 
	 * @param isBackground
	 *            是否开开启后台运行,如果为true则为后台运行
	 */
	public void exitApp(Boolean isBackground) {
		mAppManager.AppExit(this, isBackground);
	}

	/**
	 * 获取当前网络状态，true为网络连接成功，否则网络连接失败
	 * 
	 * @return
	 */
	public Boolean isNetworkAvailable() {
		return networkAvailable;
	}
	public ProgressDialog showProgressDialog(Context context) {
		LogUtil.getLogger().d("showProgressDialog");
		if (mProgressDialog == null) {
			CustomProgressDialog dialog = new CustomProgressDialog(context, R.style.dialog);
			dialog.setMessage("loading_data_please_wait");
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			mProgressDialog = dialog;
		}
		try {
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
			mProgressDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mProgressDialog;
	}

	public ProgressDialog showProgressDialog(Context context, Boolean cancel) {
		if (mProgressDialog == null) {
			CustomProgressDialog dialog = new CustomProgressDialog(context, R.style.dialog);
			dialog.setMessage("loading_data_please_wait");
			dialog.setIndeterminate(true);
			dialog.setCancelable(cancel);
			mProgressDialog = dialog;
		}
		try {
			mProgressDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mProgressDialog;
	}

	public ProgressDialog showProgressDialog(Context context, String str) {
		if (mProgressDialog == null) {
			CustomProgressDialog dialog = new CustomProgressDialog(context, R.style.dialog);
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			mProgressDialog = dialog;
		}
		try {
			mProgressDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mProgressDialog;
	}

	public void dismissProgressDialog() {
		try {
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
			}
			mProgressDialog = null;
		} catch (Exception e) {
			// We don't mind. android cleared it for us.
			mProgressDialog = null;
		}
	}

}
