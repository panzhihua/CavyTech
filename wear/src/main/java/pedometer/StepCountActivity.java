package pedometer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;

import com.cavytech.wear.R;
import com.cavytech.wear.activity.CommonActivity;
import com.cavytech.wear.util.Constants;
import com.cavytech.wear.util.ToolDateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
/**
 * Created by longjining on 15/12/10.
 */


public class StepCountActivity extends CommonActivity {

    private static final String TAG = "Pedometer";
    private SharedPreferences mSettings;
    private PedometerSettings mPedometerSettings;
    private Utils mUtils;

    private TextView mStepValueView;
    private TextView mTotalDistanceView;
    private TextView mActiveTimeView;
    private TextView mTodayView;
    private TextView mAvgStepsView;

    private TextView   mTodayStepValueView;
    public ImageButton mTodayStepImgBtn;

    public ImageButton mBack;


    private long mStepValue;
    private int mPaceValue;
    private float mDistanceValue;
    private long mActiveTime;

    private float mSpeedValue;
    private int mCaloriesValue;
    private float mDesiredPaceOrSpeed;
    private int mMaintain;
    private boolean mIsMetric;
    private float mMaintainInc;
    private boolean mQuitting = false; // Set when user selected Quit from menu, can be used by onPause, onStop, onDestroy

    private long mTodayBeginStepValue;
    private String mTodayStepKey;
    private float mTotalDistanceValueBegin;
    private long mTotalSeverDaysSteps;
    private ArrayList<String> mSeverDaySteps = new ArrayList<String>();
    private ArrayList<StepCharView> mStepsTextView = new ArrayList<StepCharView>();

    private static String MILES_KEY = "step_total_miles_key";
    private static String ACTIVITY_KEY = "activity_key";

    private static String TEXTVIEW_WEEK = "textView_week_day%d";
    private static String TEXTVIEW_DAY  = "textView_day_day%d";
    private static String TEXTVIEW_STEP  = "textView_steps_day%d";
    private static String IMGBTN_STEP  = "imageButton_steps_day%d";

    private static String BEGIN_COUNT_STEP_DAY  = "begin_count_step_day";

    private static long DEFULT_FULL_STEPS = 8000;  // 默认满格的步数

    private static int TIME = 60000;
    // 最后计步时间
    private Date mCountLastTime;
    /**
     * True, when service is running.
     */
    private boolean mIsRunning;
    private boolean mIsCounting;

    private String mBeginDate;  // YYYYMMDD

    private class StepCharView{

        TextView stepTextView;
        ImageButton stepImgBtn;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);

        mStepValue = 0;
        mPaceValue = 0;
        mTodayBeginStepValue = 0;
        mTotalDistanceValueBegin  = 0;
        mTotalSeverDaysSteps = 0;
        mIsCounting = false;

        mBeginDate = getCoreApplication().getPreferenceConfig().getString(BEGIN_COUNT_STEP_DAY, "0");
        if(mBeginDate.equals("0")){

            Date currentDate = ToolDateTime.gainCurrentDate();
            mBeginDate = ToolDateTime.formatDateTime(currentDate,
                    ToolDateTime.DFYYYYMMDD);

            getCoreApplication().getPreferenceConfig().setString(BEGIN_COUNT_STEP_DAY, mBeginDate);
        }

        initView();
        addListener();

        mUtils = Utils.getInstance();

        handler.postDelayed(runnable, TIME); //每隔60s执行
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            // handler自带方法实现定时器
            try {
                handler.postDelayed(this, TIME);
                // 如果一分钟内没有计步则认为活跃停止
                if(mIsCounting){
                    long delayTime = ToolDateTime.getSubNowTime(mCountLastTime);
                    if(delayTime > 60){
                        mIsCounting = false;
                    }else{
                        mActiveTime += 60;

                        getCoreApplication().getPreferenceConfig().setString(ACTIVITY_KEY, String.valueOf(mActiveTime));

                        if(mActiveTimeView != null){
                            mActiveTimeView.setText("" + mActiveTime/60);
                        }
                    }
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("exception...");
            }
        }
    };

    private void initView() {
        findTitle();
        title.setText(getString(R.string.step));
        mBack = (ImageButton) findViewById(R.id.back);

        mStepValueView     = (TextView) findViewById(R.id.todaystepnum);

        mTodayView = (TextView) findViewById(R.id.stepcounterdate);

        mTotalDistanceView = (TextView) findViewById(R.id.distance_unit_value);

        mActiveTimeView = (TextView) findViewById(R.id.active_unit_value);

        mAvgStepsView = (TextView) findViewById(R.id.avg_step_value);

        mTodayStepValueView = (TextView) findViewById(R.id.textView_steps_day1);
        mTodayStepImgBtn = (ImageButton) findViewById(R.id.imageButton_steps_day1);

        StepCharView charView = new StepCharView();
        charView.stepImgBtn = mTodayStepImgBtn;
        charView.stepTextView = mTodayStepValueView;

        mStepsTextView.add(charView);

        mTodayStepImgBtn.setTag(0);
        mTodayStepImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickStepImgBtn(v);
            }
        });

        initStepValue();
        findCharView();
    }

    private void initStepValue(){

        String dateKey = "";
        String steps = "";
        Date today = ToolDateTime.gainCurrentDate();
        Date date;

        String strTextViewName = "";
        int resID = 0;
        int intSteps = 0;
        TextView textView;

        mSeverDaySteps.clear();

        for(int i = 0; i < 7; i++){
            date = ToolDateTime.subDateTime(today, i * 24);
            dateKey = ToolDateTime.formatDateTime(date,
                    ToolDateTime.DFYYYYMMDD);

            steps = getCoreApplication().getPreferenceConfig().getString(dateKey, "0");
            mSeverDaySteps.add(steps);

            intSteps = Integer.parseInt(steps);
            if(i == 0){

                mTodayBeginStepValue = intSteps;
                mTodayStepKey = dateKey;

                mTodayView.setText(ToolDateTime.gainCurrentDate(ToolDateTime.DFYYYYPMMPDD));
            }

            mTotalSeverDaysSteps += intSteps;
            // week
            strTextViewName = String.format(TEXTVIEW_WEEK, i + 1);
            resID = getResources().getIdentifier(strTextViewName, "id", "com.cavytech.wear");

            textView = (TextView) findViewById(resID);

            if(textView != null){
                textView.setText(ToolDateTime.getWeekOfDate(date));
            }

            // day
            strTextViewName = String.format(TEXTVIEW_DAY, i + 1);
            resID = getResources().getIdentifier(strTextViewName, "id", "com.cavytech.wear");

            textView = (TextView) findViewById(resID);

            if(textView != null){
                Calendar  cal = Calendar.getInstance();
                cal.setTime(date);

                textView.setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
            }
        }
    }

    private void updateDistance(){
        float totalDistance = mTotalDistanceValueBegin + mDistanceValue;
        if(totalDistance > 0){
            totalDistance = totalDistance/1000f + 0.001f;
        }

        String distance = "" + totalDistance;

        if(distance.length() > 5){
            distance = distance.substring(0,5);
        }
        mTotalDistanceView.setText(distance);
    }
    private void initStepViewValue(){

        String totalDis = getCoreApplication().getPreferenceConfig().getString(MILES_KEY, "0");
        mTotalDistanceValueBegin = Float.parseFloat(totalDis);

        String activityTime = getCoreApplication().getPreferenceConfig().getString(ACTIVITY_KEY, "0");
        mActiveTime = Long.parseLong(activityTime);

        mActiveTimeView.setText("" + mActiveTime/60);

        updateDistance();
        for(int i = 1; i < 7; i++){

            updateTextViewHight(mStepsTextView.get(i).stepTextView, mStepsTextView.get(i).stepImgBtn, Long.parseLong(mSeverDaySteps.get(i)));
        }
    }

    public void onClickStepImgBtn(View v) {
        int tag = (int)v.getTag();
        TextView txView = mStepsTextView.get(tag).stepTextView;

        if (txView.getText() != ""){
            txView.setText("");
        } else {
            String steps;
            if (tag == 0) {
                steps = changeStepsTostr(mStepValue + mTodayBeginStepValue);
            } else {
                steps = mSeverDaySteps.get(tag);
            }

            if (!steps.equals("0")) {
                txView.setText(steps);

                for (int i = 0; i < mStepsTextView.size(); i++) {
                    if (i != tag) {
                        txView = mStepsTextView.get(i).stepTextView;
                        txView.setText("");
                    }
                }
            }
        }
    }

    private String changeStepsTostr(long steps){

        String strSteps = "";

        if(steps >= 10000){
            strSteps += steps/1000 + "k";
        }else{
            strSteps += steps;
        }
        return strSteps;
    }
    private  void updateTextViewHight(TextView stepView, ImageButton imgBtn, long steps){

        int top = 20;  // 距离顶部间距

        LayoutParams btnlp = imgBtn.getLayoutParams();
        if(steps == 0){
            // 如果时间比第一天的时间在7天内则显示默认的高度，否则显示高度为0

            int tag = (int)imgBtn.getTag();

            LayoutParams lp = stepView.getLayoutParams();
            if(isFirstSeverDay(tag)){
                imgBtn.setBackgroundColor(getResources().getColor(R.color.match_setting_title_bg));

                 lp.height = top + imgBtn.getHeight() * 90 / 100;
            }else{
                lp.height = top + imgBtn.getHeight();
            }

            stepView.setLayoutParams(lp);
        }else{
            imgBtn.setBackgroundColor(getResources().getColor(R.color.white));
            imgBtn.setBackgroundResource(R.drawable.color);

            float textViewHeight;
            if(steps >= DEFULT_FULL_STEPS){
                 textViewHeight = top;
            }else{
                float stepPercent = steps * 1.0f / DEFULT_FULL_STEPS;

                textViewHeight = imgBtn.getHeight() * (1.0f - stepPercent);
             }
            LayoutParams lp = stepView.getLayoutParams();

            textViewHeight += top;
            if(imgBtn.getHeight() + top < textViewHeight){
                textViewHeight = imgBtn.getHeight() + top - 1;
            }
            lp.height = (int) textViewHeight;

            stepView.setLayoutParams(lp);

        }
    }

    // 是否为最开始的七天
    private boolean isFirstSeverDay(int day){

        Date date = ToolDateTime.subDateTime(ToolDateTime.gainCurrentDate(), day * 24);

        String strDate = ToolDateTime.formatDateTime(date, ToolDateTime.DFYYYYMMDD);

        if(strDate.compareTo(mBeginDate) >= 0){

            return false;
        }

        return true;
    }

    private void updateTodayViewValue(){

        mAvgStepsView.setText(String.valueOf((mTotalSeverDaysSteps + mStepValue) / 7));

        updateTextViewHight(mTodayStepValueView, mTodayStepImgBtn, mStepValue + mTodayBeginStepValue);

        String todayStep = changeStepsTostr(mStepValue + mTodayBeginStepValue);
        mStepValueView.setText(todayStep);

        boolean bHaveShowSteps = false;
        for(int i = 1; i < 7; i++){
            if(mStepsTextView.get(i).stepTextView.getText() != ""){
                bHaveShowSteps = true;
                break;
            }
        }
        if(!bHaveShowSteps){
            mTodayStepValueView.setText(todayStep);
        }

        // 每10步保存一次
        if(mStepValue > 0 && mStepValue % 10 == 0) {
            saveStepValues(mStepValue + mTodayBeginStepValue);
            mCountLastTime = ToolDateTime.gainCurrentDate();
        }
    }

    private void saveStepValues(long steps){
        getCoreApplication().getPreferenceConfig().setString(mTodayStepKey, String.valueOf(steps));
        getCoreApplication().getPreferenceConfig().setString(MILES_KEY, String.valueOf(mDistanceValue + mTotalDistanceValueBegin));
    }

    private  void findCharView(){
        TextView stepView;
        ImageButton imgBtn;
        String strTextViewName = "";
        int resID = 0;

        for(int i = 1; i < 7; i++){
            strTextViewName = String.format(TEXTVIEW_STEP, i + 1);
            resID = getResources().getIdentifier(strTextViewName, "id", "com.cavytech.wear");
            stepView = (TextView) findViewById(resID);
            stepView.setText("");

            strTextViewName = String.format(IMGBTN_STEP, i + 1);
            resID = getResources().getIdentifier(strTextViewName, "id", "com.cavytech.wear");
            imgBtn = (ImageButton) findViewById(resID);

            imgBtn.setTag(i);

            imgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickStepImgBtn(v);
                }
            });

            StepCharView charView = new StepCharView();
            charView.stepImgBtn = imgBtn;
            charView.stepTextView = stepView;

            mStepsTextView.add(charView);
        }
    }

    private void addListener() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        saveStepValues(mStepValue + mTodayBeginStepValue);

        resetValues(true);

        mActiveTimeView = null;
    }

    @Override
    public void onResume() {
        Log.i(TAG, "[ACTIVITY] onResume");
        super.onResume();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                ResetStepValue();
            }
        }, 100);

        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mPedometerSettings = new PedometerSettings(mSettings);

        mUtils.setSpeak(mSettings.getBoolean("speak", false));

        // Read from preferences if the service was running on the last onPause
        mIsRunning = mPedometerSettings.isServiceRunning();

        // Start the service if this is considered to be an application start (last onPause was long ago)
        if (!mIsRunning && mPedometerSettings.isNewStart()) {
            startStepService();
            bindStepService();
        }
        else if (mIsRunning) {
            bindStepService();
        }
    }

    private void ResetStepValue(){
        if(checkIsSameDate()){
            initStepViewValue();
        }else{
            saveStepValues(mStepValue + mTodayBeginStepValue);
            resetValues(true);

            mStepValue = 0;
            initStepValue();
            initStepViewValue();
        }
    }

    private StepService mService;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((StepService.StepBinder)service).getService();

            mService.registerCallback(mCallback);
            mService.reloadSettings();

        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };


    private void startStepService() {
        if (! mIsRunning) {
            Log.i(TAG, "[SERVICE] Start");
            mIsRunning = true;
            startService(new Intent(StepCountActivity.this,
                    StepService.class));
        }
    }

    private void bindStepService() {
        Log.i(TAG, "[SERVICE] Bind");
        bindService(new Intent(StepCountActivity.this,
                StepService.class), mConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
    }

    private void unbindStepService() {
        Log.i(TAG, "[SERVICE] Unbind");
        unbindService(mConnection);
    }

    private void stopStepService() {
        Log.i(TAG, "[SERVICE] Stop");
        if (mService != null) {
            Log.i(TAG, "[SERVICE] stopService");
            stopService(new Intent(StepCountActivity.this,
                    StepService.class));
        }
        mIsRunning = false;
    }

    private void resetValues(boolean updateDisplay) {
        if (mService != null && mIsRunning) {
            mService.resetValues();
        }
        else {
            mStepValueView.setText("0");

            SharedPreferences state = getSharedPreferences("state", 0);
            SharedPreferences.Editor stateEditor = state.edit();
            if (updateDisplay) {
                stateEditor.putInt("steps", 0);
                stateEditor.putInt("pace", 0);
                stateEditor.putFloat("distance", 0);
                stateEditor.putFloat("speed", 0);
                stateEditor.putFloat("calories", 0);
                stateEditor.commit();
            }
        }
    }

    private StepService.ICallback mCallback = new StepService.ICallback() {
        public void stepsChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
        }
        public void paceChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(PACE_MSG, value, 0));
        }
        public void distanceChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG, (int)(value*1000), 0));
        }
        public void speedChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(SPEED_MSG, (int)(value*1000), 0));
        }
        public void caloriesChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG, (int)(value), 0));
        }
    };

    private boolean checkIsSameDate() {

        String curDate = ToolDateTime.gainCurrentDate(ToolDateTime.DFYYYYMMDD);
        
        if (!mTodayStepKey.equals(curDate)) {

            return false;
        }

        return true;
    }

    private static final int STEPS_MSG = 1;
    private static final int PACE_MSG = 2;
    private static final int DISTANCE_MSG = 3;
    private static final int SPEED_MSG = 4;
    private static final int CALORIES_MSG = 5;

    private Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case STEPS_MSG:
                    mIsCounting = true;
                    if(checkIsSameDate()){
                        mStepValue = (int)msg.arg1;

                        updateTodayViewValue();
                    }else{
                        saveStepValues(mStepValue + mTodayBeginStepValue);
                        resetValues(true);

                        mStepValue = 0;
                        initStepValue();
                        initStepViewValue();
                    }

                    break;
                case PACE_MSG:
                    mPaceValue = msg.arg1;

                    break;
                case DISTANCE_MSG:
                    int distance = (int)msg.arg1;
                    mDistanceValue = distance;

                    updateDistance();
                    /*
                    if (mDistanceValue <= 0) {
                        mDistanceValueView.setText("0");
                    }
                    else {
                        mDistanceValueView.setText(
                                ("" + (mDistanceValue + 0.000001f)).substring(0, 5)
                        );
                    }*/
                    break;
                case SPEED_MSG:
                    mSpeedValue = ((int)msg.arg1)/1000f;

                    break;
                case CALORIES_MSG:
                    mCaloriesValue = msg.arg1;

                    break;
                default:
                    super.handleMessage(msg);
            }
        }

    };

}
