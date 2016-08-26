package com.cavytech.wear2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.basecore.widget.CustomToast;
import com.cavytech.wear2.R;
import com.cavytech.wear2.base.AppCompatActivityEx;
import com.cavytech.wear2.entity.CommonEntity;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.RequestCallback;
import com.cavytech.wear2.util.Constants;
import com.squareup.okhttp.Request;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;

public class VerficatonActivity extends AppCompatActivityEx {
    @ViewInject(R.id.send_test)
    private Button send_test;

    @ViewInject(R.id.et_send_test)
    private EditText et_send_test;

    @ViewInject(R.id.title)
    private TextView title;

    @ViewInject(R.id.back)
    private ImageView back;
    private String userid;
    private String friendId;
    private int maxLen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verficaton);
        x.view().inject(this);

        Intent intent = getIntent();
        userid = intent.getStringExtra(Constants.INTENT_EXTRA_USERID);
        friendId = intent.getStringExtra(Constants.INTENT_EXTRA_FRDID);
        maxLen = intent.getIntExtra(Constants.INTENT_EXTRA_MAXLEN, 0);

//        Log.e("TAG","传过来的id-----"+userid);
        send_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userid != null && !userid.isEmpty()){
                    String ss = et_send_test.getText().toString();

                    HttpUtils.getInstance().addFriend(friendId, HttpUtils.AddFriendType.ADDFRIEND, ss, new RequestCallback<CommonEntity>() {
                        @Override
                        public void onError(Request request, Exception e) {

                        }

                        @Override
                        public void onResponse(CommonEntity response) {

                            if(response.isSuccess()){
                                Toast.makeText(VerficatonActivity.this,"发送成功",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(VerficatonActivity.this,"发送失败",Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        }
                    });
                }else{

                    String inputText = et_send_test.getText().toString();
                    if(maxLen > 0 && inputText.length() > maxLen){
                        String msg = getString(R.string.maxinput, maxLen);
                        CustomToast.showToast(VerficatonActivity.this, msg);

                        return;
                    }
                    Intent intent = new Intent();
                    intent.putExtra(Constants.INTENT_EXTRA_EDITTEXT, inputText);

                    // 通过调用setResult方法返回结果给前一个activity。
                    setResult(RESULT_OK, intent);
                    //关闭当前activity
                    finish();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title.setText(R.string.contantperson);

        String hintText = intent.getStringExtra(Constants.INTENT_EXTRA_HINT);
        if(hintText != null && !hintText.isEmpty()){
            et_send_test.setHint(hintText);
        }

        String titleText = intent.getStringExtra(Constants.INTENT_EXTRA_TITLE);
        if(titleText != null && !titleText.isEmpty()){
            title.setText(titleText);
        }

        String btnText = intent.getStringExtra(Constants.INTENT_EXTRA_BTNTEXT);
        if(btnText != null && !btnText.isEmpty()){
            send_test.setText(btnText);
        }
    }
}
