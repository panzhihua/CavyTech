package com.cavytech.wear2.slidingmenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cavytech.wear2.R;
import com.cavytech.wear2.activity.InvitationDuelActivity;
import com.cavytech.wear2.base.AppCompatActivityEx;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 第一次进入PK  展示
 */
public class PkActivity extends AppCompatActivityEx {
    @ViewInject(R.id.back)
    private ImageView back;

    @ViewInject(R.id.iv_pk_next)
    private ImageView iv_pk_next;

    @ViewInject(R.id.title)
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pk);
        x.view().inject(this);

        back.setOnClickListener(new MyonClickListener());
        iv_pk_next.setOnClickListener(new MyonClickListener());
        title.setText(R.string.pk_first);
    }

    class MyonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(v == back){
                finish();
            }else if(v == iv_pk_next){

                startActivity(new Intent(PkActivity.this,InvitationDuelActivity.class));
                finish();
            }
        }
    }
}
