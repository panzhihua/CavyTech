package com.cavytech.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.cavytech.wear2.R;

/**
 * Created by doudou on 16/7/18.
 */
public class WaitProgress extends Dialog{

    private Context context = null;
    private static WaitProgress customProgressDialog = null;

    public WaitProgress(Context context){
        super(context);
        this.context = context;
    }

    public WaitProgress(Context context, int theme) {
        super(context, theme);
    }

    public static WaitProgress createDialog(Context context){
        customProgressDialog = new WaitProgress(context,R.style.WaitProgress);
        customProgressDialog.setContentView(R.layout.waitprogress);
        customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;

        return customProgressDialog;
    }

    public void onWindowFocusChanged(boolean hasFocus){

        if (customProgressDialog == null){
            return;
        }

        ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
    }

    /**
     *
     * [Summary]
     *       setTitile 标题
     * @param strTitle
     * @return
     *
     */
    public WaitProgress setTitile(String strTitle){
        return customProgressDialog;
    }

    /**
     *
     * [Summary]
     *       setMessage 提示内容
     * @param strMessage
     * @return
     *
     */
    public WaitProgress setMessage(String strMessage){
        TextView tvMsg = (TextView)customProgressDialog.findViewById(R.id.id_tv_loadingmsg);

        if (tvMsg != null){
            tvMsg.setText(strMessage);
        }

        return customProgressDialog;
    }
}
