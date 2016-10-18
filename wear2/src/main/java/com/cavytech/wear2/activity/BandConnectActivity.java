package com.cavytech.wear2.activity;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.cavytech.wear2.R;
import com.cavytech.wear2.cavylifeband.InterfaceOfBLECallback;
import com.cavytech.wear2.cavylifeband.LifeBandBLE;
import com.cavytech.wear2.cavylifeband.PedometerData;
import com.cavytech.wear2.entity.BandInfoEntity;
import com.cavytech.wear2.util.CacheUtils;
import com.cavytech.wear2.util.Constants;
import com.cavytech.wear2.util.LifeBandBLEUtil;
import com.cavytech.wear2.util.SerializeUtils;
import com.cavytech.widget.gif.GifView;

import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by longjining on 16/4/8.
 */
public class BandConnectActivity extends BandSettingActivity implements InterfaceOfBLECallback {

    @ViewInject(R.id.forget_password)
    private TextView forget_password;

    @ViewInject(R.id.connetting_gif_view)
    private GifView connetting_gif;

    private HashMap<String, Integer> ScanDevicePreferTable = new HashMap();
    boolean isDestroy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        forget_password.setVisibility(View.GONE);

        HomePager.isConnectoinNewBand = true;

        setGifView();
        intView();

        initLifeBand();
    }

    @Override
    public void onResume() {
        super.onResume();

       // LifeBandBLEUtil.getInstance().onResume();
    }

    void initLifeBand() {

        LifeBandBLEUtil.getInstance().setCallBack(this);

        LifeBandBLEUtil.getInstance().StartScanCavyBand();

        reStartScan();
    }


    public void SetSystemTime() {
        LifeBandBLEUtil.getInstance().SetSystemTime();
    }

    void connectLifeBand(String macAddr) {
        CacheUtils.putString(BandConnectActivity.this, Constants.MCONNECTEDMACADDRESS, macAddr);

        LifeBandBLEUtil.getInstance().connect(macAddr);

    }

    void setGifView() {

        final ViewTreeObserver vto = bandOpenImg.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                bandOpenImg.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int height = bandOpenImg.getMeasuredHeight();
                int width = bandOpenImg.getMeasuredWidth();
                connetting_gif.setShowDimension(height, height, (width - height) / 2, 0);
                connetting_gif.setGifImage(R.drawable.banner_connecting);
                connetting_gif.setGifImageType(GifView.GifImageType.SYNC_DECODER);


            }
        });
    }

    void intView() {

        super.intView();
        if (stepActivity == StepType.CONNECTTING) {
            connetting_gif.setVisibility(View.VISIBLE);
            back.setVisibility(View.GONE);
        } else {
            connetting_gif.setVisibility(View.GONE);
            back.setVisibility(View.VISIBLE);
        }

        if(stepActivity == StepType.SUCCESS){
//            connetting_gif.clearAnimation();
            back.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        isDestroy = true;

        HomePager.isConnectoinNewBand = false;

        connetting_gif.clearAnimation();
    }

    // InterfaceOfBLECallback///////////////////////
    @Override
    public void onButtonClicked() {
        Log.e("TAg", "bandConnection__________________onButtonClicked");
    }

    @Override
    public void onButtonLongPressed(byte[] data) {

    }

    @Override
    public void onBatteryData(int status) {
        CacheUtils.putInt(BandConnectActivity.this,Constants.STATUS,status);

        Intent intent = new Intent(Constants.COM_CAVYTECH_WEAR2_SERVICE_STATUSRECEIVER);
        intent.putExtra("status", status);
        Log.e("TAg", "bandConnection__________________onBatteryData--------------------" + status);
        LocalBroadcastManager.getInstance(BandConnectActivity.this).sendBroadcast(intent);
    }

    @Override
    public void onWarningData() {
        Log.e("TAg", "bandConnection__________________onWarningData--------------------");

    }

    @Override
    public void onSystemData(LifeBandBLE.CavyBandSystemData data) {
        int fwVersion = data.fwVersion;
        int hwVersion = data.hwVersion;
        Log.e("hwVersion",hwVersion+"");
        CacheUtils.putInt(BandConnectActivity.this,Constants.FWVISION,fwVersion);

        Log.e("TAg", "bandConnection__________________fwVersion--------------------" + fwVersion);
        Intent intent = new Intent(Constants.DATA_FWVERSION);
        intent.putExtra("fwVersion", fwVersion);
        intent.putExtra("hwVersion", hwVersion);
        LocalBroadcastManager.getInstance(BandConnectActivity.this).sendBroadcast(intent);

        int funcEnableStatus = data.funcEnableStatus;
        Log.e("TAG",funcEnableStatus+"-=-=-=-=-=-=-");

        if(funcEnableStatus !=126){
            for(int i= 2 ; i<= 6 ;i++){
                LifeBandBLEUtil.getInstance().ConfigSetup(i,1);
                Log.e("TAG",  "设置Config-=-=-=-=-=-=-");
            }
        }
    }

    @Override
    public void onDataSync(HashMap<Integer, PedometerData> TodayData, HashMap<Integer, PedometerData> YesterdayData) {

    }

    @Override
    public void onDeviceSignature(byte[] data) {
        Log.e("TAg", "bandConnection__________________onDeviceSignature--------------------" + data);

    }

    private void sendUpdate() {
        Intent intent = new Intent(Constants.UPDATE_BANDINFO_RECEIVER);
        sendBroadcast(intent);
    }

    @Override
    public void BLEConnectionEvents(int eventCode, LifeBandBLE.CavyBandDevice device) {

        Intent intent = new Intent(Constants.ISCONNECTIONBAND);

        switch (eventCode) {
            case LifeBandBLE.STATE_CONNECTED:
                CacheUtils.saveMacAdress(BandConnectActivity.this,device.GetDevice().getAddress());

                LifeBandBLEUtil.getInstance().getBaseSysInfo();

                LifeBandBLEUtil.getInstance().StartScanCavyBand();
                LifeBandBLEUtil.getInstance().SelectOperation();

                connectedFinish();

                BandInfoEntity bandInfo = new BandInfoEntity();

                bandInfo.setAddress(device.GetDevice().getAddress());
                bandInfo.setName(device.GetDevice().getName());
                SerializeUtils.serialize(bandInfo, Constants.SERIALIZE_BAND_INFO);
                sendUpdate();
                SetSystemTime();

                CacheUtils.putInt(BandConnectActivity.this, Constants.ISCONNECTIONBAND, eventCode);
                intent.putExtra("eventCode", eventCode);
                LocalBroadcastManager.getInstance(BandConnectActivity.this).sendBroadcast(intent);

//                Log.e("TAG","BandConnectActivity---手环连线---");

                 break;
            case LifeBandBLE.STATE_CONNECTING:

               /* CacheUtils.putInt(BandConnectActivity.this, Constants.ISCONNECTIONBAND, eventCode);
                intent.putExtra("eventCode", eventCode);
                LocalBroadcastManager.getInstance(BandConnectActivity.this).sendBroadcast(intent);*/

                break;
            case LifeBandBLE.STATE_DISCONNECTED:
//                if (mLifeBand.mConnectionState == LifeBandBLE.STATE_CONNECTED || mConnectedMacAddress.isEmpty()) {
//                    break;
//                }
//                mLifeBand.Disconnect();
//                mConnectedMacAddress = "";

                reStartScan();
                break;
            default:
            case LifeBandBLE.STATE_FIND_NEW_BAND:

//                Log.e("TAG","BandConnectActivity---手环断线--STATE_FIND_NEW_BAND-");

                if (LifeBandBLEUtil.getInstance().getConnectionState() == LifeBandBLE.STATE_CONNECTING ||
                        LifeBandBLEUtil.getInstance().getConnectionState() == LifeBandBLE.STATE_CONNECTED) {
                    break;
                }

                BluetoothDevice btDev = device.GetDevice();
                String devName = btDev.getName();
                boolean isConnectionFirstPriority = device.IsConnectionFirstPriority();
                if (isConnectionFirstPriority) {
                    ScanDevicePreferTable.put(devName, device.IsConnectionFirstPriority() ? 1 : 0);
                    connectLifeBand(btDev.getAddress());
                    stepActivity = StepType.CONNECTTING;
                    intView();
                }

                reStartScan();
                break;
        }


    }

    @Override
    public void onOADStatusChanged(int statusCode, float progressRate) {

    }

    public static void timer1(int time) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                System.out.println("-------设定要指定任务--------");
            }
        }, time);// 设定指定的时间time,此处为2000毫秒
    }

    private void connectedFinish() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                stepActivity = StepType.SUCCESS;
                intView();

            }
        }, 500);

        /*stepActivity = StepType.SUCCESS;
        intView();*/
    }

    private void reStartScan() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                LifeBandBLEUtil.getInstance().StopScanCavyBand();
                if (isDestroy || LifeBandBLEUtil.getInstance().getConnectionState() == LifeBandBLE.STATE_CONNECTING ||
                        LifeBandBLEUtil.getInstance().getConnectionState() == LifeBandBLE.STATE_CONNECTED
                        ) {
                    return;
                }
                LifeBandBLEUtil.getInstance().StartScanCavyBand();
                SetSystemTime();
                reStartScan();
            }
        }, 10500);
    }

    @Override
    public void onClickBack() {
        if(stepActivity == StepType.SUCCESS) {

            LifeBandBLEUtil.getInstance().Disconnect();
            stepActivity = StepType.OPEN;
            intView();
//            LifeBandBLEUtil.getInstance().StartScanCavyBand();
//            reStartScan();

            return;
        }else {
            super.onClickBack();
        }
    }

}
