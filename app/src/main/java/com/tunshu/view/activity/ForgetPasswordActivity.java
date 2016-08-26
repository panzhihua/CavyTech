package com.tunshu.view.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.basecore.util.core.FileInfoUtils;
import com.basecore.util.core.MobileFormat;
import com.basecore.util.core.StringUtils;
import com.basecore.widget.CustomToast;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tunshu.R;
import com.tunshu.application.CommonApplication;
import com.tunshu.entity.CommonEntity;
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
 * 作者：yzb on 2015/8/19 15:56
 */
public class ForgetPasswordActivity extends CommonActivity {
    private boolean isTel;
    private ImageView imageVerification;
    private TextView sendVerification;
    private ClearEditText inputAccount, inputPassword, inputPasswordAgain;
    private EditText inputVerification;
    private TimeCount time;
    private TextView restartPassword;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        findView();
        fillView();
        addListener();
    }

    private void findView() {
        findTitle();
        imageVerification = (ImageView) findViewById(R.id.image_verification);
        sendVerification = (TextView) findViewById(R.id.send_verification);
        inputAccount = (ClearEditText) findViewById(R.id.input_account);
        inputPassword = (ClearEditText) findViewById(R.id.input_password);
        inputPasswordAgain = (ClearEditText) findViewById(R.id.input_password_again);
        inputVerification = (EditText) findViewById(R.id.input_verification);
        restartPassword = (TextView) findViewById(R.id.restart_password);
        inputPassword.setTypeface(Typeface.DEFAULT);
        inputPassword.setTransformationMethod(new PasswordTransformationMethod());
        inputPasswordAgain.setTypeface(Typeface.DEFAULT);
        inputPasswordAgain.setTransformationMethod(new PasswordTransformationMethod());
    }

    private void fillView() {
        title.setText(getString(R.string.retrieve_password));
        goText.setVisibility(View.VISIBLE);
        goText.setText(getString(R.string.email_box));
        time = new TimeCount(60000, 1000);
    }


    private void addListener() {
        goText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTel) {
                    goText.setText(getString(R.string.email_box));
                    imageVerification.setVisibility(View.GONE);
                    sendVerification.setVisibility(View.VISIBLE);
                    inputAccount.setInputType(InputType.TYPE_CLASS_NUMBER);
                    inputVerification.setInputType(InputType.TYPE_CLASS_NUMBER);
                    time.cancel();
                    time.onFinish();
                    inputAccount.setHint(getString(R.string.input_tel));
                    isTel = false;
                } else {
                    goText.setText(getString(R.string.tel));
                    imageVerification.setVisibility(View.VISIBLE);
                    sendVerification.setVisibility(View.GONE);
                    inputAccount.setHint(getString(R.string.input_email));
                    inputAccount.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    inputVerification.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    isTel = true;
                }
                getImageVerification();
                inputAccount.setText("");
                inputPassword.setText("");
                inputPasswordAgain.setText("");
                inputVerification.setText("");
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sendVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputAccount.getText().toString().length() > 0) {
                    if (new MobileFormat(inputAccount.getText().toString().trim()).isLawful()) {
                        getVerCode();
                        time.start();
                    } else {
                        CustomToast.showToast(ForgetPasswordActivity.this, getString(R.string.input_tel_flase));
                    }
                } else {
                    CustomToast.showToast(ForgetPasswordActivity.this, getString(R.string.input_tel_none));
                }

            }
        });

        restartPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String account = inputAccount.getText().toString().replace(" ", "");
                String code = inputVerification.getText().toString().replace(" ", "");
                String password = inputPassword.getText().toString().replace(" ", "");
                String passwordAgain = inputPasswordAgain.getText().toString().replace(" ", "");
                if (isTel) {
                    if (TextUtils.isEmpty(account)){
                        CustomToast.showToast(ForgetPasswordActivity.this, getString(R.string.input_email));
                        return;
                    }
                    if (!StringUtils.isEmail(account)) {
                        CustomToast.showToast(ForgetPasswordActivity.this, getString(R.string.email_fail));
                        return;
                    }
                } else {
                    if (TextUtils.isEmpty(account)){
                        CustomToast.showToast(ForgetPasswordActivity.this, getString(R.string.input_tel_none));
                        return;
                    }
                    if (!new MobileFormat(account.trim()).isLawful()) {
                        CustomToast.showToast(ForgetPasswordActivity.this, getString(R.string.input_tel_flase));
                        return;
                    }

                }

                if (TextUtils.isEmpty(code)) {
                    CustomToast.showToast(ForgetPasswordActivity.this, getString(R.string.please_input_correct_code));
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    CustomToast.showToast(ForgetPasswordActivity.this, R.string.please_input_password);
                    return;
                } else {
                    if (password.length() < 6) {
                        CustomToast.showToast(ForgetPasswordActivity.this, R.string.password_length);
                        return;
                    }
                }
                if (!password.equals(passwordAgain)) {
                    CustomToast.showToast(ForgetPasswordActivity.this, getString(R.string.password_inconsistent));
                    return;
                }
                if (!isTel) {
                    checkPhoneVerification(account, code, password);
                } else {
                    if (StringUtils.isEmail(account)) {

                        ImageRestartVerification(account, code, password);
                    } else {
                        CustomToast.showToast(ForgetPasswordActivity.this, R.string.email_fail);
                        return;
                    }
                }

            }
        });
        imageVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageVerification();
            }
        });
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

    private void getVerCode() {
        RequestParams params = getCommonParams();
        params.put("phone", inputAccount.getText().toString());
        params.put("ac", "certification");
        params.put("forgetpwd", "notnull");
        MyHttpClient.get(this, "authority/index", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    LogUtil.getLogger().d(response.toString());
                    CommonEntity entity = com.alibaba.fastjson.JSONObject.parseObject(response.toString(), CommonEntity.class);
                    if (entity != null) {
                        CustomToast.showToast(ForgetPasswordActivity.this, entity.getMsg());
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


    private void phoneRestartPassword(String account, String password) {
        showProgress();
        RequestParams params = getCommonParams();
        params.put("ac", "forgetupdatepwd");
        params.put("phone", account);
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
                            finish();
                        }
                        CustomToast.showToast(ForgetPasswordActivity.this, entity.getMsg());
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

    private void checkPhoneVerification(final String account, String code, final String password) {
        showProgress();
        RequestParams params = getCommonParams();
        params.put("ac", "forgetphonecheck");
        params.put("phone", account);
        params.put("code", code);
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
                            phoneRestartPassword(account, password);
                        } else {
                            CustomToast.showToast(ForgetPasswordActivity.this, entity.getMsg());
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

    private void ImageRestartVerification(final String account, final String code, final String password) {
        showProgress();
        RequestParams params = getCommonParams();
        params.put("ac", "forgetpwdImgCheck");
        params.put("email", account);
        params.put("imgcheckcode", code);
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
                                finish();
                        }
                        CustomToast.showToast(ForgetPasswordActivity.this, entity.getMsg());
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
}
