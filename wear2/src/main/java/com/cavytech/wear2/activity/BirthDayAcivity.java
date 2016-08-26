package com.cavytech.wear2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cavytech.wear2.R;
import com.cavytech.wear2.util.Constants;
import com.cavytech.wear2.util.SerializeUtils;
import com.cavytech.wear2.util.ToolDateTime;
import com.cavytech.widget.TuneWheel;

import java.util.Calendar;

/**
 * Created by longjining on 16/3/16.
 */
public class BirthDayAcivity extends GuideSetComActivity implements TuneWheel.OnValueChangeListener {

    TuneWheel dateWheel;
    TuneWheel dayWheel;
    TextView selDate;
    TextView selDay;

    int beginYear = 1901;
    int defualtYear = 1980;

    int maxDay = 30;

    int month;
    int year;
    int day = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_birthday);

        setTitleText(getString(R.string.title_mymessage));
        hideRightTitleText();
        findView();
        intDateWheel();
        intDayWheel();

        super.onListener();
    }

    protected void findView() {
        super.findView();

        dateWheel= (TuneWheel)findViewById(R.id.date_wheel);
        dateWheel.setValueChangeListener(this);

        dayWheel= (TuneWheel)findViewById(R.id.day_wheel);
        dayWheel.setValueChangeListener(this);

        selDate = (TextView)findViewById(R.id.sel_date);
        selDay  = (TextView)findViewById(R.id.sel_day);
    }

    private void intDateWheel(){

        // 计算 年份1901年起 至今的月份
        int difMonth = ToolDateTime.getDifMonthNowTime(beginYear);
        if(isEdit) {
            if (userInfo.getBirthday() != null) {
                String substring = userInfo.getBirthday().toString().substring(0, 10);
                String[] oldBirthday = substring.split("-");
                if (oldBirthday.length <= 1) {
                    dateWheel.initViewParam(beginYear, (defualtYear - beginYear) * 12, difMonth, TuneWheel.MOD_TYPE_ONE);
                } else {
                    dateWheel.initViewParam(beginYear, (Integer.parseInt(oldBirthday[0]) - beginYear) * 12 + Integer.parseInt(oldBirthday[1]) - 1, difMonth, TuneWheel.MOD_TYPE_ONE);
                }
            }
         }else{
            dateWheel.initViewParam(beginYear, (defualtYear - beginYear) * 12, difMonth, TuneWheel.MOD_TYPE_ONE);
        }
     }

    private void intDayWheel(){

        if(isEdit){
            if (userInfo.getBirthday() != null) {
                String substring = userInfo.getBirthday().toString().substring(0, 10);
                String[] oldBirthday = substring.split("-");
                if (oldBirthday.length <= 1) {
                    dayWheel.initViewParam(1, 0, maxDay, TuneWheel.MOD_TYPE_HALF);
                } else {
                    maxDay = getDaysCountOfMonth(Integer.parseInt(oldBirthday[0]), Integer.parseInt(oldBirthday[1]));
                    dayWheel.initViewParam(1, Integer.parseInt(oldBirthday[2]) - 1, maxDay, TuneWheel.MOD_TYPE_HALF);
                }
            }
         }else{
        }
     }
    @Override
    public void onClickNextBtn(){
        if(month < 10){
            userInfo.setBirthday(year + "-" + "0" +month + "-" + (day + 1));

            if(day < 10){
                userInfo.setBirthday(year + "-" + "0" +month + "-" + "0" +(day + 1));
            }
        }else if(day < 10){
            userInfo.setBirthday(year + "-"  +month + "-" + "0" +(day + 1));
            if(month < 10){
                userInfo.setBirthday(year + "-" + "0" +month + "-" + (day + 1));
            }
        }

        if(userInfo.getBirthday() == null){
            userInfo.setBirthday("1980-01-01");
        }

        if(isEdit){
            saveEdit(userInfo);
        }else{
            Intent intent = new Intent(BirthDayAcivity.this, TargetActivity.class);

            SerializeUtils.serialize(userInfo, Constants.SERIALIZE_USERINFO);

            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onValueChange(View wheel, float value) {

        if(dateWheel == wheel){

            year = beginYear +  (int)value/12;
            month = (int)value%12 + 1;

            selDate.setText(String.format("%d.%d", year, month));
        }else{

            day = (int)value;
            selDay.setText(String.format("%d", day + 1));
        }
    }

    @Override
    public void onValueChangeEnd(View wheel, float value){
        if(dateWheel == wheel){

            dayWheel.initViewParam(1, 0, getDaysCountOfMonth(year,month) - 1, TuneWheel.MOD_TYPE_HALF);
        }
    }

    private int getDaysCountOfMonth(int year, int month){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);//先指定年份
        calendar.set(Calendar.MONTH, month - 1);//再指定月份 Java月份从0开始算
        int daysCountOfMonth = calendar.getActualMaximum(Calendar.DATE);//获取指定年份中指定月份有几天

        return daysCountOfMonth;
    }
}
