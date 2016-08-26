package com.cavytech.wear2.util;

import com.cavytech.wear2.entity.DBfriendBean;
import com.cavytech.wear2.entity.FriendBean;

import java.util.Comparator;

/**
 * @author hf
 *         写在这里面   按ascii排序
 */
public class PinyinComparator2 implements Comparator<FriendBean.FriendInfosBean> {

    public int compare(FriendBean.FriendInfosBean o1, FriendBean.FriendInfosBean o2) {
        if (PinYinUtils.getFirstLetter(o2.getNickname()).equals("#")) {
            return -1;
        } else if (PinYinUtils.getFirstLetter(o1.getNickname()).equals("#")) {
            return 1;
        } else {
            String s1 = exChange(PinYinUtils.getPinYin(o1.getNickname()));
            String s2 = exChange(PinYinUtils.getPinYin(o2.getNickname()));
            return s1.compareTo(s2);
        }

    }

    public static String exChange(String str) {
        StringBuffer sb = new StringBuffer();
        if (str != null) {
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (Character.isUpperCase(c)) {
                    sb.append((c));
                } else if (Character.isLowerCase(c)) {
                    sb.append(Character.toUpperCase(c));
                }
            }
        }

        return sb.toString();
    }
}
