package com.cavytech.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import com.cavytech.wear2.R;

/**
 *
 * @author ttdevs
 * @version create：2014年8月26日
 */
@SuppressLint("ClickableViewAccessibility")
public class TuneWheelVertical extends View {

	public interface OnValueChangeListener {
		public void onValueChange(View wheel, float value);
		public void onValueChangeEnd(View wheel, float value);
	}

	public static final int MOD_TYPE_ONE = 10;

	private static final int ITEM_ONE_DIVIDER = 10;

	private static final int ITEM_MAX_HEIGHT = 30;
	private static final int ITEM_MIN_HEIGHT = 16;

	private static final int TEXT_SIZE = 14;

	private static final int MIDLINE_WIDTH = 2;

	private float mDensity;
	private int mValue = 0, mMaxValue = 10*12;

	private int mBeginValue = 0;

	private int mLastX, mMove;
	private int mWidth, mHeight;

	private int mMinVelocity;
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;

	private OnValueChangeListener mListener;

	@SuppressWarnings("deprecation")
	public TuneWheelVertical(Context context, AttributeSet attrs) {
		super(context, attrs);

		mScroller = new Scroller(getContext());
		mDensity = getContext().getResources().getDisplayMetrics().density;

		mMinVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();

		// setBackgroundResource(R.drawable.bg_wheel);
		//setBackgroundDrawable(createBackground());
	}

	private GradientDrawable createBackground() {
		float strokeWidth = 4 * mDensity; // 边框宽度
		float roundRadius = 6 * mDensity; // 圆角半径
		int strokeColor = Color.parseColor("#FF666666");// 边框颜色
		// int fillColor = Color.parseColor("#DFDFE0");// 内部填充颜色

		setPadding((int)strokeWidth, (int)strokeWidth, (int)strokeWidth, 0);

		int colors[] = { 0xFF999999, 0xFFFFFFFF, 0xFF999999 };// 分别为开始颜色，中间夜色，结束颜色
		GradientDrawable bgDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);// 创建drawable
		// bgDrawable.setColor(fillColor);
		bgDrawable.setCornerRadius(roundRadius);
		bgDrawable.setStroke((int)strokeWidth, strokeColor);
		// setBackgroundDrawable(gd);
		return bgDrawable;
	}

	public void initViewParam(int beginValue, int defaultValue, int maxValue) {

		mValue = defaultValue - beginValue;
		mMaxValue = maxValue - beginValue;
		mBeginValue = beginValue;

		invalidate();

		mLastX = 0;
		mMove = 0;
		notifyValueChange();
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
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		drawScaleLine(canvas);
		// drawWheel(canvas);
		drawMiddleLine(canvas);

	}

	private void drawWheel(Canvas canvas) {
	//	Drawable wheel = getResources().getDrawable(R.drawable.bg_wheel);
	//	wheel.setBounds(0, 0, getWidth(), getHeight());
	//	wheel.draw(canvas);
	}

	String getDrawText(int i, boolean isAdd){

		if(isAdd){
			return String.valueOf((mValue + i) + mBeginValue);
		}else{
			return String.valueOf((mValue - i) + mBeginValue);
		}
	}
	/**
	 * 从中间往两边开始画刻度线
	 *
	 * @param canvas
	 */
	private void drawScaleLine(Canvas canvas) {
		canvas.save();

		Paint linePaint = new Paint();
		linePaint.setStrokeWidth(2);
		linePaint.setColor(Color.LTGRAY);

		TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setTextSize(TEXT_SIZE * mDensity);

		textPaint.setColor(Color.argb(204,0,0,0));

		int height = mHeight, drawCount = 0;
		float textWidth = Layout.getDesiredWidth("0", textPaint);
		float startX, startY, stopX, stopY;

		for (int i = 0; drawCount <= 4 * height; i++) {
			int numSize = String.valueOf(mValue + i + mBeginValue).length();

			startY = stopY = (height / 2 - mMove) + i * ITEM_ONE_DIVIDER * mDensity;

			if (startY + getPaddingTop() < mHeight) {
				if ((mValue + i) % MOD_TYPE_ONE == 0) {

					if (mValue + i <= mMaxValue) {
						startX = getWidth() - mDensity * ITEM_MAX_HEIGHT;
						stopX  = getWidth();

						canvas.drawLine(startX, startY, stopX, stopY, linePaint);
						canvas.drawText(getDrawText(i, true), 0, startY + textWidth / 2, textPaint);
					}
				} else {
					if (mValue + i <= mMaxValue) {
						startX = getWidth() - mDensity * ITEM_MIN_HEIGHT;
						stopX  = getWidth();

						canvas.drawLine(startX, startY, stopX, stopY, linePaint);
					}
				}
			}

			startY = stopY = (height / 2 - mMove) - i * ITEM_ONE_DIVIDER * mDensity;
			if (startY > getPaddingBottom()) {
				if ((mValue - i) % MOD_TYPE_ONE == 0) {

					if (mValue - i >= 0) {
						startX = getWidth() - mDensity * ITEM_MAX_HEIGHT;
						stopX  = getWidth();

						canvas.drawLine(startX, startY, stopX, stopY, linePaint);
						canvas.drawText(getDrawText(i, false), 0, startY + textWidth / 2, textPaint);
					}
				} else {
					if (mValue - i >= 0) {
						startX = getWidth() - mDensity * ITEM_MIN_HEIGHT;
						stopX  = getWidth();

						canvas.drawLine(startX, startY, stopX, stopY, linePaint);
					}
				}
			}
			drawCount += 2 * ITEM_ONE_DIVIDER * mDensity;
		}

		//        canvas.restore();
		canvas.save();
		canvas.restore();
	}

	/**
	 * 计算没有数字显示位置的辅助方法
	 *
	 * @param value
	 * @param xPosition
	 * @param textWidth
	 * @return
	 */
	private float countLeftStart(long value, float xPosition, float textWidth) {
		float xp = 0f;
		if (value < 20) {
			xp = xPosition - (textWidth * 1 / 2);
		} else {
			xp = xPosition - (textWidth * 2 / 2);
		}
		return xp;
	}

	/**
	 * 画中间的红色指示线、阴影等。指示线两端简单的用了两个矩形代替
	 *
	 * @param canvas
	 */
	private void drawMiddleLine(Canvas canvas) {
		Drawable wheel =  getResources().getDrawable(R.drawable.niddle);
		wheel.setBounds(0, mHeight / 2 - MIDLINE_WIDTH * (int)mDensity, mWidth,  mHeight / 2 + MIDLINE_WIDTH * (int)mDensity);
		wheel.draw(canvas);
		//        canvas.restore();
		canvas.save();
		canvas.restore();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
	//int xPosition = (int) event.getX();
		int xPosition = (int) event.getY();

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}else{
			mVelocityTracker.clear();
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
				changeMoveAndValue();
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				countMoveEnd();
				countVelocityTracker(event);
				return false;
			// break;
			default:
				break;
		}

		mLastX = xPosition;
		return true;
	}

	private void countVelocityTracker(MotionEvent event) {
		mVelocityTracker.computeCurrentVelocity(1000);
		//float xVelocity = mVelocityTracker.getXVelocity();
		float xVelocity = mVelocityTracker.getYVelocity();
		if (Math.abs(xVelocity) > mMinVelocity) {
			mScroller.fling(0, 0, 0, (int) xVelocity,  Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
		}
	}

	private void changeMoveAndValue() {
		int tValue = (int) (mMove / (ITEM_ONE_DIVIDER * mDensity));
		if (Math.abs(tValue) > 0) {
			mValue += tValue;
			mMove -= tValue * ITEM_ONE_DIVIDER * mDensity;
			if (mValue <= 0 || mValue > mMaxValue) {
				mValue = mValue <= 0 ? 0 : mMaxValue;
				mMove = 0;
				mScroller.forceFinished(true);
			}
			notifyValueChange();
		}
		postInvalidate();
	}

	private void countMoveEnd() {
		int roundMove = Math.round(mMove / (ITEM_ONE_DIVIDER * mDensity));
		mValue = mValue + roundMove;
		mValue = mValue <= 0 ? 0 : mValue;
		mValue = mValue > mMaxValue ? mMaxValue : mValue;

		mLastX = 0;
		mMove = 0;

		notifyValueChange();
		postInvalidate();

		if(mListener != null){
			mListener.onValueChangeEnd(this, mValue + mBeginValue);
		}
	}

	private void notifyValueChange() {
		if (null != mListener) {
			mListener.onValueChange(this, mValue + mBeginValue);
		}
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		if (mScroller.computeScrollOffset()) {
			if (mScroller.getCurrY() == mScroller.getFinalY()) { // over
				countMoveEnd();
			} else {
				int xPosition = mScroller.getCurrY();
				mMove += (mLastX - xPosition);
				changeMoveAndValue();
				mLastX = xPosition;
			}
		}
	}
}