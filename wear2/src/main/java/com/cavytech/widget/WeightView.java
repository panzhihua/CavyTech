package com.cavytech.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cavytech.wear2.R;

import java.math.BigDecimal;

/**
 * Created by longjining on 16/3/18.
 */
public class WeightView  extends ViewGroup {

    public interface OnValueChangeListener {
        public void onValueChange(View wheel, float value);
        public void onValueChangeEnd(View wheel, float value);
    }
    private OnValueChangeListener mListener;

    private VelocityTracker mVelocityTracker;

    int startX, startY, endX, endY;

    float startRotatio = 0,centerX, centerY;
    double  startAngle = 0;

    float minimumValue = 0, maximumValue = 180, maxAngle = 135;
    float       value = 50;
    private int mDensity;

    ImageView imgPointer;
    TextView valueText;

    /**
     * 设置用于接收结果的监听器
     *
     * @param listener
     */
    public void setValueChangeListener(OnValueChangeListener listener) {
        mListener = listener;
    }

    @SuppressWarnings("deprecation")
    public WeightView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setBackgroundResource(R.drawable.clock_bg);
        mDensity = (int)getContext().getResources().getDisplayMetrics().density;

        imgPointer = new ImageView(context, attrs);
        imgPointer.setImageDrawable(getResources().getDrawable(R.drawable.clock_niddle));
        addView(imgPointer, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

        valueText = new TextView(context, attrs);
        valueText.setText(String.valueOf(value));
        valueText.setTextSize(30);
        valueText.setTextColor(Color.argb(204, 0, 0, 0));
      //  valueText.setBackgroundColor(Color.RED);
        addView(valueText, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    }

    void initPointterView(){
        float angle = angleForValue(value);

        imgPointer.setRotation(angle);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension(heightMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        centerX = getWidth()/2;
        centerY = getHeight()/2;

        imgPointer.layout(0, 0, bottom, bottom);

        int space = 48 * mDensity/3;
        int tmpBottom = bottom - space;
        int tmpTop = tmpBottom - 48 * mDensity;//bottom - 45 * mDensity - space;
        valueText.layout(0, tmpTop, getWidth(), tmpBottom);
        valueText.setGravity(Gravity.CENTER);

        initPointterView();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startX = (int)event.getX();
                startY = (int)event.getY();
                startRotatio = imgPointer.getRotation();
                startAngle = angleBetweenCenterAndPoint(startX, startY);
                break;
            case MotionEvent.ACTION_MOVE:
                endX = (int)event.getX();
                endY = (int)event.getY();
                changeMoveAndValue();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                endX = (int)event.getX();
                endY = (int)event.getY();

                changeMoveAndValue();
                countMoveEnd();
                return false;
            // break;
            default:
                break;
        }

        return true;
    }

    private float clampAngle(float angle){
        if (angle < -maxAngle)
            return -maxAngle;
        else if (angle > maxAngle)
            return maxAngle;
        else
            return angle;
    }

    private float angleForValue(float value)
    {
        value = ((value - minimumValue)/(maximumValue - minimumValue) - 0.5f) * (maxAngle*2.0f);
        value = changeAngle(value);
        return value;
    }

    private float valueForAngle(float angle)
    {
        return (angle/(maxAngle*2.0f) + 0.5f) * (maximumValue - minimumValue) + minimumValue;
    }

    private float angleBetweenCenterAndPoint(float x, float y)
    {
        double angle = Math.atan2(x - centerX, centerY - y) * 180.0f/Math.PI;

        return clampAngle((float)angle);
    }

    private void changeMoveAndValue() {

        double newAngle = angleBetweenCenterAndPoint(endX, endY);

        double delta = newAngle - startAngle;
        startAngle = newAngle;

        if(Math.abs(delta) > 45){
            return;
        }
        setValue((float) delta);

        float endRotation = angleForValue(value);

     //   LogUtil.getLogger().d(String.format("Degree:%f, %f", endRotation, value));

        imgPointer.setRotation(endRotation);

        notifyValueChange();

       // postInvalidate();
    }
    float changeAngle(float angle){
        // 把 -135--135  转为 0--360
        if(angle >= 90 && angle <= 135){
            angle -= 90;
        }else{
            angle = 270 + angle;
        }

        return angle;
    }
    private void countMoveEnd() {

        // 取整
        /*
        float endRotation = imgPointer.getRotation();

        int tmpRotation = (int)(endRotation / 4.5);
        endRotation = tmpRotation * 4.5f;
        imgPointer.setRotation(endRotation);*/

        if (null != mListener) {

            mListener.onValueChangeEnd(this, value);
            notifyValueChange();
        }
    }
    private void setValue(float delta){

        value += (maximumValue - minimumValue) * delta / (maxAngle*2.0);

        if (value < minimumValue)
            value = minimumValue;
        else if (value > maximumValue)
            value = maximumValue;

        value = scaleValue(value);
    }

    public void initValue(float intValue){

        value = scaleValue(intValue);


        valueText.setText(String.valueOf(value));
        invalidate();
    }

    private float scaleValue(float intValue){

        if(intValue >= maximumValue){
            return maximumValue;
        }

        if(intValue <= minimumValue){
            return minimumValue;
        }

        int iValue = (int)intValue;
        float fValue = intValue - iValue;

        if(fValue > 0.4){
            intValue = iValue;
        }else{
            intValue = (float) iValue + 0.5f;
        }

        BigDecimal b = new BigDecimal(intValue);
        intValue = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();

        return intValue;
    }

    private void notifyValueChange() {
        if (null != mListener) {
            mListener.onValueChange(this, value);
        }

        valueText.setText(String.valueOf(value));
    }
}
