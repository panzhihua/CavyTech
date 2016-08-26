package com.tunshu.view.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.basecore.util.core.MobileFormat;
import com.basecore.util.core.StringUtils;
import com.basecore.widget.CustomToast;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tunshu.R;
import com.tunshu.application.CommonApplication;
import com.tunshu.entity.CommonEntity;
import com.tunshu.entity.UserEntity;
import com.tunshu.http.MyHttpClient;
import com.tunshu.util.Constants;
import com.tunshu.util.LogUtil;
import com.tunshu.util.MD5;
import com.tunshu.widget.ClearEditText;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 作者：yzb on 2015/7/14 19:22
 */
public class RegisterActivity extends CommonActivity {
    private ClearEditText inputAccount;
    private EditText inputVerification;
    private TextView sendVerification;
    private ClearEditText inputPassword;
    private TextView registerAccount;
    private CheckBox treaty;
    private boolean isTel;
    private ImageView imageVerification;
    private String type;
    private TimeCount time;
    private TextView agreeContext;

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
        if(getLanguage().equals("en")){
            treaty.setVisibility(View.INVISIBLE);
            agreeContext.setVisibility(View.INVISIBLE);
            treaty.setChecked(true);
        }
    }

    private void onListener() {
        sendVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputAccount.getText().toString().length() > 0) {
                    if (new MobileFormat(inputAccount.getText().toString().trim()).isLawful()) {
                        getVerCode();
                        time.start();
                    } else {
                        CustomToast.showToast(RegisterActivity.this, getString(R.string.input_tel_flase));
                    }
                } else {
                    CustomToast.showToast(RegisterActivity.this, getString(R.string.input_tel_none));
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        registerAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = inputAccount.getText().toString().replace(" ", "");
                String code = inputVerification.getText().toString().replace(" ", "");
                String password = inputPassword.getText().toString().replace(" ", "");
                if (isTel) {
                    if (TextUtils.isEmpty(account)) {
                        CustomToast.showToast(RegisterActivity.this, getString(R.string.input_email));
                        return;
                    }
                    if (!StringUtils.isEmail(account)) {
                        CustomToast.showToast(RegisterActivity.this, getString(R.string.email_fail));
                        return;
                    }
                } else {
                    if (TextUtils.isEmpty(account)) {
                        CustomToast.showToast(RegisterActivity.this, getString(R.string.input_tel_none));
                        return;
                    }
                    if (!new MobileFormat(account.trim()).isLawful()) {
                        CustomToast.showToast(RegisterActivity.this, getString(R.string.input_tel_none));
                        return;
                    }

                }
                if (TextUtils.isEmpty(code)) {
                    CustomToast.showToast(RegisterActivity.this, getString(R.string.please_input_correct_code));
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    CustomToast.showToast(RegisterActivity.this, R.string.please_input_password);
                    return;
                } else {
                    if (password.length() < 6) {
                        CustomToast.showToast(RegisterActivity.this, R.string.password_length);
                        return;
                    }
                }
                if (treaty.isChecked()) {
                    type = isTel ? "1" : "0";
                    if (isTel) {
                        checkImageVerification(account, code, password, type);
                    } else {
                        register(account, code, password, type);
                    }
                } else {
                    CustomToast.showToast(RegisterActivity.this, R.string.please_read_treaty);
                    return;
                }
            }
        });
        agreeContext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, WebActivity.class);
                intent.putExtra("webUrl", Constants.LICENSE_URL);
                intent.putExtra("titleName", getString(R.string.treaty));
                startActivity(intent);
            }
        });
        goText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTel) {
                    goText.setText(getString(R.string.email_box));
                    sendVerification.setVisibility(View.VISIBLE);
                    imageVerification.setVisibility(View.GONE);
                    inputAccount.setInputType(InputType.TYPE_CLASS_NUMBER);
                    inputVerification.setInputType(InputType.TYPE_CLASS_NUMBER);
                    time.cancel();
                    time.onFinish();
                    inputAccount.setHint(getString(R.string.input_tel));
                    isTel = false;
                } else {
                    goText.setText(getString(R.string.tel));
                    inputAccount.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    inputVerification.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    sendVerification.setVisibility(View.GONE);
                    imageVerification.setVisibility(View.VISIBLE);
                    inputAccount.setHint(getString(R.string.input_email));
                    isTel = true;
                }
                getImageVerification();
                inputAccount.setText("");
                inputVerification.setText("");
                inputPassword.setText("");
            }
        });
        imageVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageVerification();
            }
        });

    }

    private void fillView() {
        title.setText(getString(R.string.register));
        goText.setVisibility(View.VISIBLE);
        goText.setText(getString(R.string.email_box));
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

    private void register(final String account, final String code, final String password, final String type) {
        showProgress();
        RequestParams params = getCommonParams();
        params.put("username", account);
        params.put("phone", account);
        params.put("password", MD5.getMD5String(password));
        params.put("code", code);
        params.put("regtype", type);
        params.put("ac", "userregist");
        MyHttpClient.get(this, "authority/index", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                hideProgress();
                try {
                    LogUtil.getLogger().d(response.toString());
                    CommonEntity entity = com.alibaba.fastjson.JSONObject.parseObject(response.toString(), CommonEntity.class);
                    if (entity != null) {
                        if (entity.getCode().equals("1001")) {
                            login(account, password, "0");
                        }
                        CustomToast.showToast(RegisterActivity.this, entity.getMsg());
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

    private void getVerCode() {
        RequestParams params = getCommonParams();
        params.put("phone", inputAccount.getText().toString());
        params.put("ac", "certification");
        MyHttpClient.get(this, "authority/index", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                hideProgress();
                try {
                    LogUtil.getLogger().d(response.toString());
                    CommonEntity entity = com.alibaba.fastjson.JSONObject.parseObject(response.toString(), CommonEntity.class);
                    if (entity != null) {
                        CustomToast.showToast(RegisterActivity.this, entity.getMsg());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void login(final String username, final String password, String loginType) {
        showProgress();
        RequestParams params =getCommonParams();
        params.put("username", username);
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
                            getCoreApplication().getPreferenceConfig().setString(Constants.USERNAME, username);
                            getCoreApplication().getPreferenceConfig().setString(Constants.PASSWORD, password);
                            getCoreApplication().getPreferenceConfig().setString(Constants.NICKNAME, entity.getData().getNikename());
                            getCoreApplication().getPreferenceConfig().setString(Constants.USER_ICON, entity.getData().getAvatar());
                            getCoreApplication().getPreferenceConfig().setString(Constants.USERID, entity.getData().getUserid());
                            getCoreApplication().getPreferenceConfig().setString(Constants.TOKEN, entity.getData().getUsertoken());
                            sendBroadcast(new Intent(Constants.USER_INFO_REFRESH));//刷新首页用户信息
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            CustomToast.showToast(RegisterActivity.this, entity.getMsg());
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

    private void getImageVerification() {
        MyHttpClient.get(this, "authority/imageCode?codekey=1", new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    try {
                        BufferedOutputStream stream = null;
                        File file = null;
                        try {
                            File filePath = new File(((CommonApplication) getApplication()).getDownloadFilePath(), "vertfy.jpg");
                            file = new File(filePath.toString());
                            LogUtil.getLogger().d(filePath.toString());
                            FileOutputStream fstream = new FileOutputStream(file);
                            stream = new BufferedOutputStream(fstream);
                            stream.write(responseBody);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (stream != null) {
                                try {
                                    stream.close();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        mImageLoader.displayImage("file://"
                                        + ((CommonApplication) getApplication()).getDownloadFilePath()
                                        + "/vertfy.jpg",
                                imageVerification);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                error.printStackTrace();
            }
        });
    }

    private void checkImageVerification(final String account, final String code, final String password, final String type) {
        RequestParams params = getCommonParams();
        params.put("imgcheckcode", code);
        params.put("ac", "forgetpwdImgCheck");
        params.put("email", account);
        params.put("isregist", "0");
        params.put("password", MD5.getMD5String(password));
        MyHttpClient.get(this, "authority/index", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                hideProgress();
                try {
                    LogUtil.getLogger().d(response.toString());
                    CommonEntity entity = com.alibaba.fastjson.JSONObject.parseObject(response.toString(), CommonEntity.class);
                    if (entity != null) {
                        if (entity.getCode().equals("1001")) {
                            register(account, code, password, type);
                        } else {
                            CustomToast.showToast(RegisterActivity.this, entity.getMsg());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

            }
        });
    }
}
