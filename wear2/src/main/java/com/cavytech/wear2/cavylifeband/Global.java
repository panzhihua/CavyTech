package com.cavytech.wear2.cavylifeband;

import android.content.pm.ActivityInfo;

public class Global
{
    public static final float DIVDE_180_PI = 57.296f;
    public static final float DIVDE_180_PI_N=-57.296f;
    public static final float DIVDE_16=0.0625f;
    public static final float DIVDE_1k=0.001f;
    //
    public static final int MAGNETIC_MODE = 0;
    public static final int GYRO_MODE = 1;
    public static final int NO_MOVE_MODE = 2;
    public static final float LOW_PASS_FACTOR = 0.2f;
    //1-LOW_PASS_FACTOR;
    public static final float LOW_PASS_FACTOR_N = 0.8f;
    //
    public static int MagneticMode = 0;
    public static boolean BT_THREAD = false;
    public static String BandAddress="";
    public static int SocketStatus=0;
    public static int MaxDataPerSecond=20;
    public static int BandDataPerSecond=32;
    public static float BandDataRateHZ=0.03125f;
    public static float[] BandDataList=new float[14];
    public static float[] BandDataListPrev=new float[14];
    public static Boolean Calibrated=false;
    //150325
    public static float magneticNorth =0;


    public static float Error_Tolerence = 0.07f;

    public static float DeviceGameRotZ= 0f;
    public static int GameOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    public static float OrientationAngel=0;

    public static byte TargetPowerMode = 0;
    public static byte TargetOpMode = 0;
    public static byte CurPowerMode = 0;
    public static byte CurOpMode = 0;

    //==================================================
    // SelectOperation constants
    public static final int LOW_POWER_MODE = 1;
    public static final int LOW_POWER_MODE_SLEEP = 0;// default
    public static final int LOW_POWER_MODE_IDLE = 1;
    public static final int LOW_POWER_MODE_STANDBY = 2;

    public static final int SNIFF_MODE = 2;
    public static final int SNIFF_MODE_NO_CHANGE = 0;// default
    public static final int SNIFF_MODE_ACC_ON = 1;
    public static final int SNIFF_MODE_MAG_ON = 2;
    public static final int SNIFF_MODE_ACC_MAG_ON = 3;

    public static final int NORMAL_MODE = 3;
    public static final int NORMAL_MODE_6x_COMPASS = 1;
    public static final int NORMAL_MODE_6x_M4G = 2;
    public static final int NORMAL_MODE_9x_NDOF_FMC_OFF = 3;
    public static final int NORMAL_MODE_9x_NDOF = 4;// default

    public static final int NORMAL_SAVE_POWER_MODE = 4;
    public static final int NORMAL_SAVE_POWER_MODE_6x_COMPASS = 1;
    public static final int NORMAL_SAVE_POWER_MODE_6x_M4G = 2;
    public static final int NORMAL_SAVE_POWER_MODE_9x_NDOF_FMC_OFF = 3;
    public static final int NORMAL_SAVE_POWER_MODE_9x_NDOF = 4;// default

    //==================================================
    // ControlLED constants
    //==================================================
    public static final int RED_LED_ID = 0;
    public static final int GREEN_LED_ID = 1;
    public static final int BLUE_LED_ID = 2;
    public static final int LED_OFF = 0;
    public static final int LED_ON = 1;
    public static final int LED_FLASH = 2;
    public static final int MIN_LED_POWER = 1;
    public static final int MAX_LED_POWER = 100;
    //==================================================
    // DoVibrate constants
    //==================================================
    public static final int VIBRATE_OFF = 0;
    public static final int VIBRATE_ONCE = 1;
    public static final int VIBRATE_TWICE = 98;
    public static final int MIN_VIBRATE_POWER = 1;
    public static final int MAX_VIBRATE_POWER = 100;
    //==================================================
    // API return values
    //==================================================
    public static final int SUCCESS = 0;
    public static final int ERR_INVALID_PARAMETERS = -1;
    public static final int ERR_SEND_COMMAND_EXCEPTION = -2;
    public static final int ERR_STILL_CONNECTING = -3;
    public static final int ERR_NOT_CONNECTED = -4;
    public static final int ERR_BLUETOOTH_DEVICE_ERROR = -5;
    //==================================================

}
