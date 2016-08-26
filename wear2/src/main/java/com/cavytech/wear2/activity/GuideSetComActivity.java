package com.cavytech.wear2.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.basecore.widget.CustomToast;
import com.cavytech.wear2.R;
import com.cavytech.wear2.entity.UserCommonEntity;
import com.cavytech.wear2.entity.UserEntity;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.RequestCallback;
import com.cavytech.wear2.util.Constants;
import com.cavytech.wear2.util.FileUtils;
import com.cavytech.wear2.util.SerializeUtils;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by longjining on 16/3/16.
 */
public class GuideSetComActivity extends CommonActivity{
    LinearLayout layout_next;
    public ImageButton btn_next;

    public boolean isEdit = false; // 是否是修改模式
    public UserEntity.ProfileEntity userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(FileUtils.isFileExit(Constants.SERIALIZE_USERINFO)){
            userInfo = (UserEntity.ProfileEntity) SerializeUtils.unserialize(Constants.SERIALIZE_USERINFO);
        }else{
            userInfo = new UserEntity.ProfileEntity();
        }

        isEdit = getIntent().getBooleanExtra(Constants.INTENT_EXTRA_ISEDIT, false);
    }

    protected void findView() {

        layout_next = (LinearLayout) findViewById(R.id.layout_next);
        btn_next = (ImageButton) findViewById(R.id.img_next);
    }

    protected void onListener() {

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onClickNextBtn();
            }
        });

    }

    public void onClickNextBtn(){

    }

    public void saveEdit(final UserEntity.ProfileEntity userInfo){

        HttpUtils.getInstance().setUserInfo(userInfo, GuideSetComActivity.this , new RequestCallback<UserCommonEntity>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        try {
                            JSONObject jsonObj = new JSONObject(e.getLocalizedMessage());
                            int code = jsonObj.optInt("code");
                            if(HttpUtils.CODE_ACCOUNT_NOT_LOGIN==code) {
                                Toast.makeText(GuideSetComActivity.this, R.string.not_login, Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(GuideSetComActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        Log.e("TAG",e+"error");
                        try {
                            JSONObject jsonObj = new JSONObject(e.getLocalizedMessage());
                            int code = jsonObj.optInt("code");
                            if(HttpUtils.CODE_ACCOUNT_NOT_LOGIN==code){
                                AlertDialog.Builder dialog = new AlertDialog.Builder(GuideSetComActivity.this);
                                dialog.setCancelable(false);
                                dialog.setMessage(R.string.not_login);
                                dialog.setPositiveButton(getString(R.string.ALERT_DLG_BTN_OK), new android.content.DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        arg0.dismiss();
                                        startActivity(new Intent(GuideSetComActivity.this,LoginActivity.class));
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
                    public void onResponse(UserCommonEntity response) {
                        Log.e("TAG",response.getCode()+"-=-=-=code");
                        if (response.getCode() == HttpUtils.newSUCCESS) {
                            SerializeUtils.serialize(userInfo, Constants.SERIALIZE_USERINFO);
                            CustomToast.showToast(GuideSetComActivity.this, R.string.edit_success);
                            finish();
                        } else {
                            CustomToast.showToast(GuideSetComActivity.this, response.getMsg());
                        }
                    }
                });
    }
}
