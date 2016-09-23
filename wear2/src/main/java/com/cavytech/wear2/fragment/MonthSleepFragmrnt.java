package com.cavytech.wear2.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cavytech.wear2.R;
import com.cavytech.wear2.application.CommonApplication;
import com.cavytech.wear2.entity.GetSleepentity;
import com.cavytech.wear2.entity.TImeBean;
import com.cavytech.wear2.util.CacheUtils;
import com.cavytech.wear2.util.Constants;
import com.cavytech.wear2.util.DateHelper;
import com.cavytech.widget.StepsPick;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.xutils.ex.DbException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by libin on 2016/5/10.
 * 邮箱：bin.li@tunshu.com
 */
public class MonthSleepFragmrnt extends Fragment implements StepsPick.OnValueChangeListener {
    private StepsPick month_wheel;
    private BarChart month_sleep_fragment_bar_chart;
    private BarData mBarData;

    private ArrayList<BarEntry> yValues;

    private ArrayList<TImeBean> empteyTImeBeen;

    //data日期
    private ArrayList<String> dateCount;

    //得到传递的集合数据 包括开始日期 结束日期 显示时间
    private ArrayList<TImeBean> tImeBeenList;

    private TextView tv_vstep_time_hour;
    private TextView tv_vstep_time_minute;
    private TextView tv_qstep_time_hour;
    private TextView tv_qstep_time_minute;
    private TextView tv_step_complete_time_minute;
    private TextView tv_step_complete_time_hour;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.month_sleep_fragment, null);

        month_wheel = (StepsPick) view.findViewById(R.id.month_wheel);
        month_sleep_fragment_bar_chart = (BarChart) view.findViewById(R.id.month_sleep_fragment_bar_chart);

        tv_vstep_time_hour = (TextView) view.findViewById(R.id.tv_vstep_time_hour);
        tv_vstep_time_minute = (TextView) view.findViewById(R.id.tv_vstep_time_minute);
        tv_qstep_time_hour = (TextView) view.findViewById(R.id.tv_qstep_time_hour);
        tv_qstep_time_minute = (TextView) view.findViewById(R.id.tv_qstep_time_minute);
        tv_step_complete_time_minute = (TextView) view.findViewById(R.id.tv_step_complete_time_minute);
        tv_step_complete_time_hour = (TextView) view.findViewById(R.id.tv_step_complete_time_hour);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //初始化得到已经入当天的数据
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(DateHelper.getSystemDateString("yyyy-MM-dd"));
        empteyTImeBeen = exchangeMonth(arrayList);

        intDayWheel();

        getDateFromDB(empteyTImeBeen.get(0).getStartTime(),empteyTImeBeen.get(0).getEndTime());

    }

    private ArrayList<TImeBean> exchangeMonth(ArrayList<String> arrayList) {
        ArrayList<TImeBean> arrayListTime = new ArrayList<TImeBean>();

        DateHelper instance = DateHelper.getInstance();
        for (int i = 0; i < arrayList.size(); i++) {
            TImeBean tImeBean = new TImeBean();

            Date date2 = instance.getDate2(arrayList.get(i));

            String month = instance.getMonthByDate(date2) + 1 + "";

            Date date3 = instance.getFirstDayOfMonth(date2);
            Date date4 = instance.getLastDayOfMonth(date2);

            String firstMonthDay = instance.getDataString_2(date3);
            String LastMonthDay = instance.getDataString_2(date4);

            tImeBean.setStartTime(firstMonthDay);
            tImeBean.setEndTime(LastMonthDay);
            tImeBean.setShouDate(month);

            //去除重复
            if (!arrayListTime.contains(tImeBean)) {
                arrayListTime.add(tImeBean);
            }
        }
        return arrayListTime;

    }

    private void intDayWheel() {
        String data = CacheUtils.getString(getActivity(), Constants.CESHI);

        ArrayList emptyArrayList = new ArrayList();

        emptyArrayList.add(empteyTImeBeen.get(0).getShouDate());

        dateCount = new ArrayList<String>();

        String[] split = data.split(",");
        for (int i = 0; i < split.length; i++) {
            dateCount.add(i, split[i]);
        }

        //将日期转化成周的集合 包括开始时间 结束时间 显示时间
        tImeBeenList = exchangeMonth(dateCount);

        for (int i = 0; i < tImeBeenList.size(); i++) {
            tImeBeenList.get(i).getStartTime();
            tImeBeenList.get(i).getEndTime();
            tImeBeenList.get(i).getShouDate();

        }

        ArrayList whellDateArray = new ArrayList();

        for (int i = 0; i < tImeBeenList.size(); i++) {

            whellDateArray.add(tImeBeenList.get(i).getShouDate());
        }

        month_wheel.initViewParam(whellDateArray, whellDateArray.size() - 1);
        month_wheel.setTextAttrs(14, 14,
                Color.argb(255, 255, 255, 255),
                getResources().getColor(R.color.step_text),
                false, true);
        month_wheel.setValueChangeListener(this);

    }

    @Override
    public void onValueChange(View wheel, float value) {

    }

    @Override
    public void onValueChangeEnd(View wheel, float value) {

        getDateFromDB(tImeBeenList.get((int) value).getStartTime(), tImeBeenList.get((int) value).getEndTime());

    }

    /**
     * 从数据库拿到数据放入图表中
     */
    private void getDateFromDB(String startData, String endtData) {
        int monthDay = DateHelper.getInstance().getMonthDay(endtData, startData);

        yValues = new ArrayList<BarEntry>();

        int totalTime =0;
        int deepTime = 0;
        int dayCount = 0;
        try {
            List<GetSleepentity.DataBean.SleepDataBean> monthSleep = CommonApplication.dm.selector(GetSleepentity.DataBean.SleepDataBean.class).where("dateTime", ">=", startData).and("dateTime", "<=", endtData).findAll();

            if(monthSleep!=null){

                /**
                 * 给数据集合补0
                 */
                ArrayList<GetSleepentity.DataBean.SleepDataBean> arrayList = new ArrayList();

                int j = 0;
                for (int i = 0; i < monthDay; i++) {

                    GetSleepentity.DataBean.SleepDataBean sleepDataBean = new GetSleepentity.DataBean.SleepDataBean();

                    if (j >= monthSleep.size()) {
                        sleepDataBean.setDeep_time(0);
                        sleepDataBean.setTotal_time(0);
                        arrayList.add(sleepDataBean);
                    }else {
                        if (DateHelper.getInstance().getStrDate2(i, startData).equals(monthSleep.get(j).getDate())) {
                            sleepDataBean.setDeep_time(monthSleep.get(j).getDeep_time());
                            sleepDataBean.setTotal_time(monthSleep.get(j).getTotal_time());
                            j++;
                            arrayList.add(sleepDataBean);
                        } else {
                            sleepDataBean.setDeep_time(0);
                            sleepDataBean.setTotal_time(0);
                            arrayList.add(sleepDataBean);
                        }
                    }

                }

                dayCount= monthSleep.size();

                for (int i = 0; i < arrayList.size(); i++) {

                    yValues.add(new BarEntry(new float[]{(float)arrayList.get(i).getDeep_time() / 60,(float)(arrayList.get(i).getTotal_time()-arrayList.get(i).getDeep_time()) / 60}, i));

                    totalTime += arrayList.get(i).getTotal_time();
                    deepTime += arrayList.get(i).getDeep_time();

                }
            }

        } catch (DbException e) {
            e.printStackTrace();
        }

        int shallowTime = totalTime - deepTime;

        int avarageTime = 0;
        int avaragetHour = 0;
        int avaragetMinute = 0;
        if(dayCount != 0){
            avarageTime = totalTime/dayCount;

            avaragetHour = avarageTime/60;
            avaragetMinute = avarageTime%60;

        }

        tv_qstep_time_hour.setText(deepTime / 60 +" ");
        tv_qstep_time_minute.setText(" "+deepTime % 60+" ");
        tv_vstep_time_hour.setText(shallowTime / 60+" ");
        tv_vstep_time_minute.setText(" "+shallowTime % 60+" ");

        if(dayCount == 0){
            tv_step_complete_time_minute.setText(0+" ");
            tv_step_complete_time_hour.setText(" "+0+" ");
        }else {
            if(avarageTime != 0){
                tv_step_complete_time_minute.setText(avaragetMinute+" ");
                tv_step_complete_time_hour.setText(" "+ avaragetHour+" ");
            }else{
                tv_step_complete_time_minute.setText(0+" ");
                tv_step_complete_time_hour.setText(" "+0+"");
            }
        }


        mBarData = getBarData(monthDay,yValues);
        showBarChart(month_sleep_fragment_bar_chart, mBarData);
    }

    private void showBarChart(final BarChart barChart, BarData barData) {
        // 如果没有数据的时候，会显示这个，类似ListView的EmptyView
        barChart.setNoDataTextDescription(getString(R.string.temporarily_no_data));

        barChart.setData(barData); // 设置数据

        barChart.setDrawBorders(false); //是否在折线图上添加边框

        barChart.setDescription("12h");// 数据描述
        barChart.setDescriptionPosition(50,40);
        barChart.setDescriptionColor(Color.WHITE);

        barChart.setDrawGridBackground(false); // 是否显示表格颜色
        //barChart.setGridBackgroundColor(Color.RED); // 表格的的颜色
        barChart.setBackgroundColor(getResources().getColor(R.color.top_bg_color));// 设置整个图标控件的背景
        barChart.setDrawBarShadow(false);//柱状图没有数据的部分是否显示阴影效果

        barChart.setTouchEnabled(true); // 设置是否可以触摸
        barChart.setDragEnabled(true);// 是否可以拖拽
        barChart.setScaleEnabled(true);// 是否可以缩放
        barChart.setPinchZoom(false);//y轴的值是否跟随图表变换缩放;如果禁止，y轴的值会跟随图表变换缩放
        barChart.setDoubleTapToZoomEnabled(false);//两指拉伸
        barChart.setScaleYEnabled(false);//是否可以上下缩放

        barChart.setDrawValueAboveBar(true);//柱状图上面的数值显示在柱子上面还是柱子里面

        barChart.getXAxis().setDrawGridLines(false);//是否显示竖直标尺线
        barChart.getXAxis().setLabelsToSkip(1);//设置横坐标显示的间隔
//        barChart.getXAxis().setLabelRotationAngle(20);//设置横坐标倾斜角度
//        barChart.getXAxis().setSpaceBetweenLabels(50);
        barChart.getXAxis().setDrawLabels(true);//是否显示X轴数值
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的位置 默认在上方

        barChart.getAxisRight().setDrawLabels(false);//右侧是否显示Y轴数值
        barChart.getAxisRight().setEnabled(false);//是否显示最右侧竖线
        barChart.getAxisRight().setDrawAxisLine(false);
        barChart.getAxisLeft().setDrawAxisLine(false);
        barChart.getXAxis().setDrawAxisLine(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getXAxis().setGridColor(Color.WHITE);

        barChart.getXAxis().setTextColor(Color.WHITE);

        barChart.getAxisLeft().setSpaceBottom(3); //设置横轴距离底部距离
        barChart.getAxisLeft().setSpaceTop(3); //设置横轴距离顶部距离

        barChart.getAxisLeft().setLabelCount(0, true);//设置Y轴显示数量

//        barChart.getAxisLeft().setAxisMaxValue(3.4f);//设置Y轴最大值
//        barChart.getAxisLeft().setAxisMinValue(3.1f);//设置Y轴最小值

        YAxisValueFormatter custom = new MyYAxisValueFormatter();//自定义Y轴文字样式
        barChart.getAxisLeft().setValueFormatter(custom);

        //barChart.getLegend().setPosition(Legend.LegendPosition.RIGHT_OF_CHART_INSIDE);//设置比例图标的位置
        barChart.getLegend().setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);//设置比例图标和文字之间的位置方向
        //barChart.getLegend().setTextColor(Color.WHITE);
        //barChart.getLegend().setForm(Legend.LegendForm.CIRCLE);

        Legend l = barChart.getLegend();
        l.setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);
        l.setFormSize(10f);
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(6f);
        l.setTextColor(Color.WHITE);
        l.setForm(Legend.LegendForm.CIRCLE);

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                barDataSet.setDrawValues(false);
            }

            @Override
            public void onNothingSelected() {
                barDataSet.setDrawValues(false);
            }
        });

        barChart.animateXY(1000,1000);
    }

    private BarDataSet barDataSet;

    private BarData getBarData(int count, ArrayList<BarEntry> yValues) {
        ArrayList<String> xValues = new ArrayList<String>();
        for (int i = 1; i <= count; i++) {
            xValues.add(i + "");
        }

        // y轴的数据集合
        barDataSet = new BarDataSet(yValues, "");
        barDataSet.setStackLabels(new String[]{getString(R.string.deep_sleep), getString(R.string.light_sleep)});
        barDataSet.setBarSpacePercent(85);
        barDataSet.setVisible(true);//是否显示柱状图柱子
        barDataSet.setDrawValues(false);//是否显示柱子上面的数值
        barDataSet.setColors(getColors());
        barDataSet.setValueTextColor(Color.WHITE);


        ArrayList<BarDataSet> barDataSets = new ArrayList<BarDataSet>();
        barDataSets.add(barDataSet); // add the datasets

        BarData barData = new BarData(xValues, barDataSet);

        return barData;
    }

    public class MyYAxisValueFormatter implements YAxisValueFormatter {

        private DecimalFormat mFormat;

        public MyYAxisValueFormatter() {
            mFormat = new DecimalFormat("###,###,###,##0");
        }

        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            return mFormat.format(value) + "h";
        }
    }

    private int[] getColors() {

        int stacksize = 2;

        // have as many colors as stack-values per entry
        int[] colors = new int[stacksize];

        for (int i = 0; i < stacksize; i++) {
            colors[i] = ColorTemplate.SLEEP_COLORS[i];
        }

        return colors;
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

    /**
     * 得到日期Count
     */
    /*private void getWheelDateCountFromNate() {
        String data = CacheUtils.getString(getActivity(), Constants.CESHI);

        dateCount = new ArrayList<String>();

        String[] split = data.split(",");
        for (int i = 0; i < split.length; i++) {
            dateCount.add(i, split[i]);
            Log.e("TAG", "测试--weekFragment" + split[i]);
        }

        //将日期转化成周的集合 包括开始时间 结束时间 显示时间
        tImeBeenList = exchangeMonth(dateCount);

        for (int i = 0; i < tImeBeenList.size(); i++) {
            tImeBeenList.get(i).getStartTime();
            tImeBeenList.get(i).getEndTime();
            tImeBeenList.get(i).getShouDate();

        }

                *//*if (dateCount.size() > 0) {
                    WheelReturnData = (String) dateCount.get(dateCount.size() - 1);
                    Log.e("TAG", "得到日期RealdateCount____________" + WheelReturnData);
                }

                getdatafromdb(WheelReturnData);*//*

        intDayWheel();
    }*/
}
