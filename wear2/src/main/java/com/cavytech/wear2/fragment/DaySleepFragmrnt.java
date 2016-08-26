package com.cavytech.wear2.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cavytech.wear2.R;
import com.cavytech.wear2.application.CommonApplication;
import com.cavytech.wear2.entity.GetSleepentity;
import com.cavytech.wear2.entity.SleepRetrun;
import com.cavytech.wear2.entity.UserEntity;
import com.cavytech.wear2.util.CacheUtils;
import com.cavytech.wear2.util.Constants;
import com.cavytech.wear2.util.DateHelper;
import com.cavytech.wear2.util.SerializeUtils;
import com.cavytech.wear2.util.SleepCount;
import com.cavytech.widget.StepsPick;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by libin on 2016/5/10.
 * 邮箱：bin.li@tunshu.com
 */
public class DaySleepFragmrnt extends Fragment implements OnChartValueSelectedListener, StepsPick.OnValueChangeListener {
    private StepsPick day_wheel;
    private PieChart day_sleep_fragment_bar_chart;

    private Typeface tf;

    protected String[] mParties = new String[]{"深睡", "浅睡"};

    private TextView tv_vstep_time_hour;
    private TextView tv_vstep_time_minute;
    private TextView tv_qstep_time_hour;
    private TextView tv_qstep_time_minute;
    private TextView tv_step_target;

    String[] dateCounts;

    /**
     * data日期
     */
    private ArrayList dateCount;
    private String WheelReturnData;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.day_sleep_fragment, null);

        day_wheel = (StepsPick) view.findViewById(R.id.day_wheel);
        day_sleep_fragment_bar_chart = (PieChart) view.findViewById(R.id.day_sleep_fragment_bar_chart);

        tv_vstep_time_hour = (TextView) view.findViewById(R.id.tv_vstep_time_hour);
        tv_vstep_time_minute = (TextView) view.findViewById(R.id.tv_vstep_time_minute);
        tv_qstep_time_hour = (TextView) view.findViewById(R.id.tv_qstep_time_hour);
        tv_qstep_time_minute = (TextView) view.findViewById(R.id.tv_qstep_time_minute);
        tv_step_target = (TextView) view.findViewById(R.id.tv_step_target);

        day_sleep_fragment_bar_chart.setUsePercentValues(true);
        day_sleep_fragment_bar_chart.setDescription("");
        day_sleep_fragment_bar_chart.setExtraOffsets(5, 10, 5, 15);


        day_sleep_fragment_bar_chart.setBackgroundColor(getResources().getColor(R.color.top_bg_color));
        day_sleep_fragment_bar_chart.setHoleColor(getResources().getColor(R.color.top_bg_color));
        day_sleep_fragment_bar_chart.setDrawHoleEnabled(true);//是否显示空心圆

        day_sleep_fragment_bar_chart.setTouchEnabled(true);

        day_sleep_fragment_bar_chart.setDragDecelerationFrictionCoef(0.95f);

//      tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
//
//      day_sleep_fragment_bar_chart.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));
        //day_sleep_fragment_bar_chart.setCenterText(generateCenterSpannableText());//设置圆环中间字体样式

        day_sleep_fragment_bar_chart.setExtraOffsets(20.f, 13.f, 20.f, 20.f); //设置圆的位置 间接设置了圆的大小

        day_sleep_fragment_bar_chart.setTransparentCircleColor(Color.WHITE);
        day_sleep_fragment_bar_chart.setTransparentCircleAlpha(100);

        day_sleep_fragment_bar_chart.setHoleRadius(60f);
        day_sleep_fragment_bar_chart.setTransparentCircleRadius(60f);

        day_sleep_fragment_bar_chart.setDrawCenterText(false);

        day_sleep_fragment_bar_chart.setRotationAngle(10);
        // enable rotation of the chart by touch
        day_sleep_fragment_bar_chart.setRotationEnabled(true);
        day_sleep_fragment_bar_chart.setHighlightPerTapEnabled(true);

        // day_sleep_fragment_bar_chart.setUnit(" €");
        // day_sleep_fragment_bar_chart.setDrawUnitsInChart(true);

        // add a selection listener
        day_sleep_fragment_bar_chart.setOnChartValueSelectedListener(this);

        //day_sleep_fragment_bar_chart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // day_sleep_fragment_bar_chart.spin(2000, 0, 360);

        Legend l = day_sleep_fragment_bar_chart.getLegend();
        l.setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);
        l.setFormSize(10f);
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(6f);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setTextColor(Color.WHITE);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getWheelDateCountFromNate();

        setTodayDate();

    }

    /**
     * 得到日期Count
     */
    private void getWheelDateCountFromNate() {

        dateCount = new ArrayList();

        String data = CacheUtils.getString(getActivity(), Constants.CESHI);

        dateCounts = data.split(",");
        for (int i = 0; i < dateCounts.length; i++) {
            dateCount.add(i, dateCounts[i]);
            Log.e("TAG", "测试--" + dateCounts[i]);
        }

        if (dateCount.size() > 0) {
            WheelReturnData = (String) dateCount.get(dateCount.size() - 1);
            Log.e("TAG", "得到日期RealdateCount____________" + WheelReturnData);
        }

        intDayWheel();
    }


    /*private ArrayList singleElement(ArrayList dateCount) {
        ArrayList newAl = new ArrayList();

        for (Iterator it = dateCount.iterator(); it.hasNext(); ) {
            Object obj = it.next();
            if (!newAl.contains(obj)) {
                newAl.add(obj);
            }
        }
        return newAl;
    }*/

    private ArrayList<Entry> yVals1;

    private void setData(int count, int vSleep, int dSleep) {

//        float mult = range;

        yVals1 = new ArrayList<Entry>();

        // IMPORTANT: In a PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other.
//        for (int i = 0; i < count + 1; i++) {
//            yVals1.add(new Entry((float) (Math.random() * mult) + mult / 5, i));
//        }

        //圆环数值 写入数值比例自己计算
        yVals1.add(new Entry(vSleep, 0));
        yVals1.add(new Entry(dSleep, 1));

        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < count + 1; i++)
            xVals.add(mParties[i % mParties.length]);

        PieDataSet dataSet = new PieDataSet(yVals1, "");
        dataSet.setSliceSpace(0f);
        dataSet.setSelectionShift(0f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        colors.add(getResources().getColor(R.color.pie_2db));
        colors.add(getResources().getColor(R.color.pie_36f));

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        dataSet.setValueLinePart1OffsetPercentage(100.f);
        dataSet.setValueLinePart1Length(0.3f);
        dataSet.setValueLinePart2Length(0.3f);
        dataSet.setValueLineColor(Color.WHITE);
        //dataSet.setDrawValues(false);//设置是否显示line
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(15f); //圆环上面显示的内容
        data.setValueTextColor(Color.WHITE);
    /*    ArrayList<String > a = new ArrayList<>();
        a.add("");
        data.setXVals(a);*/
        day_sleep_fragment_bar_chart.setData(data);

        // undo all highlights
        day_sleep_fragment_bar_chart.highlightValues(null);

        day_sleep_fragment_bar_chart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        day_sleep_fragment_bar_chart.invalidate();
    }

    private void intDayWheel() {

        /*String dateString = getDateString();

        ArrayList emptyArrayList = new ArrayList();
        emptyArrayList.add(dateString);
*/
        ArrayList whellDateArray = new ArrayList();

        for (int i = 0; i < dateCount.size(); i++) {
            String subStringData = dateCount.get(i).toString().substring(5, 10);


            whellDateArray.add(subStringData);
        }

        if (whellDateArray.size() > 0) {
            day_wheel.initViewParam(whellDateArray, whellDateArray.size() - 1);
        } /*else {
            day_wheel.initViewParam(emptyArrayList, emptyArrayList.size() - 1);
        }*/

        day_wheel.setTextAttrs(14, 14,
                Color.argb(255, 255, 255, 255),
                getResources().getColor(R.color.step_text),
                false, true);

        day_wheel.setValueChangeListener(this);

        if(WheelReturnData.equals(DateHelper.getSystemDateString("yyyy-MM-dd"))){
            setTodayDate();
        }else {
            setDate();
        }

    }

    /**
     * 设置睡眠数据
     */
    public void setDate(){
        try {
            List<GetSleepentity.DataBean.SleepDataBean> daySleep = CommonApplication.dm.selector(GetSleepentity.DataBean.SleepDataBean.class).where("dateTime", "LIKE", WheelReturnData+"%").findAll();

            if(daySleep != null ){
                if(daySleep.size() != 0){
                    setData(1, daySleep.get(0).getDeep_time() ,daySleep.get(0).getTotal_time()-daySleep.get(0).getDeep_time());
                    int total_time = daySleep.get(0).getTotal_time();
                    int deep_time = daySleep.get(0).getDeep_time();//深睡
                    int shallow_time = total_time - deep_time;//浅睡

                    tv_qstep_time_hour.setText(deep_time / 60 +" ");
                    tv_qstep_time_minute.setText(" "+deep_time % 60+" ");
                    tv_vstep_time_hour.setText(shallow_time / 60+" ");
                    tv_vstep_time_minute.setText(" "+shallow_time % 60+"");


                    /**
                     * 此处需要获得目标 需要更改
                     */
                    UserEntity.ProfileEntity userInfo = (UserEntity.ProfileEntity) SerializeUtils.unserialize(Constants.SERIALIZE_USERINFO);

                    tv_step_target.setText((int)((float)total_time /userInfo.getSleep_time_goal() * 100)+" ");
                }else {
                    setData(1,  0,  0);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置睡眠数据
     */
    public void setTodayDate(){
        SleepRetrun daySleep = SleepCount.initsleep();

        if(daySleep != null ){
                if(daySleep.getSleeptime() != 0){
                    setData(1,daySleep.getDeeptime(),daySleep.getNormaltime() );
                    int deep_time =daySleep.getDeeptime() ;//深睡
                    int shallow_time = daySleep.getNormaltime();//浅睡
                    int total_time =  daySleep.getSleeptime();//睡眠

                    tv_qstep_time_hour.setText(deep_time / 60 +" ");
                    tv_qstep_time_minute.setText(" "+deep_time % 60+" ");
                    tv_vstep_time_hour.setText(shallow_time / 60+" ");
                    tv_vstep_time_minute.setText(" "+shallow_time % 60+"");

                    /**
                     * 此处需要获得目标 需要更改
                     */
                    UserEntity.ProfileEntity userInfo = (UserEntity.ProfileEntity) SerializeUtils.unserialize(Constants.SERIALIZE_USERINFO);

                    tv_step_target.setText((int)((float)total_time /userInfo.getSleep_time_goal() * 100)+" ");
                }else {
                    setData(1,  0,  0);
                }
            }

    }


    @Override
    public void onValueChange(View wheel, float value) {

    }

    @Override
    public void onValueChangeEnd(View wheel, float value) {

        if (dateCount.size() > 0) {
            WheelReturnData = (String) dateCount.get((int) value);
        }

        if(WheelReturnData.equals(DateHelper.getSystemDateString("yyyy-MM-dd"))){
            setTodayDate();
        }else {
            setDate();
        }

    }



    /*private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("MPAndroidChart\ndeveloped by Philipp Jahoda");
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 14, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
        s.setSpan(new ForegroundColorSpan(Color.BLUE), 14, s.length() - 15, 0);
        s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;
    }*/

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }


    /*public String getDateString() {
        String todayData = "";

        Time time = new Time("GMT+8");
        time.setToNow();
        int year = time.year;
        int month = time.month;
        int day = time.monthDay;
        int minute = time.minute;
        int hour = time.hour;
        int sec = time.second;
        todayData = year + "-" + month + "-" + day;

        Log.w("TAG", "得到日期getDateString____________" + todayData);

        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = sDateFormat.format(new java.util.Date());

        Log.w("TAG", "得到日期date____________" + date);
        return date;

    }*/

}
