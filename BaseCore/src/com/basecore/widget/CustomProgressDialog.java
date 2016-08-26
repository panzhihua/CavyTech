
package com.basecore.widget;





import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.basecore.R;

/**
 * @author gqs
 * @version 创建时间：2012-11-23 上午10:59:43
 * 类说明
 */
public class CustomProgressDialog  extends ProgressDialog{

	public CustomProgressDialog(Context context, int theme) {
		super(context, theme);
		// TODO dvsdfads
	}

	public CustomProgressDialog(Context context) {
		super(context);
		// TODO sdfsdf
		
	}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	
//		progressDialog.setIndeterminate(true);

		//progressDialog.show()
		setContentView(R.layout.progressdialog);
		
    }
    public void showDialog()
    {
    	show();
    }
}