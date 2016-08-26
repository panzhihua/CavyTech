package com.tunshu.view.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.basecore.util.core.StringUtils;
import com.basecore.widget.CustomToast;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tunshu.R;
import com.tunshu.entity.CommonEntity;
import com.tunshu.http.MyHttpClient;
import com.tunshu.util.LogUtil;

import org.apache.http.Header;
import org.json.JSONObject;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;

public class FeedbackActivity extends CommonActivity{

    private EditText etcontent;
    private EditText etcontact;
    private TextView tvsubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initView();
        addListener();
    }

    private void initView() {
        findTitle();
        etcontent = (EditText) findViewById(R.id.et_content);
        etcontact = (EditText) findViewById(R.id.et_contact);
        tvsubmit = (TextView) findViewById(R.id.tv_submit);
        title.setText(getString(R.string.feedback));

        // 解决scrollView中嵌套EditText导致不能上下滑动的问题
        etcontent.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                v.getParent().requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });
    }

    private void addListener() {
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvsubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etcontent.getText().toString().trim().length() < 3 || etcontent.getText().toString().trim().length() > 200) {

                    CustomToast.showToast(FeedbackActivity.this, getString(R.string.feedback_isnull));
                    return;
                }

                String contact = etcontact.getText().toString().trim();
                if (contact.length() > 0 && StringUtils.count(contact) < 201 && (StringUtils.isEmail(contact) || StringUtils.isPhoneNumberValid(contact))) {
                    submit();
                } else {
                    CustomToast.showToast(FeedbackActivity.this, getString(R.string.contact_is_wrong));
                }
            }
        });
    }

    private void submit() {
        showProgress();
        RequestParams params = getCommonParams();
        params.put("ac", "useropinion");
        params.put("content", etcontent.getText().toString().trim());
        params.put("contact", etcontact.getText().toString().trim());
        MyHttpClient.get(FeedbackActivity.this, "common/index", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                hideProgress();
                try {
                    LogUtil.getLogger().d(response.toString());
                    CommonEntity entity = com.alibaba.fastjson.JSONObject.parseObject(response.toString(), CommonEntity.class);
                    if (entity.getCode().equals("1001")) {
                        finish();
                    }
                    CustomToast.showToast(FeedbackActivity.this, entity.getMsg());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                CustomToast.showToast(FeedbackActivity.this, getString(R.string.feedback_failed));
                hideProgress();
            }
        });
    }


}
