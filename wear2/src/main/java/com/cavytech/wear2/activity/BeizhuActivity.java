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

import com.cavytech.wear2.R;
import com.cavytech.wear2.base.AppCompatActivityEx;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.RequestCallback;
import com.squareup.okhttp.Request;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by hf on 2016/5/25.
 * 修改好友备注
 */
public class BeizhuActivity extends AppCompatActivityEx {


    private static final int EXBEIZHU = 1;
    @ViewInject(R.id.back)
    private ImageView back;

    @ViewInject(R.id.title)
    private TextView title;

    @ViewInject(R.id.et_send_test)
    private EditText et_send_test;

    @ViewInject(R.id.send_test)
    private Button send_test;
    private String friendId;
    private String remarkid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beizhu);

        x.view().inject(this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        title.setText("备注");

        Intent intent = getIntent();
        friendId = intent.getStringExtra("friendId");

        send_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remarkid = et_send_test.getText().toString();
                if (remarkid.equals("")) {
                    Toast.makeText(BeizhuActivity.this, "备注名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    post2remark();
                }
            }
        });

    }

    private void post2remark() {

        Log.e("TAG", "好友id----" + friendId + "备注信息-----" + remarkid);
        HttpUtils.getInstance().setFriendRemark(friendId, remarkid, new RequestCallback<Object>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(Object response) {
                Log.e("TAG", "修改好友备注----" + response.toString());
                Intent intent = new Intent();
                intent.putExtra("beizhu",remarkid);
                setResult(EXBEIZHU,intent);
                finish();
            }
        });
    }
}
