package com.tunshu.view.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.basecore.util.core.MobileFormat;
import com.basecore.util.core.StringUtils;
import com.basecore.widget.CustomToast;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tunshu.R;
import com.tunshu.entity.InfoEntity;
import com.tunshu.entity.UserEntity;
import com.tunshu.http.MyHttpClient;
import com.tunshu.util.Constants;
import com.tunshu.util.LogUtil;
import com.tunshu.util.MD5;
import com.tunshu.widget.ClearEditText;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * 作者：yzb on 2015/7/15 10:33
 */
public class LoginActivity extends CommonActivity {
    private ClearEditText inputAccount;
    private ClearEditText inputPassword;
    private TextView loginAccount;
    private TextView forgetPassword;
    private CheckBox rememberPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findView();
        onListener();
        fillView();
    }

    private void findView() {
        findTitle();
        inputAccount = (ClearEditText) findViewById(R.id.input_account);
        inputPassword = (ClearEditText) findViewById(R.id.input_password);
        loginAccount = (TextView) findViewById(R.id.login_account);
        forgetPassword = (TextView) findViewById(R.id.forget_password);
        rememberPassword = (CheckBox) findViewById(R.id.remember_password);
        inputPassword.setTypeface(Typeface.DEFAULT);
        inputPassword.setTransformationMethod(new PasswordTransformationMethod());
    }

    private void onListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        goText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(it, 100);
            }
        });
        loginAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tel = inputAccount.getText().toString().replace(" ", "");
                String password = inputPassword.getText().toString().replace(" ", "");
                String loginType;

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
                if (new MobileFormat(tel.trim()).isLawful()) {
                    loginType = "0";
                } else {
                    loginType = "1";
                }
                login(tel, password, loginType);
            }
        });
        rememberPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getCoreApplication().getPreferenceConfig().setBoolean(Constants.IS_REMEMBER, isChecked);
            }
        });
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(it);
            }
        });
    }

    private void fillView() {
        title.setText(getString(R.string.login));
        goText.setText(getString(R.string.register));
        goText.setVisibility(View.VISIBLE);
        inputAccount.setText(getCoreApplication().getPreferenceConfig().getString(Constants.USERNAME, ""));
        inputPassword.setText(getCoreApplication().getPreferenceConfig().getString(Constants.PASSWORD, ""));
        rememberPassword.setChecked(getCoreApplication().getPreferenceConfig().getBoolean(Constants.IS_REMEMBER, false));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            finish();
        }
    }

    private void login(final String tel, final String password, String loginType) {
        showProgress();
        RequestParams params = getCommonParams();
        params.put("username", tel);
        params.put("password", MD5.getMD5String(password));
        params.put("logintype", loginType);
        params.put("ac", "login");
        MyHttpClient.post(this, "authority/index", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                hideProgress();
                try {
                    LogUtil.getLogger().d(response.toString());
                    UserEntity entity = com.alibaba.fastjson.JSONObject.parseObject(response.toString(), UserEntity.class);
                    if (entity != null) {
                        if (entity.getCode().equals("1001")) {
                            CustomToast.showToast(LoginActivity.this, getString(R.string.login_success));
                            getCoreApplication().getPreferenceConfig().setString(Constants.USERNAME, tel);
                            getCoreApplication().getPreferenceConfig().setString(Constants.PASSWORD, password);
                            getCoreApplication().getPreferenceConfig().setString(Constants.NICKNAME, entity.getData().getNikename());
                            getCoreApplication().getPreferenceConfig().setString(Constants.USER_ICON, entity.getData().getAvatar());
                            getCoreApplication().getPreferenceConfig().setString(Constants.USERID, entity.getData().getUserid());
                            getCoreApplication().getPreferenceConfig().setString(Constants.TOKEN, entity.getData().getUsertoken());
                            sendBroadcast(new Intent(Constants.USER_INFO_REFRESH));//刷新首页用户信息
                            finish();
                        } else {
                            CustomToast.showToast(LoginActivity.this, entity.getMsg());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                hideProgress();
            }
        });
    }
}
