package com.cavytech.wear2.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.basecore.widget.SystemBarTintManager;
import com.cavytech.wear2.R;
import com.cavytech.wear2.entity.ClockBean;
import com.cavytech.wear2.util.Constants;
import com.cavytech.wear2.util.FileUtils;
import com.cavytech.wear2.util.SerializeUtils;
import com.cavytech.widget.ScrollViewTimePacker;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;


public class KnowClickDetailActivity extends Activity {
    @ViewInject(R.id.checkBox_one)
    private CheckBox checkBox_one;

    @ViewInject(R.id.checkBox_two)
    private CheckBox checkBox_two;

    @ViewInject(R.id.checkBox_three)
    private CheckBox checkBox_three;

    @ViewInject(R.id.checkBox_four)
    private CheckBox checkBox_four;

    @ViewInject(R.id.checkBox_five)
    private CheckBox checkBox_five;

    @ViewInject(R.id.checkBox_six)
    private CheckBox checkBox_six;

    @ViewInject(R.id.checkBox_seven)
    private CheckBox checkBox_seven;

    @ViewInject(R.id.tv_colock_delete)
    private TextView tv_colock_delete;

    @ViewInject(R.id.timePicker_clock)
    private ScrollViewTimePacker timePicker_clock;

    @ViewInject(R.id.back)
    private ImageView back;

    @ViewInject(R.id.go)
    private ImageView go;

    @ViewInject(R.id.title)
    private TextView title;

    @ViewInject(R.id.txt_one)
    private TextView txtOne;

    @ViewInject(R.id.txt_two)
    private TextView txtTwo;

    @ViewInject(R.id.txt_three)
    private TextView txtThree;

    @ViewInject(R.id.txt_four)
    private TextView txtFour;

    @ViewInject(R.id.txt_five)
    private TextView txtFive;

    @ViewInject(R.id.txt_six)
    private TextView txtSix;

    @ViewInject(R.id.txt_seven)
    private TextView txtSeven;

    public int realHour;
    public int realMinute;

    private int position;

    public String timeStrinmg="";

    private List<ClockBean> mListItems = null;
    private List<CheckBox> mListCheckBox = new ArrayList<CheckBox>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.top_bg_color);//通知栏所需颜色
            tintManager.setNavigationBarTintEnabled(true);
            tintManager.setNavigationBarTintResource(R.color.black);
        }
        setContentView(R.layout.activity_know_click_detail);
        x.view().inject(this);

        title.setText(R.string.know_clock);
        go.setImageResource(R.drawable.nav_right);

        //获取item带回来的信息
        setDataTimeFromItem();

        selectDay();

        back.setOnClickListener(new MyonClickListener());
        go.setOnClickListener(new MyonClickListener());
        tv_colock_delete.setOnClickListener(new MyonClickListener());
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     *获取item带回来的信息
     */
    private void setDataTimeFromItem() {

        Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);

        if(FileUtils.isFileExit(Constants.SERIALIZE_CLOCKBEAN)){
            mListItems = (List<ClockBean>) SerializeUtils.unserialize(Constants.SERIALIZE_CLOCKBEAN);

            if(position == -1){
                // 如果是-1则认为是新增一个闹钟
                mListItems.add(new ClockBean());
                position = mListItems.size() - 1;
                tv_colock_delete.setVisibility(View.GONE);
            }else{
                tv_colock_delete.setVisibility(View.VISIBLE);
            }
        }else{
            mListItems = new ArrayList<ClockBean>();

            mListItems.add(new ClockBean(true));
            position = 0;
        }
        initListCheckBox();

        //  String timeString = mListItems.get(position).getTimeString();
        int hour = mListItems.get(position).getHour();
        int minute = mListItems.get(position).getMinute();
        System.out.print("hour"+hour+"minute"+minute+"--------------------------");

        setTimePicker(hour,minute);

        setData();
    }
    /**
     * 设置星期选择
     */
    private void setData(){

        for(int i = 0; i < mListItems.get(position).getWeekSet().size(); i++){
            mListCheckBox.get(i).setChecked(mListItems.get(position).getWeekSet().get(i));
            setColorText(i,mListItems.get(position).getWeekSet().get(i));
        }
    }
    private void initListCheckBox(){

        mListCheckBox.add(checkBox_one);
        mListCheckBox.add(checkBox_two);
        mListCheckBox.add(checkBox_three);
        mListCheckBox.add(checkBox_four);
        mListCheckBox.add(checkBox_five);
        mListCheckBox.add(checkBox_six);
        mListCheckBox.add(checkBox_seven);

        for(int i = 0; i < mListCheckBox.size(); i++){
            mListCheckBox.get(i).setTag(i);
        }
    }

    private void setTimePicker(int hour,int minute) {
        timePicker_clock.setIs24HourView(true);

        realHour = hour;
        realMinute = minute;

        timePicker_clock.setCurrentHour(hour);
        timePicker_clock.setCurrentMinute(minute);

        timePicker_clock.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                System.out.println("h:"+ hourOfDay +" m:"+minute);
                realHour = hourOfDay;
                realMinute = minute;
            }
        });
    }

    class MyonClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if(v == back ){
                finish();
            }else if(v == go){
                if(isSelWeek()){
                    mListItems.get(position).setNotSelect(false);
                    mListItems.get(position).setHour(realHour);
                    mListItems.get(position).setMinute(realMinute);
                    mListItems.get(position).setOpen(true);
                    setResult(RESULT_OK);
                    SerializeUtils.serialize(mListItems, Constants.SERIALIZE_CLOCKBEAN);
                    finish();
                }else{
                    mListItems.get(position).setNotSelect(true);
                    mListItems.get(position).setOpen(true);
                    mListItems.get(position).setHour(realHour);
                    mListItems.get(position).setMinute(realMinute);
                    setResult(RESULT_OK);
                    SerializeUtils.serialize(mListItems, Constants.SERIALIZE_CLOCKBEAN);
                    finish();

                    //Toast.makeText(KnowClickDetailActivity.this,"请选择星期",Toast.LENGTH_SHORT).show();
                }

            }else if(v == tv_colock_delete){
                mListItems.remove(position);
                setResult(RESULT_OK);
                SerializeUtils.serialize(mListItems, Constants.SERIALIZE_CLOCKBEAN);
                finish();
            }
        }
    }

    private boolean isSelWeek(){

        for(int i = 0; i < mListItems.get(position).getWeekSet().size(); i++){
            if(mListItems.get(position).getWeekSet().get(i)){
                return true;
            }
        }
        return false;
    }
    /**
     * 选择星期
     */
    private void selectDay() {

        for(int i = 0; i < mListCheckBox.size(); i++){
            mListCheckBox.get(i).setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        }
    }

    class MyOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            int tag = (int)buttonView.getTag();
            setColorText(tag,isChecked);

            mListItems.get(position).getWeekSet().set(tag, isChecked);

        }
    }

    public void setColorText(int tag,boolean isChecked){
        if(tag==0){
            if(isChecked){
                txtOne.setTextColor(Color.parseColor("#FFFFFF"));
                txtOne.setText(R.string.mon);
            }else{
                txtOne.setTextColor(Color.parseColor("#7a7a7a"));
                txtOne.setText(R.string.mon);
            }
        }else if(tag==1){
            if(isChecked){
                txtTwo.setTextColor(Color.parseColor("#FFFFFF"));
                txtTwo.setText(R.string.tues);
            }else{
                txtTwo.setTextColor(Color.parseColor("#7a7a7a"));
                txtTwo.setText(R.string.tues);
            }
        }else if(tag==2){
            if(isChecked){
                txtThree.setTextColor(Color.parseColor("#FFFFFF"));
                txtThree.setText(R.string.wed);
            }else{
                txtThree.setTextColor(Color.parseColor("#7a7a7a"));
                txtThree.setText(R.string.wed);
            }
        }else if(tag==3){
            if(isChecked){
                txtFour.setTextColor(Color.parseColor("#FFFFFF"));
                txtFour.setText(R.string.thurs);
            }else{
                txtFour.setTextColor(Color.parseColor("#7a7a7a"));
                txtFour.setText(R.string.thurs);
            }
        }else if(tag==4){
            if(isChecked){
                txtFive.setTextColor(Color.parseColor("#FFFFFF"));
                txtFive.setText(R.string.fri);
            }else{
                txtFive.setTextColor(Color.parseColor("#7a7a7a"));
                txtFive.setText(R.string.fri);
            }
        }else if(tag==5){
            if(isChecked){
                txtSix.setTextColor(Color.parseColor("#FFFFFF"));
                txtSix.setText(R.string.sat);
            }else{
                txtSix.setTextColor(Color.parseColor("#7a7a7a"));
                txtSix.setText(R.string.sat);
            }
        }else if(tag==6){
            if(isChecked){
                txtSeven.setTextColor(Color.parseColor("#FFFFFF"));
                txtSeven.setText(R.string.sun);
            }else{
                txtSeven.setTextColor(Color.parseColor("#7a7a7a"));
                txtSeven.setText(R.string.sun);
            }
        }
    }
}
