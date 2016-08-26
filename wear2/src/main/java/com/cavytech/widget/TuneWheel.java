package com.cavytech.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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
import android.widget.Switch;
import android.widget.Toast;

import com.cavytech.wear2.R;
import com.cavytech.wear2.util.LogUtil;

import java.util.ArrayList;

/**
 *
 * @author ttdevs
 * @version create：2014年8月26日
 */
@SuppressLint("ClickableViewAccessibility")
public class TuneWheel extends View {

	public interface OnValueChangeListener {
		public void onValueChange(View wheel, float value);
		public void onValueChangeEnd(View wheel, float value);
	}

	public static final int MOD_TYPE_HALF = 5;  // 每一个大单位可以分为多少个最小单位
	public static final int MOD_TYPE_ONE = 12;
	public static final int MOD_TYPE_TEXT = 1;  // 只显示文本

	private static final int ITEM_ONE_DIVIDER = 10;
	private static final int ITEM_HALF_DIVIDER = ITEM_ONE_DIVIDER * 2;  // 每一个最小单位间隔

	private static final int ITEM_MAX_HEIGHT = 30;
	private static final int ITEM_MIN_HEIGHT = 16;

	private static final int TEXT_SIZE = 14;
	private static final int SEL_TEXT_SIZE = 18;

	private static final int MIDLINE_WIDTH = 2;
	private static final int TRIANGLE_HIGHT = 40;  // 三角型的高

	private float mDensity;
	private int mValue = 0, mMaxValue = 10*12;
	private int  mModType = MOD_TYPE_ONE, mLineDivider = ITEM_ONE_DIVIDER;

	private int mBeginValue = 0;

	// private int mValue = 50, mMaxValue = 500, mModType = MOD_TYPE_ONE,
	// mLineDivider = ITEM_ONE_DIVIDER;

	private int mLastX, mMove;
	private int mWidth, mHeight;

	private int mMinVelocity;
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;

	private OnValueChangeListener mListener;

	private ArrayList<String> mText;

	Path mTrianglePath;
	Paint mTrianglePaint;

	@SuppressWarnings("deprecation")
	public TuneWheel(Context context, AttributeSet attrs) {
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

	/**
	 *
	 * 考虑可扩展，但是时间紧迫，只可以支持两种类型效果图中两种类型
	 *
	 * @param value
	 *            初始值
	 * @param maxValue
	 *            最大值
	 * @param model
	 *            刻度盘精度：<br>
	 *            {@link MOD_TYPE_HALF}<br>
	 *            {@link MOD_TYPE_ONE}<br>
	 */
	public void initViewParam(int beginValue, int defaultValue, int maxValue, int model) {
		switch (model) {
			case MOD_TYPE_HALF:
				mModType = MOD_TYPE_HALF;
				mLineDivider = ITEM_HALF_DIVIDER;
				mValue = defaultValue;
				mMaxValue = maxValue;
				break;
			case MOD_TYPE_ONE:
				mModType = MOD_TYPE_ONE;
				mLineDivider = ITEM_ONE_DIVIDER;
				mValue = defaultValue;
				mMaxValue = maxValue;
				break;
			case MOD_TYPE_TEXT:
				mModType = MOD_TYPE_TEXT;
				if(mWidth != 0){
					mLineDivider = mWidth/3;
				}
				mValue = defaultValue;
				mMaxValue = maxValue;
				break;
			default:
				break;
		}
		mBeginValue = beginValue;

		invalidate();

		mLastX = 0;
		mMove = 0;
		notifyValueChange();
	}
	/**
	 *
	 * 考虑可扩展，但是时间紧迫，只可以支持两种类型效果图中两种类型
	 *
	 * @param text
	 *            需要显示的文本
	 */
	public void initViewParam(ArrayList<String> text) {

		mModType = MOD_TYPE_TEXT;
		if(mWidth != 0){
			mLineDivider = mWidth/3;
		}
		mValue = text.size() - 1;
		mMaxValue = text.size() - 1;
		mText = text;
		mBeginValue = 0;

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
		if(mModType == MOD_TYPE_TEXT){
			mLineDivider = mWidth/3/(int)mDensity;
		}
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if(mModType == MOD_TYPE_TEXT){
			if(mText == null || mText.size() == 0){
				return;
			}
		}
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
		switch (mModType) {
			case MOD_TYPE_TEXT:

				if(mValue + i > mText.size()){
					return "-1";
				}
				if(isAdd){
					return mText.get(mValue + i);
				}else{
					return mText.get(mValue - i);
				}
			case MOD_TYPE_HALF:
				if(isAdd){
					return String.valueOf((mValue + i) + mBeginValue);
				}else{
					return String.valueOf((mValue - i) + mBeginValue);
				}
			case MOD_TYPE_ONE:
				if(isAdd){
					return String.valueOf((mValue + i)/mModType + mBeginValue);
				}else{
					return String.valueOf((mValue - i)/mModType + mBeginValue);
				}

			default:
				break;
		}
		return "-1";
	}

	boolean isDrawHeightLine(int index){

		switch (mModType) {
			case MOD_TYPE_TEXT:

				return true;
			case MOD_TYPE_HALF:
				int type = mModType;
				if ((mValue + index + 1) % type == 0 && (mValue + index) >= type
						|| (mValue + index) == (type - 1)) {
					// 按五的倍数显示分割
					return true;
				}else{
					return false;
				}
			case MOD_TYPE_ONE:
				if ((mValue + index) % mModType == 0) {

					return true;
				}else{
					return false;
				}

			default:
				break;
		}

		return false;
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

		if(mModType == MOD_TYPE_TEXT){
			textPaint.setFakeBoldText(true);
			textPaint.setColor(Color.argb(255, 255, 255, 255));
		}else{
			textPaint.setColor(Color.argb(204,0,0,0));
		}
		int width = mWidth, drawCount = 0;
		float xPosition = 0, textWidth = Layout.getDesiredWidth("0", textPaint);

		for (int i = 0; drawCount <= 4 * width; i++) {
			int numSize = String.valueOf(mValue + i + mBeginValue).length();

			xPosition = (width / 2 - mMove) + i * mLineDivider * mDensity;
			if (xPosition + getPaddingRight() < mWidth) {

				if (isDrawHeightLine(i)) {

					if (mValue + i <= mMaxValue) {
						if(mModType != MOD_TYPE_TEXT){
							canvas.drawLine(xPosition, getHeight(), xPosition, getHeight() - mDensity * ITEM_MAX_HEIGHT, linePaint);
						}
						switch (mModType) {
							case MOD_TYPE_TEXT:
								if(i == 0){
									textPaint.setTextSize(SEL_TEXT_SIZE * mDensity);
									textPaint.setColor(Color.argb(255, 255, 255, 255));
									canvas.drawText(getDrawText(i, true), countLeftStart(mValue + i, xPosition, textWidth), getHeight()/2, textPaint);
								}else{
									textPaint.setTextSize(TEXT_SIZE * mDensity);
									textPaint.setColor(Color.argb(77, 255, 255, 255));
									canvas.drawText(getDrawText(i, true), countLeftStart(mValue + i, xPosition, textWidth), getHeight()/2, textPaint);
								}
								break;
							case MOD_TYPE_HALF:
								canvas.drawText(getDrawText(i, true), countLeftStart(mValue + i, xPosition, textWidth), textWidth * 2, textPaint);
								break;
							case MOD_TYPE_ONE:
								canvas.drawText(getDrawText(i, true), xPosition - (textWidth * numSize / 2), textWidth * 2, textPaint);
								break;

							default:
								break;
						}
					}
				} else {
					if (mValue + i <= mMaxValue) {
						canvas.drawLine(xPosition, getHeight(), xPosition, getHeight() - mDensity * ITEM_MIN_HEIGHT, linePaint);
					}
				}
			}

			xPosition = (width / 2 - mMove) - i * mLineDivider * mDensity;
			if (xPosition > getPaddingLeft()) {
				if (isDrawHeightLine(-i)) {

					if (mValue - i >= 0) {
						if(mModType != MOD_TYPE_TEXT){
							canvas.drawLine(xPosition, getHeight(), xPosition, getHeight() - mDensity * ITEM_MAX_HEIGHT, linePaint);
						}
						switch (mModType) {
							case MOD_TYPE_TEXT:
								if (i != 0) {
									textPaint.setTextSize(TEXT_SIZE * mDensity);
									textPaint.setColor(Color.argb(77, 255, 255, 255));
									canvas.drawText(getDrawText(i, false), countLeftStart(mValue - i, xPosition, textWidth), getHeight()/2, textPaint);
								}
								break;
							case MOD_TYPE_HALF:
								canvas.drawText(getDrawText(i, false), countLeftStart(mValue - i, xPosition, textWidth), textWidth * 2, textPaint);
								break;
							case MOD_TYPE_ONE:
								canvas.drawText(getDrawText(i, false), xPosition - (textWidth * numSize / 2), textWidth * 2, textPaint);
								break;

							default:
								break;
						}
					}
				} else {
					if (mValue - i >= 0) {
						canvas.drawLine(xPosition, getHeight(), xPosition, getHeight() - mDensity * ITEM_MIN_HEIGHT, linePaint);
					}
				}
			}
			drawCount += 2 * mLineDivider * mDensity;
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
		if(mModType == MOD_TYPE_TEXT){
			String str = mText.get((int)value);

			xp = xPosition - str.length() * textWidth/2;
		}else{
			if (value < 20) {
				xp = xPosition - (textWidth * 1 / 2);
			} else {
				xp = xPosition - (textWidth * 2 / 2);
			}
		}

		return xp;
	}

	private void drawTriangle(Canvas canvas){
		if(mTrianglePath == null){
			mTrianglePath = new Path();
			mTrianglePath.moveTo(mWidth/2, mHeight - TRIANGLE_HIGHT);// 此点为多边形的起点
			mTrianglePath.lineTo(mWidth / 2 - TRIANGLE_HIGHT, mHeight);
			mTrianglePath.lineTo(mWidth/2 + TRIANGLE_HIGHT, mHeight);
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
		/*
		// TOOD 常量太多，暂时放这，最终会放在类的开始，放远了怕很快忘记
		int gap = 12, indexWidth = 4, indexTitleWidth = 24, indexTitleHight = 10, shadow = 6;
		String color = "#66999999";

		canvas.save();

		Paint redPaint = new Paint();
		redPaint.setStrokeWidth(indexWidth);
		redPaint.setColor(Color.RED);
		canvas.drawLine(mWidth / 2, 0, mWidth / 2, mHeight, redPaint);
*/
		if(mModType == MOD_TYPE_TEXT){
			drawTriangle(canvas);
		}else{
			Drawable wheel = getResources().getDrawable(R.drawable.niddle);
			// int left, int top, int right, int bottom
			wheel.setBounds(mWidth / 2 - MIDLINE_WIDTH * (int)mDensity, 0, mWidth / 2 + MIDLINE_WIDTH * (int)mDensity, mHeight);
			wheel.draw(canvas);
		}


		/*
		Paint ovalPaint = new Paint();
		ovalPaint.setColor(Color.RED);
		ovalPaint.setStrokeWidth(indexTitleWidth);
		canvas.drawLine(mWidth / 2, 0, mWidth / 2, indexTitleHight, ovalPaint);
		canvas.drawLine(mWidth / 2, mHeight - indexTitleHight, mWidth / 2, mHeight, ovalPaint);

		Paint shadowPaint = new Paint();
		shadowPaint.setStrokeWidth(shadow);
		shadowPaint.setColor(Color.parseColor(color));
		canvas.drawLine(mWidth / 2 + gap, 0, mWidth / 2 + gap, mHeight, shadowPaint);
*/
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
		mVelocityTracker.computeCurrentVelocity(1);
		float xVelocity = mVelocityTracker.getXVelocity();
		if (Math.abs(xVelocity) > mMinVelocity) {
			mScroller.fling(0, 0, (int) xVelocity, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
		}else{
			if (null != mListener) {
				mListener.onValueChangeEnd(this, mValue);
			}
		}
	}

	private void changeMoveAndValue() {
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
			if (mModType == MOD_TYPE_ONE) {
				mListener.onValueChange(this, mValue);
			}
			if (mModType == MOD_TYPE_HALF) {
				mListener.onValueChange(this, mValue);
			}
		}
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		if (mScroller.computeScrollOffset()) {
			if (mScroller.getCurrX() == mScroller.getFinalX()) { // over
				countMoveEnd();
				if (null != mListener) {
					mListener.onValueChangeEnd(this, mValue);
				}
			} else {
				int xPosition = mScroller.getCurrX();
				mMove += (mLastX - xPosition);
				changeMoveAndValue();
				mLastX = xPosition;
			}
		}
	}
}