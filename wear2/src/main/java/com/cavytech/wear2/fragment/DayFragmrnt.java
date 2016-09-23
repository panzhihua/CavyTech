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
import com.cavytech.wear2.entity.BandSleepStepBean;
import com.cavytech.wear2.entity.GetStepCountBean;
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
import java.util.HashMap;
import java.util.List;

/**
 * Created by libin on 4/26 0026.
 * 邮箱：bin.li@tunshu.com
 */
public class DayFragmrnt extends Fragment implements StepsPick.OnValueChangeListener {
    private StepsPick day_wheel;
    private BarChart day_fragment_bar_chart;
    private BarData mBarData;

    private TextView tv_step_count;
    private TextView tv_step_journey;
    private TextView tv_step_target;
    private TextView tv_step_time_hour;
    private TextView tv_step_time_minute;

    private ArrayList<BarEntry> yValues;

    /**
     * Pk列表
     */
//    private ArrayList<GetStepCountBean.StepListBean> stepList;

    /**
     * data日期
     */
    private String[] dateCount;//timepicker数组

    private String WheelReturnData;

    private HashMap<String, Integer> map;

    private DecimalFormat   fnum  =   new  DecimalFormat("##0.00");


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.day_fragment, null);
        day_wheel = (StepsPick) view.findViewById(R.id.day_wheel);
        day_fragment_bar_chart = (BarChart) view.findViewById(R.id.day_fragment_bar_chart);
        tv_step_count = (TextView) view.findViewById(R.id.tv_step_count);
        tv_step_journey = (TextView) view.findViewById(R.id.tv_step_journey);
        tv_step_target = (TextView) view.findViewById(R.id.tv_step_target);
        tv_step_time_hour = (TextView) view.findViewById(R.id.tv_step_time_hour);
        tv_step_time_minute = (TextView) view.findViewById(R.id.tv_step_time_minute);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        intDayWheel();

        getdatafromdb(DateHelper.getSystemDateString("yyyy-MM-dd"));

//        getWheelDateCountFromNate();

    }
    /**
     * 从网上获取计步数据
     */

   /* private void getdatafromnet() {

        HttpUtils.getInstance().getStepDataday();



    }*/


    /**
     * 数据库得到计步数据
     */
    private void getdatafromdb(String data) {
        yValues = new ArrayList<BarEntry>();
        try {

            if(DateHelper.getSystemDateString("yyyy-MM-dd").equals(data)){
                if(SleepCount.stepcountHour() != null){
                    yValues =SleepCount.stepcountHour() ;
                }

            }else{
                List<GetStepCountBean.DataBean.StepsDataBean.HoursBean> hourSteps = CommonApplication.dm.selector(GetStepCountBean.DataBean.StepsDataBean.HoursBean.class).where("dateTime", "LIKE", data + "%").findAll();

                if(hourSteps!=null){
                    for (int i = 0; i < hourSteps.size(); i++) {
                        Log.e("pipa", hourSteps.get(i).getSteps() + "");
                        yValues.add(new BarEntry(hourSteps.get(i).getSteps(), i));
                    }
                }
            }

            //图表下半部数据填充
            List<GetStepCountBean.DataBean.StepsDataBean> stepsAndhour = CommonApplication.dm.selector(GetStepCountBean.DataBean.StepsDataBean.class).where("date", "LIKE", data + "%").findAll();

            if(DateHelper.getSystemDateString("yyyy-MM-dd").equals(data)){
                List<BandSleepStepBean> hour = CommonApplication.dm.selector(BandSleepStepBean.class).where("date", "LIKE", data + "%").and("steps",">",0).findAll();

                if(hour != null){
                    int count = 0;
                    for(int i = 0;i<hour.size();i++){
                        Log.e("TAG","ooooooo"+hour.get(i).getSteps());
                        count += hour.get(i).getSteps();
                    }

                    int time = hour.size() *10;

                    int hour2 = time / 60;
                    int minute = time % 60;

                    float journey = count * 6 / (float) 10000;
                    String journeyStr=fnum.format(journey);
                    int anInt = CacheUtils.getInt(getContext(), Constants.STEPCOMPLETE);
                    int conplete = (int) ((float) count / (float) anInt * 100);

                    tv_step_time_hour.setText(hour2+" ");
                    tv_step_time_minute.setText(" "+minute+" ");
                    tv_step_target.setText(conplete + " ");
                    tv_step_count.setText(count + " ");
                    tv_step_journey.setText(journeyStr + " ");
                }


            }else if(stepsAndhour!=null){
               if(stepsAndhour.size() != 0){
                   int total_steps = stepsAndhour.get(0).getTotal_steps();
                   int total_time = stepsAndhour.get(0).getTotal_time();

                   int hour = total_time / 60;
                   int minute = total_time % 60;

                   float journey = total_steps * 6 / (float) 10000;
                   String journeyStr=fnum.format(journey);
                   tv_step_count.setText(total_steps + " ");
                   tv_step_journey.setText(journeyStr + " ");

                   int anInt = CacheUtils.getInt(getContext(), Constants.STEPCOMPLETE);
                   int conplete = (int) ((float) total_steps / (float) anInt * 100);
                   tv_step_target.setText(conplete + " ");

                   tv_step_time_hour.setText(hour+" ");
                   tv_step_time_minute.setText(" "+minute+" ");
               }else{
                   tv_step_time_hour.setText(0+" ");
                   tv_step_time_minute.setText(" "+0+" ");
                   tv_step_target.setText(0 + " ");
                   tv_step_count.setText(0 + " ");
                   tv_step_journey.setText(0 + " ");
               }
           }

        } catch (DbException e) {
            e.printStackTrace();
        }

        mBarData = getBarData(24, yValues);
        showBarChart(day_fragment_bar_chart, mBarData);

        day_fragment_bar_chart.invalidate();


        /* try {
            List<GetStepCountBean.StepListBean> persons = CommonApplication.dm.selector(GetStepCountBean.StepListBean.class).where("dateTime", ">=", data+" 00:00:00").and("dateTime", "<=", data+" 23:59:59").findAll();
            tohour(persons);



        } catch (DbException e) {
            e.printStackTrace();
        }
        */
    }


    /**
     * 后台得到计步数据，转换成小时
     *
     */
   /* private void tohour(List<GetStepCountBean.StepListBean> hourlist) {

//    private void tohour(GetStepCountBean response) {

//        List<GetStepCountBean.StepListBean> hourlist = response.getStepList();

//        for(int i=0;i<hourlist.size();i++){
//            Log.e("TAG","计步数据--"+hourlist.get(i).getDateTime()+"步数---"+hourlist.get(i).getStepCount());
//        }

//        ArrayList<HashMap<String, Integer>> newlist = new ArrayList<HashMap<String, Integer>>();

        map = new HashMap<String, Integer>();
        map.put("00", 0);
        map.put("01", 0);
        map.put("02", 0);
        map.put("03", 0);
        map.put("04", 0);
        map.put("05", 0);
        map.put("06", 0);
        map.put("07", 0);
        map.put("08", 0);
        map.put("09", 0);
        map.put("10", 0);
        map.put("11", 0);
        map.put("12", 0);
        map.put("13", 0);
        map.put("14", 0);
        map.put("15", 0);
        map.put("16", 0);
        map.put("17", 0);
        map.put("18", 0);
        map.put("19", 0);
        map.put("20", 0);
        map.put("21", 0);
        map.put("22", 0);
        map.put("23", 0);

        for (GetStepCountBean.StepListBean step2 : hourlist) {
//            String key = step2.getdateTime();
//            map.put(key,  map.get(key) + step2.count);
            String key = step2.getDateTime().substring(11, 13);
//            Log.e("TAG", "key-----" + key);
            map.put(key, map.get(key) + step2.getStepCount());

        }

        Map<String, Integer> mapByKey = sortMapByKey(map);


        yValues = new ArrayList<BarEntry>();

//        for (Integer v : mapByKey.values()) {
////            System.out.println("value= " + v);
//            Log.e("TAG","需要显示的数据----"+v);
//            yValues.add(new BarEntry(v, mapByKey.get()));
//
//        }

        for (Map.Entry<String, Integer> entry : mapByKey.entrySet()) {
//            System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
            //Log.e("TAG","key= " + entry.getKey() + " and value= " + entry.getValue());
            yValues.add(new BarEntry(entry.getValue(), Integer.valueOf(entry.getKey())));
        }


        //图表下半部数据填充
        int count = 0;
        int time = 0;
        for(int i = 0 ; i < yValues.size();i++){
            float val = yValues.get(i).getVal();

            count = (int) (count +val);

            if(val != 0.0){
                time++;
            }
        }

        int sumTime = time*10;
        float journey = count*6/(float)10000;
        tv_step_count.setText(count+"步");
        tv_step_journey.setText(journey+"公里");
        int anInt = CacheUtils.getInt(getContext(), Constants.STEPCOMPLETE);
        int conplete  = (int) ((float)count/(float) anInt*100);
        tv_step_target.setText(conplete+"%");
        int hour = sumTime/60;
        int minute = sumTime%60;
        tv_step_time.setText(hour+"小时"+minute+"分钟");

        mBarData = getBarData(24, yValues);
        showBarChart(day_fragment_bar_chart, mBarData);

        day_fragment_bar_chart.invalidate();

    }*/


    /**
     * 使用 Map按key进行排序
     *
     * @param
     * @return
     */
   /* public static Map<String, Integer> sortMapByKey(Map<String, Integer> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<String, Integer> sortMap = new TreeMap<String, Integer>(new MapKeyComparator());

        sortMap.putAll(map);

        return sortMap;
    }*/
    private void intDayWheel() {

        String data = CacheUtils.getString(getActivity(), Constants.CESHI);

        ArrayList emptyArrayList = new ArrayList();

        emptyArrayList.add(DateHelper.getSystemDateString("yyyy-MM-dd"));

        dateCount = data.split(",");

        ArrayList whellDateArray = new ArrayList();

        for (int i = 0; i < dateCount.length; i++) {
            String subStringData = dateCount[i].toString().substring(5, 10);

            whellDateArray.add(subStringData);
        }

        if (whellDateArray.size() > 0) {
            day_wheel.initViewParam(whellDateArray, whellDateArray.size() - 1);
        } else {
            day_wheel.initViewParam(emptyArrayList, emptyArrayList.size() - 1);
        }

        day_wheel.setTextAttrs(14, 14,
                Color.argb(255, 255, 255, 255),
                getResources().getColor(R.color.step_text),
                false, true);
        day_wheel.setValueChangeListener(this);

    }

    @Override
    public void onValueChange(View wheel, float value) {


    }

    @Override
    public void onValueChangeEnd(View wheel, float value) {

        if (dateCount.length > 0) {
            WheelReturnData = (String) dateCount[(int) value];
        }

        getdatafromdb(WheelReturnData);
    }

    private void showBarChart(final BarChart barChart, BarData barData) {
        // 如果没有数据的时候，会显示这个，类似ListView的EmptyView
        barChart.setNoDataTextDescription(getString(R.string.temporarily_no_data));

        barChart.setData(barData); // 设置数据

        barChart.setDrawBorders(false); //是否在折线图上添加边框

//        barChart.setDescription("");// 数据描述
//        barChart.setDescriptionPosition(100,20);//数据描述的位置
//        barChart.setDescriptionColor(Color.RED);//数据的颜色
//        barChart.setDescriptionTextSize(40);//数据字体大小

        barChart.setDrawGridBackground(false); // 是否显示表格颜色
        //barChart.setGridBackgroundColor(getResources().getColor(R.color.bar_bg)); // 表格的的颜色
        barChart.setBackgroundColor(getResources().getColor(R.color.top_bg_color));// 设置整个图标控件的背景
        barChart.setDrawBarShadow(false);//柱状图没有数据的部分是否显示阴影效果

        barChart.setTouchEnabled(true); // 设置是否可以触摸
        barChart.setDragEnabled(false);// 是否可以拖拽
        barChart.setScaleEnabled(false);// 是否可以缩放
        barChart.setPinchZoom(false);//y轴的值是否跟随图表变换缩放;如果禁止，y轴的值会跟随图表变换缩放

        barChart.setDrawValueAboveBar(true);//柱状图上面的数值显示在柱子上面还是柱子里面

        barChart.getXAxis().setDrawGridLines(false);//是否显示竖直标尺线
        barChart.getXAxis().setLabelsToSkip(5);//设置横坐标显示的间隔
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

        barChart.getXAxis().setTextColor(Color.WHITE);

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
        barChart.setDescription(count + "");
        barChart.setDescriptionPosition(100, 40);
        barChart.setDescriptionColor(Color.WHITE);

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
        for (int i = 0; i < count; i++) {
            xValues.add(i + "");
        }

       /*   for (int i = 0; i < count; i++) {
            float value = (float) (Math.random() * range*//*100以内的随机数*//*) + 3;
            yValues.add(new BarEntry(value, i));
        }*/

        // y轴的数据集合
        barDataSet = new BarDataSet(yValues, getResources().getString(R.string.step_cont));

        barDataSet.setBarSpacePercent(70);
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
            //return mFormat.format(value) + "K";
            return mFormat.format(value) + "";
        }
    }

    //old--------------------------------------------

    /*static class MapKeyComparator implements Comparator<String> {

        @Override
        public int compare(String str1, String str2) {

            return str1.compareTo(str2);
        }
    }*/


    /**
     * 得到日期Count
     */
    private void getWheelDateCountFromNate() {

        /*HttpUtils.getInstance().getStepData(new RequestCallback<GetStepCountBean>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(GetStepCountBean response) {
                stepList = (ArrayList) response.getStepList();

                dateCount = new ArrayList();

                for (int i = 0; i < stepList.size(); i++) {

                    String subStringData = stepList.get(i).getDateTime().substring(0, 10);

                    dateCount.add(subStringData);

                }

//                for (int i = 0; i < stepList.size(); i++) {
//
//                    int stepCount = stepList.get(i).getStepCount();
//
////                    Log.e("TAg","stepCount---"+stepCount);
//                }
                dateCount = singleElement(dateCount);


                if (dateCount.size() > 0) {
                    WheelReturnData = (String) dateCount.get(dateCount.size() - 1);
                    Log.e("TAG", "得到日期RealdateCount____________" + WheelReturnData);
                }

                getdatafromdb(WheelReturnData);

                intDayWheel();
            }
        });*/
    }


}
