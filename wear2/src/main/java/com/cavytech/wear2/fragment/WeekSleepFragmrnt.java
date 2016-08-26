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
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
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
public class WeekSleepFragmrnt extends Fragment implements StepsPick.OnValueChangeListener {
    private StepsPick week_wheel;
    private BarChart week_sleep_fragment_bar_chart;
    private BarData mBarData;

    ArrayList<BarEntry> yValues;

    ArrayList<TImeBean> empteyTImeBeen;

    private TextView tv_vstep_time_hour;
    private TextView tv_vstep_time_minute;
    private TextView tv_qstep_time_hour;
    private TextView tv_qstep_time_minute;
    private TextView tv_step_complete_time_minute;
    private TextView tv_step_complete_time_hour;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.week_sleep_fragment, null);

        week_wheel = (StepsPick) view.findViewById(R.id.week_wheel);
        week_sleep_fragment_bar_chart = (BarChart) view.findViewById(R.id.week_sleep_fragment_bar_chart);

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
        empteyTImeBeen = exchangeWeek(arrayList);

        intDayWheel();

        getDateFromDB(empteyTImeBeen.get(0).getStartTime(), empteyTImeBeen.get(0).getEndTime());

    }

    /**
     * 从数据库拿到数据放入图表中
     */
    private void getDateFromDB(String startData, String endtData) {
        yValues = new ArrayList<BarEntry>();

        int totalTime =0;
        int deepTime = 0;
        int dayCount = 0;
        try {
            List<GetSleepentity.DataBean.SleepDataBean> weekSleep = CommonApplication.dm.selector(GetSleepentity.DataBean.SleepDataBean.class).where("dateTime", ">=", startData).and("dateTime", "<=", endtData).findAll();

            if(weekSleep!=null){

                /**
                 * 给数据集合补0
                 */
                ArrayList<GetSleepentity.DataBean.SleepDataBean> arrayList = new ArrayList();

                int j = 0;
                for (int i = 0; i < 7; i++) {

                    GetSleepentity.DataBean.SleepDataBean sleepDataBean = new GetSleepentity.DataBean.SleepDataBean();

                    if (j >= weekSleep.size()) {
                        sleepDataBean.setDeep_time(0);
                        sleepDataBean.setTotal_time(0);
                        arrayList.add(sleepDataBean);
                    }else {
                        if (DateHelper.getInstance().getStrDate2(i, startData).equals(weekSleep.get(j).getDate())) {
                            sleepDataBean.setDeep_time(weekSleep.get(j).getDeep_time());
                            sleepDataBean.setTotal_time(weekSleep.get(j).getTotal_time());
                            j++;
                            arrayList.add(sleepDataBean);
                        } else {
                            sleepDataBean.setDeep_time(0);
                            sleepDataBean.setTotal_time(0);
                            arrayList.add(sleepDataBean);
                        }
                    }

                }

                dayCount= arrayList.size();

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
            tv_step_complete_time_hour.setText(" "+0+"");
        }else {
            if(avarageTime != 0){
                tv_step_complete_time_minute.setText(avaragetMinute+" ");
                tv_step_complete_time_hour.setText(" "+ avaragetHour+" ");
            }else{
                tv_step_complete_time_minute.setText(0+" ");
                tv_step_complete_time_hour.setText(" "+0+"");
            }

        }

        mBarData = getBarData(7, yValues);
        showBarChart(week_sleep_fragment_bar_chart, mBarData);
    }

    private ArrayList<TImeBean> exchangeWeek(ArrayList<String> arrayList) {
        ArrayList<TImeBean> arrayListTime = new ArrayList<TImeBean>();

        DateHelper instance = DateHelper.getInstance();
        for (int i = 0; i < arrayList.size(); i++) {


            TImeBean tImeBean = new TImeBean();

            Date date2 = instance.getDate2(arrayList.get(i));

            Date dateFirst = instance.getFirstDayOfWeek(date2);
            Date dateLast = instance.getLastDayOfWeek(date2);

            String stringFirst = instance.getDataString_2(dateFirst);
            String stringLast = instance.getDataString_2(dateLast);

            String fm = instance.subStringData(1, stringFirst);
            String lm = instance.subStringData(1, stringLast);
            String fd = instance.subStringData(2, stringFirst);
            String ld = instance.subStringData(2, stringLast);

            String showData = instance.deleteDate(fm) + "." + instance.deleteDate(fd) + "-" + instance.deleteDate(lm) + "." + instance.deleteDate(ld);

            tImeBean.setStartTime(stringFirst);
            tImeBean.setEndTime(stringLast);
            tImeBean.setShouDate(showData);


            if (!arrayListTime.contains(tImeBean)) {
                arrayListTime.add(tImeBean);
            }

        }

        return arrayListTime;

    }

    /**
     * Pk列表
     */
//    private ArrayList<GetStepCountBean.StepListBean> stepList;

    /**
     * data日期
     */
    private ArrayList<String> dateCount;

    /**
     * 得到传递的集合数据 包括开始日期 结束日期 显示时间
     */
    private ArrayList<TImeBean> tImeBeenList;


    private void intDayWheel() {
        String data = CacheUtils.getString(getActivity(), Constants.CESHI);

        ArrayList emptyArrayList = new ArrayList();

        emptyArrayList.add(empteyTImeBeen.get(0).getShouDate());

        dateCount = new ArrayList<String>();

        String[] split = data.split(",");
        for (int i = 0; i < split.length; i++) {
            dateCount.add(i, split[i]);
//            Log.e("TAG", "测试--weekFragment" + split[i]);
        }

        //将日期转化成周的集合 包括开始时间 结束时间 显示时间
        tImeBeenList = exchangeWeek(dateCount);

        for (int i = 0; i < tImeBeenList.size(); i++) {
            tImeBeenList.get(i).getStartTime();
            tImeBeenList.get(i).getEndTime();
            tImeBeenList.get(i).getShouDate();

        }

//        initsleep(tImeBeenList.get(tImeBeenList.size() - 1).getStartTime());

        ArrayList whellDateArray = new ArrayList();

        for (int i = 0; i < tImeBeenList.size(); i++) {

            whellDateArray.add(tImeBeenList.get(i).getShouDate());
        }

        if (whellDateArray.size() > 0) {
            week_wheel.initViewParam(whellDateArray, whellDateArray.size() - 1);
        } else {
            week_wheel.initViewParam(emptyArrayList, emptyArrayList.size() - 1);
        }

        week_wheel.setTextAttrs(14, 14,
                Color.argb(255, 255, 255, 255),
                getResources().getColor(R.color.step_text),
                false, true);

        week_wheel.setValueChangeListener(this);

    }

    @Override
    public void onValueChange(View wheel, float value) {

    }

    @Override
    public void onValueChangeEnd(View wheel, float value) {

        getDateFromDB(tImeBeenList.get((int) value).getStartTime(), tImeBeenList.get((int) value).getEndTime());


    }

    private void showBarChart(BarChart barChart, BarData barData) {
        // 如果没有数据的时候，会显示这个，类似ListView的EmptyView
        barChart.setNoDataTextDescription("暂无数据");

        barChart.setData(barData); // 设置数据

        barChart.setDrawBorders(false); //是否在折线图上添加边框

        barChart.setDescription("12h");// 数据描述
        barChart.setDescriptionPosition(50, 40);//数据描述的位置
        barChart.setDescriptionColor(Color.WHITE);//数据的颜色

        barChart.setDrawGridBackground(false); // 是否显示表格颜色
        //barChart.setGridBackgroundColor(Color.RED); // 表格的的颜色
        barChart.setBackgroundColor(getResources().getColor(R.color.top_bg_color));// 设置整个图标控件的背景
        barChart.setDrawBarShadow(false);//柱状图没有数据的部分是否显示阴影效果

        barChart.setTouchEnabled(true); // 设置是否可以触摸
        barChart.setDragEnabled(false);// 是否可以拖拽
        barChart.setScaleEnabled(false);// 是否可以缩放
        barChart.setPinchZoom(false);//y轴的值是否跟随图表变换缩放;如果禁止，y轴的值会跟随图表变换缩放

        barChart.setDrawValueAboveBar(true);//柱状图上面的数值显示在柱子上面还是柱子里面

        barChart.getXAxis().setDrawGridLines(false);//是否显示竖直标尺线
//        barChart.getXAxis().setLabelsToSkip(5);//设置横坐标显示的间隔
//        barChart.getXAxis().setLabelRotationAngle(20);//设置横坐标倾斜角度
//        barChart.getXAxis().setSpaceBetweenLabels(50);
        barChart.getXAxis().setDrawLabels(true);//是否显示X轴数值
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的位置 默认在上

        barChart.getAxisRight().setDrawLabels(false);//右侧是否显示Y轴数值
        barChart.getAxisRight().setEnabled(false);//是否显示最右侧竖线
        barChart.getAxisRight().setDrawAxisLine(false);
        barChart.getAxisLeft().setDrawAxisLine(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getXAxis().setDrawAxisLine(false);
        barChart.getXAxis().setGridColor(Color.WHITE);

        barChart.getXAxis().setTextColor(Color.WHITE);

        barChart.getAxisLeft().setLabelCount(0, true);//设置Y轴显示数量

        barChart.getAxisLeft().setSpaceBottom(3); //设置横轴距离底部距离
        barChart.getAxisLeft().setSpaceTop(3); //设置横轴距离顶部距离

        YAxisValueFormatter custom = new MyYAxisValueFormatter();//自定义Y轴文字样式
        barChart.getAxisLeft().setValueFormatter(custom);

        //barChart.getLegend().setPosition(Legend.LegendPosition.RIGHT_OF_CHART_INSIDE);//设置比例图标的位置
        //barChart.getLegend().setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);//设置比例图标和文字之间的位置方向
        //barChart.getLegend().setTextColor(getResources().getColor(R.color.bar_lend));
        //barChart.getLegend().setForm(Legend.LegendForm.CIRCLE);

        Legend l = barChart.getLegend();
        l.setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);
        l.setFormSize(10f);
        l.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);//设置比例图标和文字之间的位置方向
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(6f);
        l.setTextColor(Color.WHITE);
        l.setForm(Legend.LegendForm.CIRCLE);

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                barDataSet.setDrawValues(true);
            }

            @Override
            public void onNothingSelected() {
                barDataSet.setDrawValues(false);
            }
        });

        barChart.animateXY(1000, 1000);
    }

    private BarDataSet barDataSet;

    private BarData getBarData(int count, ArrayList<BarEntry> yValues) {
        ArrayList<String> xValues = new ArrayList<String>();
        xValues.add("周一");
        xValues.add("周二");
        xValues.add("周三");
        xValues.add("周四");
        xValues.add("周五");
        xValues.add("周六");
        xValues.add("周日");
        /*for (int i = 0; i < count; i++) {
            xValues.add(i + "");
        }*/

        if (week_sleep_fragment_bar_chart.getData() != null &&
                week_sleep_fragment_bar_chart.getData().getDataSetCount() > 0) {
            barDataSet = (BarDataSet) week_sleep_fragment_bar_chart.getData().getDataSetByIndex(0);
            barDataSet.setYVals(yValues);
            week_sleep_fragment_bar_chart.getData().setXVals(xValues);
            week_sleep_fragment_bar_chart.getData().notifyDataChanged();
            week_sleep_fragment_bar_chart.notifyDataSetChanged();
        } else {
            barDataSet = new BarDataSet(yValues, "");
            barDataSet.setColors(getColors());
            barDataSet.setStackLabels(new String[]{"深睡", "浅睡"});
            barDataSet.setBarSpacePercent(85);
            barDataSet.setVisible(true);//是否显示柱状图柱子
            barDataSet.setDrawValues(false);//是否显示柱子上面的数值
            barDataSet.setValueTextColor(Color.WHITE);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(barDataSet);

            BarData data = new BarData(xValues, dataSets);
            //data.setValueFormatter(new MyValueFormatter());

            week_sleep_fragment_bar_chart.setData(data);
        }

        week_sleep_fragment_bar_chart.invalidate();

        BarData barData = new BarData(xValues, barDataSet);

        barData.setXVals(xValues);

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

   /* private static List<GetSleepBean.SleepListBean> persons;
    private static String d;

    private static Integer t20;
    private static Integer t10;
    private static Integer tt20;
    private static Integer tt10;

    private static boolean e2;
    private static boolean e1;
    private static boolean e3;
//    private static GetStepCountBean.StepListBean step;

    private HashMap<Integer, Integer> mapSeven;*/

    /**
     * 睡眠算法的处理
     */
    /*public void initsleep(String WheelReturnData) {
        mapSeven = new HashMap<Integer, Integer>();
        mapSeven.put(0, 0);
        mapSeven.put(1, 0);
        mapSeven.put(2, 0);
        mapSeven.put(3, 0);
        mapSeven.put(4, 0);
        mapSeven.put(5, 0);
        mapSeven.put(6, 0);

        ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();

        for (int a = 0; a < 7; a++) {
            String strDate2 = DateHelper.getInstance().getStrDate2(a, WheelReturnData);
//      Log.e("TAG","后退一天---"+strDate2);

//昨天18：20到今天17:40

            try {
                persons = CommonApplication.dm.selector(GetSleepBean.SleepListBean.class)
                        .where("dateTime", ">=", strDate2 + " 18:00:00").and("dateTime", "<=", strDate2 + " 18:00:00").findAll();

            } catch (DbException e) {
                e.printStackTrace();
            }

//        for (int i=0;i<persons.size();i++){
//            Log.e("TAG","数据库数据---"+persons.get(i).getDateTime()+"--"+persons.get(i).getRollCount());
//        }

            if (persons != null) {
                if (persons.size() != 0) {
                    d = persons.get(persons.size() - 1).getDateTime();
                    Log.e("TAG", "数据库最后一天---" + d);
                }

            }

            HashMap<String, Integer> map = new HashMap<String, Integer>();

            map.put(strDate2 + " 18:00:00", 0);
            map.put(strDate2 + " 18:10:00", 0);
            map.put(strDate2 + " 18:20:00", 0);
            map.put(strDate2 + " 18:30:00", 0);
            map.put(strDate2 + " 18:40:00", 0);
            map.put(strDate2 + " 18:50:00", 0);

            map.put(strDate2 + " 19:00:00", 0);
            map.put(strDate2 + " 19:10:00", 0);
            map.put(strDate2 + " 19:20:00", 0);
            map.put(strDate2 + " 19:30:00", 0);
            map.put(strDate2 + " 19:40:00", 0);
            map.put(strDate2 + " 19:50:00", 0);

            map.put(strDate2 + " 20:00:00", 0);
            map.put(strDate2 + " 20:10:00", 0);
            map.put(strDate2 + " 20:20:00", 0);
            map.put(strDate2 + " 20:30:00", 0);
            map.put(strDate2 + " 20:40:00", 0);
            map.put(strDate2 + " 20:50:00", 0);

            map.put(strDate2 + " 21:00:00", 0);
            map.put(strDate2 + " 21:10:00", 0);
            map.put(strDate2 + " 21:20:00", 0);
            map.put(strDate2 + " 21:30:00", 0);
            map.put(strDate2 + " 21:40:00", 0);
            map.put(strDate2 + " 21:50:00", 0);

            map.put(strDate2 + " 22:00:00", 0);
            map.put(strDate2 + " 22:10:00", 0);
            map.put(strDate2 + " 22:20:00", 0);
            map.put(strDate2 + " 22:30:00", 0);
            map.put(strDate2 + " 22:40:00", 0);
            map.put(strDate2 + " 22:50:00", 0);

            map.put(strDate2 + " 23:00:00", 0);
            map.put(strDate2 + " 23:10:00", 0);
            map.put(strDate2 + " 23:20:00", 0);
            map.put(strDate2 + " 23:30:00", 0);
            map.put(strDate2 + " 23:40:00", 0);
            map.put(strDate2 + " 23:50:00", 0);

//        String data = DateHelper.getInstance().getSystemDateString();
//        Log.e("TAG","当前时间---"+data);

            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 00:00:00")) {
                map.put(WheelReturnData + " 00:00:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 00:10:00")) {
                map.put(WheelReturnData + " 00:10:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 00:20:00")) {
                map.put(WheelReturnData + " 00:20:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 00:30:00")) {
                map.put(WheelReturnData + " 00:30:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 00:40:00")) {
                map.put(WheelReturnData + " 00:40:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 00:50:00")) {
                map.put(WheelReturnData + " 00:50:00", 0);
            }


            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 01:00:00")) {
                map.put(WheelReturnData + " 01:00:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 01:10:00")) {
                map.put(WheelReturnData + " 01:10:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 01:20:00")) {
                map.put(WheelReturnData + " 01:20:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 01:30:00")) {
                map.put(WheelReturnData + " 01:30:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 01:40:00")) {
                map.put(WheelReturnData + " 01:40:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 01:50:00")) {
                map.put(WheelReturnData + " 01:50:00", 0);
            }

            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 02:00:00")) {
                map.put(WheelReturnData + " 02:00:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 02:10:00")) {
                map.put(WheelReturnData + " 02:10:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 02:20:00")) {
                map.put(WheelReturnData + " 02:20:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 02:30:00")) {
                map.put(WheelReturnData + " 02:30:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 02:40:00")) {
                map.put(WheelReturnData + " 02:40:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 02:50:00")) {
                map.put(WheelReturnData + " 02:50:00", 0);
            }


            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 03:00:00")) {
                map.put(WheelReturnData + " 03:00:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 03:10:00")) {
                map.put(WheelReturnData + " 03:10:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 03:20:00")) {
                map.put(WheelReturnData + " 03:20:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 03:30:00")) {
                map.put(WheelReturnData + " 03:30:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 03:40:00")) {
                map.put(WheelReturnData + " 03:40:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 03:50:00")) {
                map.put(WheelReturnData + " 03:50:00", 0);
            }


            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 04:00:00")) {
                map.put(WheelReturnData + " 04:00:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 04:10:00")) {
                map.put(WheelReturnData + " 04:10:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 04:20:00")) {
                map.put(WheelReturnData + " 04:20:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 04:30:00")) {
                map.put(WheelReturnData + " 04:30:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 04:40:00")) {
                map.put(WheelReturnData + " 04:40:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 04:50:00")) {
                map.put(WheelReturnData + " 04:50:00", 0);
            }


            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 05:00:00")) {
                map.put(WheelReturnData + " 05:00:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 05:10:00")) {
                map.put(WheelReturnData + " 05:10:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 05:20:00")) {
                map.put(WheelReturnData + " 05:20:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 05:30:00")) {
                map.put(WheelReturnData + " 05:30:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 05:40:00")) {
                map.put(WheelReturnData + " 05:40:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 05:50:00")) {
                map.put(WheelReturnData + " 05:50:00", 0);
            }

            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 06:00:00")) {
                map.put(WheelReturnData + " 06:00:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 06:10:00")) {
                map.put(WheelReturnData + " 06:10:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 06:20:00")) {
                map.put(WheelReturnData + " 06:20:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 06:30:00")) {
                map.put(WheelReturnData + " 06:30:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 06:40:00")) {
                map.put(WheelReturnData + " 06:40:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 06:50:00")) {
                map.put(WheelReturnData + " 06:50:00", 0);
            }


            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 07:00:00")) {
                map.put(WheelReturnData + " 07:00:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 07:10:00")) {
                map.put(WheelReturnData + " 07:10:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 07:20:00")) {
                map.put(WheelReturnData + " 07:20:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 07:30:00")) {
                map.put(WheelReturnData + " 07:30:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 07:40:00")) {
                map.put(WheelReturnData + " 07:40:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 07:50:00")) {
                map.put(WheelReturnData + " 07:50:00", 0);
            }

            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 08:00:00")) {
                map.put(WheelReturnData + " 08:00:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 08:10:00")) {
                map.put(WheelReturnData + " 08:10:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 08:20:00")) {
                map.put(WheelReturnData + " 08:20:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 08:30:00")) {
                map.put(WheelReturnData + " 08:30:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 08:40:00")) {
                map.put(WheelReturnData + " 08:40:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 08:50:00")) {
                map.put(WheelReturnData + " 08:50:00", 0);
            }

            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 09:00:00")) {
                map.put(WheelReturnData + " 09:00:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 09:10:00")) {
                map.put(WheelReturnData + " 09:10:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 09:20:00")) {
                map.put(WheelReturnData + " 09:20:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 09:30:00")) {
                map.put(WheelReturnData + " 09:30:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 09:40:00")) {
                map.put(WheelReturnData + " 09:40:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 09:50:00")) {
                map.put(WheelReturnData + " 09:50:00", 0);
            }


            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 10:00:00")) {
                map.put(WheelReturnData + " 10:00:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 10:10:00")) {
                map.put(WheelReturnData + " 10:10:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 10:20:00")) {
                map.put(WheelReturnData + " 10:20:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 10:30:00")) {
                map.put(WheelReturnData + " 10:30:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 10:40:00")) {
                map.put(WheelReturnData + " 10:40:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 10:50:00")) {
                map.put(WheelReturnData + " 10:50:00", 0);
            }

            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 11:00:00")) {
                map.put(WheelReturnData + " 11:00:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 11:10:00")) {
                map.put(WheelReturnData + " 11:10:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 11:20:00")) {
                map.put(WheelReturnData + " 11:20:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 11:30:00")) {
                map.put(WheelReturnData + " 11:30:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 11:40:00")) {
                map.put(WheelReturnData + " 11:40:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 11:50:00")) {
                map.put(WheelReturnData + " 11:50:00", 0);
            }

            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 12:10:00")) {
                map.put(WheelReturnData + " 12:00:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 12:10:00")) {
                map.put(WheelReturnData + " 12:10:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 12:20:00")) {
                map.put(WheelReturnData + " 12:20:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 12:30:00")) {
                map.put(WheelReturnData + " 12:30:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 12:40:00")) {
                map.put(WheelReturnData + " 12:40:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 12:50:00")) {
                map.put(WheelReturnData + " 12:50:00", 0);
            }


            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 13:00:00")) {
                map.put(WheelReturnData + " 13:00:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 13:10:00")) {
                map.put(WheelReturnData + " 13:10:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 13:20:00")) {
                map.put(WheelReturnData + " 13:20:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 13:30:00")) {
                map.put(WheelReturnData + " 13:30:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 13:40:00")) {
                map.put(WheelReturnData + " 13:40:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 13:50:00")) {
                map.put(WheelReturnData + " 13:50:00", 0);
            }

            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 14:00:00")) {
                map.put(WheelReturnData + " 14:00:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 14:10:00")) {
                map.put(WheelReturnData + " 14:10:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 14:20:00")) {
                map.put(WheelReturnData + " 14:20:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 14:30:00")) {
                map.put(WheelReturnData + " 14:30:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 14:40:00")) {
                map.put(WheelReturnData + " 14:40:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 14:50:00")) {
                map.put(WheelReturnData + " 14:50:00", 0);
            }

            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 15:00:00")) {
                map.put(WheelReturnData + " 15:00:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 15:10:00")) {
                map.put(WheelReturnData + " 15:10:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 15:20:00")) {
                map.put(WheelReturnData + " 15:20:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 15:30:00")) {
                map.put(WheelReturnData + " 15:30:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 15:40:00")) {
                map.put(WheelReturnData + " 15:40:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 15:50:00")) {
                map.put(WheelReturnData + " 15:50:00", 0);
            }


            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 16:00:00")) {
                map.put(WheelReturnData + " 16:00:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 16:10:00")) {
                map.put(WheelReturnData + " 16:10:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 16:20:00")) {
                map.put(WheelReturnData + " 16:20:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 16:30:00")) {
                map.put(WheelReturnData + " 16:30:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 16:40:00")) {
                map.put(WheelReturnData + " 16:40:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 16:50:00")) {
                map.put(WheelReturnData + " 16:50:00", 0);
            }

            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 17:00:00")) {
                map.put(WheelReturnData + " 17:00:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 17:10:00")) {
                map.put(WheelReturnData + " 17:10:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 17:20:00")) {
                map.put(WheelReturnData + " 17:20:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 17:30:00")) {
                map.put(WheelReturnData + " 17:30:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 17:40:00")) {
                map.put(WheelReturnData + " 17:40:00", 0);
            }
            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 17:50:00")) {
                map.put(WheelReturnData + " 17:50:00", 0);
            }


            if (DateHelper.getInstance().compare_date(d, WheelReturnData + " 18:00:00")) {
                map.put(WheelReturnData + " 18:00:00", 0);
            }

            if (persons != null) {
                for (GetSleepBean.SleepListBean sleep : persons) {
                    String key = sleep.getDateTime().substring(0, 19);
//            Log.e("TAG", "key-----" + key);
                    if (sleep != null) {
                        map.put(key, map.get(key) + sleep.getRollCount());
                    }

                }

                Map<String, Integer> mapByKey = sortMapByKey(map);
                //        Log.e("TAG","睡眠数据补零---排序--"+mapByKey.toString());

//条件1：之前20分钟tilt总量+当前10分钟tilt总量 +之后20分钟tilt总量<40
//条件2：当前10分钟tilt<15
//条件3：当前10分钟step<30


                //以上测试通过
                //--------------------------------------------------------------------------------------------------------------------------------

                int countall = 0;     //标志位
                ArrayList<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>();

                for (Map.Entry<String, Integer> entry : mapByKey.entrySet()) {
//            Log.e("TAG","key= " + entry.getKey() + " and value= " + entry.getValue());
                    boolean e2 = entry.getValue() < 15;  //条件2
//            Log.e("TAG","条件2--"+e2);

                    String s = DateHelper.getInstance().givedTimeToBefer(entry.getKey(), -1200, "yyyy-MM-dd HH:mm:ss");
                    if (mapByKey.get(s) != null) {
                        t20 = mapByKey.get(s);  //取出之前20分钟的数据
                    }

                    String ss = DateHelper.getInstance().givedTimeToBefer(entry.getKey(), -600, "yyyy-MM-dd HH:mm:ss");
                    if (mapByKey.get(s) != null) {
                        t10 = mapByKey.get(ss);  //取出之前10分钟的数据
                    }

                    String s1 = DateHelper.getInstance().givedTimeToBefer(entry.getKey(), 1200, "yyyy-MM-dd HH:mm:ss");
                    if (mapByKey.get(s1) != null) {
                        tt20 = mapByKey.get(s1);  //取出之后20分钟的数据
                    }

                    String ss1 = DateHelper.getInstance().givedTimeToBefer(entry.getKey(), 600, "yyyy-MM-dd HH:mm:ss");
                    if (mapByKey.get(s1) != null) {
                        tt10 = mapByKey.get(ss1);  //取出之后10分钟的数据
                    }

                    if (t20 != null && t10 != null && tt20 != null && tt10 != null && entry.getValue() != null) {
                        e1 = (t20 + t10 + entry.getValue() + tt20 + tt10) < 40;//条件1
//                Log.e("TAG","条件1--"+e1);
                    }
                    *//**
     * 此处需要更改--放开
     *//*

*//*                    try {
                        List<GetStepCountBean.StepListBean> time = CommonApplication.dm.selector(GetStepCountBean.StepListBean.class).where("dateTime", "=", entry.getKey()).findAll();
//                Log.e("TAG","测试一下  是否正确  需要为1--"+time.size());
                        for (int i = 0; i < time.size(); i++) {
//                   Log.e("TAG","条件3----"+time.get(i).getStepCount());//当前10分钟step
                            e3 = time.get(i).getStepCount() < 30;//条件三

                        }

                    } catch (DbException e) {
                        e.printStackTrace();
                    }*//*

                    if (e1 && e2 && e3) {//满足 条件1 and 条件2 and 条件3 and 条件4
                        countall++;
                        list.add(entry);
                    }
                }

//        Log.e("TAG","处理之前的累加值--"+countall);

                //tile+step=0 不连续大于2小时
//
//        for (Map.Entry<String, Integer> entry : mapByKey.entrySet()){
//            list.add(entry);
//        }

                int count = 0;//标志位--标志数组连续多久是0

                //我需要删除count个数据，数据库删除  从当前开始  前推count个 都是0的数据

                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getValue() == 0) {//此十分钟为0
                        count++;            //计数器加1
                        if (count >= 12) { //已经超过两个小时
                            if ((i + 1) < list.size()) {
                                Integer in = list.get(i + 1).getValue();
                                if (in != 0) {
                                    for (int j = 0; j < count; j++) {
                                        list.remove(i - j);
                                    }
                                    countall -= count;
                                    count = 0;
                                }
                            }
                        }
                    } else {
                        count = 0;
                    }
                }

//        Log.e("TAG","最后一次---"+countall);

                for (int i = 0; i < list.size(); i++) {
                    Log.e("TAG", "睡眠S---" + list.get(i).getKey() + "---" + list.get(i).getValue());
                }
                double deep = list.size();

                Log.e("TAG", "测试list的值S--" + list.size());


                ArrayList<Map.Entry<String, Integer>> dlist = new ArrayList<Map.Entry<String, Integer>>();

                *//**
     * 此处需要更改--放开
     *//*

*//*                for (int i = 0; i < list.size(); i++) {
//            Log.e("TAG","完成--翻转--"+list.get(i).getValue());
                    try {
                        step = CommonApplication.dm.selector(GetStepCountBean.StepListBean.class).where("dateTime", "=", list.get(i).getKey()).findFirst();
                    } catch (DbException e) {
                        e.printStackTrace();
                    }

                    if (step != null) {
                        if (list.get(i).getValue() == 0 && step.getStepCount() == 0) {//tilt and step=0的总时长计为d
                            dlist.add(list.get(i));
                        }
                    } else {//step=0的情况--数据库没有此时间，代表step为0
                        if (list.get(i).getValue() == 0) {
                            dlist.add(list.get(i));
                        }

                    }
                }*//*


//            for (int i = 0; i < dlist.size(); i++) {
//                Log.e("TAG", "测试dlist的值--" + dlist.get(i).getKey() + "--" + dlist.get(i).getValue());
//            }

//        d*0.9=深睡时长
//        S-d*0.9=浅睡时长
//        单位都是分钟

                int v = (int) (dlist.size() * 10 * 0.9);
                int q = (int) (deep * 10 - v);

                Log.e("TAG", "深睡---" + v);
                Log.e("TAG", "浅睡---" + (deep * 10 - v));

                ArrayList<Integer> arrayList = new ArrayList<>();

                arrayList.add(0, v);
                arrayList.add(1, q);

                yValues.add(new BarEntry(new float[]{(float) v / 60, (float) q / 60}, a));
            }
        }
        Log.e("TAG", yValues.size() + "");
        mBarData = getBarData(7, yValues);
        showBarChart(week_sleep_fragment_bar_chart, mBarData);
    }

*/

   /* *//**
     * 使用 Map按key进行排序
     *
     * @param map
     * @return
     *//*
    public Map<String, Integer> sortMapByKey(Map<String, Integer> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<String, Integer> sortMap = new TreeMap<String, Integer>(new MapKeyComparator());

        sortMap.putAll(map);

        return sortMap;
    }
*/
    /*class MapKeyComparator implements Comparator<String> {

        @Override
        public int compare(String str1, String str2) {

            return str1.compareTo(str2);
        }
    }
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
        tImeBeenList = exchangeWeek(dateCount);

        for (int i = 0; i < tImeBeenList.size(); i++) {
            tImeBeenList.get(i).getStartTime();
            tImeBeenList.get(i).getEndTime();
            tImeBeenList.get(i).getShouDate();

        }

        initsleep(tImeBeenList.get(tImeBeenList.size() - 1).getStartTime());
        intDayWheel();
    }*/
}
