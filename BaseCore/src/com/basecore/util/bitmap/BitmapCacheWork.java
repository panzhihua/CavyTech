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
package com.basecore.util.bitmap;

import android.content.Context;
import android.content.res.Resources;
import android.widget.ImageView;

import com.basecore.util.cache.FileCache;
import com.basecore.util.cache.FileCache.TACacheParams;
import com.basecore.util.cache.FileCacheWork;

public class BitmapCacheWork extends FileCacheWork<ImageView>
{

	protected Resources mResources;
	private TACacheParams mCacheParams;
	private static final String TAG = "TABitmapCacheWork";
	private Context mContext;

	public BitmapCacheWork(Context context)
	{
		mResources = context.getResources();
		this.mContext = context;
	}

	@Override
	public void loadFormCache(Object data, ImageView responseObject)
	{
		// TODO Auto-generated method stub
		if (getCallBackHandler() == null)
		{
			BitmapCallBackHanlder callBackHanlder = new BitmapCallBackHanlder();
			setCallBackHandler(callBackHanlder);
		}
		if (getProcessDataHandler() == null)
		{
			DownloadBitmapHandler downloadBitmapFetcher = new DownloadBitmapHandler(
					mContext, 100);
			setProcessDataHandler(downloadBitmapFetcher);
		}
		super.loadFormCache(data, responseObject);
	}

	/**
	 * 设置图片缓存
	 * 
	 * @param cacheParams
	 *            响应参数
	 */
	public void setBitmapCache(TACacheParams cacheParams)
	{
		mCacheParams = cacheParams;
		setFileCache(new FileCache(cacheParams));
	}

	@Override
	protected void initDiskCacheInternal()
	{
		// TODO Auto-generated method stub
		DownloadBitmapHandler downloadBitmapFetcher = (DownloadBitmapHandler) getProcessDataHandler();
		super.initDiskCacheInternal();
		if (downloadBitmapFetcher != null)
		{
			downloadBitmapFetcher.initDiskCacheInternal();
		}
	}

	protected void clearCacheInternal()
	{
		super.clearCacheInternal();
		DownloadBitmapHandler downloadBitmapFetcher = (DownloadBitmapHandler) getProcessDataHandler();
		if (downloadBitmapFetcher != null)
		{
			downloadBitmapFetcher.clearCacheInternal();
		}
	}

	protected void flushCacheInternal()
	{
		super.flushCacheInternal();
		DownloadBitmapHandler downloadBitmapFetcher = (DownloadBitmapHandler) getProcessDataHandler();
		if (downloadBitmapFetcher != null)
		{
			downloadBitmapFetcher.flushCacheInternal();
		}
	}

	protected void closeCacheInternal()
	{
		super.closeCacheInternal();
		DownloadBitmapHandler downloadBitmapFetcher = (DownloadBitmapHandler) getProcessDataHandler();
		if (downloadBitmapFetcher != null)
		{
			downloadBitmapFetcher.closeCacheInternal();
		}
	}
}
