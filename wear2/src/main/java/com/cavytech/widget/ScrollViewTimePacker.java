package com.cavytech.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by libin on 4/18 0018.
 * 邮箱：bin.li@tunshu.com
 */
public class ScrollViewTimePacker extends TimePicker {
    public ScrollViewTimePacker(Context context) {
        super(context);

        setNumberPickerTextColor(this);
    }

    public ScrollViewTimePacker(Context context, AttributeSet attrs) {
        super(context, attrs);

        setNumberPickerTextColor(this);
    }

    public ScrollViewTimePacker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            ViewParent p = getParent();
            if (p != null)
                p.requestDisallowInterceptTouchEvent(true);
        }

        return false;
    }

    private List<NumberPicker> findNumberPicker(ViewGroup viewGroup) {
        List<NumberPicker> npList = new ArrayList<NumberPicker>();
        View child = null;

        if (null != viewGroup) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                child = viewGroup.getChildAt(i);
                if (child instanceof NumberPicker) {
                    npList.add((NumberPicker) child);
                } else if (child instanceof LinearLayout) {
                    List<NumberPicker> result = findNumberPicker((ViewGroup) child);
                    if (result.size() > 0) {
                        return result;
                    }
                }
            }
        }

        return npList;
    }

    private void setNumberPickerTextColor(ViewGroup viewGroup) {
        List<NumberPicker> npList = findNumberPicker(viewGroup);
        if (null != npList) {
            for (NumberPicker np : npList) {
                setNumberPickerTextColor(np, Color.BLACK);
            }
        }
    }

    public static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                try {
                    Field selectorWheelPaintField = numberPicker.getClass().getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText) child).setTextColor(color);

                    numberPicker.invalidate();
                    return true;
                } catch (NoSuchFieldException e) {
                    Log.w("setNumberPicker", e);
                } catch (IllegalAccessException e) {
                    Log.w("setNumberPicker", e);
                } catch (IllegalArgumentException e) {
                    Log.w("setNumberPicker", e);
                }
            }
        }
        return false;
    }
}