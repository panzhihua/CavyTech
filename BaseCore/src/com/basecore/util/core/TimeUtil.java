
package com.basecore.util.core;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class TimeUtil {
	/**
	 * 
	  * @Title: TimeCompare
	
	  * @Description: TODO
	
	  * @param @param time1
	  * @param @param time2
	  * @param @return    设定文件
	
	  * @return int   ==0相等;<0 time1<time2;>0time1>time2
	
	  * @throws
	 */
	public static int TimeCompare(String time1,String time2){
		DateFormat df=new SimpleDateFormat("yyy-MM-dd HH:mm:ss:SSS");
		Calendar c1=Calendar.getInstance();
		Calendar c2=Calendar.getInstance();
		try {
			c1.setTime(df.parse(time1));
			c2.setTime(df.parse(time2));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return c1.compareTo(c2);
	}
	public static String compareTime(long timestamp) {
		String info = "";
		Date d = new Date();
		try {
			Date newD = new Date(timestamp);
			if (newD.getYear() != d.getYear()) {// 年份不同
				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				return info = sdf2.format(newD);
			} else {
				if (newD.getDate() == d.getDate() && newD.getMonth() == d.getMonth()) {// 同年同月同日
					SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
					return "今天 "+sdf2.format(newD);
				} else {
					SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd HH:mm");
					return info = sdf2.format(newD);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return info;
		}

	}

}
