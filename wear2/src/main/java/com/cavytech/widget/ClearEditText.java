package com.cavytech.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.cavytech.wear2.R;

/**
 * Created by ShadowNight on 2015/9/24.
 */
public class ClearEditText extends EditText implements TextWatcher,
        View.OnFocusChangeListener {
    private Paint mPaint;
    private int color;
    public static final int STATUS_FOCUSED = 1;
    public static final int STATUS_UNFOCUSED = 2;
    public static final int STATUS_ERROR = 3;
    private int status = 2;
    private Drawable del_btn;
    private Drawable del_btn_down;
    //    private int focusedDrawableId = R.drawable.user_select;// 默认的
//    private int unfocusedDrawableId = R.drawable.user;
//    private int errorDrawableId = R.drawable.user_error;
    Drawable left = null;
    private Context mContext;
    /**
     * 是否获取焦点，默认没有焦点
     */
    private boolean hasFocus = false;
    /**
     * 手指抬起时的X坐标
     */
    private int xUp = 0;

    public ClearEditText(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStryle) {
        super(context, attrs, defStryle);
        mContext = context;
//        TypedArray a = context.obtainStyledAttributes(attrs,
//                R.styleable.lineEdittext, defStryle, 0);
//        focusedDrawableId = a.getResourceId(
//                R.styleable.lineEdittext_drawableFocus, R.drawable.user_select);
//        unfocusedDrawableId = a.getResourceId(
//                R.styleable.lineEdittext_drawableUnFocus, R.drawable.user);
//        errorDrawableId = a.getResourceId(
//                R.styleable.lineEdittext_drawableError, R.drawable.user_error);
//        a.recycle();
        init();
    }

    /**
     * 2014/7/31
     *
     * @author Aimee.ZHANG
     */
    private void init() {
        mPaint = new Paint();
        // mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(3.0f);
        color = Color.parseColor("#bfbfbf");
        setStatus(status);
        del_btn = mContext.getResources().getDrawable(R.drawable.icon_wrong);
        del_btn_down = mContext.getResources().getDrawable(R.drawable.icon_wrong);
        addListeners();
        setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
    }

    // 处理删除事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (del_btn != null && event.getAction() == MotionEvent.ACTION_UP) {
            // 获取点击时手指抬起的X坐标
            xUp = (int) event.getX();
            // 当点击的坐标到当前输入框右侧的距离小于等于getCompoundPaddingRight()的距离时，则认为是点击了删除图标
            if ((getWidth() - xUp) <= getCompoundPaddingRight()) {
                if (!TextUtils.isEmpty(getText().toString())) {
                    setText("");
                    setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
                }
            }
        } else if (del_btn != null && event.getAction() == MotionEvent.ACTION_DOWN && getText().length() != 0) {
            setCompoundDrawablesWithIntrinsicBounds(left, null, del_btn_down, null);
        } else if (getText().length() != 0) {
            setCompoundDrawablesWithIntrinsicBounds(left, null, del_btn, null);
        }
        return super.onTouchEvent(event);
    }

    public void setStatus(int status) {
        this.status = status;

//        if (status == STATUS_ERROR) {
//            try {
//                left = getResources().getDrawable(errorDrawableId);
//            } catch (Resources.NotFoundException e) {
//                e.printStackTrace();
//            }
//            setColor(Color.parseColor("#f57272"));
//        } else if (status == STATUS_FOCUSED) {
//            try {
//                left = getResources().getDrawable(focusedDrawableId);
//            } catch (Resources.NotFoundException e) {
//                e.printStackTrace();
//            }
//            setColor(Color.parseColor("#5e99f3"));
//        } else {
//            try {
//                left = getResources().getDrawable(unfocusedDrawableId);
//            } catch (Resources.NotFoundException e) {
//                e.printStackTrace();
//            }
//            setColor(Color.parseColor("#bfbfbf"));
//        }
        if (left != null) {
//          left.setBounds(0, 0, 30, 40);
//          this.setCompoundDrawables(left, null, null, null);
            setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
        }
        postInvalidate();
    }

    public void setLeftDrawable(int focusedDrawableId, int unfocusedDrawableId,
                                int errorDrawableId) {
//        this.focusedDrawableId = focusedDrawableId;
//        this.unfocusedDrawableId = unfocusedDrawableId;
//        this.errorDrawableId = errorDrawableId;
        setStatus(status);
    }

    private void addListeners() {
        try {
            setOnFocusChangeListener(this);
            addTextChangedListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        this.hasFocus = focused;
        if (focused) {
            setStatus(STATUS_FOCUSED);
        } else {
            setStatus(STATUS_UNFOCUSED);
//            setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    ;

    public void setColor(int color) {
        this.color = color;
        this.setTextColor(color);
        invalidate();
    }

    @Override
    public void afterTextChanged(Editable arg0) {
        postInvalidate();
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        if (TextUtils.isEmpty(arg0)) {
            // 如果为空，则不显示删除图标
            setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
        } else {
            // 如果非空，则要显示删除图标
            setCompoundDrawablesWithIntrinsicBounds(left, null, del_btn, null);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int after) {
        if (hasFocus) {
            if (TextUtils.isEmpty(s)) {
                // 如果为空，则不显示删除图标
                setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
            } else {
                // 如果非空，则要显示删除图标
                setCompoundDrawablesWithIntrinsicBounds(left, null, del_btn, null);
            }
        }
    }

    @Override
    public void onFocusChange(View arg0, boolean arg1) {
        try {
            this.hasFocus = arg1;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
