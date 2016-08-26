package com.cavytech.wear2.cavylifeband;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;


/**
 * Created by blacksmith on 2015/11/27.
 */
public class LifeBandBLE {
    public final static String TAG = "PlayBand BLE Mode";
    private final static boolean IsDebug = true;
    private final byte PACKET_DATA_BNO055 = (byte) 0xA1;
    private final byte PACKET_DATA_BATTERY = (byte) 0xB1;
    private final byte PACKET_DATA_SYSTEM = (byte) 0xC1;
    private final byte PACKET_DATA_BUTTON = (byte) 0xD1;
    private final byte PACKET_DATA_DEVICE_SIGNATURE = (byte)0xE1;
    private final byte PACKET_DATA_WARNING = (byte) 0xF1;
    private final byte PACKET_DATA_DATA_SYNC = (byte)0xDA;
    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;
    public static final int STATE_FIND_NEW_BAND = 3;
    //======================================================
    // Bluetooth LowEnergy Member Variable Part
    //======================================================
    private static final long BLE_SCAN_PERIOD = 10000;
    public final static UUID UUID_SERVICE = UUID.fromString("14839AC4-7D7E-415C-9A42-167340CF2339");
    public final static UUID READ_CHARACTERISTIC = UUID.fromString("0734594A-A8E7-4B1A-A6B1-CD5243059A57");
    public final static UUID SEND_CHARACTERISTIC = UUID.fromString("8B00ACE7-EB0B-49B0-BBE9-9AEE0A26E1A3");
    public final static UUID UUID_OAD_SERVICE = UUID.fromString("f000ffc0-0451-4000-b000-000000000000");
    public final static UUID UUID_OAD_IDENTIFY = UUID.fromString("f000ffc1-0451-4000-b000-000000000000");
    public final static UUID UUID_OAD_BLOCK = UUID.fromString("f000ffc2-0451-4000-b000-000000000000");
    public final static UUID UUID_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGattCharacteristic readCharacteristic;
    private BluetoothGattCharacteristic sendCharacteristic;
    private BluetoothGattCharacteristic OAD_Identify_Characteristic;
    private BluetoothGattCharacteristic OAD_BLock_Characteristic;
    private String mBluetoothDeviceAddress;
    protected Handler mBLE_ScanHandler;
    protected boolean mScanning = false;
    private BluetoothGatt mBluetoothGatt;
    public int mConnectionState = STATE_DISCONNECTED;
    private Thread WriteToBand;

    private Context mContext;
    //private Semaphore mutexLock = new Semaphore(1);
    private String BandAddress= "";
    public Activity mActivity;
    public Object mCallBackObj;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private ArrayList<String> mWriteToBandCmd = new ArrayList<String>();
    private FileInputStream imgInputStream;

    public LifeBandBLE(Activity activity)
    {
        this.initialize(activity);
    }
    //public HashMap<Integer, PedometerData> BeforeYesterdayPedometerData = new HashMap<Integer, PedometerData>();
    public HashMap<Integer, PedometerData> YesterdayPedometerData = new HashMap<Integer, PedometerData>();
    public HashMap<Integer, PedometerData> TodayPedometerData = new HashMap<Integer, PedometerData>();
    private boolean YesterdayPedometerDataSync = false;
    //private boolean BeforeYesterdayPedometerDataSync = false;

    private boolean initialize(Activity activity) {
        this.mActivity = activity;
        this.mContext = activity;
        this.mCallBackObj = activity;
        mBLE_ScanHandler = new Handler();
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager)mContext
                    .getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                DebugLog( "Unable to get BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            DebugLog( "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        if (mBluetoothAdapter.isEnabled() == false) {
            //mBluetoothAdapter.enable();
            Intent _EnableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(_EnableIntent, 1);
        }

        for(int i = 1; i < 145; i++)
        {
            PedometerData temp = new PedometerData();
            YesterdayPedometerData.put(i, temp);
            PedometerData temp2 = new PedometerData();
            TodayPedometerData.put(i, temp2);
        }

        Handler mHandler = new Handler();
        WriteToBand = new Thread()
        {
            public void run()
            {
                try
                {
                    while(true)
                    {
                        if( mConnectionState == STATE_CONNECTED && sendCharacteristic != null && mWriteToBandCmd.size() >= 1)
                        {
                            String cmd = mWriteToBandCmd.get(0);
                            Log.d("BLE WriteToBand", cmd);
                            sendCharacteristic.setValue(cmd.getBytes());
                            mBluetoothGatt.writeCharacteristic(sendCharacteristic);
                            mWriteToBandCmd.remove(0);
                        }
                        sleep(1000);
                    }
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        WriteToBand.start();
        return true;
    }

    public void setActivity(Activity activity){
        this.mActivity = activity;
        this.mContext = activity;
        this.mCallBackObj = activity;
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d("BluetoothGatt", "State = " + newState);
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                DebugLog( "Connected to GATT server.");
                DebugLog( "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mConnectionState = STATE_DISCONNECTED;
                SendConnectionCallBack(STATE_DISCONNECTED, gatt.getDevice(), false);
                mWriteToBandCmd.clear();
                Close();
                DebugLog( "Disconnected from GATT server.");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService serviceBLE = gatt.getService(UUID_SERVICE);
                DebugLog( "Found Service!!");

                readCharacteristic = serviceBLE.getCharacteristic(READ_CHARACTERISTIC);
                sendCharacteristic = serviceBLE.getCharacteristic(SEND_CHARACTERISTIC);
                if(sendCharacteristic != null)
                {
                    DebugLog("Found sendCharacteristic!!");
                    DebugLog("write: permission= " + sendCharacteristic.getPermissions() + ", properties: " + sendCharacteristic.getProperties()
                            + ", writeType: " + sendCharacteristic.getWriteType());
                }
                if(readCharacteristic != null)
                {
                    DebugLog("Found readCharacteristic!!");
                    DebugLog("read: permission= " + readCharacteristic.getPermissions() + ", properties: " + readCharacteristic.getProperties()
                            + ", writeType: " + readCharacteristic.getWriteType());
                    mBluetoothGatt.setCharacteristicNotification(readCharacteristic, true);
                }

                searchOADCharacteristics(gatt);

                if(readCharacteristic != null && sendCharacteristic != null)
                {
                    SendConnectionCallBack(STATE_CONNECTED, gatt.getDevice(), false);
                    mConnectionState = STATE_CONNECTED;

                    //for(int i = 1; i < 10; i++)
                    //    ConfigSetup(i, 1);
                    //setTimeNow();
                }
                else
                {
                    gatt.discoverServices();
                }

            } else {
                DebugLog( "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
            switch (status) {
                case BluetoothGatt.GATT_SUCCESS:
                    DebugLog( "write data success");
                    break;// 写入成功
                case BluetoothGatt.GATT_FAILURE:
                    DebugLog( "write data failed");
                    break;// 写入失败
                case BluetoothGatt.GATT_WRITE_NOT_PERMITTED:
                    DebugLog( "write not permitted");
                    break;// 没有写入的权限
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            readCharacterisricValue(characteristic);
        }
    };

    private void SendConnectionCallBack(int code, BluetoothDevice device, boolean priority)
    {
        final int messageCode = code;
        final CavyBandDevice temp = new CavyBandDevice(device, priority);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (InterfaceOfBLECallback.class.isInstance(mCallBackObj)) {
                    ((InterfaceOfBLECallback) mCallBackObj).BLEConnectionEvents(messageCode, temp);
                }
            }
        });
    }
    public int InquireBatteryStatus() {
        String _CmdStr = "?BAT\n";
        DebugLog("[InquireBatteryStatus()] will finish");
        return WriteOutputStream(_CmdStr);
    }

    public int InquireSystemStatus() {
        String _CmdStr = "?SYSTEM\n";
        DebugLog("[InquireSystemStatus()] will finish");
        return WriteOutputStream(_CmdStr);
    }

    public int SelectOperation(int iParam1, int iParam2, int iParam3) {

        if (iParam1 < Global.LOW_POWER_MODE || iParam1 > Global.NORMAL_SAVE_POWER_MODE) {
            return Global.ERR_INVALID_PARAMETERS;
        }
        //
        String _CmdStr;
        if (iParam1 == Global.NORMAL_SAVE_POWER_MODE) {
            if (iParam2 < Global.NORMAL_SAVE_POWER_MODE_6x_COMPASS || iParam2 > Global.NORMAL_SAVE_POWER_MODE_9x_NDOF) {
                return Global.ERR_INVALID_PARAMETERS;
            }
        } else if (iParam1 == Global.NORMAL_MODE){
            if (iParam2 < Global.NORMAL_MODE_6x_COMPASS	|| iParam2 > Global.NORMAL_MODE_9x_NDOF) {
                return Global.ERR_INVALID_PARAMETERS;
            }
        }else if (iParam1 == Global.SNIFF_MODE) {
            if (iParam2 < Global.SNIFF_MODE_NO_CHANGE || iParam2 > Global.SNIFF_MODE_ACC_ON) {
                return Global.ERR_INVALID_PARAMETERS;
            }
        } else if (iParam1 == Global.LOW_POWER_MODE) {
            if (iParam2 < Global.LOW_POWER_MODE_SLEEP || iParam2 > Global.LOW_POWER_MODE_STANDBY) {
                return Global.ERR_INVALID_PARAMETERS;
            }
        }

        _CmdStr = String.format("%%OPR=%d,%d,%d\n", iParam1, iParam2,iParam3);

        Global.BandDataPerSecond=iParam3;
        Global.BandDataRateHZ=(float)1/iParam3;

        DebugLog( "[SelectOperation()] will finish");
        return WriteOutputStream(_CmdStr);
    }

    public int ControlLED(int iParam1, int iParam2, int iParam3, int iParam4,
                          int iParam5) {
        if (iParam1 < Global.RED_LED_ID || iParam1 > Global.BLUE_LED_ID
                || iParam2 < Global.LED_OFF || iParam2 > Global.LED_FLASH
                || iParam3 < Global.MIN_LED_POWER
                || iParam3 > Global.MAX_LED_POWER || iParam4 < 0 || iParam5 < 0) {
            return Global.ERR_INVALID_PARAMETERS;
        }

        String _CmdStr;
        if (iParam2 == Global.LED_OFF) {
            _CmdStr = String.format("%%LED=%d,%d\n", iParam1, iParam2);
        } else if (iParam2 == Global.LED_ON) {
            _CmdStr = String.format("%%LED=%d,%d,%d\n", iParam1, iParam2,
                    iParam3);
        } else {
            _CmdStr = String.format("%%LED=%d,%d,%d,%d,%d\n", iParam1, iParam2,
                    iParam3, iParam4, iParam5);
        }

        DebugLog("[ControlLED()] will finish");
        return WriteOutputStream(_CmdStr);
    }

    public int DoVibrate(int iParam1, int iParam2, int iParam3, int iParam4)
    {
        if (iParam1 < Global.VIBRATE_OFF || iParam1 > Global.VIBRATE_TWICE
                || iParam2 < Global.MIN_VIBRATE_POWER
                || iParam2 > Global.MAX_VIBRATE_POWER || iParam3 < 0
                || iParam4 < 0) {
            return Global.ERR_INVALID_PARAMETERS;
        }

        String _CmdStr;
        if (iParam1 == Global.VIBRATE_OFF) {
            _CmdStr = String.format("%%VIB=%d\n", iParam1);
        } else {
            if (iParam1 == Global.VIBRATE_ONCE) {
                _CmdStr = String.format("%%VIB=%d,%d,%d\n", iParam1,iParam2, iParam3);
            } else
            {
                _CmdStr = String.format("%%VIB=%d,%d,%d,%d\n", iParam1,iParam2, iParam3, iParam4);
            }
        }

        DebugLog("[DoVibrate()] will finish");
        return WriteOutputStream(_CmdStr);
    }

    /**
     * 设置手环休眠
     * @return
     */
    public int SelectIdleMode()
    {
        String _CmdStr = String.format("%%OPR=1,1\n");

        DebugLog( "[SelectIdleMode()] will finish");
        return WriteOutputStream(_CmdStr);
    }

    /**
     * ControlLED2, control led flash with counting times
     * @param ledId red = 1, green = 2, blue = 3
     * @param mode off = 1, on = 1, flash = 2
     * @param onPeriod led on period time
     * @param offPeriod led off period time
     * @param count flash counting times
     */
    public int ControlLED2(int ledId, int mode, int onPeriod, int offPeriod, int count) {
        final int maxLedFlashCountMode = 3;
        if (ledId < 1 || ledId > 3 || mode < Global.LED_OFF || mode > maxLedFlashCountMode || onPeriod < 0 || offPeriod < 0) {
            return Global.ERR_INVALID_PARAMETERS;
        }

        if(onPeriod > 98)
            onPeriod = 98;
        if(offPeriod > 98)
            offPeriod = 98;

        String _CmdStr = "";

        if(mode == Global.LED_OFF)
        {
            _CmdStr = String.format("%%LED2=%d,%d\n", ledId, mode);
        }
        else if(mode == Global.LED_ON)
        {
            _CmdStr = String.format("%%LED2=%d,%d\n", ledId, mode);
        }
        else if(mode == Global.LED_FLASH)
        {
            _CmdStr = String.format("%%LED2=%d,%d,%d,%d\n", ledId, mode, onPeriod, offPeriod);
        }
        else
        {
            _CmdStr = String.format("%%LED2=%d,%d,%d,%d,%d\n", ledId, mode, onPeriod, offPeriod, count);
        }

        DebugLog("[ControlLED2()] will finish");
        return WriteOutputStream(_CmdStr);
    }

    /**
     * DateSetup, setup the date to band, format=> %DATE=yyyy,mm,dd\n
     * @param year the year now
     * @param month the month now
     * @param day today
     */
    public int DateSetup(int year, int month, int day)
    {
        String _CmdStr = "";
        _CmdStr = String.format("%%DATE=%d,%d,%d\n", year, month, day);
        DebugLog("[DateSetup()] will finish");
        return WriteOutputStream(_CmdStr);
    }

    /**
     * TimeSetup, set the time to band, format=> %TIME=minutes,weekday\n
     * @param time the time now in minutes as a unit
     * @param dayOfWeek day of week, sun:7 sat:6 fri:5 thu:4 wed:3 tue:2 mon:1
     */
    public int TimeSetup(int time, int dayOfWeek)
    {
        String _CmdStr = "";
        _CmdStr = String.format("%%TIME=%d,%d\n", time, dayOfWeek);
        DebugLog("[TimeSetup()] will finish");
        return WriteOutputStream(_CmdStr);
    }

    /**
     * AlarmSetup, set the Alarm clock, format=> %ALARM=Mode,Time,DaysInWeek Flag\n
     * @param mode 0=off, 1=one shot, 2=period
     * @param time the time in minutes as unit
     * @param daysFlag  b7 b6  b5  b4  b3  b2  b1  b0
    x  sun sat fri thu wed tue mon, example: everyday flag = 01111111 = 127
     */
    public int AlarmSetup(int mode, int time, int daysFlag)
    {
        String _CmdStr = "";
        _CmdStr = String.format("%%ALARM1=%d,%d,%d\n", mode, time, daysFlag);
        DebugLog("[AlarmSetup()] will finish");
        return WriteOutputStream(_CmdStr);
    }

    /**
     * ConfigSetup, set function enable or disable
     * @param func 1~9
     * @param enable true:1 or false:0
     */
    public int ConfigSetup(int func, int enable)
    {
        String _CmdStr = "";
        _CmdStr = String.format("%%CFG=%d,%d\n", func, enable);
        DebugLog("[ConfigSetup()] will finish");
        return WriteOutputStream(_CmdStr);
    }

    /**
     * DataSync, send the sync request to band for getting pedometer data in onDataSync callback
     * @param today 0 is the day before yesterday, 1 is yesterday, 2 is today
     * @param startTime the start time in every ten minutes as a unit
     */
    public int DataSync(int today, int startTime)
    {
        String _CmdStr = "";
        _CmdStr = String.format("%%SYNC=%d,%d\n", today, startTime);
        DebugLog("[DataSync()] will finish");
        return WriteOutputStream(_CmdStr);
    }

    /**
     * DeviceSignature, send the signature request to band for getting signature data in onDeviceSignature callback
     */
    public int DeviceSignature()
    {
        String _CmdStr = "?SIGNATURE\n";
        DebugLog("[DeviceSignature()] will finish");
        return WriteOutputStream(_CmdStr);
    }

    public int WriteOutputStream(String _CmdStr) {
        if(sendCharacteristic == null) {
            DebugLog("WriteOutputStream fail!!");
            return Global.ERR_NOT_CONNECTED;
        }
        mWriteToBandCmd.add(_CmdStr);
        return 1;
    }

    private void onSystemData(byte buffer[]) {
        if(InterfaceOfBLECallback.class.isInstance(mCallBackObj))
            ((InterfaceOfBLECallback)mCallBackObj).onSystemData(new CavyBandSystemData(buffer));
    }

    private void onButtonData(byte buffer[]) {
        int data = buffer[2];

        if(data == 3)
        {
            if(InterfaceOfBLECallback.class.isInstance(mCallBackObj))
                ((InterfaceOfBLECallback)mCallBackObj).onButtonLongPressed(buffer);
        }
        else
        {
            if(InterfaceOfBLECallback.class.isInstance(mCallBackObj))
                ((InterfaceOfBLECallback)mCallBackObj).onButtonClicked();
        }
    }

    private void onBatteryData(byte buffer[]) {
        int _BatLife = buffer[2];
        int _Status = buffer[5];

        String res = String.format("%d,%d", _BatLife,_Status);
        if(InterfaceOfBLECallback.class.isInstance(mCallBackObj))
            ((InterfaceOfBLECallback)mCallBackObj).onBatteryData(_BatLife);
    }

    private void onWarningData(byte buffer[]) {
        int lowBattery = buffer[2];
        //int hightTemp = buffer[3];
        //int otherWarning = buffer[4];
        //int willReset5s = buffer[5];
        //String res = String.format("%d,%d,%d,%d", lowBattery,hightTemp,otherWarning,willReset5s);
        if(InterfaceOfBLECallback.class.isInstance(mCallBackObj))
            ((InterfaceOfBLECallback)mCallBackObj).onWarningData();
    }

    /**
     * onDeviceSignature, on get the signature data
     * @param data bytes data
     */
    private void onDeviceSignature(byte[] data)
    {
        byte[] useData = new byte[16];
        System.arraycopy(data, 2, useData, 0, 16);

        byte[] resultData = AES.DecryptAES(useData);

        if(InterfaceOfBLECallback.class.isInstance(mCallBackObj))
            ((InterfaceOfBLECallback)mCallBackObj).onDeviceSignature(resultData);
    }

    /**
     * onDataSync, on get the sync data
     * @param data bytes data
     */
    private void onDataSync(byte[] data)
    {
        int searchDay = data[2];
        int time, tilts, steps;

        //Log.w("PedometerData", String.format(" %d, %d, %d, %d \n", data[0], data[1], data[2], data[3]));
        //Log.w("PedometerData", String.format(" %d, %d, %d, %d \n", unsignedToBytes(data[4]), data[5], data[6], data[7]));
        //Log.w("PedometerData", String.format(" %d, %d, %d, %d \n", unsignedToBytes(data[8]), data[9], data[10], data[11]));
        //Log.w("PedometerData", String.format(" %d, %d, %d, %d \n", unsignedToBytes(data[12]), data[13], data[14], data[15]));
        //Log.w("PedometerData", String.format(" %d, %d, %d, %d \n", unsignedToBytes(data[16]), data[17], data[18], data[19]));

        if(searchDay == PedometerData.Yesterday)
        {
            if(!YesterdayPedometerDataSync)
                YesterdayPedometerDataSync = true;
        }

        if( data[3] != (byte)0xFF)
        {
            for(int i = 0; i < 4; i++)
            {
                if(unsignedToBytes(data[3]) == 36 && unsignedToBytes(data[(i + 1) * 4]) == 0)
                    searchDay++;

                time = unsignedToBytes(data[(i + 1) * 4]) + 1;
                tilts = unsignedToBytes(data[(i + 1) * 4 + 1]);
                steps = unsignedToBytes(data[(i + 1) * 4 + 2]) << 8 | unsignedToBytes(data[(i + 1) * 4 + 3]);

                PedometerData temp = getPedometerData(searchDay, time);
                if(temp == null)
                    continue;
                temp.SearchDay = searchDay;
                temp.Steps = steps;
                temp.Tilts = tilts;
                temp.Time = time;
            }
        }
        else
        {
            if(InterfaceOfBLECallback.class.isInstance(mCallBackObj))
            {
                if(YesterdayPedometerDataSync)// && !BeforeYesterdayPedometerDataSync)
                {
                    ((InterfaceOfBLECallback) mCallBackObj).onDataSync(TodayPedometerData, YesterdayPedometerData);
                    YesterdayPedometerDataSync = false;
                }
                else
                {
                    ((InterfaceOfBLECallback) mCallBackObj).onDataSync(TodayPedometerData, null);
                }
            }
        }

    }

    public static int unsignedToBytes(byte b) {
        return b & 0xFF;
    }

    int getTimeNowInMinutes()
    {
        Calendar calendar = Calendar.getInstance();
        int minutes = calendar.get(Calendar.MINUTE);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);

        return  hours * 60 + minutes;
    }

    PedometerData getPedometerData(int searchDay, int timeIndex)
    {
        //if(searchDay == PedometerData.BeforeYesterday)
        //    return BeforeYesterdayPedometerData.get(timeIndex);
        //else
        if(searchDay == PedometerData.Today)
            return TodayPedometerData.get(timeIndex);
        else
            return YesterdayPedometerData.get(timeIndex);
    }

    void setTimeNow()
    {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int date = calendar.get(Calendar.DATE);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int minutes = calendar.get(Calendar.MINUTE);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);

        dayOfWeek -= 1;
        if(dayOfWeek == 0)
            dayOfWeek = 7;

        int time = hours * 60 + minutes;
        DateSetup(year, month, date);
        TimeSetup(time, dayOfWeek);
        //AlarmSetup(2, time + 1, 127);

        DataSync(1, 4);

    }

    public boolean Connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            DebugLog( "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        if(!mBluetoothAdapter.isEnabled())
        {
            DebugLog( "BluetoothAdapter is closed!!.");
         /*   Intent _EnableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(_EnableIntent, 1);*/
            return false;
        }
        if( mConnectionState != STATE_DISCONNECTED  )
        {
            DebugLog("Still connecting!!");
            return false;
        }
        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            DebugLog( "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        DebugLog( "type = " + device.getType());
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            mBluetoothGatt = device.connectGatt(this.mContext, false, mGattCallback, 2);
            DebugLog( "LOLLIPOP up!!Build.VERSION.SDK_INT = " + Build.VERSION.SDK_INT);
        }
        else
        {
            mBluetoothGatt = device.connectGatt(this.mContext, false, mGattCallback);
            DebugLog( "Build.VERSION.SDK_INT = " + Build.VERSION.SDK_INT);
        }
        boolean result = mBluetoothGatt.connect();
        BandAddress = address;
        DebugLog( "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        if(InterfaceOfBLECallback.class.isInstance(mCallBackObj))
            ((InterfaceOfBLECallback)mCallBackObj).BLEConnectionEvents(STATE_CONNECTING, new CavyBandDevice(device, false));
        return true;
    }

    public void Disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            DebugLog( "BluetoothAdapter not initialized");
            return;
        }
        if( mConnectionState == STATE_CONNECTED && sendCharacteristic != null && mWriteToBandCmd.size() >= 1)
        {
            String cmd = "%%OPR=2,1,500\n";
            sendCharacteristic.setValue(cmd.getBytes());
            mBluetoothGatt.writeCharacteristic(sendCharacteristic);
        }
        mConnectionState = STATE_DISCONNECTED;
        mBluetoothGatt.disconnect();
    }

    public void Close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    private void readCharacterisricValue(
            BluetoothGattCharacteristic characteristic) {
        byte[] data = characteristic.getValue();
        byte modeByte = data[1];
        if (modeByte == PACKET_DATA_BNO055) {
            //Log.d(TAG, "On SensorData");
        } else if (modeByte == PACKET_DATA_SYSTEM) {
            onSystemData(data);
        } else if (modeByte == PACKET_DATA_BATTERY) {
            onBatteryData(data);
        } else if (modeByte == PACKET_DATA_BUTTON) {
            onButtonData(data);
        }else if (modeByte == PACKET_DATA_WARNING) {
            onWarningData(data);
        }else if (modeByte == PACKET_DATA_DATA_SYNC) {
            onDataSync(data);
        }else if (modeByte == PACKET_DATA_DEVICE_SIGNATURE) {
            onDeviceSignature(data);
        }
        else if(mIsUpdateing && data.length == 2)
        {
            writeToBle(data);
        }
    }

    public static void DebugLog(String string)
    {
        if(IsDebug)
            Log.d(TAG, string);
    }

    public void StartScanCavyBand()
    {
        if (mBluetoothAdapter.isEnabled() == false) {
            Intent _EnableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //mActivity.startActivityForResult(_EnableIntent, 1);
            return;
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M )
        {
            if(mActivity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
                builder.setTitle("This app needs location access!");
                builder.setMessage("Please grant location access so this app can detect cavy bands.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mActivity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
            else
            {
                this.scanBLE_Device(true);
            }
        }
        else
        {
            this.scanBLE_Device(true);
        }
    }

    public void StopScanCavyBand()
    {
        this.scanBLE_Device(false);
    }

    private void scanBLE_Device(final boolean enable) {
        if (enable) {
            mBLE_ScanHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mScanning = false;
                    DebugLog("Stop Scan");
                }
            }, BLE_SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            DebugLog("Start Scan");
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            DebugLog("Stop Scan");
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, final byte[] scanRecord) {
                    final int RSSI = rssi;
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (device != null && device.getName() != null && device.getType() == BluetoothDevice.DEVICE_TYPE_LE) {
                                final String deviceName = device.getName();
                                DebugLog("deviceName : " + deviceName);
                                if (deviceName.contains("Cavy")) {
                                    DebugLog("Peripheral UUID: " + device.getUuids());
                                    if( InterfaceOfBLECallback.class.isInstance(mCallBackObj) )
                                    {
                                        CavyBandDevice cavyDevice = new CavyBandDevice(device, scanRecord[15] == 1);
                                        ((InterfaceOfBLECallback) mCallBackObj).BLEConnectionEvents(STATE_FIND_NEW_BAND, cavyDevice);


                                        Log.i("scanData: " + deviceName + ": ", (int) scanRecord[15] + "");
                                    }
                                }
                            }
                        }
                    });
                }
            };

    public class CavyBandDevice
    {
        public CavyBandDevice(BluetoothDevice device, boolean isFirstPriority)
        {
            mDevice = device;
            mConnectFirst = isFirstPriority;
        }

        BluetoothDevice mDevice;
        boolean mConnectFirst;

        public BluetoothDevice GetDevice() { return mDevice; }
        public boolean IsConnectionFirstPriority() { return mConnectFirst; }
    }

    public class CavyBandSystemData
    {
        public CavyBandSystemData(byte[] data)
        {
            deviceState = data[2];
            funcEnableStatus = data[3];
            hwVersion = data[4];
            fwVersion = data[5];
            calibratedStatus = data[6];
        }

        public int deviceState, funcEnableStatus, hwVersion, fwVersion, calibratedStatus;
    }

    //=====================
    //  OAD Part
    //=====================
    byte[] require = new byte[16];
    BufferedInputStream mBufferedInputStream;
    int mUpdatingIndex = 0;
    int mFileSize = 0;
    boolean mIsUpdateing = false;
    byte mPriviousData[] = new byte[16];
    byte mUpdatingData[] = new byte[16];
    byte mSendUpdatingData[] = new byte[18];
    public final static int OAD_END = 0;
    public final static int OAD_START = 1;
    public final static int OAD_UPDATING = 2;

    private void OADStatusChangedEvents(int statusCode, float progressRate)
    {
        if(InterfaceOfBLECallback.class.isInstance(mCallBackObj))
        {
            ((InterfaceOfBLECallback)mCallBackObj).onOADStatusChanged(statusCode, progressRate);
        }
    }

    private void searchOADCharacteristics(BluetoothGatt gatt)
    {
        BluetoothGattService OADService = gatt.getService(UUID_OAD_SERVICE);
        OAD_BLock_Characteristic = OADService.getCharacteristic(UUID_OAD_BLOCK);
        OAD_Identify_Characteristic = OADService.getCharacteristic(UUID_OAD_IDENTIFY);
        if(OAD_BLock_Characteristic != null)
        {
            DebugLog( "Found OAD_BLock_Characteristic!!");
            mBluetoothGatt.setCharacteristicNotification(OAD_BLock_Characteristic, true);
            BluetoothGattDescriptor config = OAD_BLock_Characteristic.getDescriptor(UUID_CONFIG);
            config.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(config);
        }
        if(OAD_Identify_Characteristic != null)
        {
            DebugLog( "Found OAD_Identify_Characteristic!!");
            mBluetoothGatt.setCharacteristicNotification(OAD_Identify_Characteristic, true);
            BluetoothGattDescriptor config = OAD_Identify_Characteristic.getDescriptor(UUID_CONFIG);
            config.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(config);
        }
    }

    private void startOAD()
    {
        DebugLog("StartOAD");
        mUpdatingIndex = 0;
        mIsUpdateing = true;
        OADStatusChangedEvents(OAD_START, 0.0f);
    }

    private void finsihOAD()
    {
        DebugLog("FinishOAD");
        mUpdatingIndex = 0;
        mFileSize = 0;
        mIsUpdateing = false;
        OADStatusChangedEvents(OAD_END, 100.0f);
    }

    private byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[mFileSize];
        int len = -1;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }

    private int crc16(int crc, byte val)
    {
        final int poly = 0x1021;
        int cnt;
        boolean msb;
        for (cnt = 0; cnt < 8; cnt++, val <<= 1)
        {
            if ((crc & 0x8000)>0){
                msb=true;
            }else {
                msb=false;
            }
            crc <<= 1;
            if ((val & 0x80)>0)
            {
                crc |= 0x0001;
            }
            if (msb)
            {
                crc ^= poly;
            }
        }
        crc &= 0xffff;
        return crc;
    }

    void dataSeries(byte[] byte1, byte[] byte2)
    {
        for(int i = 0; i < 2; i++) {
            mSendUpdatingData[i] = byte1[i];
        }
        for (int i=2 ; i<18 ; i++){
            mSendUpdatingData[i] = byte2[i-2];
        }
    }

    int ord(byte d) {
        return d & 0xff;
    }

    float twoBytes(byte d1, byte d2) {
        // """ unmarshal two bytes into int16 """
        float d = ord(d1) * 256 + ord(d2);
        if (d > 32767)
            d -= 65536;
        return d;
    }

    public void UpdateFirmwareByFileName(String fileName)
    {
        try
        {
            String filesName[] = mActivity.getAssets().list("");
            for(int i = 0; i < filesName.length; i++)
            {
                if(filesName[i].toLowerCase().equals(fileName.toLowerCase()))
                {
                    loadFirmwareFile(filesName[i], 0);
                    break;
                }
            }
        }
        catch (IOException e)
        {

        }
    }

    public void UpdateFirmwareByFilePath(String filePath)
    {
        loadFirmwareFile(filePath, 1);
    }

    private void loadFirmwareFile(String fileName, int mode)
    {
        try {
            InputStream firstInputStream;
            switch (mode)
            {
                case 0:
                    firstInputStream = mActivity.getAssets().open(fileName);
                    break;
                default:
                case 1:

                    Log.e("TAG","TW---固件地址---"+fileName);
                    File file = new File(fileName);
                    mFileSize = (int) file.length();
                    firstInputStream=new FileInputStream(file);
                    break;
            }

            mFileSize = firstInputStream.available();
            byte[] filebt = readStream(firstInputStream);
            Log.d("gatt_show_filebt", filebt.length+"");
            int imageCRC = 0;
            int idx = 0;
            for (idx = 4; idx <= mFileSize-1; idx++)
            {
                imageCRC = crc16(imageCRC, filebt[idx]);
            }
            imageCRC = crc16(imageCRC, (byte) 0);
            imageCRC = crc16(imageCRC, (byte) 0);
            Log.i("gatt_show_crc1616", imageCRC+"");

            require[0] = (byte)((imageCRC & 0x00ff));
            require[1] = (byte)((imageCRC & 0xff00) >>> 8);
            require[2] = (byte) 0xff;
            require[3] = (byte) 0xff;
            require[4] = (byte) 0x00;
            require[5] = (byte) 0x00;
            require[6] = (byte) (((mFileSize/4) & 0x00ff));
            require[7] = (byte) (((mFileSize/4) & 0xff00) >>> 8);
            require[8] = (byte) 0x45;
            require[9] = (byte) 0x45;
            require[10] = (byte) 0x45;
            require[11] = (byte) 0x45;
            require[12] = (byte) 0x00;
            require[13] = (byte) 0x00;
            require[14] = (byte) 0x01;
            require[15] = (byte) 0xff;

            imgInputStream=new FileInputStream(fileName);
            //mBufferedInputStream=new BufferedInputStream(mActivity.getAssets().open(fileName));

            String cmd = "%OAD=1\n";
            sendCharacteristic.setValue(cmd.getBytes());
            mBluetoothGatt.writeCharacteristic(sendCharacteristic);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            startOAD();
            writeFirmwareDataToBand(require, 0);
            DebugLog("File Loaded, file size: " + mFileSize);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int len;
    void writeToBle(byte[] writeHeader)
    {
        try {
            len=imgInputStream.read(mUpdatingData);
            if(len!=-1)
            {
                if(len!=16) {
                    Log.i("gatt_show_writeToBle", "not 16 bytes. Some Error");
                }
                else
                {
                    int replyHeader= (int) twoBytes(writeHeader[1], writeHeader[0]);
                    Log.i("gatt_show_writeToBle", "index="+mUpdatingIndex);
                    if (mUpdatingIndex==replyHeader) {
                        dataSeries(writeHeader, mUpdatingData);
                        writeFirmwareDataToBand(mSendUpdatingData, 1);
                        mUpdatingIndex++;
                    }else if (mUpdatingIndex-1==replyHeader){
                        dataSeries(writeHeader, mPriviousData);
                        writeFirmwareDataToBand(mSendUpdatingData, 1);
                    }

                    if (mUpdatingIndex>8191){
                        finsihOAD();
                        mBufferedInputStream.close();
                        Log.i("gatt_show_writeToBle", "完成");
                    }
                    else
                    {
                        OADStatusChangedEvents(OAD_UPDATING, (float)mUpdatingIndex / 8191.0f * 100.0f);
                    }
                    for(int i = 0; i < 16; i++)
                    {
                        mPriviousData[i] = mUpdatingData[i];
                    }
                }
            }
            else {
                Log.i("gatt_show_writeToBle", "完成");
                finsihOAD();
                mBufferedInputStream.close();
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void writeFirmwareDataToBand(byte[] data, int mode)
    {
        if(mode == 0)
        {
            OAD_Identify_Characteristic.setValue(data);
            mBluetoothGatt.writeCharacteristic(OAD_Identify_Characteristic);
        }
        else
        {
            OAD_BLock_Characteristic.setValue(data);
            mBluetoothGatt.writeCharacteristic(OAD_BLock_Characteristic);
        }
    }

    public void CallBLEConnectionEvents(int eventCode, LifeBandBLE.CavyBandDevice device)
    {
        if( InterfaceOfBLECallback.class.isInstance(mCallBackObj) )
        {
            ((InterfaceOfBLECallback) mCallBackObj).BLEConnectionEvents(eventCode, device);
        }
    }
}
