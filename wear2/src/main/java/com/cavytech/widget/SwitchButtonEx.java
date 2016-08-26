package com.cavytech.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.CheckBox;

import com.basecore.widget.switchbutton.FrameAnimationController;
import com.cavytech.wear2.R;


/**
 * Created by longjining on 16/4/12.
 */
public class SwitchButtonEx extends CheckBox implements View.OnClickListener{

    protected Paint mPaint;

    protected Bitmap mBottom;

    protected Bitmap mCurBtnPic;

    private Bitmap mBtnCheck;

    private Bitmap mBtnUnCheck;

    private float mBtnWidth;

    protected float mBtnPos; // 开关的绘制位置

    protected float mBtnOnPos; // 开关打开的位置

    protected float mBtnOffPos; // 开关关闭的位置

    protected boolean mChecked = false;

    protected boolean mBroadcasting;

    protected PerformClick mPerformClick;

    protected OnCheckedChangeListener mOnCheckedChangeListener;

    protected boolean mAnimating;

    protected float mAnimationPosition;

    protected float mAnimatedVelocity;

    protected float mVelocity;

    protected int mClickTimeout;

    protected float mRealPos; // 图片的绘制位置

    public SwitchButtonEx(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.checkboxStyle);
    }

    public SwitchButtonEx(Context context) {
        this(context, null);
    }
    public SwitchButtonEx(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initView(context);
    }

    private void initView(Context context) {
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        Resources resources = context.getResources();

        // get viewConfiguration
        mClickTimeout = ViewConfiguration.getPressedStateDuration()
                + ViewConfiguration.getTapTimeout();

        // get Bitmap
        mBottom = BitmapFactory.decodeResource(resources, R.drawable.btn_line);
        mBtnCheck = BitmapFactory.decodeResource(resources, R.drawable.btn_open);
        mBtnUnCheck = BitmapFactory.decodeResource(resources, R.drawable.btn_normal);

        mCurBtnPic = mBtnUnCheck;
        mBtnWidth  = mBtnCheck.getWidth();

        mBtnOffPos = 0;
        mBtnOnPos = mBtnWidth;

        mBtnPos = mChecked ? mBtnOnPos : mBtnOffPos;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        /*
        canvas.saveLayerAlpha(mSaveLayerRectF, mAlpha, Canvas.MATRIX_SAVE_FLAG
                | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
                | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG);
*/
        // 绘制底部图片
        canvas.drawBitmap(mBottom, 0, -mBtnWidth/2, mPaint);
       // mPaint.setXfermode(null);

        // 绘制按钮
        canvas.drawBitmap(mCurBtnPic, mBtnPos, 0, mPaint);
        //        canvas.restore();
        canvas.save();
        canvas.restore();
    }
    public boolean isChecked() {
        return mChecked;
    }

    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int) mBtnWidth * 2, (int) mBtnWidth);
    }


    /**
     * <p>
     * Changes the checked state of this button.
     * </p>
     *
     * @param checked true to check the button, false to uncheck it
     */
    public void setChecked(boolean checked) {

        if (mChecked != checked) {
            mChecked = checked;

            mBtnPos = checked ? mBtnOnPos : mBtnOffPos;
            mCurBtnPic = checked ? mBtnCheck : mBtnUnCheck;
            invalidate();

            // Avoid infinite recursions if setChecked() is called from a
            // listener
            if (mBroadcasting) {
                return;
            }

            mBroadcasting = true;
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(SwitchButtonEx.this, mChecked);
            }

            mBroadcasting = false;
        }
    }

    @Override
    public void onClick(View v) {

    }

    private final class PerformClick implements Runnable {
        public void run() {
            performClick();
        }
    }

    @Override
    public boolean performClick() {
        startAnimation(!mChecked);
        return true;
    }

    private void startAnimation(boolean turnOn) {
        mAnimating = true;
        mAnimatedVelocity = turnOn ? -mVelocity : mVelocity;
        mAnimationPosition = mBtnPos;

        new SwitchAnimation().run();
    }

    private void stopAnimation() {
        mAnimating = false;
    }

    private final class SwitchAnimation implements Runnable {

        @Override
        public void run() {
            if (!mAnimating) {
                return;
            }
            doAnimation();
            FrameAnimationController.requestAnimationFrame(this);
        }
    }

    private void doAnimation() {
        mAnimationPosition += mAnimatedVelocity * FrameAnimationController.ANIMATION_FRAME_DURATION
                / 1000;
        if (mAnimationPosition <= mBtnOffPos) {
            stopAnimation();
            mAnimationPosition = mBtnOffPos;
            setCheckedDelayed(true);
        } else if (mAnimationPosition >= mBtnOnPos) {
            stopAnimation();
            mAnimationPosition = mBtnOnPos;
            setCheckedDelayed(false);
        }
        moveView(mAnimationPosition);
    }

    private void moveView(float position) {
        mBtnPos = position;
        invalidate();
    }

    /**
     * 内部调用此方法设置checked状态，此方法会延迟执行各种回调函数，保证动画的流畅度
     *
     * @param checked
     */
    private void setCheckedDelayed(final boolean checked) {
        this.postDelayed(new Runnable() {

            @Override
            public void run() {
                setChecked(checked);
            }
        }, 10);
    }

    /**
     * Register a callback to be invoked when the checked state of this button
     * changes.
     *
     * @param listener the callback to call on checked state change
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:
                float time = event.getEventTime() - event.getDownTime();

                break;
            case MotionEvent.ACTION_UP:

                time = event.getEventTime() - event.getDownTime();
                if (time < mClickTimeout) {
                    if (mPerformClick == null) {
                        mPerformClick = new PerformClick();
                    }
                    if (!post(mPerformClick)) {
                        performClick();
                    }
                } else {
                    startAnimation(!mChecked);
                }
                break;
        }

        invalidate();
        return isEnabled();
    }
}
