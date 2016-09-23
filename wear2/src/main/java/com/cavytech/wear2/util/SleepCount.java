package com.cavytech.wear2.util;

import android.util.Log;

import com.cavytech.wear2.application.CommonApplication;
import com.cavytech.wear2.entity.BandSleepStepBean;
import com.cavytech.wear2.entity.SleepRetrun;
import com.github.mikephil.charting.data.BarEntry;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by doudou on 16/6/17.
 */
public class SleepCount {
    private static String d;

    private static Integer t20;
    private static Integer t10;
    private static Integer tt20;
    private static Integer tt10;

    private static boolean e2;
    private static boolean e1;
    private static boolean e3;

    private static List<BandSleepStepBean> slist;
    private static List<BandSleepStepBean> dlist;
    private static BandSleepStepBean ds20;
    private static BandSleepStepBean ds10;
    private static BandSleepStepBean dt20;
    private static BandSleepStepBean dt10;
    private static Map<String, Integer> mapByKey;


    /**
     * 睡眠算法的处理
     * <p/>
     * 只处理当日
     * <p/>
     * 返回值单位为分钟
     */
    public static SleepRetrun initsleep() {

        Log.e("TAG","睡眠在计算-----");

//        String strDate2 = DateHelper.getInstance().getStrDate2(-1, WheelReturnData);
//        Log.e("TAG","昨天--"+strDate2);
//        Log.e("TAG","今天--"+WheelReturnData);

        String nowtime = DateHelper.getInstance().getSystemDateString("yyyy-MM-dd");

//        Log.e("TAG", "昨天--" + DateHelper.getInstance().getStrDate("-1") + " 18:00:00");

        try {
//            dlist = CommonApplication.dm.selector(BandSleepStepBean.class).where("date", "LIKE", nowtime + "%").findAll();
            dlist = CommonApplication.dm.selector(BandSleepStepBean.class).where("date", ">=", DateHelper.getInstance().getStrDate("-1") + " 21:00:00").and("date", "<=", nowtime + " 09:00:00").findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }

        int count = 0;     //标志位

//        if(dlist != null ){
//            for (int i = 0; i < dlist.size(); i++) {
//                Log.e("TAG", "两天的睡眠数据---" + dlist.get(i).getData() + "--" + dlist.get(i).getSteps() + "--" + dlist.get(i).getTilts());
//            }
//        }

        ArrayList<BandSleepStepBean> newlist = new ArrayList<BandSleepStepBean>();

        ArrayList<BandSleepStepBean> finallist = new ArrayList<BandSleepStepBean>();

        if (dlist != null) {

            for (int i = 1; i < dlist.size(); i++) {

                if (dlist.get(i).getTilts() + dlist.get(i).getSteps() == 0) {
//                Log.e("TAG","开始值--"+count);
                    count++;
//                    if (count >= 12) { //已经超过两个小时
                    if (count >= 9) { //已经超过一个半小时

                        if ((i + 1) < dlist.size()) {
                            if ((dlist.get(i + 1).getTilts() + dlist.get(i + 1).getSteps()) != 0) {
                                for (int j = 0; j <=count; j++) {
//                                dlist.remove(i - j);
                                    newlist.add(dlist.get(i - j));
                                }
//                            Log.e("TAG","--2.第"+i+"次---"+count);
                                count = 0;
                            }
                        }

                        if (i == dlist.size()) {
//                        Log.e("TAG","进来了--"+i);
                            for (int j = 0; j < count; j++) {
//                            dlist.remove(i - j);
                                newlist.add(dlist.get(i - j));
                            }
                            count = 0;
                        }

                        if (count == dlist.size() - 1) {
//                        Log.e("TAG","lalalalalala--"+(dlist.size()-1));
                            for (int j = 0; j < count + 1; j++) {
                                newlist.add(dlist.get(i - j));
                            }
                            count = 0;
                        }

                    }
                } else {
                    count = 0;
                }
            }

//        for (int i = 0; i < newlist.size(); i++) {
//            Log.e("TAG", "无睡眠数据---" + newlist.get(i).getData() + "--" + newlist.get(i).getTilts() + "--" + newlist.get(i).getSteps());
//        }
//        Log.e("TAG", "无睡眠大小---" + newlist.size());

            dlist.removeAll(newlist);

//        for (int i = 0; i < dlist.size(); i++) {
//            Log.e("TAG", "相减--" + dlist.get(i).getData() + "--" + dlist.get(i).getTilts() + "--" + dlist.get(i).getSteps());
//        }


//条件1：之前20分钟tilt总量+当前10分钟tilt总量 +之后20分钟tilt总量<40
//条件2：当前10分钟tilt<15
//条件3：当前10分钟step<30

//            ArrayList<BandSleepStepBean> finallist = new ArrayList<BandSleepStepBean>();

            for (int i = 0; i < dlist.size(); i++) {
                e2 = dlist.get(i).getTilts() < 15;//条件2
                e3 = dlist.get(i).getSteps() < 30;//条件3

                //String s20 = DateHelper.getInstance().givedTimeToBefer(dlist.get(i).getData(), -1200, "yyyy-MM-dd HH:mm:ss");
                String s10 = DateHelper.getInstance().givedTimeToBefer(dlist.get(i).getData(), -600, "yyyy-MM-dd HH:mm:ss");
                //String t20 = DateHelper.getInstance().givedTimeToBefer(dlist.get(i).getData(), 1200, "yyyy-MM-dd HH:mm:ss");
                String t10 = DateHelper.getInstance().givedTimeToBefer(dlist.get(i).getData(), 600, "yyyy-MM-dd HH:mm:ss");

                try {
                    //ds20 = CommonApplication.dm.selector(BandSleepStepBean.class).where("date", "=", s20).findFirst();//之前20分钟
                    ds10 = CommonApplication.dm.selector(BandSleepStepBean.class).where("date", "=", s10).findFirst();//之前10分钟
                    //dt20 = CommonApplication.dm.selector(BandSleepStepBean.class).where("date", "=", t20).findFirst();//之前10分钟
                    dt10 = CommonApplication.dm.selector(BandSleepStepBean.class).where("date", "=", t10).findFirst();//之前10分钟
                } catch (DbException e) {
                    e.printStackTrace();
                }


                //条件1：之前10分钟tilt总量+当前10分钟tilt总量 +之后10分钟tilt总量<30

                if (ds20 != null && ds10 != null && dt20 != null && dt10 != null) {
                    e1 = ds10.getTilts() + dt10.getTilts() + dlist.get(i).getTilts() < 30;
                    if (e1 && e2 && e3) {
                        finallist.add(dlist.get(i));
                    }
                }

            }


        }

//        for(int i=0;i<finallist.size();i++){
//            Log.e("TAG","最后的睡眠结果--"+finallist.get(i).getData()+"--"+finallist.get(i).getTilts()+"--"+finallist.get(i).getSteps());
//
//        }

        /**
         * finallist 为排除无睡眠状态和满足条件1,2,3,4的值
         *
         *
         */

        int dd = 0;

        for (int i = 0; i < finallist.size(); i++) {
            if (finallist.get(i).getSteps() + finallist.get(i).getTilts() == 0) {
                dd++;
            }
        }

//        Log.e("TAG","睡眠时长--"+dd);


//        d*0.8=深睡时长
//        S-d*0.8=浅睡时长
//        单位都是分钟

        int v = (int) (dd * 10 * 0.8);
        int q = (int) (finallist.size() * 10 - v);

//        Log.e("TAG", "深睡---" + v);
//        Log.e("TAG", "浅睡---" + q);
//        Log.e("TAG", "睡眠时长---" + finallist.size()*10);

        SleepRetrun sr = new SleepRetrun();
        sr.setSleeptime(finallist.size() * 10);
        sr.setDeeptime(v);
        sr.setNormaltime(q);

        return sr;

//        if (e1 && e2 && e3) {//满足 条件1 and 条件2 and 条件3 and 条件4
//            countall++;
//            list.add(entry);
//        }


/*
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
*
 * 此处需要放开

                try {
                    List<GetStepCountBean.StepListBean> time = CommonApplication.dm.selector(GetStepCountBean.StepListBean.class).where("dateTime", "=", entry.getKey()).findAll();
//                Log.e("TAG","测试一下  是否正确  需要为1--"+time.size());
                    for (int i = 0; i < time.size(); i++) {
//                   Log.e("TAG","条件3----"+time.get(i).getStepCount());//当前10分钟step
                        e3 = time.get(i).getStepCount() < 30;//条件三

                    }

                } catch (DbException e) {
                    e.printStackTrace();
                }

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
                        //此处有问题

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
                    if(count==12){
                        for (int j = 0; j < count; j++) {
                            list.remove(i - j);
                        }
                        countall -= count;
                        count = 0;
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
*
 * 此处需要放开

            for (int i = 0; i < list.size(); i++) {
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
            }



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

            arrayList.add(0,v);
            arrayList.add(1,q);

            return arrayList;

        }

        return null;*/


    }


    /**
     * 使用 Map按key进行排序
     *
     * @param map
     * @return
     */
    public static Map<String, Integer> sortMapByKey(Map<String, Integer> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<String, Integer> sortMap = new TreeMap<String, Integer>(new MapKeyComparator());

        sortMap.putAll(map);

        return sortMap;
    }

    static class MapKeyComparator implements Comparator<String> {

        @Override
        public int compare(String str1, String str2) {

            return str1.compareTo(str2);
        }
    }


    /**
     * 计步算法的处理
     * <p/>
     * 只限当日计算
     */

    public static int stepcountday() {

        String nowtime = DateHelper.getInstance().getSystemDateString("yyyy-MM-dd");

        try {
            slist = CommonApplication.dm.selector(BandSleepStepBean.class).where("date", "LIKE", nowtime + "%").findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }

        int k = 0;
        if (slist != null) {
            for (int i = 0; i < slist.size(); i++) {
                k += slist.get(i).getSteps();
            }
            Log.e("TAG", "当日的步数---" + k);
        }
        return k;
    }


    /**
     * 计步算法的处理
     * <p/>
     * 只限当日计算
     */

    public static ArrayList<BarEntry> stepcountHour() {

        ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();

        String nowtime = DateHelper.getInstance().getSystemDateString("yyyy-MM-dd");
        try {
            slist = CommonApplication.dm.selector(BandSleepStepBean.class).where("date", "LIKE", nowtime + "%").findAll();

//            for(int i=0;i<slist.size();i++){
//                Log.e("TAG","测试数据----"+DateHelper.timeExchangeData1(slist.get(i).getTime()).substring(11,13)+"----"+slist.get(i).getSteps());
//            }

            HashMap<String, Integer> map = new HashMap<String, Integer>();

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

            if (slist != null) {
                for (BandSleepStepBean step2 : slist) {
//                int key = step2.getTime();
                    String key = DateHelper.timeExchangeData2(step2.getTime(),"0").substring(11, 13);
                    map.put(key, map.get(key) + step2.getSteps());

                }
            }

            mapByKey = sortMapByKey(map);

            for (Map.Entry<String, Integer> entry : mapByKey.entrySet()) {
                yValues.add(new BarEntry(entry.getValue(), Integer.valueOf(entry.getKey())));
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return yValues;
    }


//        if (dlist != null) {
//            if (dlist.size() != 0) {
//                d = dlist.get(dlist.size() - 1).getData();
//                Log.e("TAG", "数据库最后一天---" + d);
//            }
//        }
//
//        HashMap<String, Integer> map = new HashMap<String, Integer>();


//        if (dlist != null) {
//            for (BandSleepStepBean sleep : dlist) {
//                String key = sleep.getData().substring(0, 19);
//                Log.e("TAG", "key-----" + key);
//                if (sleep != null) {
//                    map.put(key, map.get(key) + sleep.getTilts());
//                }
//
//            }
//        }

}
