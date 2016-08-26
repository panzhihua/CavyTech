package com.cavytech.wear2.util;

/**
 * Created by libin on 4/26 0026.
 * 邮箱：bin.li@tunshu.com
 */

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.text.TextUtils;

import com.cavytech.wear2.application.CommonApplication;
import com.cavytech.wear2.entity.DBfriendBean;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.xutils.DbManager;
import org.xutils.db.Selector;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * author zaaach on 2016/1/28.
 */
public class PinYinUtils {

    private static final String NAME = "nickname";
    private static final String PINYIN = "pinyin";

    /**
     * 获取拼音的首字母（大写）
     * @param pinyin
     * @return
     */
    public static String getFirstLetter(final String pinyin){
        if (TextUtils.isEmpty(pinyin)) return "";
        String c = pinyin.substring(0, 1);
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c).matches()){
            return c.toUpperCase();
        }
        return "";
    }
    /**
     * 将汉字转换为全拼
     *
     * @param src
     * @return String
     */
    public static String getPinYin(String src) {
        char[] t1 = null;
        t1 = src.toCharArray();
        // System.out.println(t1.length);
        String[] t2 = new String[t1.length];
        // System.out.println(t2.length);
        // 设置汉字拼音输出的格式
        HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
        t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        t3.setVCharType(HanyuPinyinVCharType.WITH_V);
        String t4 = "";
        int t0 = t1.length;
        try {
            for (int i = 0; i < t0; i++) {
                // 判断能否为汉字字符
                // System.out.println(t1[i]);
                if (Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+")) {
                    t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);// 将汉字的几种全拼都存到t2数组中
                    t4 += t2[0];// 取出该汉字全拼的第一种读音并连接到字符串t4后
                } else {
                    // 如果不是汉字字符，间接取出字符并连接到字符串t4后
                    t4 += Character.toString(t1[i]);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return t4;
    }

    /**
     * 将字符串转换成ASCII码
     * @param cnStr
     * @return String
     */
    public static String getCnASCII(String cnStr) {
        StringBuffer strBuf = new StringBuffer();
        // 将字符串转换成字节序列
        byte[] bGBK = cnStr.getBytes();
        for (int i = 0; i < bGBK.length; i++) {
            // 将每个字符转换成ASCII码
            strBuf.append(Integer.toHexString(bGBK[i] & 0xff));
        }
        return strBuf.toString();
    }

    /**
     * 通过名字或者拼音搜索
     * @param keyword
     * @return
     */
    public static List<DBfriendBean> searchFriend(final String keyword){
//        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory().toString() + "CB_LIST.db", null);
        SQLiteDatabase db = CommonApplication.dm.getDatabase();
        Cursor cursor = db.rawQuery("select * from " + "friend" +" where nickname like \"%" + keyword
                + "%\" or pinyin like \"%" + keyword + "%\"", null);
        List<DBfriendBean> result = new ArrayList<>();
        DBfriendBean city;
        while (cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex(NAME));
            String pinyin = cursor.getString(cursor.getColumnIndex(PINYIN));
            city = new DBfriendBean();
            city.setPinyin(pinyin);
            city.setNickname(name);
            result.add(city);
        }
        cursor.close();
//        db.close();
        Collections.sort(result, new PinyinComparator());
        return result;
    }

}

