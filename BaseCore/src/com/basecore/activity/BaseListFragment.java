/**
 * Copyright (c) 2012-2013.
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
package com.basecore.activity;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.ImageView;

import com.basecore.R;
import com.basecore.application.BaseApplication;
import com.basecore.util.log.LogUtil;
import com.basecore.util.netstate.NetWorkUtil.NetType;
import com.basecore.window.IWindow;
import com.basecore.window.WindowWrapper;
import com.nostra13.universalimageloader.core.ImageLoader;

public abstract class BaseListFragment extends ListFragment implements IWindow {

	private WindowWrapper mWindowWrapper;

	protected ImageLoader mImageLoader = ImageLoader.getInstance();

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		onPreOnCreate(savedInstanceState);
		super.onCreate(savedInstanceState);
		initWindow(getActivity());
		onAfterCreate(savedInstanceState);
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private void initWindow(Activity activity) {
		mWindowWrapper = new WindowWrapper(this);
	}

	protected void onPreOnCreate(Bundle savedInstanceState) {
	}

	protected void onAfterCreate(Bundle savedInstanceState) {
	}

	/**
	 * 网络连接连接时调用
	 */
	protected void onConnect(NetType type) {
	}

	/**
	 * 当前没有网络连接
	 */
	protected void onDisConnect() {
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public final BaseApplication getCoreApplication() {
		return mWindowWrapper.getCoreApplication();
	}

	public void doCamera() {
		mWindowWrapper.doCamera(getActivity());
	}

	public void doCamera(boolean isCrop) {
		mWindowWrapper.doCamera(getActivity(), isCrop);
	}
	public void doCamera(int width,int height){
		mWindowWrapper.doCamera(getActivity(), width, height);
	}
	public void doCamera(boolean isCrop,int width,int height){
		mWindowWrapper.doCamera(getActivity(), isCrop, width, height);
	}

	public void doGallery() {
		mWindowWrapper.doGallery(getActivity(), false);
	}

	public void doGallery(boolean isCrop) {
		mWindowWrapper.doGallery(getActivity(), isCrop);
	}
	public void doGallery(int width,int height){
		mWindowWrapper.doGallery(getActivity(), width, height);
	}
	public void doGallery(boolean isCrop,int width,int height){
		mWindowWrapper.doGallery(getActivity(), isCrop, width, height);
	}

	public void startPhotoCrop(String sourceUri, String desUri) {
		mWindowWrapper.startPhotoCrop(getActivity(), sourceUri, desUri);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		mWindowWrapper.onActivityResult(getActivity(), requestCode, resultCode,
				data);
	}

	public void decodeBitmap(final String strUri, final ImageView imageView) {
		mWindowWrapper.decodeBitmap(getActivity(), strUri, imageView);
	}

	@Override
	public void showProgress() {
		LogUtil.getLogger().d("showProgress");
		getCoreApplication().showProgressDialog(getActivity());
	}

	@Override
	public void hideProgress() {
		getCoreApplication().dismissProgressDialog();
	}

	public void doActivity(Class<?> cls) {
		mWindowWrapper.doActivity(getActivity(), cls);
	}

	public void doActivity(Class<?> cls, Bundle bundle) {
		mWindowWrapper.doActivity(getActivity(), cls, bundle);
	}

	@Override
	public void onReturnImageUri(String imageUri) {
	}

	@Override
	public void onReturnBitmap(String strUri, ImageView imageView,
			Bitmap bitmap, File file) {
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		getActivity().overridePendingTransition(R.anim.push_left_in,
				R.anim.push_left_out);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		getActivity().overridePendingTransition(R.anim.push_left_in,
				R.anim.push_left_out);
	}

}
