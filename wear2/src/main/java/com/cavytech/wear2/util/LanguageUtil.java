package com.cavytech.wear2.util;

import android.content.Context;

import java.util.Locale;
/**
 * Created by panzhihua on 2016/9/19.
 */
public class LanguageUtil {

    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }

}
