/**
 * Copyright (c) 2012-2013.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.basecore.activity;

import java.io.File;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.basecore.R;
import com.basecore.application.BaseApplication;
import com.basecore.util.log.LogUtil;
import com.basecore.util.netstate.NetWorkUtil.NetType;
import com.basecore.window.IWindow;
import com.basecore.window.WindowWrapper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseFragmentActivity extends FragmentActivity implements IWindow {

    private WindowWrapper mWindowWrapper;

    protected ImageLoader mImageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        onPreOnCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        initActivity();
        onAfterCreate(savedInstanceState);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.setAutoLocation(false);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void initActivity() {
        initWindow();
        getCoreApplication().getAppManager().addActivity(this);
    }

    private void initWindow() {
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
        getCoreApplication().getAppManager().removeActivity(this);
    }

    @Override
    public final BaseApplication getCoreApplication() {
        return mWindowWrapper.getCoreApplication();
    }

    public void doCamera() {
        mWindowWrapper.doCamera(this);
    }

    public void doCamera(boolean isCrop) {
        mWindowWrapper.doCamera(this, isCrop);
    }

    public void doCamera(int width, int height) {
        mWindowWrapper.doCamera(this, width, height);
    }

    public void doCamera(boolean isCrop, int width, int height) {
        mWindowWrapper.doCamera(this, isCrop, width, height);
    }

    public void doGallery() {
        mWindowWrapper.doGallery(this, false);
    }

    public void doGallery(boolean isCrop) {
        mWindowWrapper.doGallery(this, isCrop);
    }

    public void doGallery(int width, int height) {
        mWindowWrapper.doGallery(this, width, height);
    }

    public void doGallery(boolean isCrop, int width, int height) {
        mWindowWrapper.doGallery(this, isCrop, width, height);
    }

    public void startPhotoCrop(String sourceUri, String desUri) {
        mWindowWrapper.startPhotoCrop(this, sourceUri, desUri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mWindowWrapper.onActivityResult(this, requestCode, resultCode, data);
    }

    public void decodeBitmap(final String strUri, final ImageView imageView) {
        mWindowWrapper.decodeBitmap(this, strUri, imageView);
    }

    @Override
    public void showProgress() {
        getCoreApplication().showProgressDialog(this);
    }

    @Override
    public void hideProgress() {
        getCoreApplication().dismissProgressDialog();
    }

    public void doActivity(Class<?> cls) {
        mWindowWrapper.doActivity(this, cls);
    }

    public void doActivity(Class<?> cls, Bundle bundle) {
        mWindowWrapper.doActivity(this, cls, bundle);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    public void defaultFinish() {
        super.finish();
    }

    @Override
    public void onReturnImageUri(String imageUri) {
    }

    @Override
    public void onReturnBitmap(String strUri, ImageView imageView, Bitmap bitmap, File file) {
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        boolean showAnimation = intent.getBooleanExtra("showAnimation", true);
        LogUtil.getLogger().d(showAnimation);
        if (showAnimation) {
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        } else {
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

}
