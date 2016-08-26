package com.cavytech.wear2.util;

import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;

import com.cavytech.wear2.cavylifeband.PedometerData;
import com.cavytech.wear2.entity.BandSleepStepBean;
import com.cavytech.wear2.entity.GetSleepBean;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by LiBin on 2016/6/1.
 */
public class DateHelper {

    private static DateHelper util;

    public static long DATEMM = 86400L;

    public static DateHelper getInstance() {

        if (util == null) {
            util = new DateHelper();
        }
        return util;

    }

    private DateHelper() {
        super();
    }

    public SimpleDateFormat date_Formater_1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public SimpleDateFormat date_Formater_2 = new SimpleDateFormat("yyyy-MM-dd");

    public Date getDate(String dateStr) {
        Date date = new Date();
        if (TextUtils.isEmpty(dateStr)) {
            return date;
        }
        try {
            date = date_Formater_1.parse(dateStr);
            return date;

        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return date;

    }

    public String getDataString_1(Date date) {
        if (date == null) {
            date = new Date();
        }
        String str = date_Formater_1.format(date);
        return str;

    }

    public String getDataString_2(Date date) {
        if (date == null) {
            date = new Date();
        }
        String str = date_Formater_2.format(date);
        return str;

    }

    /**
     * 将日期变成常见中文格式
     *
     * @param date
     * @return
     */
    public String getRencentTime(String date) {
        Date time = getDate(date);
        if (time == null) {
            return "一个月前";
        }
        String ftime = "";
        Calendar cal = Calendar.getInstance();

        String curDate = date_Formater_2.format(cal.getTime());
        String paramDate = date_Formater_2.format(time);
        if (curDate.equals(paramDate)) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
            return ftime;
        }

        long lt = time.getTime() / 86400000;
        long ct = cal.getTimeInMillis() / 86400000;
        int days = (int) (ct - lt);
        if (days == 0) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
        } else if (days == 1) {
            ftime = "昨天";
        } else if (days == 2) {
            ftime = "前天";
        } else if (days > 2 && days <= 10) {
            ftime = days + "天前";
        } else if (days > 10) {
            ftime = "一个月前";
        } else {
            ftime = date_Formater_2.format(time);
        }
        return ftime;
    }

    /**
     * 日期时间格式转换
     *
     * @param typeFrom 原格式
     * @param typeTo   转为格式
     * @param value    传入的要转换的参数
     * @return
     */
    public String stringDateToStringData(String typeFrom, String typeTo,
                                         String value) {
        String re = value;
        SimpleDateFormat sdfFrom = new SimpleDateFormat(typeFrom);
        SimpleDateFormat sdfTo = new SimpleDateFormat(typeTo);

        try {
            re = sdfTo.format(sdfFrom.parse(re));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return re;
    }

    /**
     * 得到这个月有多少天
     *
     * @param year
     * @param month
     * @return
     */
    public int getMonthLastDay(int year, int month) {
        if (month == 0) {
            return 0;
        }
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);// 把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);// 日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 得到年份
     *
     * @return
     */
    public String getCurrentYear() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR) + "";
    }

    /**
     * 得到月份
     *
     * @return
     */
    public String getCurrentMonth() {
        Calendar c = Calendar.getInstance();
        return (c.get(Calendar.MONTH) + 1) + "";
    }

    /**
     * 获得当天的日期
     *
     * @return
     */
    public String getCurrDay() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DAY_OF_MONTH) + "";
    }

    /**
     * 得到几天/周/月/年后的日期，整数往后推,负数往前移动
     *
     * @param calendar
     * @param calendarType Calendar.DATE,Calendar.WEEK_OF_YEAR,Calendar.MONTH,Calendar.
     *                     YEAR
     * @param next
     * @return
     */
    public String getDayByDate(Calendar calendar, int calendarType, int next) {

        calendar.add(calendarType, next);
        Date date = calendar.getTime();
        String dateString = date_Formater_1.format(date);
        return dateString;

    }

    /**
     * 可以获取后退N天的日期
     * 格式：传入2 得到2014-11-30
     *
     * @param backDay
     * @return String
     */
    public String getStrDate(String backDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, Integer.parseInt(backDay));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String back = sdf.format(calendar.getTime());
        return back;
    }

    /**
     * 根据结束时间以及间隔差值，求符合要求的日期集合；
     *
     * @param endTime
     * @param interval
     * @param isEndTime
     * @return
     */
    public static Map<String, String> getErrandDate(String endTime, Integer interval, boolean isEndTime) {
        Map<String, String> result = new HashMap<String, String>();
        if (interval == 0 || isEndTime) {
            if (isEndTime)
                result.put(endTime, endTime);
        }
        if (interval > 0) {
            int begin = 0;
            for (int i = begin; i < interval; i++) {
                endTime = givedTimeToBefer(endTime, DATEMM, "yyyy-MM-dd");
                result.put(endTime, endTime);
            }
        }
        return result;
    }

    /**
     * 日期转换成Calendar
     */
    public static Calendar convertDateToCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;

    }

    /**
     * 对一个日期进行偏移
     *
     * @param date   日期
     * @param offset 偏移两
     * @return 偏移后的日期
     */

    public static Date addDayByDate(Date date, int offset) {

        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        int day = cal.get(Calendar.DAY_OF_YEAR);

        cal.set(Calendar.DAY_OF_YEAR, day + offset);

        return cal.getTime();
    }

    /**
     * 获得指定日期的年份
     *
     * @param date
     * @return
     */

    public static int getYearByDate(Date date) {

        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        return cal.get(Calendar.YEAR);

    }

    /**
     * 获得指定日期的月份
     *
     * @param date
     * @return
     */

    public static int getMonthByDate(Date date) {

        Calendar cal = Calendar.getInstance();

        cal.setTime(date);

        return cal.get(Calendar.MONTH);

    }

    /**
     * 根据数组得到小时
     */
/*    public static ArrayList exchangeHourData(ArrayList<BandSleepStepBean> a){

        int b = a.size()/6;
        int c = a.size()%6;

        Log.e("TAg",b+"===j===");
        Log.e("TAg",c+"===余数===");

        *//**
     *转换小时
     *//*
        ArrayList arrayList = new ArrayList();//转换小时后的集合
        for(int i = 0 ;i<b ; i++){

            int l = 0;
            for(int j = 0 ;j<6;j++){
                String k =a.get((i)*6+j).getCountDate();
                Log.e("TAg",k+"++++;;;;;;;;;;+");
                l = l +Integer.valueOf(k);
            }
            Log.e("TAg",l+"------每小时数据");
            arrayList.add(l+"");
        }
        int x = 0;
        for(int i = 0 ; i < c;i++){
            String y  =a.get(b*6+i).getCountDate();
            x = x+Integer.valueOf(y);
        }
        Log.e("TAg",x+"-------");
        arrayList.add(x+"");

        for(int i = 0 ; i < arrayList.size();i++){
            Log.e("TAg",arrayList.get(i)+"------arrayList"+i+"小时");
        }

        return arrayList;

    }*/

    /**
     * 将得到的数组对应时间
     */
    public static ArrayList setDataTime(ArrayList<BandSleepStepBean> a) {
        ArrayList arrayList = new ArrayList();
        return arrayList;
    }

    //得到String系统时间
    public static String getDateString() {
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

        //格式可以更改
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = sDateFormat.format(new java.util.Date());
        return date;

    }

    /**
     * 手环数组转对应日期
     */
    public static String timeExchangeData1(int a) {
        DateHelper dateHelper = DateHelper.getInstance();

        String date = dateHelper.getDateString();

        String ss = "";

        int b = a / 6;
        int c = a % 6;

//        Log.e("TAG",b+"----商");
//        Log.e("TAG",c+"====余数");

        String hour = "";
        String minute = c + "0";
        if (b <= 9) {
            hour = "0" + b;
        } else {
            hour = b + "";
        }

        ss = date + " " + hour + ":" + minute + ":" + "00";

        return ss;


    }

    /**
     * 手环数组转对应日期
     * bancDate 相差几天
     */
    public static String timeExchangeData2(int a, String bancDate) {
        DateHelper dateHelper = DateHelper.getInstance();
        //String date = dateHelper.getDateString();
        String date = dateHelper.getStrDate(bancDate);
        String ss = "";
        int b = a / 6;
        int c = a % 6;
        String hour = "";
        String minute = c + "0";
        if (b <= 9) {
            hour = "0" + b;
        } else {
            hour = b + "";
        }

        if(a == 144){

            Date date1 = addDayByDate(getInstance().getDate(date), 0);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String back = sdf.format(date1);

            ss = back + " " + "00" + ":" + "00" + ":" + "00";

        }else{
            ss = date + " " + hour + ":" + minute + ":" + "00";
        }


        return ss;

    }

    /**
     * 去除计步手环零数据
     * @param arrayListYesterday
     * @return
     */
/*    public static ArrayList addsteps(ArrayList<BandSleepStepBean> arrayListYesterday) {
        ArrayList<BandSleepStepBean> list = new ArrayList<BandSleepStepBean>();
        for (int i = 0; i < arrayListYesterday.size(); i++) {
            if(arrayListYesterday.get(i).getStepCount()!=0){
                list.add(arrayListYesterday.get(i));
            }
        }
        return list;
    }*/


    /**
     * 去除tittle手环零数据
     *
     * @param arrayListYesterday
     * @return
     */
    public static ArrayList addTitlts(ArrayList<GetSleepBean.SleepListBean> arrayListYesterday) {
        ArrayList<GetSleepBean.SleepListBean> list = new ArrayList<GetSleepBean.SleepListBean>();
        for (int i = 0; i < arrayListYesterday.size(); i++) {
            if (arrayListYesterday.get(i).getRollCount() != 0) {
                list.add(arrayListYesterday.get(i));
            }
        }
        return list;
    }

    private static Calendar gregorianCalendar = Calendar.getInstance();

    /**
     * 获取日期星期一日期
     *
     * @param
     * @return date
     */
    public static Date getFirstDayOfWeek(Date date) {
        if (date == null) {
            return null;
        }
        gregorianCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        gregorianCalendar.setTime(date);
        gregorianCalendar.set(Calendar.DAY_OF_WEEK, gregorianCalendar.getFirstDayOfWeek()); // Monday
        return gregorianCalendar.getTime();
    }

    /**
     * 获取日期星期天日期
     *
     * @param
     * @return date
     */
    public static Date getLastDayOfWeek(Date date) {
        if (date == null) {
            return null;
        }
        gregorianCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        gregorianCalendar.setTime(date);
        gregorianCalendar.set(Calendar.DAY_OF_WEEK, gregorianCalendar.getFirstDayOfWeek() + 6); // Monday
        return gregorianCalendar.getTime();
    }

    public Date getDate2(String dateStr) {
        Date date = new Date();
        if (TextUtils.isEmpty(dateStr)) {
            return date;
        }
        try {
            date = date_Formater_2.parse(dateStr);
            return date;

        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return date;

    }

    /**
     * 得到当前日期的截取年,月,日
     * i = 0年;i=1月;i=2日;
     */

    public String subStringData(int i, String data) {
        String[] split = data.split("-");

        return split[i];

    }

    /**
     * 去除月,日前面的0
     */

    public String deleteDate(String date) {
        if (date.substring(0, 1).equals("0")) {
            Log.e("TAG", date.substring(1, 2));
            return date.substring(1, 2);
        } else {
            return date;
        }
    }

    public static ArrayList removeDuplicateWithOrder(List list) {
        Set set = new HashSet();
        List newList = new ArrayList();
        for (Iterator iter = list.iterator(); iter.hasNext(); ) {
            Object element = iter.next();
            if (set.add(element))
                newList.add(element);
        }
        list.clear();
        list.addAll(newList);
        System.out.println(" remove duplicate " + list);
        return null;
    }

    /**
     * 获取指定月的第一天
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfMonth(Date date) {
        gregorianCalendar.setTime(date);
        gregorianCalendar.set(Calendar.DAY_OF_MONTH, 1);
        return gregorianCalendar.getTime();
    }

    /**
     * 获取指定月的最后一天
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfMonth(Date date) {
        gregorianCalendar.setTime(date);
        gregorianCalendar.set(Calendar.DAY_OF_MONTH, 1);
        gregorianCalendar.add(Calendar.MONTH, 1);
        gregorianCalendar.add(Calendar.DAY_OF_MONTH, -1);
        return gregorianCalendar.getTime();
    }

    /**
     * 可以获取后退N天的日期 重点:指定日期
     * 格式：传入2 得到2014-11-30
     *
     * @param backDay 指定的日期 正数向前 负数向后
     * @return String 传入的时间 YYYY-MM-dd
     */
    public String getStrDate2(int backDay, String dataString) {
        Date date1 = getInstance().getDate2(dataString);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        calendar.add(Calendar.DATE, backDay);
        Date date2 = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


        String back = sdf.format(date2);
        return back;
    }

    /**
     * 求某一个时间向前多少秒的时间(currentTimeToBefer)---OK
     *
     * @param givedTime        给定的时间
     * @param interval         间隔时间的毫秒数；计算方式 ：n(天)*24(小时)*60(分钟)*60(秒)(类型)
     * @param format_Date_Sign 输出日期的格式；如yyyy-MM-dd、yyyyMMdd等；
     */
    public static String givedTimeToBefer(String givedTime, long interval, String format_Date_Sign) {
        String tomorrow = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format_Date_Sign);
            Date gDate = sdf.parse(givedTime);
            long current = gDate.getTime(); // 将Calendar表示的时间转换成毫秒
            long beforeOrAfter = current - interval * 1000L; // 将Calendar表示的时间转换成毫秒
            Date date = new Date(beforeOrAfter); // 用timeTwo作参数构造date2
            tomorrow = new SimpleDateFormat(format_Date_Sign).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return tomorrow;
    }

    /**
     * 比较两个日期先后
     * <p>
     * false:后一个比较大
     * true: 前一个比较大
     */

    public static boolean compare_date(String DATE1, String DATE2) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        boolean b = false;
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() >= dt2.getTime()) {
                b = true;
            } else if (dt1.getTime() < dt2.getTime()) {
                b = false;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return b;
    }

    /**
     * 得到系统时间
     *
     * @param ss 格式 : yyyy-MM-dd HH:mm:ss  / yyyy-MM-dd
     * @return
     */
    public static String getSystemDateString(String ss) {
        Time time = new Time("GMT+8");
        time.setToNow();
        SimpleDateFormat sDateFormat = new SimpleDateFormat(ss);
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }

    /**
     * 得到系统时间
     *
     * @return
     */
    public static String getSystemDateString2() {
        String todayData = "";

        Time time = new Time("GMT+8");
        time.setToNow();
        int year = time.year;
        int month = time.month;
        int day = time.monthDay;
        int minute = time.minute;
        int hour = time.hour;
        int sec = time.second;
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }

    /**
     * 根据日期获得星期
     *
     * @param date
     * @return String
     */
    public static String getWeekOfDateString(Date date) {
        String[] weekDaysName = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        String[] weekDaysCode = {"0", "1", "2", "3", "4", "5", "6"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return weekDaysCode[intWeek];
    }

    /**
     * 根据日期获得星期
     *
     * @param date
     * @return int
     */
    public static int getWeekOfDateInt(Date date) {
        int[] weekDaysCode = {0, 1, 2, 3, 4, 5, 6};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return weekDaysCode[intWeek];
    }

    /**
     * 根据给定某个月日期得到之间差得天数
     *
     * @param endTime
     * @param startTime
     * @return
     */
    public int getMonthDay(String endTime, String startTime) {
        int i = 0;
        try {
            i = Integer.valueOf(endTime.substring(8, 10)).intValue() - Integer.valueOf(startTime.substring(8, 10)).intValue();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return i + 1;
    }

    /**
     * 分钟转换hour
     *
     * @param minutes
     * @return
     */
    public int exchangeHour(int minutes) {
        int hour = 344 / 60;

        return hour;
    }

    /**
     * 分钟转换minute
     *
     * @param minutes
     * @return
     */
    public int exchangeMinute(int minutes) {
        int minute = 344 % 60;

        return minute;
    }

    /**
     * 通过传入开始日期得到截止当天的所有天数集合
     *
     * @param dBegin1
     * @return
     */
    public static List<String> findDates(String dBegin1) {

        Date dBegin = getInstance().getDate2(dBegin1);
        Date dEnd = getInstance().getDate2(getSystemDateString2());

        List lDate = new ArrayList();
        lDate.add(getInstance().getDataString_2(dBegin));
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(dBegin);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(dEnd);
        // 测试此日期是否在指定日期之后
        while (dEnd.after(calBegin.getTime())) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.DAY_OF_MONTH, 1);

            lDate.add(getInstance().getDataString_2(calBegin.getTime()));
        }

        return lDate;
    }

    /**
     * 根据时间转化为第多少个十分钟
     */

    public static int extime(String date) {

        String hours = date.substring(11, 13);
        String minutes = date.substring(14, 16);

        int hour = Integer.valueOf(hours).intValue();
        int minute = Integer.valueOf(minutes).intValue();

        int count = hour * 6 + minute / 10;

        Log.e("TAG----", count + "====");

        return count;

    }


    /**
     * 使用 Map按key进行排序
     *
     * @param map
     * @return
     */
    public static Map<Integer, PedometerData> sortMapByKey(Map<Integer, PedometerData> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<Integer, PedometerData> sortMap = new TreeMap<Integer, PedometerData>(new MapKeyComparator());

        sortMap.putAll(map);

        return sortMap;
    }

    static class MapKeyComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer lhs, Integer rhs) {
            return lhs.compareTo(rhs);
        }
//
//        @Override
//        public int compare(String str1, String str2) {
//
//            return str1.compareTo(str2);
//        }
    }


    /**
     * @param s1 本地版本号
     * @param s2 网络版本号
     * @return true 更新 false 不更新
     */
    public boolean CompareVersion(String s1, String s2) {
        String replace1 = s1.replace(".", ":");
        String replace2 = s2.replace(".", ":");

        String split1[] = replace1.split(":");
        String split2[] = replace2.split(":");

        if (Integer.parseInt(split1[0]) < Integer.parseInt(split2[0])) {
            return true;
        } else if (Integer.parseInt(split1[0]) == Integer.parseInt(split2[0])) {
            if (Integer.parseInt(split1[1]) < Integer.parseInt(split2[1])) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}
