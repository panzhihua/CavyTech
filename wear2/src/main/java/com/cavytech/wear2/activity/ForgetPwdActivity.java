package com.cavytech.wear2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.cavytech.wear2.R;

/**
 * Created by longjining on 16/3/4.
 */
public class ForgetPwdActivity extends RegForgetPwdBaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isForgetPwdView = true;
        super.onCreate(savedInstanceState);
        setForgetPwd();
    }

    private void setForgetPwd(){
        treaty.setVisibility(View.GONE);
        treaty.setChecked(true);
        agreeContext.setText(getString(R.string.remeber_pwd));
        registerAccount.setText(getString(R.string.complite));
        title.setText("忘记密码");
    }

    @Override
    protected void onClickText(){
        Intent intent = new Intent(ForgetPwdActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
