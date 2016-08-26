package com.cavytech.wear2.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.basecore.util.core.MobileFormat;
import com.basecore.util.core.StringUtils;
import com.basecore.widget.CustomToast;
import com.cavytech.wear2.R;
import com.cavytech.wear2.application.CommonApplication;
import com.cavytech.wear2.entity.CommonEntity;
import com.cavytech.wear2.entity.LoginEntity;
import com.cavytech.wear2.entity.RegForgPwdBean;
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

/**
 * 作者：yzb on 2015/7/14 19:22
 */
public class RegForgetPwdBaseActivity extends CommonActivity {
    protected ClearEditText inputAccount;
    protected EditText inputVerification;
    protected TextView sendVerification;
    protected ClearEditText inputPassword;
    protected TextView registerAccount;
    protected CheckBox treaty;
    protected boolean isEmailUI;
    protected ImageView imageVerification;
    protected TimeCount time;
    protected TextView agreeContext;

    protected boolean isForgetPwdView = false;
    private String account;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findView();
        fillView();
        onListener();
    }

    private void findView() {
        findTitle();
        inputAccount = (ClearEditText) findViewById(R.id.input_account);
        inputVerification = (EditText) findViewById(R.id.input_verification);
        sendVerification = (TextView) findViewById(R.id.send_verification);
        inputPassword = (ClearEditText) findViewById(R.id.input_password);
        registerAccount = (TextView) findViewById(R.id.regter_account);
        treaty = (CheckBox) findViewById(R.id.box_agree);
        agreeContext = (TextView) findViewById(R.id.agree_context);
        imageVerification = (ImageView) findViewById(R.id.image_verification);
        inputPassword.setTypeface(Typeface.DEFAULT);
        inputPassword.setTransformationMethod(new PasswordTransformationMethod());
    }

    private void onListener() {

        /**
         * 获取验证码
         */
        sendVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEmailUI){
                    account = inputAccount.getText().toString().replace(" ", "");
                    String code = inputVerification.getText().toString().replace(" ", "");
                    String password = inputPassword.getText().toString().replace(" ", "");

                    if (TextUtils.isEmpty(account)) {
                        CustomToast.showToast(RegForgetPwdBaseActivity.this, getString(R.string.input_email));
                        return;
                    }

                    if (!StringUtils.isEmail(account)) {
                        CustomToast.showToast(RegForgetPwdBaseActivity.this, getString(R.string.email_fail));
                        return;
                    }

                    getVerCode();
                    time.start();

                }else {
                    if (inputAccount.getText().toString().length() > 0) {
                        if (new MobileFormat(inputAccount.getText().toString().trim()).isLawful()) {
                            getVerCode();
                            time.start();
                        } else {
                            CustomToast.showToast(RegForgetPwdBaseActivity.this, getString(R.string.input_tel_flase));
                        }
                    } else {
                        CustomToast.showToast(RegForgetPwdBaseActivity.this, getString(R.string.input_tel_none));
                    }
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegForgetPwdBaseActivity.this,GuideActivity.class));
                finish();
            }
        });


        /**
         * --------立即注册----------
         */
        registerAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String,String> map = new HashMap<String,String>();
                map.put("device_serial", PhoneUtils.getUDID(RegForgetPwdBaseActivity.this));
                map.put("device_model",android.os.Build.MODEL+"-"+Build.VERSION.SDK_INT);
                map.put("band_mac",CacheUtils.getMacAdress(RegForgetPwdBaseActivity.this));
                map.put("user_id",CommonApplication.userID);
                map.put("longitude",CacheUtils.getString(RegForgetPwdBaseActivity.this, Constants.LONGITUDE));
                map.put("latitude",CacheUtils.getString(RegForgetPwdBaseActivity.this, Constants.LATITUDE));
                MobclickAgent.onEvent(RegForgetPwdBaseActivity.this, Constants.USER_SIGNUP,map);

                account = inputAccount.getText().toString().replace(" ", "");
                String code = inputVerification.getText().toString().replace(" ", "");
                String password = inputPassword.getText().toString().replace(" ", "");

                if (isEmailUI) {
                    if (TextUtils.isEmpty(account)) {
                        CustomToast.showToast(RegForgetPwdBaseActivity.this, getString(R.string.input_email));
                        return;
                    }
                    if (!StringUtils.isEmail(account)) {
                        CustomToast.showToast(RegForgetPwdBaseActivity.this, getString(R.string.email_fail));
                        return;
                    }
                } else {
                    if (TextUtils.isEmpty(account)) {
                        CustomToast.showToast(RegForgetPwdBaseActivity.this, getString(R.string.input_tel_none));
                        return;
                    }
                    if (!new MobileFormat(account.trim()).isLawful()) {
                        CustomToast.showToast(RegForgetPwdBaseActivity.this, getString(R.string.input_tel_none));
                        return;
                    }

                }
                if (TextUtils.isEmpty(code)) {
                    CustomToast.showToast(RegForgetPwdBaseActivity.this, getString(R.string.please_input_correct_code));
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    CustomToast.showToast(RegForgetPwdBaseActivity.this, R.string.please_input_password);
                    return;
                } else {
                    if (password.length() < 6) {
                        CustomToast.showToast(RegForgetPwdBaseActivity.this, R.string.password_length);
                        return;
                    }
                }
                if (treaty.isChecked()) {
                    register(account, code, password);
                } else {
                    CustomToast.showToast(RegForgetPwdBaseActivity.this, R.string.please_read_treaty);
                    return;
                }
            }
        });
        agreeContext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickText();
            }
        });

        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmailUI) {
                    rightText.setText(getString(R.string.email_box));
                    sendVerification.setVisibility(View.VISIBLE);
                    imageVerification.setVisibility(View.GONE);
                    inputAccount.setInputType(InputType.TYPE_CLASS_NUMBER);
                    inputVerification.setInputType(InputType.TYPE_CLASS_NUMBER);
                    time.cancel();
                    time.onFinish();
                    inputAccount.setHint(getString(R.string.input_tel));
                    isEmailUI = false;
                } else {//邮箱注册
                    rightText.setText(getString(R.string.tel));
//                    inputAccount.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
//                    inputVerification.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
//                    sendVerification.setVisibility(View.GONE);
//                    imageVerification.setVisibility(View.VISIBLE);
                    sendVerification.setVisibility(View.VISIBLE);
                    imageVerification.setVisibility(View.GONE);
                    inputAccount.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    inputVerification.setInputType(InputType.TYPE_CLASS_NUMBER);
                    time.cancel();
                    time.onFinish();

                    inputAccount.setHint(getString(R.string.input_email));
                    isEmailUI = true;
                }
                getImageVerification();
                clearInput();
            }
        });
        imageVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageVerification();
            }
        });
    }

    protected void onClickText(){

    }
    private void fillView() {
        title.setText(getString(R.string.register));
        rightText.setVisibility(View.VISIBLE);
        rightText.setText(getString(R.string.email_box));
        time = new TimeCount(60000, 1000);
    }

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            sendVerification.setClickable(false);
            sendVerification.setText(millisUntilFinished / 1000 + getString(R.string.seconds));
        }

        @Override
        public void onFinish() {
            sendVerification.setText(getString(R.string.send_verification));
            sendVerification.setClickable(true);
        }
    }

    /**
     * 注册
     * @param account
     * @param code
     * @param password
     */
    private void register(final String account, final String code, final String password) {
        showProgress();

        HttpUtils.getInstance().regForget(RegForgetPwdBaseActivity.this,isEmailUI,account, password, code, isForgetPwdView, new RequestCallback<RegForgPwdBean>() {
            @Override
            public void onError(Request request, Exception e) {
                try {
                    JSONObject jsonObj = new JSONObject(e.getLocalizedMessage());
                    int code = jsonObj.optInt("code");
                    if(HttpUtils.VERIFICATION_CODE_NO_CORRECT==code){
                        CustomToast.showToast(RegForgetPwdBaseActivity.this, R.string.verification_error);
                        hideProgress();
                    }else {
                        hideProgress();
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void onResponse(RegForgPwdBean response) {
                Log.e("TAG","手机号注册");
                hideProgress();
                if (response != null) {
                    if (response.getCode() == HttpUtils.newSUCCESS) {
                        if(isForgetPwdView){
                            startActivity(LoginActivity.getIntent(RegForgetPwdBaseActivity.this, account));
                        }else {
                            login(account, password);
                        }
                    } else if(HttpUtils.CODE_ACCOUNT_IS_EXIST==response.getCode()){
                            AlertDialog.Builder dialog = new AlertDialog.Builder(RegForgetPwdBaseActivity.this);
                            dialog.setCancelable(false);
                            dialog.setMessage(getString(R.string.MSG_ACCOUNT_IS_EXIST));
                            dialog.setPositiveButton(getString(R.string.ALERT_DLG_BTN_OK), new android.content.DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    startActivity(LoginActivity.getIntent(RegForgetPwdBaseActivity.this, account));
                                    finish();
                                }
                            });
                            dialog.setNeutralButton(getString(R.string.ALERT_DLG_BTN_CANCEL), new android.content.DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    arg0.dismiss();

                                    inputAccount.requestFocus();
                                    clearInput();
                                }
                            } );
                            dialog.show();

                        }
                }
            }
        });
    }

    private void clearInput(){
        inputAccount.setText("");
        inputVerification.setText("");
        inputPassword.setText("");

        time.cancel();
        time.onFinish();
    }

    private void setUserInfo(){
        UserEntity.ProfileEntity userInfo = (UserEntity.ProfileEntity) SerializeUtils.unserialize(Constants.SERIALIZE_USERINFO);

        HttpUtils.getInstance().setUserInfo(userInfo, RegForgetPwdBaseActivity.this ,new RequestCallback<CommonEntity>() {
                    @Override
                    public void onError(Request request, Exception e) {

                    }

                    @Override
                    public void onResponse(CommonEntity response) {
                        if (response.getCode() == HttpUtils.newSUCCESS) {
                            Intent intent = new Intent(RegForgetPwdBaseActivity.this, HomePager.class);
                            startActivity(intent);
                            finish();
                        }
//                        else {
//                            HttpUtils.getInstance().showToast(RegForgetPwdBaseActivity.this, response);
//                         }
                    }
                });
    }
    private void getVerCode() {
        HttpUtils.getInstance().getVerCode(isEmailUI,inputAccount.getText().toString(), isForgetPwdView,new RequestCallback<CommonEntity>() {
            public int code;
            @Override
            public void onError(Request request, Exception e) {
                Log.e("TAG","错误信息---"+e.getLocalizedMessage()+"---"+e.getMessage());
                try {
                    JSONObject jsonObj = new JSONObject(e.getLocalizedMessage());
                    code = jsonObj.optInt("code");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                if(HttpUtils.CODE_ACCOUNT_IS_EXIST==code){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(RegForgetPwdBaseActivity.this);
                    dialog.setCancelable(false);
                    dialog.setMessage(getString(R.string.MSG_ACCOUNT_IS_EXIST));
                    dialog.setPositiveButton(getString(R.string.ALERT_DLG_BTN_OK), new android.content.DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.dismiss();
                            startActivity(LoginActivity.getIntent(RegForgetPwdBaseActivity.this, account));
                            finish();
                        }
                    });
                    dialog.setNeutralButton(getString(R.string.ALERT_DLG_BTN_CANCEL), new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.dismiss();

                            inputAccount.requestFocus();
                            clearInput();
                        }
                    } );
                    dialog.show();
                }
            }

            @Override
            public void onResponse(CommonEntity response) {
                Log.e("TAG","获取验证----"+response.getMsg()+"----"+response.getCode());
                    if (response.getCode() == HttpUtils.newSUCCESS) {
                        CustomToast.showToast(RegForgetPwdBaseActivity.this, response.getMsg());
                        if(isEmailUI){
                            CustomToast.showToast(RegForgetPwdBaseActivity.this, "验证码已经发送至您的邮箱");
                        }
                    }

            }
        });
    }

    private void login(final String account, final String password) {

        showProgress();

        HttpUtils.getInstance().login(CacheUtils.getMacAdress(RegForgetPwdBaseActivity.this),""+ Build.VERSION.SDK_INT,CacheUtils.getString(this, Constants.LONGITUDE), CacheUtils.getString(this, Constants.LATITUDE),RegForgetPwdBaseActivity.this,account, password, new RequestCallback<LoginEntity>() {
            @Override
            public void onError(Request request, Exception e) {
                CustomToast.showToast(RegForgetPwdBaseActivity.this, e.toString());

                hideProgress();
            }

            @Override
            public void onResponse(LoginEntity response) {
                Log.e("TAG","登录---");
                hideProgress();
                if (response.getCode() == HttpUtils.newSUCCESS) {
                    CommonApplication.isLogin = true;
                    CacheUtils.putString(RegForgetPwdBaseActivity.this, Constants.USERNAME, account);
                    CacheUtils.putString(RegForgetPwdBaseActivity.this, Constants.PASSWORD, password);
                    CacheUtils.putString(RegForgetPwdBaseActivity.this, Constants.USERID, response.getUser_id());
                    CacheUtils.putString(RegForgetPwdBaseActivity.this,"token",response.getAuth_token());

                    getUserInfo();

                    /**
                     * 更改
                     */
                   /* String strNickName = getString(R.string.CAVY_VIP) + "-";

                    String strNickName = getString(R.string.CAVY_VIP) + "-";

                    UserEntity.ProfileEntity userInfo = (UserEntity.ProfileEntity) SerializeUtils.unserialize(Constants.SERIALIZE_USERINFO);
                    userInfo.setNickname(strNickName);
                    SerializeUtils.serialize(userInfo, Constants.SERIALIZE_USERINFO);
                    BandInfoEntity bandInfo = (BandInfoEntity) SerializeUtils.unserialize(Constants.SERIALIZE_BAND_INFO);
                    LifeBandBLEUtil.getInstance().saveMacAdress(bandInfo.getAddress());

                    setUserInfo();*/


                } else {
                    CustomToast.showToast(RegForgetPwdBaseActivity.this, response.getMsg());
                }
            }
        });
    }

    private void getUserInfo(){

        HttpUtils.getInstance().getUserInfo(RegForgetPwdBaseActivity.this,new RequestCallback<UserEntity>() {
            @Override
            public void onError(Request request, Exception e) {

                try {
                    JSONObject jsonObj = new JSONObject(e.getLocalizedMessage());
                    int code = jsonObj.optInt("code");
                    if(HttpUtils.CODE_ACCOUNT_NOT_LOGIN==code){
                        AlertDialog.Builder dialog = new AlertDialog.Builder(RegForgetPwdBaseActivity.this);
                        dialog.setCancelable(false);
                        dialog.setMessage(R.string.not_login);
                        dialog.setPositiveButton(getString(R.string.ALERT_DLG_BTN_OK), new android.content.DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.dismiss();
                                startActivity(new Intent(RegForgetPwdBaseActivity.this,LoginActivity.class));
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
                 * 如果没有绑定macAddress进行绑定
                 * 绑定过,判断个人信息是否完整
                 */

                if(CacheUtils.getMacAdress(RegForgetPwdBaseActivity.this) == null){
                    Intent it = new Intent(RegForgetPwdBaseActivity.this, BandConnectActivity.class);
                    startActivity(it);
                }else {
                    if(CacheUtils.getMacAdress(RegForgetPwdBaseActivity.this).equals("") ){
                        Intent it = new Intent(RegForgetPwdBaseActivity.this, BandConnectActivity.class);
                        startActivity(it);
                    }else if(profile.getWeight() == 0 || profile.getBirthday().isEmpty() || profile.getHeight() == 0){
                        Intent it = new Intent(RegForgetPwdBaseActivity.this, SexActivity.class);
                        startActivity(it);
                    }else {
                        Intent intent = new Intent(RegForgetPwdBaseActivity.this, HomePager.class);
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

    private void getImageVerification() {

        mImageLoader.displayImage(HttpUtils.URL + HttpUtils.HTTP_PRE_IMGCODE,
                imageVerification);
    }
}
