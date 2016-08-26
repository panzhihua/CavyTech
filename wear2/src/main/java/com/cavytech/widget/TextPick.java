package com.cavytech.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import org.xutils.ex.DbException;

import java.util.ArrayList;

/**
 * Created by longjining on 16/4/19.
 */

/**
 *
 * @author ttdevs
 * @version create：2014年8月26日
 */

public class TextPick extends View {

    public interface OnValueChangeListener {
        public void onValueChange(View wheel, float value);
        public void onValueChangeEnd(View wheel, float value) throws DbException;
    }

    private static final int ITEM_ONE_DIVIDER = 10;

    private static final int TEXT_SIZE = 14;

    private float mDensity;
    private int mValue = 0, mMaxValue = 10*12;
    private int  mLineDivider = ITEM_ONE_DIVIDER;

    private int mBeginValue = 0;

    private int mLastX, mMove;
    private int mWidth, mHeight;

    private int mMinVelocity;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    private OnValueChangeListener mListener;

    private ArrayList<String> mText;
    private int mSelTextSize     = TEXT_SIZE;
    private int mUnSelTextSize    = TEXT_SIZE;
    private int mSelTextColor   = Color.argb(255, 255, 255, 255);
    private int mUnSelTextColor   = Color.argb(77, 255, 255, 255);

    TextPaint mTextPaint = null;

    Path mTrianglePath;
    Paint mTrianglePaint;

    private boolean mIsShowTrg = true;

    @SuppressWarnings("deprecation")
    public TextPick(Context context, AttributeSet attrs) {
        super(context, attrs);

        mScroller = new Scroller(getContext());
        mDensity = getContext().getResources().getDisplayMetrics().density;

        mMinVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();
    }

    /**
     *
     *
     *
     * @param text
     *            需要显示的文本
     * @param defultIndex
     *            需要显示的默认文本下标
     *
     */
    public void initViewParam(ArrayList<String> text, int defultIndex) {

        mValue = defultIndex;
        mMaxValue = text.size() - 1;
        mText = text;
        mBeginValue = 0;

        invalidate();

        mLastX = 0;
        mMove = 0;
        notifyValueChange();
    }
    /**
     *
     *
     *
     * @param selTextSize
     *            选定的文本大小
     * @param unSeltextSize
     *            未选定的文本大小
     * @param selTextColor
     *            选定的文本颜色
     * @param unSelTextColor
     *            未选定的文本颜色
     * @param isShowTrg
     *            是否显示三角型
     *
     */
    public void setTextAttrs(int selTextSize,int unSeltextSize, int selTextColor, int unSelTextColor, boolean isShowTrg) {

        mSelTextSize    = selTextSize * (int)mDensity;
        mUnSelTextSize  = unSeltextSize * (int)mDensity;

        mSelTextColor   = selTextColor;
        mUnSelTextColor = unSelTextColor;

        mIsShowTrg = isShowTrg;
    }
    /**
     * 设置用于接收结果的监听器
     *
     * @param listener
     */
    public void setValueChangeListener(OnValueChangeListener listener) {
        mListener = listener;
    }

    /**
     * 获取当前刻度值
     *
     * @return
     */
    public float getValue() {
        return mValue;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mWidth = getWidth();
        mHeight = getHeight();
        mLineDivider = mWidth/3/(int)mDensity;  // 3列居中

        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mText == null || mText.size() == 0){
            return;
        }
        drawScaleLine(canvas);
        drawMiddleLine(canvas);
    }

    String getDrawText(int i, boolean isAdd){
        if(mValue + i > mText.size()){
            return mText.get(0);
        }
        if(isAdd){
            return mText.get(mValue + i);
        }else{
            return mText.get(mValue - i);
        }
    }

    /**
     * 从中间往两边开始画刻度线
     *
     * @param canvas
     */
    private void drawScaleLine(Canvas canvas) {
        canvas.save();

        if(mTextPaint == null){
            mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint.setFakeBoldText(true);
            mTextPaint.setTextAlign(Paint.Align.CENTER);
        }

        int width = mWidth, drawCount = 0;
        float xPosition = 0;

        for (int i = 0; drawCount <= 4 * width; i++) {

            xPosition = (width / 2 - mMove) + i * mLineDivider * mDensity;
            if (xPosition + getPaddingRight() < mWidth) {

                if (mValue + i <= mMaxValue) {
                    if(i == 0){
                        mTextPaint.setColor(mSelTextColor);
                        mTextPaint.setTextSize(mSelTextSize);
                    }else{
                        mTextPaint.setColor(mUnSelTextColor);
                        mTextPaint.setTextSize(mUnSelTextSize);
                    }
                     Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
                    int baseline = (getBottom() + getTop() - fontMetrics.bottom - fontMetrics.top) / 2;

                    canvas.drawText(getDrawText(i, true), xPosition, baseline, mTextPaint);
                }
            }

            xPosition = (width / 2 - mMove) - i * mLineDivider * mDensity;
            if (xPosition > getPaddingLeft()) {
                if (mValue - i >= 0 && i != 0 ) {

                    mTextPaint.setColor(mUnSelTextColor);
                    mTextPaint.setTextSize(mUnSelTextSize);
                    Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
                    int baseline = (getBottom() + getTop() - fontMetrics.bottom - fontMetrics.top) / 2;

                    canvas.drawText(getDrawText(i, false), xPosition, baseline, mTextPaint);
                }
            }
            drawCount += 2 * mLineDivider * mDensity;
        }

        //        canvas.restore();
        canvas.save();
        canvas.restore();
    }

    private void drawTriangle(Canvas canvas){
        if(mTrianglePath == null){
            mTrianglePath = new Path();
            int triangleHight = mHeight / 4;
            mTrianglePath.moveTo(mWidth/2, mHeight - triangleHight);// 此点为多边形的起点
            mTrianglePath.lineTo(mWidth / 2 - triangleHight, mHeight);
            mTrianglePath.lineTo(mWidth/2 + triangleHight, mHeight);
            mTrianglePath.close(); // 使这些点构成封闭的多边形
            mTrianglePaint = new Paint();
            mTrianglePaint.setColor(Color.WHITE);
            mTrianglePaint.setAntiAlias(true);// 设置画笔的锯齿效果
        }

        canvas.drawPath(mTrianglePath, mTrianglePaint);
    }
    /**
     * 画中间的红色指示线、阴影等。指示线两端简单的用了两个矩形代替
     *
     * @param canvas
     */
    private void drawMiddleLine(Canvas canvas) {
        if(mIsShowTrg){
            drawTriangle(canvas);
        }
//        canvas.restore();
        canvas.save();
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int xPosition = (int) event.getX();

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:

                mScroller.forceFinished(true);

                mLastX = xPosition;
                mMove = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                mMove += (mLastX - xPosition);
                try {
                    changeMoveAndValue();
                } catch (DbException e) {
                    e.printStackTrace();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                countMoveEnd();
                try {
                    countVelocityTracker(event);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                return false;
            // break;
            default:
                break;
        }

        mLastX = xPosition;
        return true;
    }

    private void countVelocityTracker(MotionEvent event) throws DbException {
        mVelocityTracker.computeCurrentVelocity(1000);
        float xVelocity = mVelocityTracker.getXVelocity();
        if (Math.abs(xVelocity) > mMinVelocity) {
            mScroller.fling(0, 0, (int) xVelocity, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
        }else{
            if (null != mListener) {
                mListener.onValueChangeEnd(this, mValue);
            }
        }
    }

    private void changeMoveAndValue() throws DbException {
        int tValue = (int) (mMove / (mLineDivider * mDensity));
        if (Math.abs(tValue) > 0) {
            mValue += tValue;
            mMove -= tValue * mLineDivider * mDensity;
            if (mValue <= 0 || mValue > mMaxValue) {
                mValue = mValue <= 0 ? 0 : mMaxValue;
                mMove = 0;
                mScroller.forceFinished(true);

                if (null != mListener) {
                    mListener.onValueChangeEnd(this, mValue);
                }
            }
            notifyValueChange();
        }
        postInvalidate();
    }

    private void countMoveEnd() {
        int roundMove = Math.round(mMove / (mLineDivider * mDensity));
        mValue = mValue + roundMove;
        mValue = mValue <= 0 ? 0 : mValue;
        mValue = mValue > mMaxValue ? mMaxValue : mValue;

        mLastX = 0;
        mMove = 0;

        notifyValueChange();
        postInvalidate();
    }

    private void notifyValueChange() {
        if (null != mListener) {
            mListener.onValueChange(this, mValue);
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            if (mScroller.getCurrX() == mScroller.getFinalX()) { // over
                countMoveEnd();
                if (null != mListener) {
                    try {
                        mListener.onValueChangeEnd(this, mValue);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                int xPosition = mScroller.getCurrX();
                mMove += (mLastX - xPosition);
                try {
                    changeMoveAndValue();
                } catch (DbException e) {
                    e.printStackTrace();
                }
                mLastX = xPosition;
            }
        }
    }
}