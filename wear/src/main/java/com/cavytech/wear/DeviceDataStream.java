package com.cavytech.wear;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.cavytech.wear.util.LogUtil;

import net.sourceforge.opencamera.MainActivity;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public abstract class DeviceDataStream extends Thread {

    public static final String STREAM_BROADCAST = "STREAM_BROADCAST";
    public static final String STREAM_PARAM = "STREAM_PARAM";
    public static final int PARAM_START_CAL = 0;
    public static final int PARAM_PROC_CAL = 1;
    public static final int PARAM_END_CAL = 2;


    private boolean mTerminate = false;
    private boolean mIOError = false;
    private InputStream mInStream;
    private OutputStream mOutStream;

    private final static byte PACKET_DATA_BNO055 = (byte) 0xA1;
    private final static byte PACKET_DATA_BAT = (byte) 0xB1;
    private final static byte PACKET_DATA_BUTTON = (byte) 0xD1;  // 按钮按下消息
    private final static byte PACKET_DATA_SYSTEM = (byte) 0xC1;
    private final static byte PACKET_DATA_WARNING = (byte) 0xE1;

    public boolean weight = false;

    public static boolean mSystemMode = true;

    protected int mMagUpdateTimes = 0;
    protected int mMagStatus = -1;
    protected int mMag_Off_X = -1;
    protected int mMag_Off_Y = -1;
    protected int mMag_Off_Z = -1;
    protected int mMag_Radius = -1;
    protected int mMag_CalRef = -1;

    byte mSystemState[];

    private Handler mHandler;
    protected Context mContext;

    public DeviceDataStream() {

    }

    public DeviceDataStream(InputStream inputStream, OutputStream outputStream) {
        mInStream = inputStream;
        mOutStream = outputStream;
    }

    public DeviceDataStream(InputStream inputStream, OutputStream outputStream, Handler handler) {
        mInStream = inputStream;
        mOutStream = outputStream;
        mHandler = handler;
    }

    public void terminate() {
        mTerminate = true;
        this.interrupt();
    }

    public void WriteStream(String command) {
        try {
            byte[] cmd = command.getBytes();
            mOutStream.write(cmd, 0, cmd.length);
        } catch (IOException e) {
            e.printStackTrace();
            mIOError = true;
        }
    }

    private int ReadStream(byte buf[], int shift, int size) {
        int readSize = -1;
        try {
            readSize = mInStream.read(buf, shift, size);
        } catch (IOException e) {
            e.printStackTrace();
            mIOError = true;
        }
        return readSize;
    }

    public static int execRootCmd(String cmd) {
        int result = -1;
        DataOutputStream dos = null;
        try {
            Process p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());
            LogUtil.getLogger().d(cmd);
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            p.waitFor();
            result = p.exitValue();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.getLogger().d("get root fail");
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public boolean IsCalibrateCompleted() {
        return (mMagStatus == 3) && (mMag_Off_X != 0) && (mMag_Off_Y != 0) && (mMag_Off_Z != 0) && (mMag_Radius != 0);
    }

    public void run() {
        while (!mTerminate) {
            byte buffer[] = new byte[23];

            while (buffer[0] != '$') {
                if (mTerminate || mIOError)
                    break;
                ReadStream(buffer, 0, 1);
            }

            int shift = 1;
            while (shift < 23) {
                if (mTerminate || mIOError)
                    break;
                if (ReadStream(buffer, shift, 1) > 0)
                    shift++;
            }

            if (mTerminate || mIOError)
                break;

            if (mSystemMode) {
                // GioChen Todo : Disable all action of button

                if (weight) {
                    // First device
                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                    if (buffer[1] == PACKET_DATA_BUTTON && sharedPrefs.getBoolean("SettingButton", true)) {
                        execRootCmd("input keyevent 24");
                        Intent intent=new Intent(MainActivity.TAKE_PHOTO_ACTION);
                        mContext.sendBroadcast(intent);
                    } else {
                        LogUtil.getLogger().d("Button function disabled.");
                    }
                }

                if (buffer[1] == PACKET_DATA_SYSTEM) {
                    mMagUpdateTimes++;
                    mMagStatus = (int) buffer[6];

                    mMag_Off_X = (int) (buffer[8] << 8 | buffer[9]);
                    mMag_Off_Y = (int) (buffer[10] << 8 | buffer[11]);
                    mMag_Off_Z = (int) (buffer[12] << 8 | buffer[13]);
                    mMag_Radius = (int) (buffer[14] << 8 | buffer[15]);
                    //Log.i(TAG, "PACKET_DATA_SYSTEM Mag[" + mMagStatus + "] X[" + mMag_Off_X + "] Y[" + mMag_Off_Y + "] Z[" + mMag_Off_Z + "] Radius[" + mMag_Radius + "]");
                    mMag_CalRef = (int) buffer[18];

                    mSystemState = Arrays.copyOf(buffer, buffer.length);
                } else if (buffer[1] == PACKET_DATA_WARNING) {
                    if (buffer[2] == 1)
                        BatteryTooLow();
                }
            } else {
                // Todo : send to game app
                if (buffer[1] == PACKET_DATA_BUTTON) {
                    Intent intent=new Intent(MainActivity.TAKE_PHOTO_ACTION);
                    mContext.sendBroadcast(intent);
                    LogUtil.getLogger().d(" PACKET_DATA_BUTTON");
                } else if (buffer[1] == PACKET_DATA_SYSTEM) {
                    LogUtil.getLogger().d(" PACKET_DATA_SYSTEM");
                } else if (buffer[1] == PACKET_DATA_BAT) {
                    LogUtil.getLogger().d(" PACKET_DATA_BAT");
                } else if (buffer[1] == PACKET_DATA_BNO055) {
                    LogUtil.getLogger().d(" PACKET_DATA_BNO055");
                } else if (buffer[1] == PACKET_DATA_WARNING) {
                    if (buffer[2] == 1)
                        BatteryTooLow();
                }
                UpStream(buffer);
            }

        }

        try {
            mInStream.close();
            mOutStream.close();
            mHandler.removeCallbacks(mStateMachine);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LogUtil.getLogger().d("Thread terminate");
    }

    public boolean mStateStable() {
        return mState == STATE_CONNECTABLE;
    }

    protected int mState = STATE_INIT_0;

    public void stateMachineRun() {
        mMagUpdateTimes = 0;
        mWaitCalitrationTimes = 0;
        mState = STATE_INIT_0;
        mHandler.postDelayed(mStateMachine, 1);
    }

    public void forceCalibrate() {

        mHandler.removeCallbacks(mStateMachine);

        mMagStatus = -1;
        mMag_Off_X = -1;
        mMag_Off_Y = -1;
        mMag_Off_Z = -1;
        mMag_Radius = -1;

        Intent intent = new Intent(STREAM_BROADCAST);
        intent.putExtra(STREAM_PARAM, PARAM_START_CAL);
        mContext.sendBroadcast(intent);

        mWaitCalitrationTimes = 0;
        mState = STATE_CALIBRATION_1;
        mHandler.postDelayed(mStateMachine, 1);
    }

    protected final static int STATE_INIT_0 = 1;
    protected final static int STATE_INIT_1 = 2;
    protected final static int STATE_INIT_2 = 3;
    protected final static int STATE_INIT_3 = 4;
    protected final static int STATE_INIT_4 = 5;
    protected final static int STATE_WAIT_SYSTEM = 6;
    protected final static int STATE_CONNECTABLE = 7;
    protected final static int STATE_CALIBRATION_1 = 8;
    protected final static int STATE_CALIBRATION_2 = 9;
    protected final static int STATE_CALIBRATION_3 = 10;
    protected final static int STATE_CALIBRATION_4 = 11;
    protected final static int STATE_CALIBRATION_5 = 12;
    protected final static int STATE_CALIBRATION_6 = 13;

    private int mWaitSystemTimes = 0;
    private int mWaitCalitrationTimes = 0;

    private void changeState(int state, int delay) {
        mState = state;
        mHandler.postDelayed(mStateMachine, delay);
    }

    private void sendToState(String cmd, int state, int delay) {
        WriteStream(cmd);
        changeState(state, delay);
    }

    private Runnable mStateMachine = new Runnable() {
        Intent intent;

        @Override
        public void run() {
            switch (mState) {
                case STATE_INIT_0:
                    sendToState(BraceletService.BT_GAME_MODE, STATE_INIT_1, 2000);
                    break;
                case STATE_INIT_1:
                    sendToState("?BAT\n", STATE_INIT_2, 1000);
                    break;
                case STATE_INIT_2:
                    sendToState("%LED=2,2,30,100,2000\n", STATE_INIT_3, 1000);
                    break;
                case STATE_INIT_3:
                    sendToState("%VIBRATE=1,50,500\n", STATE_INIT_4, 1000);
                    break;
                case STATE_INIT_4:
                    sendToState("?SYSTEM\n", STATE_WAIT_SYSTEM, 1000);
                    break;

                case STATE_WAIT_SYSTEM:
                    if (mWaitSystemTimes >= 3) {
                        // GioChen Todo : force dis-connect.
                        LogUtil.getLogger().d("mWaitSystemTimes > 3");
                        sendToState("%OPR=2,1,100\n", STATE_CONNECTABLE, 1000);
                        //changeState(STATE_CONNECTABLE, 1000);
                    } else if (mMagUpdateTimes == 0) {
                        mWaitSystemTimes++;
                        sendToState("?SYSTEM\n", STATE_WAIT_SYSTEM, 1000);
                    } else {
                        //if ( IsCalibrateCompleted() ) {
                        if (mMag_CalRef == 0x03) {
                            //changeState(STATE_CONNECTABLE, 1000);
                            sendToState("%OPR=2,1,100\n", STATE_CONNECTABLE, 1000);
                        } else {
                            intent = new Intent(STREAM_BROADCAST);
                            intent.putExtra(STREAM_PARAM, PARAM_START_CAL);
                            mContext.sendBroadcast(intent);

                            changeState(STATE_CALIBRATION_2, 1);
                        }
                    }
                    break;
                case STATE_CONNECTABLE:
                    changeState(STATE_CONNECTABLE, 1000);
                    break;

                case STATE_CALIBRATION_1:
                    sendToState("%OPR=3,4,10\n", STATE_CALIBRATION_2, 2000);
                    break;
                case STATE_CALIBRATION_2:
                    sendToState("%OPR=2,0,100\n", STATE_CALIBRATION_3, 2000);
                    break;
                case STATE_CALIBRATION_3:
                    if (IsCalibrateCompleted()) {
                        intent = new Intent(STREAM_BROADCAST);
                        intent.putExtra(STREAM_PARAM, PARAM_PROC_CAL);
                        mContext.sendBroadcast(intent);

                        sendToState("%STORE=1\n", STATE_CALIBRATION_4, 2000);
                    } else {
                        mWaitCalitrationTimes++;
                        if (mWaitCalitrationTimes >= 50) {
                            sendToState(BraceletService.BT_GAME_MODE, STATE_CALIBRATION_5, 2000);
                            break;
                        }
                        sendToState("?MAGCAL\n", STATE_CALIBRATION_3, 1000);

                    }
                    break;
                case STATE_CALIBRATION_4:
                    sendToState(BraceletService.BT_GAME_MODE, STATE_CALIBRATION_5, 2000);
                    break;
                case STATE_CALIBRATION_5:
                    sendToState("%OPR=2,1,100\n", STATE_CALIBRATION_6, 2000);
                    break;
                case STATE_CALIBRATION_6:
                    intent = new Intent(STREAM_BROADCAST);
                    intent.putExtra(STREAM_PARAM, PARAM_END_CAL);
                    mContext.sendBroadcast(intent);

                    changeState(STATE_CONNECTABLE, 1000);
                    break;
            }

        }
    };

    public abstract void UpStream(byte[] raw);

    public abstract void BatteryTooLow();

}

