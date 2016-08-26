package com.cavytech.wear2.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.basecore.util.core.MobileFormat;
import com.basecore.util.core.StringUtils;
import com.basecore.widget.CustomToast;
import com.basecore.widget.SystemBarTintManager;
import com.cavytech.wear2.R;
import com.cavytech.wear2.application.CommonApplication;
import com.cavytech.wear2.entity.LoginEntity;
import com.cavytech.wear2.entity.UserEntity;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.RequestCallback;
import com.cavytech.wear2.util.CacheUtils;
import com.cavytech.wear2.util.Constants;
import com.cavytech.wear2.util.PhoneUtils;
import com.cavytech.wear2.util.SerializeUtils;
import com.cavytech.widget.ClearEditText;
import com.squareup.okhttp.Request;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends CommonActivity {

    private ClearEditText inputAccount;
    private ClearEditText inputPassword;
    private TextView loginAccount;
    private TextView forgetPassword;

    private static String INTEN_ACOUNT = "inputAccount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.top_bg_color);//通知栏所需颜色
        }
        CommonApplication.isLogin = true;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitleText(getString(R.string.login_title));
        setRightTitleText(getString(R.string.register));
        findView();
        onListener();
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public static Intent getIntent(Context context, String account){

        Intent intent = new Intent(context, LoginActivity.class);

        intent.putExtra(INTEN_ACOUNT, account);

        return intent;
    }

    private void findView() {

        inputAccount = (ClearEditText) findViewById(R.id.input_account);
        inputPassword = (ClearEditText) findViewById(R.id.input_password);
        loginAccount = (TextView) findViewById(R.id.login_account);
        forgetPassword = (TextView) findViewById(R.id.forget_password);

        String account = getIntent().getStringExtra(INTEN_ACOUNT);

        if(null != account){
            inputAccount.setText(account);
            inputPassword.requestFocus();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(LoginActivity.this,GuideActivity.class));
        finish();
    }

    private void onListener() {

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,GuideActivity.class));
                finish();
            }
        });



        loginAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onLogin();
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onForgetPassword();
            }
        });

        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onRightText();
            }
        });
    }

    private void onLogin(){

        String tel = inputAccount.getText().toString().replace(" ", "");
        String password = inputPassword.getText().toString().replace(" ", "");

        if (TextUtils.isEmpty(tel)) {
            CustomToast.showToast(LoginActivity.this, R.string.please_input_account);
            return;
        }
        if (!(new MobileFormat(tel.trim()).isLawful() || StringUtils.isEmail(tel))) {
            CustomToast.showToast(LoginActivity.this, R.string.account_error);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            CustomToast.showToast(LoginActivity.this, R.string.please_input_correct_password);
            return;
        } else {
            if (password.length() < 6) {
                CustomToast.showToast(LoginActivity.this, R.string.password_length);
                return;
            }
        }

        login(tel, password);
    }

    private void onForgetPassword(){
        Intent intent = new Intent(LoginActivity.this, ForgetPwdActivity.class);
        startActivity(intent);
    }

    private void getUserInfo(){

        HttpUtils.getInstance().getUserInfo(LoginActivity.this,new RequestCallback<UserEntity>() {
                    @Override
                    public void onError(Request request, Exception e) {

                        try {
                            JSONObject jsonObj = new JSONObject(e.getLocalizedMessage());
                            int code = jsonObj.optInt("code");
                            if(HttpUtils.CODE_ACCOUNT_NOT_LOGIN==code){
                                AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
                                dialog.setCancelable(false);
                                dialog.setMessage(R.string.not_login);
                                dialog.setPositiveButton(getString(R.string.ALERT_DLG_BTN_OK), new android.content.DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        arg0.dismiss();
                                        startActivity(new Intent(LoginActivity.this,LoginActivity.class));
                                        finish();
                                    }
                                });
                                dialog.setNeutralButton(getString(R.string.ALERT_DLG_BTN_CANCEL), new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        arg0.dismiss();
                                    }
                                } );
                                dialog.show();
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                    }

                    @Override
                    public void onResponse(UserEntity response) {

                        UserEntity.ProfileEntity profile = response.getProfile();

                        SerializeUtils.serialize(profile, Constants.SERIALIZE_USERINFO);

                        sendUpdate();

                        /**
                         * 如果没有绑定 则进行macAddress进行绑定
                         * 绑定过,判断个人信息是否完整
                         */

                        if(CacheUtils.getMacAdress(LoginActivity.this) == null){
                            Intent it = new Intent(LoginActivity.this, BandConnectActivity.class);
                            startActivity(it);
                        }else {
                            if(CacheUtils.getMacAdress(LoginActivity.this).equals("")){
                                Intent it = new Intent(LoginActivity.this, BandConnectActivity.class);
                                startActivity(it);
                            }else if(profile.getWeight() == 0 && profile.getHeight() == 0){
                                Intent it = new Intent(LoginActivity.this, SexActivity.class);
                                startActivity(it);
                            }else {
                                Intent intent = new Intent(LoginActivity.this, HomePager.class);
                                startActivity(intent);
                            }
                        }

                        finish();
                    }
                });
    }

    private void sendUpdate(){
        Intent intent = new Intent(Constants.UPDATE_USERINFO_RECEIVER);
        sendBroadcast(intent);
    }

    private void login(final String account, final String password) {

        showProgress();

        Log.e("TAG","装置类型----"+ Build.VERSION.SDK_INT);

        HashMap<String,String> map = new HashMap<String,String>();
        map.put("device_serial", PhoneUtils.getUDID(LoginActivity.this));
        map.put("device_model",android.os.Build.MODEL+"-"+Build.VERSION.SDK_INT);
        map.put("band_mac",CacheUtils.getMacAdress(LoginActivity.this));
        map.put("user_id",CommonApplication.userID);
        map.put("longitude",CacheUtils.getString(LoginActivity.this, Constants.LONGITUDE));
        map.put("latitude",CacheUtils.getString(LoginActivity.this, Constants.LATITUDE));
        MobclickAgent.onEvent(LoginActivity.this, Constants.USER_LOGIN,map);

        HttpUtils.getInstance().login(CacheUtils.getMacAdress(LoginActivity.this),""+Build.VERSION.SDK_INT,CacheUtils.getString(this, Constants.LONGITUDE), CacheUtils.getString(this, Constants.LATITUDE),LoginActivity.this,account, password, new RequestCallback<LoginEntity>() {
             @Override
             public void onError(Request request, Exception e) {
                 CustomToast.showToast(LoginActivity.this, e.toString());
                 hideProgress();

                 try {
                     JSONObject jsonObj = new JSONObject(e.getLocalizedMessage());
                     int code = jsonObj.optInt("code");
                     int user_id = jsonObj.optInt("user_id");
                     CommonApplication.userID = user_id+"";

                     if(HttpUtils.CODE_ACCOUNT_NOT_EXIST==code){
                         AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
                         dialog.setCancelable(false);
                         dialog.setMessage("该用户不存在，是否前去注册?");
                         dialog.setPositiveButton(getString(R.string.ALERT_DLG_BTN_OK), new android.content.DialogInterface.OnClickListener() {

                             @Override
                             public void onClick(DialogInterface arg0, int arg1) {
                                 arg0.dismiss();
                                 startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                                 finish();
                             }
                         });
                         dialog.setNeutralButton(getString(R.string.ALERT_DLG_BTN_CANCEL), new android.content.DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface arg0, int arg1) {
                                 arg0.dismiss();
                             }
                         } );
                         dialog.show();
                     }else if(HttpUtils.PASSWORD_ERROR==code){
                         CustomToast.showToast(LoginActivity.this, R.string.password_error);
                         return;
                     }
                 } catch (JSONException e1) {
                     e1.printStackTrace();
                 }
             }

             @Override
             public void onResponse(LoginEntity response) {
                 hideProgress();
                 Log.e("TAG", "-=-=-=-=-="+response.toString());

                 if (1000 == HttpUtils.newSUCCESS) {
                     CommonApplication.isLogin = true;
                     CacheUtils.putString(LoginActivity.this, Constants.USERNAME, account);
                     CacheUtils.putString(LoginActivity.this, Constants.PASSWORD, password);
                     CacheUtils.putString(LoginActivity.this, Constants.USERID, response.getUser_id());
                     CacheUtils.putString(LoginActivity.this,Constants.TOKEN,response.getAuth_token());
                     getUserInfo();
                 } else {
                     HttpUtils.getInstance().showToast(LoginActivity.this, response);
                 }
             }
         });
    }
    @Override
    public void onRightText(){
        Intent it = new Intent(LoginActivity.this, RegisterActivity.class);

        startActivity(it);

        finish();
    }
}
