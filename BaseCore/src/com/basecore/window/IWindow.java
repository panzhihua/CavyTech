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
package com.basecore.window;

import java.io.File;

import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.basecore.application.BaseApplication;

public interface IWindow {

	public BaseApplication getCoreApplication();

	/**
	 * 显示进度条
	 */
	public void showProgress();

	/**
	 * 隐藏进度条
	 */
	public void hideProgress();

	public void onReturnImageUri(String imageUri);

	public void onReturnBitmap(String strUri, ImageView imageView,
			Bitmap bitmap, File file);

	public void startActivity(Intent intent);

	public void startActivityForResult(Intent intent, int requestCode);

}
