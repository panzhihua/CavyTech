package com.cavytech.wear2.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.cavytech.wear2.R;
import com.cavytech.wear2.entity.UserEntity;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.RequestCallback;
import com.cavytech.wear2.util.CacheUtils;
import com.cavytech.wear2.util.Constants;
import com.cavytech.wear2.util.SerializeUtils;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class AchieveActivity extends AppCompatActivity {
    @ViewInject(R.id.medal_1)
    private ImageView medal_1;

    @ViewInject(R.id.medal_2)
    private ImageView medal_2;

    @ViewInject(R.id.medal_3)
    private ImageView medal_3;

    @ViewInject(R.id.medal_4)
    private ImageView medal_4;

    @ViewInject(R.id.medal_5)
    private ImageView medal_5;

    @ViewInject(R.id.medal_6)
    private ImageView medal_6;

    @ViewInject(R.id.tv_steps)
    private TextView tv_steps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achieve);
        x.view().inject(this);

        int achieveSize = CacheUtils.getInt(AchieveActivity.this,"achieveSize");

        setAchieveView(achieveSize);

        UserEntity.ProfileEntity unserialize = (UserEntity.ProfileEntity) SerializeUtils.unserialize(Constants.SERIALIZE_USERINFO);

        tv_steps.setText("累计"+unserialize.getSteps()+"步");

        HttpUtils.getInstance().getUserInfo(AchieveActivity.this, new RequestCallback<UserEntity>() {
            @Override
            public void onError(Request request, Exception e) {
                try {
                    JSONObject jsonObj = new JSONObject(e.getLocalizedMessage());
                    int code = jsonObj.optInt("code");
                    if(HttpUtils.CODE_ACCOUNT_NOT_LOGIN==code){
                        AlertDialog.Builder dialog = new AlertDialog.Builder(AchieveActivity.this);
                        dialog.setCancelable(false);
                        dialog.setMessage(R.string.not_login);
                        dialog.setPositiveButton(getString(R.string.ALERT_DLG_BTN_OK), new android.content.DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.dismiss();
                                startActivity(new Intent(AchieveActivity.this,LoginActivity.class));
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
                int steps = response.getProfile().getSteps();

                tv_steps.setText("累计"+steps+"步");
            }
        });

    }

    /**
     * 设置成就图标
     *
     * @param size
     */
    private void setAchieveView(int size) {
        if (size == 1) {
            medal_1.setBackgroundResource(R.drawable.medal_1_lighted);
        } else if (size == 2) {
            medal_1.setBackgroundResource(R.drawable.medal_1_lighted);
            medal_2.setBackgroundResource(R.drawable.medal_2_lighted);
        } else if (size == 3) {
            medal_1.setBackgroundResource(R.drawable.medal_1_lighted);
            medal_2.setBackgroundResource(R.drawable.medal_2_lighted);
            medal_3.setBackgroundResource(R.drawable.medal_3_lighted);
        } else if (size == 4) {
            medal_1.setBackgroundResource(R.drawable.medal_1_lighted);
            medal_2.setBackgroundResource(R.drawable.medal_2_lighted);
            medal_3.setBackgroundResource(R.drawable.medal_3_lighted);
            medal_4.setBackgroundResource(R.drawable.medal_4_lighted);
        } else if (size == 5) {
            medal_1.setBackgroundResource(R.drawable.medal_1_lighted);
            medal_2.setBackgroundResource(R.drawable.medal_2_lighted);
            medal_3.setBackgroundResource(R.drawable.medal_3_lighted);
            medal_4.setBackgroundResource(R.drawable.medal_4_lighted);
            medal_5.setBackgroundResource(R.drawable.medal_5_lighted);
        } else if (size == 6) {
            medal_1.setBackgroundResource(R.drawable.medal_1_lighted);
            medal_2.setBackgroundResource(R.drawable.medal_2_lighted);
            medal_3.setBackgroundResource(R.drawable.medal_3_lighted);
            medal_4.setBackgroundResource(R.drawable.medal_4_lighted);
            medal_5.setBackgroundResource(R.drawable.medal_5_lighted);
            medal_6.setBackgroundResource(R.drawable.medal_6_lighted);
        }
    }
}
