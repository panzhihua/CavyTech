
package com.basecore.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;



/**
 * 

  * @ClassName: CustomListView

  * @Description: 自定义ListView,通过重写onMeasure方法,达到对ScrollView适配的效果

  * @author sjchen

  * @date 2014-6-5 上午11:22:08

  *
 */
public class CustomListView extends ListView{

	public CustomListView(Context context) {
		super(context);
	}

	public CustomListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec=MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
	

}
