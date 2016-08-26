package com.cavytech.wear2.cavylifeband;

import java.util.HashMap;

/**
 * Created by blacksmith on 2016/4/14.
 */
public interface InterfaceOfBLECallback {
    public void onButtonClicked();
    public void onButtonLongPressed(byte[] data);
    public void onBatteryData(int status);
    public void onWarningData();
    public void onSystemData(LifeBandBLE.CavyBandSystemData data);
    public void onDataSync(HashMap<Integer, PedometerData> TodayData, HashMap<Integer, PedometerData> YesterdayData);//, HashMap<Integer, PedometerData> BeforeYesterdayData);
    public void onDeviceSignature(byte[] data);
    public void BLEConnectionEvents(int eventCode, LifeBandBLE.CavyBandDevice device);
    public void onOADStatusChanged(int statusCode, float progressRate);
}
