package com.cavytech.wear2.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.basecore.activity.BaseFragmentActivity;
import com.basecore.widget.SystemBarTintManager;
import com.cavytech.wear2.R;

public class CommonActivity extends BaseFragmentActivity {
    public ImageButton back;
    public TextView title, rightText;
    public ImageButton go;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(R.color.com_titlebar);
    }

    public void setStatusBar(int color) {
        // create our manager instance after the content view is set
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(color);
    }

    @Override
    protected void onStart() {
        super.onStart();
     }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void findTitle() {
        back = (ImageButton) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        rightText = (TextView) findViewById(R.id.right_text);
      //  go = (ImageButton) findViewById(R.id.go);
        onListener();
    }
    private void onListener() {

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBack();
            }
        });

        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onRightText();
            }
        });
    }

    public void onClickBack(){
        finish();
    }

    public void onRightText(){

    }
    public void setTitleText(String titleText){
        if(title == null){
            findTitle();
        }
        title.setText(titleText);
    }

    public void setRightTitleText(String titleText){
        if(rightText == null){
            findTitle();
        }
        rightText.setText(titleText);
    }
    public void hideRightTitleText() {
        if (rightText == null) {
            findTitle();
        }
        rightText.setVisibility(View.GONE);
    }
}
