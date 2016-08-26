package com.cavytech.wear2.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cavytech.wear2.R;
import com.cavytech.wear2.base.AppCompatActivityEx;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class PkHelpActivity extends AppCompatActivityEx {
    @ViewInject(R.id.title)
    private TextView title;

    @ViewInject(R.id.back)
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pk_help);
        x.view().inject(this);
        setToolBar();

        title.setText(R.string.pk);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
