package com.cavytech.wear2.activity;

import com.cavytech.wear2.R;
import com.cavytech.widget.TuneWheel;
import com.cavytech.widget.TuneWheel.OnValueChangeListener;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.OutputStream;
import java.io.DataOutputStream;

public class TestActivity extends Activity implements OnValueChangeListener {

	LinearLayout layout;
	RelativeLayout root;
	HorizontalScrollView scroll;
	TextView indicator;
    TuneWheel wheel;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sex);
      //  setContentView(R.layout.activity_test);

        /*
        indicator = (TextView)findViewById(R.id.indicator);
        wheel= (TuneWheel)findViewById(R.id.ruler);

        wheel.setValueChangeListener(this);*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onValueChange(View wheel, float value) {
        int year = 1970 +  (int)value/12;
        int mouth = (int)value%12 + 1;

        indicator.setText(String.format("%d.%d", year, mouth));
        execShellCmd("input keyevent 3");//home
    }

    public void onValueChangeEnd(View wheel, float value){

    }
    /**
     * 执行shell命令
     *
     * @param cmd
     */
    private void execShellCmd(String cmd) {

        try {
            // 申请获取root权限，这一步很重要，不然会没有作用
            Process process = Runtime.getRuntime().exec("su");
            // 获取输出流
            OutputStream outputStream = process.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(
                    outputStream);
            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
