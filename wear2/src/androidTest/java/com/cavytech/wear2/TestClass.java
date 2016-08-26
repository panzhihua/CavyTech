package com.cavytech.wear2;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.cavytech.wear2.util.DateHelper;

/**
 * Created by LiBin on 2016/8/5.
 *
 * 测试类
 */
public class TestClass extends InstrumentationTestCase {

    public void test(){
        for(int i=0;i<144;i++){
            String date = DateHelper.getInstance().timeExchangeData2(i, -1 + "");
            Log.e("TAG","测试昨天----"+date);
        }

        for(int i=1;i<145;i++){
            String date2 = DateHelper.timeExchangeData2(i - 1, 0 + "");
            Log.e("TAG","测试今天----"+date2);
        }

    }
}
