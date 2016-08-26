package com.basecore.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.basecore.R;
import com.basecore.util.core.ScreenUtil;

/**
 * 
 * CustomToast:[自定义Toast].
 * 
 * @author sjchen
 * @Email:sjchen@wisedu.com
 * @version [MCPClient_Wisedu , 2012-6-7 下午03:46:03]
 */

public class CustomToast extends Toast {
	private static Toast toast;

	public CustomToast(Context context) {
		super(context);
	}

	public static void showToast(Context context, int resStringParam) {
		try {
			showToast(context, context.getString(resStringParam));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showToast(Context context, String stringParam) {
		try {
			showToast(context, stringParam, Gravity.BOTTOM, Toast.LENGTH_SHORT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showToast(Context context, String toastStr, int gravity,
			int duration) {
		LinearLayout toastLayout = new LinearLayout(context);
		TextView textView = new TextView(context);
		textView.setMinimumHeight(ScreenUtil.dip2px(context, 40));
		textView.setMinWidth(ScreenUtil.dip2px(context, 100));
		textView.setMaxWidth(ScreenUtil.dip2px(context, 200));
		textView.setGravity(Gravity.CENTER);
		textView.setPadding(ScreenUtil.dip2px(context, 15),
				ScreenUtil.dip2px(context, 5), ScreenUtil.dip2px(context, 15),
				ScreenUtil.dip2px(context, 5));
		textView.setTextColor(Color.parseColor("#ffffff"));
		textView.setTextSize(16);
		textView.setText(Html.fromHtml(toastStr));
		toastLayout.addView(textView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		toastLayout.setBackgroundResource(R.drawable.circle_bg_hint);
//		toastLayout.setBackgroundResource(R.drawable.toast_frame);

		if(toast==null){
			toast = new Toast(context);
		}
		switch (gravity) {// 设置Toast在页面中的显示位置
		case Gravity.CENTER_VERTICAL:
			toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			break;
		case Gravity.BOTTOM:
			toast.setGravity(Gravity.BOTTOM, 0, ScreenUtil.dip2px(context, 100));
			break;
		case Gravity.TOP:
			toast.setGravity(Gravity.TOP, 0, 0);
			break;
		case Gravity.CENTER:
			toast.setGravity(Gravity.CENTER, 0, 0);
			break;
		}
		if (duration == Toast.LENGTH_LONG) {
			toast.setDuration(Toast.LENGTH_LONG);
		} else {
			toast.setDuration(Toast.LENGTH_SHORT);
		}
		toast.setView(toastLayout);
		if (toastStr.length() > 0) {
			toast.show();
		}
	}
	public static void showToast(Context context, String toastStr, int textGravity,int gravity, int duration) {
		LinearLayout toastLayout = new LinearLayout(context);
		TextView textView = new TextView(context);
		textView.setMinimumHeight(ScreenUtil.dip2px(context, 40));
		textView.setMinWidth(ScreenUtil.dip2px(context, 100));
		textView.setMaxWidth(ScreenUtil.dip2px(context, 400));
		switch (gravity) {// 设置Toast在页面中的显示位置
		case Gravity.LEFT:
			textView.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
			break;
		case Gravity.RIGHT:
			textView.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
			break;
		case Gravity.TOP:
			textView.setGravity(Gravity.TOP);
			break;
		case Gravity.CENTER:
			textView.setGravity(Gravity.CENTER);
			break;
		}
		
		textView.setPadding(ScreenUtil.dip2px(context, 15),
				ScreenUtil.dip2px(context, 5), ScreenUtil.dip2px(context, 15),
				ScreenUtil.dip2px(context, 5));
		textView.setTextColor(Color.parseColor("#ffffff"));
		textView.setTextSize(14);
		textView.setText(Html.fromHtml(toastStr));
		toastLayout.addView(textView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		toastLayout.setBackgroundResource(R.drawable.circle_bg_hint);
//		toastLayout.setBackgroundResource(R.drawable.toast_frame);

		if(toast==null){
			toast = new Toast(context);
		}
		switch (gravity) {// 设置Toast在页面中的显示位置
		case Gravity.CENTER_VERTICAL:
			toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			break;
		case Gravity.BOTTOM:
			toast.setGravity(Gravity.BOTTOM, 0, ScreenUtil.dip2px(context, 100));
			break;
		case Gravity.TOP:
			toast.setGravity(Gravity.TOP, 0, 0);
			break;
		case Gravity.CENTER:
			toast.setGravity(Gravity.CENTER, 0, 0);
			break;
		}
		if (duration == Toast.LENGTH_LONG) {
			toast.setDuration(Toast.LENGTH_LONG);
		} else {
			toast.setDuration(Toast.LENGTH_SHORT);
		}
		toast.setView(toastLayout);
		if (toastStr.length() > 0) {
			toast.show();
		}
	}

}
