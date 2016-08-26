package com.cavytech.wear2.fragment;

import android.graphics.Color;
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
import com.cavytech.wear2.entity.GetStepCountBean;
import com.cavytech.wear2.entity.TImeBean;
import com.cavytech.wear2.util.CacheUtils;
import com.cavytech.wear2.util.Constants;
import com.cavytech.wear2.util.DateHelper;
import com.cavytech.wear2.util.SleepCount;
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

import org.xutils.ex.DbException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by libin on 4/26 0026.
 * 邮箱：bin.li@tunshu.com
 */
public class WeekFragmrnt extends Fragment implements StepsPick.OnValueChangeListener {
    private StepsPick week_wheel;
    private BarChart week_fragment_bar_chart;
    private BarData mBarData;

    private TextView tv_step_count;
    private TextView tv_step_journey;
    private TextView tv_step_target;
    private TextView tv_step_time_minute;
    private TextView tv_step_time_hour;
    private TextView tv_zongbushu;

    private ArrayList<BarEntry> yValues;

    ArrayList<TImeBean> empteyTImeBeen;

    ArrayList<String> dataist;//X轴显示日期的集合

    private ArrayList<String> dateCount;//data日期

    private ArrayList<TImeBean> tImeBeenList;//得到传递的集合数据 包括开始日期 结束日期 显示时间

    private DecimalFormat   fnum  =   new  DecimalFormat("##0.00");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.week_fragment, null);

        week_wheel = (StepsPick) view.findViewById(R.id.week_wheel);
        week_fragment_bar_chart = (BarChart) view.findViewById(R.id.week_fragment_bar_chart);

        tv_step_count = (TextView) view.findViewById(R.id.tv_step_count);
        tv_step_journey = (TextView) view.findViewById(R.id.tv_step_journey);
        tv_step_target = (TextView) view.findViewById(R.id.tv_step_target);
        tv_step_time_hour = (TextView) view.findViewById(R.id.tv_step_time_hour);
        tv_zongbushu = (TextView) view.findViewById(R.id.tv_zongbushu);
        tv_step_time_minute = (TextView) view.findViewById(R.id.tv_step_time_minute);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tv_zongbushu.setText("周总步数");

        dataist = new ArrayList<>();
        dataist.add("周一");
        dataist.add("周二");
        dataist.add("周三");
        dataist.add("周四");
        dataist.add("周五");
        dataist.add("周六");
        dataist.add("周日");
        /*for (int i = 1; i <= 7; i++) {
            dataist.add("" + i);
        }*/

        //初始化得到已经入当天的数据
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(DateHelper.getSystemDateString("yyyy-MM-dd"));
        empteyTImeBeen = exchangeWeek(arrayList);

        intDayWheel();

        getdatafromdb(empteyTImeBeen.get(0).getStartTime(), empteyTImeBeen.get(0).getEndTime());

    }

    /**
     * 将日期集合转换为 包括开始日期 结束日期 显示时间 的星期
     *
     * @param arrayList
     * @return
     */
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

/*      初始化显示图表 第一次不滚动不调用滚动监听得不到数据 所以先得到最后一天数据显示
        getdatafromdb(tImeBeenList.get(tImeBeenList.size() - 1).getStartTime(), tImeBeenList.get(tImeBeenList.size() - 1).getEndTime());

        intDayWheel();

        String dateString = getDateString();

        ArrayList emptyArrayList = new ArrayList();
        emptyArrayList.add(dateString);*/

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

//        List<GetStepCountBean.StepListBean> bb = CommonApplication.dm.selector(GetStepCountBean.StepListBean.class).where("dateTime", ">=", data+" 00:00:00").and("dateTime", "<=", data+" 23:59:59").findAll();
//        tohour(persons);
    }

    @Override
    public void onValueChange(View wheel, float value) {

    }

    @Override
    public void onValueChangeEnd(View wheel, float value) {

        getdatafromdb(tImeBeenList.get((int) value).getStartTime(), tImeBeenList.get((int) value).getEndTime());

    }

    private void showBarChart(BarChart barChart, BarData barData) {
        // 如果没有数据的时候，会显示这个，类似ListView的EmptyView
        barChart.setNoDataTextDescription("暂无数据");

        barChart.setData(barData); // 设置数据

        barChart.setDrawBorders(false); //是否在折线图上添加边框

//        barChart.setDescription("");// 数据描述
//        barChart.setDescriptionPosition(100,20);//数据描述的位置
//        barChart.setDescriptionColor(Color.RED);//数据的颜色
//        barChart.setDescriptionTextSize(40);//数据字体大小

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
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的位置 默认在上方
        barChart.getXAxis().setDrawAxisLine(false);//是否显示X轴
        barChart.getXAxis().setTextColor(Color.WHITE);

        barChart.getAxisRight().setDrawLabels(false);//右侧是否显示Y轴数值
        barChart.getAxisRight().setEnabled(false);//是否显示最右侧竖线
        barChart.getAxisRight().setDrawAxisLine(false);
        barChart.getAxisLeft().setDrawAxisLine(false);
        barChart.getAxisLeft().setEnabled(false);//是否显示最右侧竖线
        barChart.getAxisLeft().setDrawLabels(false);

        barChart.getAxisLeft().setLabelCount(0, true);//设置Y轴显示数量

        barChart.getAxisLeft().setTextColor(Color.WHITE);

        barChart.getAxisLeft().setSpaceBottom(0); //设置横轴距离底部距离
        barChart.getAxisLeft().setSpaceTop(0); //设置横轴距离顶部距离

        YAxisValueFormatter custom = new MyYAxisValueFormatter();//自定义Y轴文字样式
        barChart.getAxisLeft().setValueFormatter(custom);

        barChart.getLegend().setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);//设置比例图标的位置
        barChart.getLegend().setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);//设置比例图标和文字之间的位置方向
        barChart.getLegend().setTextColor(Color.WHITE);
        barChart.getLegend().setForm(Legend.LegendForm.CIRCLE);

        int count = 0;
        for (int i = 0; i < yValues.size(); i++) {
            if (count < yValues.get(i).getVal()) {
                count = (int) yValues.get(i).getVal();
            }
        }

        barChart.setDescription("" + count);
        barChart.setDescriptionPosition(100, 40);
        barChart.setDescriptionColor(Color.WHITE);

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                Log.e("TAG", "e" + e.toString());
                Log.e("TAG", "dataSetIndex" + dataSetIndex);
                Log.e("TAG", "Highlight" + h);

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
        for (int i = 0; i < count; i++) {
            xValues.add(i + "");
        }

       /* ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();

        for (int i = 0; i < count; i++) {
            float value = (float) (Math.random() * range*//*100以内的随机数*//*) + 3;
            yValues.add(new BarEntry(value, i));
        }*/

        // y轴的数据集合
        barDataSet = new BarDataSet(yValues, getResources().getString(R.string.step_cont));

        barDataSet.setBarSpacePercent(85);
        barDataSet.setVisible(true);//是否显示柱状图柱子
        barDataSet.setDrawValues(false);//是否显示柱子上面的数值
        barDataSet.setColor(Color.WHITE);
        barDataSet.setValueTextColor(Color.WHITE);

        ArrayList<BarDataSet> barDataSets = new ArrayList<BarDataSet>();
        barDataSets.add(barDataSet); // add the datasets

        BarData barData = new BarData(xValues, barDataSet);

        barData.setXVals(dataist);

        return barData;
    }

    public class MyYAxisValueFormatter implements YAxisValueFormatter {

        private DecimalFormat mFormat;

        public MyYAxisValueFormatter() {
            mFormat = new DecimalFormat("###,###,###,##0");
        }

        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            return mFormat.format(value) + "";
        }
    }

    /**
     * 从数据库拿到周记步数据
     *
     * @param startData
     * @param endtData
     */
    private void getdatafromdb(String startData, String endtData) {
        yValues = new ArrayList<BarEntry>();

        try {

            List<GetStepCountBean.DataBean.StepsDataBean> hourSteps = CommonApplication.dm.selector(GetStepCountBean.DataBean.StepsDataBean.class).where("date", ">=", startData).and("date", "<=", endtData).findAll();

            int totalStep = 0;
            int totalHour = 0;
            if (hourSteps != null) {
                /**
                 * 给数据集合补0
                 */
                ArrayList<GetStepCountBean.DataBean.StepsDataBean> arrayList = new ArrayList();

                int j = 0;
                for (int i = 0; i < 7; i++) {

                    GetStepCountBean.DataBean.StepsDataBean stepsDataBean = new GetStepCountBean.DataBean.StepsDataBean();

                    if (j >= hourSteps.size()) {
                        stepsDataBean.setTotal_steps(0);
                        stepsDataBean.setTotal_time(0);
                        arrayList.add(stepsDataBean);
                    }else {
                        if (DateHelper.getInstance().getStrDate2(i, startData).equals(hourSteps.get(j).getDate())) {
                            if(DateHelper.getSystemDateString("yyyy-MM-dd").equals(hourSteps.get(j).getDate())){
                                ArrayList<BarEntry> yValues=SleepCount.stepcountHour();
                                int sum=0;
                                if(yValues!=null&&!yValues.isEmpty()){
                                    for(BarEntry yValue:yValues){
                                        sum=sum+(int)yValue.getVal();
                                    }
                                }
                                stepsDataBean.setTotal_steps(sum);
                            }else{
                                stepsDataBean.setTotal_steps(hourSteps.get(j).getTotal_steps());
                            }
                            stepsDataBean.setTotal_time(hourSteps.get(j).getTotal_time());
                            j++;
                            arrayList.add(stepsDataBean);
                        } else {
                            stepsDataBean.setTotal_steps(0);
                            stepsDataBean.setTotal_time(0);
                            arrayList.add(stepsDataBean);
                        }
                    }

                }

                for (int i = 0 ; i  < arrayList.size();i++){
                    Log.e("TAG",arrayList.get(i).getTotal_steps()+"step");
                }

                for (int i = 0; i < arrayList.size(); i++) {
                    yValues.add(new BarEntry(arrayList.get(i).getTotal_steps(), i));

                    totalStep += arrayList.get(i).getTotal_steps();

                    totalHour += arrayList.get(i).getTotal_time();
                }
            }
            float journey = totalStep * 6 / (float) 10000;
            String journeyStr=fnum.format(journey);
            int avarageStep = totalStep / 7;
            int hour = totalHour / 60;
            int minute = totalHour % 60;
            //图表下半部数据填充
            tv_step_count.setText(totalStep + " "); //周总步数
            tv_step_journey.setText(journeyStr + " "); //路程
            tv_step_target.setText(avarageStep + " "); //日均步数
            tv_step_time_hour.setText(hour + " ");
            tv_step_time_minute.setText(" " + minute + " ");

        } catch (DbException e) {
            e.printStackTrace();
        }

        mBarData = getBarData(7, yValues);
        showBarChart(week_fragment_bar_chart, mBarData);

        week_fragment_bar_chart.invalidate();
    }

    //------------------------------------------------
    //old down

    /**
     * 得到日期Count
     */
    private void getWheelDateCountFromNate() {
        /*String data = CacheUtils.getString(getActivity(), Constants.CESHI);

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

        // 初始化显示图表 第一次不滚动不调用滚动监听得不到数据 所以先得到最后一天数据显示
//        getdatafromdb(tImeBeenList.get(tImeBeenList.size() - 1).getStartTime(), tImeBeenList.get(tImeBeenList.size() - 1).getEndTime());

        intDayWheel();*/
    }

//    /**
//     * 数据库得到计步数据
//     * 此处需要更改
//     */
//    private void getdatafromdb(String startData, String endtData) {
//        try {
//            toDay(startData);
//        } catch (DbException e) {
//            e.printStackTrace();
//        }
//    }

    //private HashMap<Integer, Integer> map;

/*    private void toDay(String startData) throws DbException {
        map = new HashMap<Integer, Integer>();
        map.put(0, 0);
        map.put(1, 0);
        map.put(2, 0);
        map.put(3, 0);
        map.put(4, 0);
        map.put(5, 0);
        map.put(6, 0);

        //走路时间
        int time = 0;
        for (int i = 0; i < 7; i++) {
            //得到一周的日期
            String delayDate = DateHelper.getInstance().getStrDate2(i, startData);
            Log.e("TAG", delayDate + "-[------" + i);
            List<GetStepCountBean.StepListBean> dayList = CommonApplication.dm.selector(GetStepCountBean.StepListBean.class).where("dateTime", ">=", delayDate + " 00:00:00").and("dateTime", "<=", delayDate + " 23:59:59").findAll();
            int daySteps = 0;
            for (int j = 0; j < dayList.size(); j++) {
                //每一天的步数
                daySteps += dayList.get(j).getStepCount();

                if (dayList.get(j).getStepCount() != 0) {
                    time++;
                }
            }
            Log.e("TAG", daySteps + "-[-----}-" + delayDate);

            map.put(i, daySteps);
        }

        //填充图表数据
        yValues = new ArrayList<BarEntry>();

        for (int i = 0; i < map.size(); i++) {
            yValues.add(new BarEntry(map.get(i), i));
        }

        //图表下半部数据填充
        int count = 0;
        for (int i = 0; i < yValues.size(); i++) {
            float val = yValues.get(i).getVal();

            count = (int) (count + val);
        }

        int sumTime = time * 10;
        float journey = count * 6 / (float) 10000;
        tv_step_count.setText(count + "步");
        tv_step_journey.setText(journey + "公里");
        tv_step_target.setText(count / 7 + "步");
        int hour = sumTime / 60;
        int minute = sumTime % 60;
        tv_step_time.setText(hour + "小时" + minute + "分钟");

        mBarData = getBarData(7, yValues);
        showBarChart(week_fragment_bar_chart, mBarData);

        week_fragment_bar_chart.invalidate();

    }*/


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
}

