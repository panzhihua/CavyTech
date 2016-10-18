package com.cavytech.wear2.cavylifeband;

import java.util.HashMap;

/**
 * Created by blacksmith on 2016/4/14.
 */
public interface InterfaceOfBLECallback {
    public void onButtonClicked();//按下
    public void onButtonLongPressed(byte[] data);//长按
    public void onBatteryData(int status);//询问电量状态
    public void onWarningData();
    public void onSystemData(LifeBandBLE.CavyBandSystemData data);//查询系统状态
    public void onDataSync(HashMap<Integer, PedometerData> TodayData, HashMap<Integer, PedometerData> YesterdayData);//数据同步
    public void onDeviceSignature(byte[] data);//设备标示
    public void BLEConnectionEvents(int eventCode, LifeBandBLE.CavyBandDevice device);//连接上手环
    public void onOADStatusChanged(int statusCode, float progressRate);//手环固件升级
}
