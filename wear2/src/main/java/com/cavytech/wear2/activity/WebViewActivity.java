package com.cavytech.wear2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.cavytech.wear2.R;
import com.cavytech.wear2.base.AppCompatActivityEx;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by hf on 2016/6/21.
 * 相关APP的 H5
 */
public class WebViewActivity extends AppCompatActivityEx {

    @ViewInject(R.id.title)
    private TextView title;

    @ViewInject(R.id.webView)
    private WebView webView;

    @ViewInject(R.id.back)
    private ImageView back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.other_webview);
        x.view().inject(this);
        setToolBar();
        title.setText(R.string.relatve_App);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent i = getIntent();
        String url = i.getStringExtra("web");
        Log.e("TAG","H5的测试----"+url);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBlockNetworkImage(false);
        webView.getSettings().setSupportZoom(true);

        webView.loadUrl(url);

    }
}
