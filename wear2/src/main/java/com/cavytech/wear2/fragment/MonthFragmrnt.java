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
import java.util.HashMap;
import java.util.List;

/**
 * Created by libin on 4/26 0026.
 * 邮箱：bin.li@tunshu.com
 */
public class MonthFragmrnt extends Fragment implements StepsPick.OnValueChangeListener {
    private StepsPick month_wheel;
    private BarChart month_fragment_bar_chart;
    private BarData mBarData;

    private TextView tv_step_count;
    private TextView tv_step_journey;
    private TextView tv_step_target;
    private TextView tv_step_time_minute;
    private TextView tv_step_time_hour;
    private TextView tv_zongbushu;

    private ArrayList<BarEntry> yValues;

    ArrayList<TImeBean> empteyTImeBeen;

    //data日期
    private ArrayList<String> dateCount;

    //得到传递的集合数据 包括开始日期 结束日期 显示时间
    private ArrayList<TImeBean> tImeBeenList;

    private DecimalFormat   fnum  =   new  DecimalFormat("##0.00");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.month_fragment, null);

        month_wheel = (StepsPick) view.findViewById(R.id.month_wheel);
        month_fragment_bar_chart = (BarChart) view.findViewById(R.id.month_fragment_bar_chart);

        tv_step_count = (TextView) view.findViewById(R.id.tv_step_count);
        tv_step_journey = (TextView) view.findViewById(R.id.tv_step_journey);
        tv_step_target = (TextView) view.findViewById(R.id.tv_step_target);
        tv_step_time_hour = (TextView) view.findViewById(R.id.tv_step_time_hour);
        tv_step_time_minute = (TextView) view.findViewById(R.id.tv_step_time_minute);
        tv_zongbushu = (TextView) view.findViewById(R.id.tv_zongbushu);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tv_zongbushu.setText(getString(R.string.monthly_total_steps));

        //初始化得到已经入当天的数据
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(DateHelper.getSystemDateString("yyyy-MM-dd"));
        empteyTImeBeen = exchangeMonth(arrayList);

        intDayWheel();

        getdatafromdb(empteyTImeBeen.get(0).getStartTime(),empteyTImeBeen.get(0).getEndTime());

    }

    /**
     * 数据库得到计步数据
     *
     * 此处需要更改
     */
    private void getdatafromdb(String startData, String endtData) {

        int monthDay = DateHelper.getInstance().getMonthDay(endtData, startData);

        //填充图表数据
        yValues = new ArrayList<BarEntry>();

        try {

            List<GetStepCountBean.DataBean.StepsDataBean> hourSteps = CommonApplication.dm.selector(GetStepCountBean.DataBean.StepsDataBean.class).where("date", ">=", startData).and("date", "<=", endtData).findAll();

            int totalStep = 0;
            int totalHour = 0;
            if(hourSteps!=null){

                /**
                 * 给数据集合补0
                 */
                ArrayList<GetStepCountBean.DataBean.StepsDataBean> arrayList = new ArrayList();

                int j = 0;
                for (int i = 0; i < monthDay; i++) {

                    GetStepCountBean.DataBean.StepsDataBean stepsDataBean = new GetStepCountBean.DataBean.StepsDataBean();

                    if (j >= hourSteps.size()) {
                        stepsDataBean.setTotal_steps(0);
                        stepsDataBean.setTotal_time(0);
                        arrayList.add(stepsDataBean);
                    }else {
                        if (DateHelper.getInstance().getStrDate2(i, startData).equals(hourSteps.get(j).getDate())) {
                            if(DateHelper.getSystemDateString("yyyy-MM-dd").equals(hourSteps.get(j).getDate())){
                                ArrayList<BarEntry> yValues= SleepCount.stepcountHour();
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

                for(int i = 0; i<arrayList.size() ; i++){
                    yValues.add(new BarEntry(arrayList.get(i).getTotal_steps(),i));

                    totalStep += arrayList.get(i).getTotal_steps();

                    totalHour += arrayList.get(i).getTotal_time();
                }
            }

            float journey = totalStep * 6 / (float) 10000;
            String journeyStr=fnum.format(journey);
            int avarageStep = totalStep/30;
            int hour = totalHour / 60;
            int minute = totalHour % 60;
            //图表下半部数据填充
            tv_step_count.setText(totalStep+" "); //月总步数
            tv_step_journey.setText(journeyStr +" "); //路程
            tv_step_target.setText(avarageStep + " "); //日均步数
            tv_step_time_hour.setText(hour+" ");
            tv_step_time_minute.setText(" "+minute+" ");
        } catch (DbException e) {
            e.printStackTrace();
        }

        mBarData = getBarData(monthDay, yValues);
        showBarChart(month_fragment_bar_chart, mBarData);

        month_fragment_bar_chart.invalidate();


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
            Log.e("TAG", "测试--weekFragment" + split[i]);
        }

        //将日期转化成月的集合 包括开始时间 结束时间 显示时间
        tImeBeenList = exchangeMonth(dateCount);

        for (int i = 0; i < tImeBeenList.size(); i++) {
            tImeBeenList.get(i).getStartTime();
            tImeBeenList.get(i).getEndTime();
            tImeBeenList.get(i).getShouDate();

        }

        //Log.e("TAG", tImeBeenList.get(tImeBeenList.size() - 1).getStartTime() + "=============" + tImeBeenList.get(tImeBeenList.size() - 1).getEndTime());

        // 初始化显示图表 第一次不滚动不调用滚动监听得不到数据 所以先得到最后一天数据显示
        //getdatafromdb(tImeBeenList.get(tImeBeenList.size() - 1).getStartTime(), tImeBeenList.get(tImeBeenList.size() - 1).getEndTime());


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

        getdatafromdb(tImeBeenList.get((int) value).getStartTime(), tImeBeenList.get((int) value).getEndTime());

    }

    private void showBarChart(BarChart barChart, BarData barData) {
        // 如果没有数据的时候，会显示这个，类似ListView的EmptyView
        barChart.setNoDataTextDescription(getString(R.string.temporarily_no_data));

        barChart.setData(barData); // 设置数据

        barChart.setDrawBorders(false); //是否在折线图上添加边框

        barChart.setDescription("");// 数据描述
//        barChart.setDescriptionPosition(100,20);//数据描述的位置
//        barChart.setDescriptionColor(Color.RED);//数据的颜色
//        barChart.setDescriptionTextSize(40);//数据字体大小

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
        barChart.getXAxis().setDrawAxisLine(false);//是否显示X轴
        barChart.getXAxis().setTextColor(Color.WHITE);

        barChart.getAxisRight().setDrawLabels(false);//右侧是否显示Y轴数值
        barChart.getAxisRight().setEnabled(false);//是否显示最右侧竖线
        barChart.getAxisRight().setDrawAxisLine(false);
        barChart.getAxisLeft().setDrawAxisLine(false);
        barChart.getAxisLeft().setEnabled(false);//是否显示最左侧竖线
        barChart.getAxisLeft().setDrawLabels(false);

        barChart.getAxisLeft().setLabelCount(0, true);//设置Y轴显示数量

        barChart.getAxisLeft().setTextColor(Color.WHITE);

        barChart.getAxisLeft().setSpaceBottom(0); //设置横轴距离底部距离
        barChart.getAxisLeft().setSpaceTop(0); //设置横轴距离顶部距离

//        barChart.getAxisLeft().setAxisMaxValue(3.4f);//设置Y轴最大值
//        barChart.getAxisLeft().setAxisMinValue(3.1f);//设置Y轴最小值

        YAxisValueFormatter custom = new MyYAxisValueFormatter();//自定义Y轴文字样式
        barChart.getAxisLeft().setValueFormatter(custom);

        barChart.getLegend().setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);//设置比例图标的位置
        barChart.getLegend().setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);//设置比例图标和文字之间的位置方向
        barChart.getLegend().setTextColor(Color.WHITE);
        barChart.getLegend().setForm(Legend.LegendForm.CIRCLE);

        int count = 0;
        for (int i = 0 ; i <yValues.size();i++){
            if(count < yValues.get(i).getVal()){
                count = (int) yValues.get(i).getVal();
            }
        }

        barChart.setDescription(count+"");
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

        barChart.animateXY(1000,1000);
    }

    private BarDataSet barDataSet;

    private BarData getBarData(int count, ArrayList<BarEntry> yValues) {
        ArrayList<String> xValues = new ArrayList<String>();
        for (int i = 1; i <= count; i++) {
            xValues.add(i + "");
        }

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

        //将日期转化成月的集合 包括开始时间 结束时间 显示时间
        tImeBeenList = exchangeMonth(dateCount);

        for (int i = 0; i < tImeBeenList.size(); i++) {
            tImeBeenList.get(i).getStartTime();
            tImeBeenList.get(i).getEndTime();
            tImeBeenList.get(i).getShouDate();

        }

        Log.e("TAG", tImeBeenList.get(tImeBeenList.size() - 1).getStartTime() + "=============" + tImeBeenList.get(tImeBeenList.size() - 1).getEndTime());

        // 初始化显示图表 第一次不滚动不调用滚动监听得不到数据 所以先得到最后一天数据显示
        getdatafromdb(tImeBeenList.get(tImeBeenList.size() - 1).getStartTime(), tImeBeenList.get(tImeBeenList.size() - 1).getEndTime());


        intDayWheel();
    }*/

    private HashMap<Integer, Integer> map;

    private void toMonth(String startData, int monthDay) throws DbException {
        /*//初始化map 先添加所有0 加上空数据 图表要用
        map = new HashMap<Integer, Integer>();
        for (int i = 0; i < monthDay; i++) {
            map.put(i, 0);
        }


        for (int i = 0; i < monthDay; i++) {
            //得到一月的天的日期
            String delayDate = DateHelper.getInstance().getStrDate2(i, startData);
            Log.e("TAG", delayDate + "-[------" + i);
            List<GetStepCountBean.DataBean.StepsDataBean> dayList = CommonApplication.dm.selector(GetStepCountBean.DataBean.StepsDataBean.class).where("date", "LIKE", delayDate+"%").findAll();

            map.put(i, dayList.get(i).getTotal_steps());
        }*/



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

}
