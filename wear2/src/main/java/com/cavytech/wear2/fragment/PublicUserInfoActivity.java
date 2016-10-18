package com.cavytech.wear2.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.basecore.widget.CustomToast;
import com.cavytech.wear2.R;
import com.cavytech.wear2.activity.LoginActivity;
import com.cavytech.wear2.base.AppCompatActivityEx;
import com.cavytech.wear2.entity.CommonEntity;
import com.cavytech.wear2.entity.UserEntity;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.RequestCallback;
import com.cavytech.wear2.util.Constants;
import com.cavytech.wear2.util.SerializeUtils;
import com.cavytech.widget.SwitchButtonEx;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by longjining on 16/4/12.
 */
public class PublicUserInfoActivity extends AppCompatActivityEx implements CompoundButton.OnCheckedChangeListener {

    @ViewInject(R.id.height_public_switch)
    private SwitchButtonEx heightPublicSwitch;

    @ViewInject(R.id.weight_public_switch)
    private SwitchButtonEx weightPublicSwitch;

    @ViewInject(R.id.birthday_public_switch)
    private SwitchButtonEx birthdayPublicSwitch;

    @ViewInject(R.id.title)
    private TextView title;

    @ViewInject(R.id.back)
    private ImageView back;

    UserEntity.ProfileEntity userInfo = null;
    UserEntity.ProfileEntity editUserInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_userinfo_public);

        setToolBar();

        x.view().inject(this);

        intSwithc();

        setTitleText(getResources().getString(R.string.information));

        onListener();
    }

    public void setTitleText(String titleText) {

        title.setText(titleText);
    }

    private void intSwithc() {
        heightPublicSwitch.setOnCheckedChangeListener(this);
        weightPublicSwitch.setOnCheckedChangeListener(this);
        birthdayPublicSwitch.setOnCheckedChangeListener(this);

        userInfo = (UserEntity.ProfileEntity) SerializeUtils.unserialize(Constants.SERIALIZE_USERINFO);
        if (null != userInfo) {
            heightPublicSwitch.setChecked(userInfo.isShare_height());
            weightPublicSwitch.setChecked(userInfo.isShare_weight());
            birthdayPublicSwitch.setChecked(userInfo.isShare_birthday());
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (heightPublicSwitch == buttonView) {

            return;
        }

        if (weightPublicSwitch == buttonView) {

            return;
        }

        if (birthdayPublicSwitch == buttonView) {

            return;
        }
    }

    private void onListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isModify()) {
                    editUserInfo = userInfo;

                    editUserInfo.setShare_weight(weightPublicSwitch.isChecked());
                    editUserInfo.setShare_height(heightPublicSwitch.isChecked());
                    editUserInfo.setShare_birthday(birthdayPublicSwitch.isChecked());

                    setUserInfo();
                    return;
                }
                finish();
            }
        });
    }

    private void setUserInfo() {


        HttpUtils.getInstance().setUserInfo(userInfo, PublicUserInfoActivity.this, new RequestCallback<CommonEntity>() {
            @Override
            public void onError(Request request, Exception e) {
                CustomToast.showToast(PublicUserInfoActivity.this, e.toString());
                try {
                    JSONObject jsonObj = new JSONObject(e.getLocalizedMessage());
                    int code = jsonObj.optInt("code");
                    if(HttpUtils.CODE_ACCOUNT_NOT_LOGIN==code){
                        AlertDialog.Builder dialog = new AlertDialog.Builder(PublicUserInfoActivity.this);
                        dialog.setCancelable(false);
                        dialog.setMessage(R.string.not_login);
                        dialog.setPositiveButton(getString(R.string.ALERT_DLG_BTN_OK), new android.content.DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.dismiss();
                                startActivity(new Intent(PublicUserInfoActivity.this,LoginActivity.class));
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
                if (response.getCode() == HttpUtils.newSUCCESS) {

                    SerializeUtils.serialize(editUserInfo, Constants.SERIALIZE_USERINFO);
                    CustomToast.showToast(PublicUserInfoActivity.this, getString(R.string.edit_success));

                    finish();
                } else {
                    CustomToast.showToast(PublicUserInfoActivity.this, response.getMsg());
                }
            }
        });
    }

    private boolean isModify() {

        if (null == userInfo) {
            return false;
        }
        if (userInfo.isShare_height() != heightPublicSwitch.isChecked()) {
            return true;
        }

        if (userInfo.isShare_weight() != weightPublicSwitch.isChecked()) {
            return true;
        }

        if (userInfo.isShare_birthday() != birthdayPublicSwitch.isChecked()) {
            return true;
        }

        return false;
    }
}
