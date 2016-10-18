package com.cavytech.wear2.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.TextView;

import com.basecore.widget.CustomToast;
import com.cavytech.wear2.R;
import com.cavytech.wear2.entity.UserEntity;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.RequestCallback;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class AchieveAlertDialogActivity extends AppCompatActivity {
    @ViewInject(R.id.achievement_img_medal1)
    private ImageButton achievement_img_medal1;

    @ViewInject(R.id.achievement_img_medal2)
    private ImageButton achievement_img_medal2;

    @ViewInject(R.id.achievement_img_medal3)
    private ImageButton achievement_img_medal3;

    @ViewInject(R.id.achievement_img_medal4)
    private ImageButton achievement_img_medal4;

    @ViewInject(R.id.achievement_img_medal5)
    private ImageButton achievement_img_medal5;

    @ViewInject(R.id.achievement_img_medal6)
    private ImageButton achievement_img_medal6;

    @ViewInject(R.id.achievement_value)
    private TextView achievement_value;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achieve_alert_dialog);
        x.view().inject(this);

        getdatafromnet();
    }

    private void getdatafromnet() {
        HttpUtils.getInstance().getUserInfo(AchieveAlertDialogActivity.this,new RequestCallback<UserEntity>() {

            @Override
            public void onError(Request request, Exception e) {
                CustomToast.showToast(AchieveAlertDialogActivity.this, e.toString());
                try {
                    JSONObject jsonObj = new JSONObject(e.getLocalizedMessage());
                    int code = jsonObj.optInt("code");
                    if(HttpUtils.CODE_ACCOUNT_NOT_LOGIN==code){
                        AlertDialog.Builder dialog = new AlertDialog.Builder(AchieveAlertDialogActivity.this);
                        dialog.setCancelable(false);
                        dialog.setMessage(R.string.not_login);
                        dialog.setPositiveButton(getString(R.string.ALERT_DLG_BTN_OK), new android.content.DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.dismiss();
                                startActivity(new Intent(AchieveAlertDialogActivity.this,LoginActivity.class));
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
            public void onResponse(UserEntity response) {
                if (response.getCode() == HttpUtils.newSUCCESS) {

                    achievement_value.setText("您已累计行走"+response.getProfile().getAwards().get(response.getProfile().getAwards().size()-1)+"步");
                    int type = response.getProfile().getAwards().size();
                    setIcon(type);

                } else {
                    CustomToast.showToast(AchieveAlertDialogActivity.this, response.getMsg());
                }
            }
        });
    }

    private void setIcon(int type) {
        switch (type) {
            case 0:
                achievement_img_medal1.setImageResource(R.drawable.medal_1_lighted);
                break;
            case 1:
                achievement_img_medal1.setImageResource(R.drawable.medal_1_lighted);
                achievement_img_medal2.setImageResource(R.drawable.medal_2_lighted);
                break;
            case 2:
                achievement_img_medal1.setImageResource(R.drawable.medal_1_lighted);
                achievement_img_medal2.setImageResource(R.drawable.medal_2_lighted);
                achievement_img_medal3.setImageResource(R.drawable.medal_3_lighted);
                break;
            case 3:
                achievement_img_medal1.setImageResource(R.drawable.medal_1_lighted);
                achievement_img_medal2.setImageResource(R.drawable.medal_2_lighted);
                achievement_img_medal3.setImageResource(R.drawable.medal_3_lighted);
                achievement_img_medal4.setImageResource(R.drawable.medal_4_lighted);
                break;
            case 4:
                achievement_img_medal1.setImageResource(R.drawable.medal_1_lighted);
                achievement_img_medal2.setImageResource(R.drawable.medal_2_lighted);
                achievement_img_medal3.setImageResource(R.drawable.medal_3_lighted);
                achievement_img_medal4.setImageResource(R.drawable.medal_4_lighted);
                achievement_img_medal5.setImageResource(R.drawable.medal_5_lighted);
                break;
            case 5:
                achievement_img_medal1.setImageResource(R.drawable.medal_1_lighted);
                achievement_img_medal2.setImageResource(R.drawable.medal_2_lighted);
                achievement_img_medal3.setImageResource(R.drawable.medal_3_lighted);
                achievement_img_medal4.setImageResource(R.drawable.medal_4_lighted);
                achievement_img_medal5.setImageResource(R.drawable.medal_5_lighted);
                achievement_img_medal6.setImageResource(R.drawable.medal_6_lighted);
                break;
            default:
                break;
        }
    }
}
