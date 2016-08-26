package com.tunshu.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.tunshu.R;

/**
 * author：ping on 2015/8/19 15:48
 * email：912036793@qq.com
 */
public class ShareDialog extends Dialog {
    int y=0;
    IShareThing iShareThing;
    Button shareMomentsBtn;
    Button shareFriednsBtn;
    public ShareDialog(Context context) {
        super(context, 0);
    }

    public ShareDialog(Context context, int style,int screenY) {
        super(context, style);
        y = screenY;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_share);
        init();
        setListener();
        initWindow();
    }

    private void init(){
        shareMomentsBtn = (Button)findViewById(R.id.dialog_share_moments_btn);
        shareFriednsBtn = (Button)findViewById(R.id.dialog_share_friends_btn);
    }
    private void setListener(){
        shareMomentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iShareThing.shareDetails(0,"WechatMoments");
                dismiss();
            }
        });
        shareFriednsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iShareThing.shareDetails(1,"Wechat");
                dismiss();
            }
        });
    }

    private void initWindow() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.y = y;

        params.gravity = Gravity.TOP|Gravity.RIGHT;
        getWindow().setAttributes(params);
    }

    public void setViewListener( IShareThing iShareThing){
        this.iShareThing =iShareThing;
    }

    public interface IShareThing{
        public void shareDetails(int index,String platform);
    }

}
