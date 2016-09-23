package com.cavytech.wear2.util;

import android.app.Activity;
import android.util.Log;

import com.cavytech.wear2.cavylifeband.LifeBandBLE;

import java.util.Calendar;

/**
 * 手环操作封装
 * Created by longjining on 16/6/7.
 */
public class LifeBandBLEUtil {

    private static LifeBandBLEUtil mInstance;

    private static LifeBandBLE mLifeBand;

    private Activity mActivity;

    public static LifeBandBLEUtil getInstance()
    {
        if (mInstance == null)
        {
            synchronized (LifeBandBLEUtil.class)
            {
                if (mInstance == null)
                {
                    mInstance = new LifeBandBLEUtil();
                }
            }
        }
        return mInstance;
    }

    public void setCallBack(Activity activity){

        mActivity = activity;

        if(null == mLifeBand){
            mLifeBand = new LifeBandBLE(activity);
        }else{
            mLifeBand.setActivity(activity);
        }
    }

    public void StartScanCavyBand(){
        mLifeBand.StartScanCavyBand();
    }

    public void StopScanCavyBand(){
        if(mLifeBand!=null) {
            mLifeBand.StopScanCavyBand();
        }
    }

    public int getConnectionState(){
        return mLifeBand.mConnectionState;
    }


    public void getBaseSysInfo(){
        mLifeBand.InquireSystemStatus();
        mLifeBand.InquireBatteryStatus();
        mLifeBand.DeviceSignature();
    }

    public void getBaseSysInfo1(){
        mLifeBand.InquireSystemStatus();
        mLifeBand.InquireBatteryStatus();
    }

    public void getBaseSysInfo2(){
        mLifeBand.DeviceSignature();
    }

    public void Disconnect(){
        mLifeBand.Disconnect();
    }

    public void SetSystemTime(){
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);//系统得到的月份要+1
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int realMinute = (hour * 60) + minute;
        int week = c.get(Calendar.DAY_OF_WEEK);

        int realWeek;
        if (1 == week) {
            realWeek = 7;
        }else{
            realWeek = week - 1;
        }

        Log.e("TAG","realMinute="+realMinute+"realWeek="+realWeek);
        mLifeBand.DateSetup(year, month + 1, day);
        mLifeBand.TimeSetup(realMinute, realWeek);
    }

    public void connect(String macAddr) {

        mLifeBand.StopScanCavyBand();

        if(mLifeBand.mConnectionState == LifeBandBLE.STATE_CONNECTED){
            mLifeBand.Disconnect();
        }

        mLifeBand.Connect(macAddr);
    }

    public void SelectOperation(){
        //mLifeBand.SelectOperation(3, 4, 25);
        mLifeBand.SelectOperation(2, 1, 500);
    }

    /*public String getMacAdress(){


        Map<String, String> userBandInfoMap = (Map<String, String>)SerializeUtils.unserialize(Constants.SERIALIZE_USERBAND_INFO);

        if(null != userBandInfoMap){
            String userID = CacheUtils.getString(mActivity, Constants.USERID);
            return userBandInfoMap.get(userID);
        }

        return "";
    }

    public void saveMacAdress(String macAdr){


        Map<String, String> userBandInfoMap = (Map<String, String>)SerializeUtils.unserialize(Constants.SERIALIZE_USERBAND_INFO);

        if(null == userBandInfoMap){

            userBandInfoMap = new HashMap<String, String>();
         }

        userBandInfoMap.put(CacheUtils.getString(mActivity, Constants.USERID), macAdr);

        SerializeUtils.serialize(userBandInfoMap, Constants.SERIALIZE_USERBAND_INFO);
    }
*/
    public int DataSync(int today, int startTime)
    {
        return mLifeBand.DataSync(today, startTime);
    }

    public int AlarmSetup(int mode, int time, int daysFlag)
    {
        return mLifeBand.AlarmSetup(mode, time, daysFlag);
    }

    public int DoVibrate(int iParam1, int iParam2, int iParam3, int iParam4)
    {
        return mLifeBand.DoVibrate(iParam1, iParam2,iParam3, iParam4);
    }

    public void UpdateFirmwareByFilePath(String filePath)
    {
        mLifeBand.UpdateFirmwareByFilePath(filePath);
    }

    public void ConfigSetup(int func, int enable)
    {
        mLifeBand.ConfigSetup(func, enable);
    }

    public void SelectIdleMode()
    {
        mLifeBand.SelectIdleMode();
    }

    public void Close()
    {
        mLifeBand.Close();
    }


}
