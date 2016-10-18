package com.cavytech.wear2.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by longjining on 16/5/18.
 */
public class FeedbackActivity extends AppCompatActivityEx {

    @ViewInject(R.id.et_content)
    private EditText et_content;

    @ViewInject(R.id.title)
    private TextView title;

    @ViewInject(R.id.back)
    private ImageView back;

    @ViewInject(R.id.send)
    private TextView send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feedback);

        setToolBar();

        x.view().inject(this);


        onListener();

        setTitle();
    }

    private void setTitle(){
        title.setText(R.string.feedback);
    }

    private void onListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HttpUtils.getInstance().submitFeedback(FeedbackActivity.this,et_content.getText().toString(), new RequestCallback<CommonEntity>() {
                    @Override
                    public void onError(Request request, Exception e) {

                        try {
                            JSONObject jsonObj = new JSONObject(e.getLocalizedMessage());
                            int code = jsonObj.optInt("code");
                            if(HttpUtils.CODE_ACCOUNT_NOT_LOGIN==code){
                                AlertDialog.Builder dialog = new AlertDialog.Builder(FeedbackActivity.this);
                                dialog.setCancelable(false);
                                dialog.setMessage(R.string.not_login);
                                dialog.setPositiveButton(getString(R.string.ALERT_DLG_BTN_OK), new android.content.DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        arg0.dismiss();
                                        startActivity(new Intent(FeedbackActivity.this,LoginActivity.class));
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
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }

                    }

                    @Override
                    public void onResponse(CommonEntity response) {
//                        Log.e("TAG","意见反馈----"+response.getCode());
                        if(response.isSuccess()){
                            Toast.makeText(getApplicationContext(),"意见反馈成功",Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            CustomToast.showToast(FeedbackActivity.this, response.getMsg());
                        }
                    }
                });
            }
        });
    }
}
