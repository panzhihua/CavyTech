package com.cavytech.wear2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.cavytech.wear2.R;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.util.Constants;

/**
 * 作者：yzb on 2015/7/14 19:22
 */
public class RegisterActivity extends RegForgetPwdBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isForgetPwdView = false;
        super.onCreate(savedInstanceState);
        setRegister();
     }

    private void setRegister(){

        if(HttpUtils.getLanguage().equals("en")){
            treaty.setVisibility(View.INVISIBLE);
            agreeContext.setVisibility(View.INVISIBLE);
            treaty.setChecked(true);
        }else{
            treaty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onClickText(){
        Intent intent = new Intent(RegisterActivity.this, WebActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_WEBURL, Constants.LICENSE_URL);
        intent.putExtra(Constants.INTENT_EXTRA_TITLE, getString(R.string.treaty));
        startActivity(intent);
        finish();
    }
}
