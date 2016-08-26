package com.cavytech.wear;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.cavytech.wear.activity.MatchActivity;
import com.cavytech.wear.application.CommonApplication;
import com.cavytech.wear.util.Constants;
import com.cavytech.wear.util.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Semaphore;

public class BraceletService extends Service {
    private static final String TAG = "BraceletService";
    private static final String BT_MAIN_MODE = "%OPR=2,1,100\n";
    public static final String BT_GAME_MODE = "%OPR=3,4,50\n";

    // 停止重新连接
    public static final String ACTION_STOP_RECONNECTE = "ACTION_STOP_RECONNECTE";

    public static boolean isShowDisconnetAlert = false;

    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private static enum ListItem {SOCKET, ADDRESS, DATA_STREAM, WEIGHT}

    private ArrayList<HashMap<ListItem, Object>> mConnectedDeviceList;
    private Intent mBackToMain = null;
    private Context mServiceContext;

    Messenger mClient, mController;
    private IncomingHandler mHandler = new IncomingHandler();
    private final Messenger mIncomingMessenger = new Messenger(mHandler);
    private Notification notification = null;

    class IncomingHandler extends Handler {
        public void handleMessage(Message msg) {
            LogUtil.getLogger().d(msg.getData().toString());
            Bracelet.dump(TAG + "(RECV)", msg);
            switch (msg.what) {
                case Bracelet.CMD_ACK:
                    onBraceletAck(msg);
                    break;

                case Bracelet.CMD_REG_CONTROLLER:
                    onBraceletRegController(msg);
                    break;

                case Bracelet.CMD_REG_CLIENT:
                    onBraceletRegClient(msg);
                    break;

                case Bracelet.CMD_CONNECTION:
                    onBraceletConnection(msg);
                    break;

                case Bracelet.CMD_DATA:
                    onBraceletData(msg);
                    break;

                case Bracelet.CMD_DEVICES:
                    onBraceletGetDevice(msg);
                    break;

                case 1001: // get state
                    onBraceletState(msg);
                    break;

                case 1002: // force calibrate
                    onBraceletCalibrate(msg);
                    break;

                case 1003: // link lost trigger
                    onBraceletLinkLostTrigger(msg);
                    break;

                case 1004: // HID enable trigger
                    onBraceletHidEnableTrigger(msg);
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void onCreate() {

        mServiceContext = getBaseContext();

        mConnectedDeviceList = new ArrayList<HashMap<ListItem, Object>>();
        registerReceiver(mConnectStateReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
        registerReceiver(mConnectStateReceiver, new IntentFilter(BraceletService.ACTION_STOP_RECONNECTE));

        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(new exPhoneCallListener(), PhoneStateListener.LISTEN_CALL_STATE);

        String serviceStart = getString(R.string.app_name) + " " + getString(R.string.service) + " " + getString(R.string.started);
        notification = new Notification(R.drawable.ic_launcher, serviceStart, System.currentTimeMillis());
        mBackToMain = new Intent(mServiceContext, MatchActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mServiceContext, 0, mBackToMain, 0);
        serviceStart = getString(R.string.app_name) + " " + getString(R.string.service);
        notification.setLatestEventInfo(this, serviceStart, "", pendingIntent);
//        startForeground(1, notification);

        mWatchDogCountDownValue = 10;
        mHandler.postDelayed(mWatchDogForClient, 500);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        mHandler.removeCallbacks(mWatchDogForClient);
        mHandler.removeCallbacks(mReconnectTrigger);

        for (HashMap<ListItem, Object> h : mConnectedDeviceList) {
            try {
                ((BraceletStream) h.get(ListItem.DATA_STREAM)).terminate();
                ((BluetoothSocket) h.get(ListItem.SOCKET)).close();
            } catch (IOException e) {
                LogUtil.getLogger().d("exception thrown on close");
            }
        }
        mConnectedDeviceList.clear();

        unregisterReceiver(mConnectStateReceiver);

        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mIncomingMessenger.getBinder();
    }

    public int sendCommand(Messenger sendTo, int command, int arg) {
        return Bracelet.sendCommand(TAG + "(SEND)", sendTo, mIncomingMessenger, command, arg);
    }

    public int sendCommand(Messenger sendTo, int command, int arg, String data) {
        return Bracelet.sendCommand(TAG + "(SEND)", sendTo, mIncomingMessenger, command, arg, data);
    }

    public int sendCommand(Messenger sendTo, int command, int arg, ArrayList<String> data) {
        return Bracelet.sendCommand(TAG + "(SEND)", sendTo, mIncomingMessenger, command, arg, data);
    }

    public int sendCommand(Messenger sendTo, int command, int arg, byte[] data) {
        return Bracelet.sendCommand(TAG + "(SEND)", sendTo, mIncomingMessenger, command, arg, data);
    }

    public int sendACK(Message message, boolean result) {
        return Bracelet.sendACK(TAG + "(SEND)", message, result);
    }

    public int sendACK(Message message, boolean result, String data) {
        return Bracelet.sendACK(TAG + "(SEND)", message, result, data);
    }

    public int sendACK(Message message, boolean result, byte[] data) {
        return Bracelet.sendACK(TAG + "(SEND)", message, result, data);
    }

    public int sendACK(Message message, boolean result, ArrayList<String> data) {
        return Bracelet.sendACK(TAG + "(SEND)", message, result, data);
    }

    private int onBraceletAck(Message command) {
        mWatchDogCountDownValue = 10;
        return 0;
    }

    private int onBraceletRegClient(Message command) {
        mClient = (command.arg1 == Bracelet.ARG_REGISTER) ? command.replyTo : null;

        if (mClient != null)
            mWatchDogCountDownValue = 10;

        ChangeBraceletOperationModeByConnection();
        mDeviceListNotice(0);


        return sendACK(command, true);
    }

    private int onBraceletRegController(Message command) {
        if (!command.replyTo.equals(mClient))
            return 0;

        mController = (command.arg1 == Bracelet.ARG_REGISTER) ? command.replyTo : null;

        if (command.replyTo.equals(mController)) // Remove client
            mClient = null;
        ChangeBraceletOperationModeByConnection();

        return sendACK(command, true);
    }

    private int onBraceletConnection(Message command) {
        if (!command.replyTo.equals(mController))
            return 0;

        String address = new String(command.getData().getString(Bracelet.KEY_STRING));
        LogUtil.getLogger().d("BT remote address:" + address);
        // Todo: make connection and reply state.

        switch (command.arg1) {
            case 0:
                try {
                    connectLock.acquire();
                    if (mBTConnectThread != null && mBTConnectThread.isAlive()) {
                        Log.d(TAG, "There is a connect thread running.");
                        sendACK(command, true);
                        return sendCommand(mController, Bracelet.CMD_CONNECTION, 1, address);
                    }

                    mBTConnectThread = new BTConnectThread(address);
                    mBTConnectThread.start();
                    connectLock.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 1: {
                int idx;
                for (idx = 0; idx < mConnectedDeviceList.size(); idx++) {
                    if (mConnectedDeviceList.get(idx).get(ListItem.ADDRESS).equals(address)) {
                        try {
                            ((BraceletStream) mConnectedDeviceList.get(idx).get(ListItem.DATA_STREAM)).terminate();
                            ((BluetoothSocket) mConnectedDeviceList.get(idx).get(ListItem.SOCKET)).close();
                        } catch (IOException e) {
                            Log.d(TAG, "Close exception");
                        }
                        break;
                    }
                }

                if (idx != mConnectedDeviceList.size()) {
                    mConnectedDeviceList.remove(idx);
                }
                mDeviceListNotice(0);
            }
            break;
        }

        return sendACK(command, true);
    }

    private int onBraceletData(Message command) {
        String raw = new String(command.getData().getByteArray(Bracelet.KEY_BYTE_ARRAY));
        //Log.d(TAG, raw);

        if (!command.replyTo.equals(mController) && !command.replyTo.equals(mClient))
            return 0;

        for (int idx = 0; idx < mConnectedDeviceList.size(); idx++) {
            if (((BraceletStream) mConnectedDeviceList.get(idx).get(ListItem.DATA_STREAM)).DwStream(raw))
                break;
        }

        return sendACK(command, true);
    }

    private int onBraceletGetDevice(Message command) {
        if (!command.replyTo.equals(mController))
            return 0;

        ArrayList<String> deviceList = new ArrayList<String>();
        for (int idx = 0; idx < mConnectedDeviceList.size(); idx++) {
            HashMap<ListItem, Object> deviceItem = mConnectedDeviceList.get(idx);
            deviceList.add((String) deviceItem.get(ListItem.ADDRESS));
        }

        return sendACK(command, true, deviceList);
    }

    private int onBraceletState(Message command) {
        if (!command.replyTo.equals(mController))
            return 0;
        String address = new String(command.getData().getString(Bracelet.KEY_STRING));
        int idx;
        for (idx = 0; idx < mConnectedDeviceList.size(); idx++) {
            if (mConnectedDeviceList.get(idx).get(ListItem.ADDRESS).equals(address)) {
                BraceletStream stream = (BraceletStream) mConnectedDeviceList.get(idx).get(ListItem.DATA_STREAM);
                byte rtn[] = new byte[stream.mSystemState.length + stream.mDeviceMac.length];
                for (int j = 0; j < rtn.length; j++)
                    rtn[j] = j < stream.mDeviceMac.length ? stream.mDeviceMac[j] : stream.mSystemState[j - stream.mDeviceMac.length];

                return sendACK(command, true, rtn);
            }
        }
        return 0;
    }

    private int onBraceletCalibrate(Message command) {
        if (!command.replyTo.equals(mController))
            return 0;
        if(command.getData()!=null) {
            String address = command.getData().getString(Bracelet.KEY_STRING)==null?"":command.getData().getString(Bracelet.KEY_STRING);
            for (int i = 0,size=mConnectedDeviceList.size(); i < size; i++) {
                if (mConnectedDeviceList.get(i).get(ListItem.ADDRESS).equals(address)) {
                    BraceletStream stream = (BraceletStream) mConnectedDeviceList.get(i).get(ListItem.DATA_STREAM);
                    stream.forceCalibrate();
                    return sendACK(command, true);
                }
            }
        }
        return 0;
    }

    private int onBraceletLinkLostTrigger(Message command) {
        if (!command.replyTo.equals(mController))
            return 0;
        int idx;
        for (idx = 0; idx < mConnectedDeviceList.size(); idx++) {
            BraceletStream stream = (BraceletStream) mConnectedDeviceList.get(idx).get(ListItem.DATA_STREAM);
            switch (command.arg1) {
                case 0:
                    stream.WriteStream("%SLL=0\n");
                    break;
                case 1:
                    stream.WriteStream("%SLL=1\n");
                    break;

            }
        }
        return sendACK(command, true);
    }

    private int onBraceletHidEnableTrigger(Message command) {
        if (!command.replyTo.equals(mController))
            return 0;
        int idx;
        for (idx = 0; idx < mConnectedDeviceList.size(); idx++) {
            BraceletStream stream = (BraceletStream) mConnectedDeviceList.get(idx).get(ListItem.DATA_STREAM);
            switch (command.arg1) {
                case 0:
                    stream.WriteStream("%SHKEY=0\n");
                    break;
                case 1:
                    stream.WriteStream("%SHKEY=1\n");
                    break;

            }
        }
        return sendACK(command, true);
    }

    private int mWatchDogCountDownValue;
    private Runnable mWatchDogForClient = new Runnable() {
        @Override
        public void run() {
            if (mClient != null) {
                if (mWatchDogCountDownValue == 0) {
                    LogUtil.getLogger().d("The game client was not response about 5 seconds!");
                    mClient = null;
                    ChangeBraceletOperationModeByConnection();
                } else {
                    mWatchDogCountDownValue -= 1;
                }
            }
            mHandler.postDelayed(this, 500);
        }
    };

    // Added by GioChen {{{ For BT connect.

    public void ChangeBraceletOperationModeByConnection() {
        if (!BraceletStream.mSystemMode && mClient == null) {
            if (mConnectedDeviceList.size() > 0) {
                try {
                    LogUtil.getLogger().d( "--> Into sniff operation 0 period 100ms");
                    for (int idx = 0; idx < mConnectedDeviceList.size(); idx++) {
                        String address = (String) mConnectedDeviceList.get(idx).get(ListItem.ADDRESS);
                        ((BraceletStream) mConnectedDeviceList.get(idx).get(ListItem.DATA_STREAM)).DwStream(address + BT_MAIN_MODE);
                    }
                    Thread.sleep(100);
                    for (int idx = 0; idx < mConnectedDeviceList.size(); idx++) {
                        String address = (String) mConnectedDeviceList.get(idx).get(ListItem.ADDRESS);
                        ((BraceletStream) mConnectedDeviceList.get(idx).get(ListItem.DATA_STREAM)).DwStream(address + "%LED=2,2,30,100,2000\n");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (BraceletStream.mSystemMode && mClient != null) {
            if (mConnectedDeviceList.size() > 0) {
                LogUtil.getLogger().d("--> Into normal operation 4 period 50ms");
                for (int idx = 0; idx < mConnectedDeviceList.size(); idx++) {
                    String address = (String) mConnectedDeviceList.get(idx).get(ListItem.ADDRESS);
                    ((BraceletStream) mConnectedDeviceList.get(idx).get(ListItem.DATA_STREAM)).DwStream(address + BT_GAME_MODE);
                }
            }
        }
        BraceletStream.mSystemMode = mClient == null;
    }

    private void mDeviceListNotice(int state) {
        ArrayList<String> deviceList = new ArrayList<String>();
        for (int idx = 0; idx < mConnectedDeviceList.size(); idx++) {
            HashMap<ListItem, Object> deviceItem = mConnectedDeviceList.get(idx);
            deviceList.add((String) deviceItem.get(ListItem.ADDRESS));
        }
        sendCommand(mClient, Bracelet.CMD_DEVICES, 0, deviceList);
    }

    BtConnectHandler mBtConnectHandler = new BtConnectHandler();

    class BtConnectHandler extends Handler {
        public void handleMessage(Message msg) {
            Bracelet.dump(TAG + "(RECV)", msg);
            switch (msg.what) {
                case BT_CONNECT_RESULT_OK:
                    LogUtil.getLogger().d("Connect " + msg.getData().getString(CONNECT_ADDRESS) + " OK : " + msg.getData().getString(RESULT_COMMENT));
                    sendCommand(mController, Bracelet.CMD_CONNECTION, 0, msg.getData().getString(CONNECT_ADDRESS));
                    mDeviceListNotice(1);
                    break;
                case BT_CONNECT_RESULT_NG:
                    LogUtil.getLogger().d("Connect " + msg.getData().getString(CONNECT_ADDRESS) + " NG : " + msg.getData().getString(RESULT_COMMENT));
                    sendCommand(mController, Bracelet.CMD_CONNECTION, 1, msg.getData().getString(CONNECT_ADDRESS));
                    break;
            }
        }
    }

    private static final int BT_CONNECT_RESULT_OK = 5001;
    private static final int BT_CONNECT_RESULT_NG = 5002;
    private static final String CONNECT_ADDRESS = "BtConnectAddress";
    private static final String RESULT_COMMENT = "BtConnectResultComment";

    //private boolean mReConnectIntent;
    private Semaphore connectLock = new Semaphore(1);
    BTConnectThread mBTConnectThread = null;

    private class BTConnectThread extends Thread {
        public String mAddress;

        private boolean mReConnectIntent;

        public BTConnectThread(String address) {
            super();
            mAddress = address;
            mReConnectIntent = false;
        }

        public BTConnectThread(String address, boolean reConnectIntent) {
            super();
            mAddress = address;
            mReConnectIntent = reConnectIntent;
        }

        public void stopReConnect(){
            mReConnectIntent = false;
        }

        private void notice(boolean result, String comment) {
            Message msg;
            Bundle bundle;

            if (mReConnectIntent) {
                if (mClient == null && !result) {
                    String disconnect = ((CommonApplication) getApplicationContext()).getPreferenceConfig().getString(Constants.SETTING_DISCONNECT_VALUE, "0");
                    if (!disconnect.equals("-1")) {
                        // IMPORTANT!!!
                        // Todo : That cause same Activity call the method onCreate twice. Fix later.
                        mBackToMain.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mBackToMain.putExtra(MatchActivity.DISCONNECT_CALL, true);
                        getBaseContext().startActivity(mBackToMain);
                    }
                }
            } else {
                boolean doNotice = true;
                for (HashMap reconnectItem : mReconnectList) {
                    if (reconnectItem.get("Address").equals(mAddress)) {
                        doNotice = false;
                        break;
                    }
                }
                if (doNotice) {
                    msg = new Message();
                    bundle = new Bundle();
                    msg.what = result ? BT_CONNECT_RESULT_OK : BT_CONNECT_RESULT_NG;
                    bundle.putString(RESULT_COMMENT, comment);
                    bundle.putString(CONNECT_ADDRESS, mAddress);
                    msg.setData(bundle);

                    mBtConnectHandler.sendMessage(msg);
                    BraceletService.isShowDisconnetAlert = false;
                }
            }
        }

        public void run() {
            BluetoothAdapter adapter;
            BluetoothDevice device;
            BluetoothSocket socket = null;
            OutputStream out_stream = null;
            InputStream in_stream = null;
            BraceletStream streamThread;

            adapter = BluetoothAdapter.getDefaultAdapter();
            if (adapter == null || !adapter.isEnabled()) {
                notice(false, "BT adapter is disabled.");
                return;
            }

            device = adapter.getRemoteDevice(mAddress);
            if (device == null) {
                notice(false, "The remote BT device is not exist.");
                return;
            }

            try {
                socket = device.createInsecureRfcommSocketToServiceRecord(SPP_UUID);
            } catch (Exception e) {
                e.printStackTrace();
                notice(false, "Failed to make socket to remote BT device.");
                return;
            }
            //Log.i(TAG, "Got socket.");

            if (socket == null) {
                notice(false, "Failed to make socket to remote BT device.");
                return;
            }

            try {
                socket.connect();
                //Log.i(TAG, "Connect success");
                out_stream = socket.getOutputStream();
                in_stream = socket.getInputStream();
                //Log.i(TAG, "Stream success");

                streamThread = new BraceletStream(in_stream, out_stream, mHandler);

                HashMap<ListItem, Object> item = new HashMap<ListItem, Object>();
                item.put(ListItem.SOCKET, socket);
                item.put(ListItem.DATA_STREAM, streamThread);
                item.put(ListItem.ADDRESS, mAddress);

                int weight = mConnectedDeviceList.size();
                streamThread.weight = weight == 0;
                item.put(ListItem.WEIGHT, weight);

                mConnectedDeviceList.add(item);
                streamThread.SetAddress(mAddress);
                streamThread.start();
                streamThread.stateMachineRun();

                for (HashMap reconnectItem : mReconnectList) {
                    if (reconnectItem.get("Address").equals(mAddress)) {
                        mReconnectList.remove(reconnectItem);
                        break;
                    }
                }

                try {
                    while (!streamThread.mStateStable())
                        Thread.sleep(1000);

                    if (mClient != null) {
                        streamThread.WriteStream(BT_GAME_MODE);
                        BraceletStream.mSystemMode = false;
                    }

                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                boolean linkLostVibrate=((CommonApplication) getApplicationContext()).getPreferenceConfig().getBoolean(Constants.SETTING_BRACELET_VIBRATE, false);
                //原有代码功能已注释掉，保留hidEnable标记
                boolean hidEnable=((CommonApplication) getApplicationContext()).getPreferenceConfig().getBoolean(Constants.SETTING_HID_ENABLE,false);
                try {
                    if (linkLostVibrate) {
                        streamThread.WriteStream("%SLL=1\n");
                    } else {
                        streamThread.WriteStream("%SLL=0\n");
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (hidEnable) {
                        streamThread.WriteStream("%SHKEY=1\n");
                    } else {
                        streamThread.WriteStream("%SHKEY=0\n");
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mReConnectIntent = false;
                notice(true, "success");

                BraceletService.isShowDisconnetAlert = false;
            } catch (Exception e) {
                e.printStackTrace();

                if (BraceletService.isShowDisconnetAlert){
                    return;
                }
                BraceletService.isShowDisconnetAlert = true;

                notice(false, "failed");

                try {
                    if (out_stream != null)
                        out_stream.close();
                    if (in_stream != null)
                        in_stream.close();
                    if (socket != null)
                        socket.close();
                } catch (Exception e2) {
                    LogUtil.getLogger().d("Close failed");
                }
            }

        }
    }

    private Runnable mReconnectTrigger = new Runnable() {
        @Override
        public void run() {
            try {
                connectLock.acquire();

                if (!(mBTConnectThread != null && mBTConnectThread.isAlive())) {
                    if (!mReconnectList.isEmpty()) {

                        HashMap<Object, Object> reconnectItem = mReconnectList.get(0);

                        String address = (String) reconnectItem.get("Address");
                        boolean alarm = reconnectItem.get("Alarm") != null;

                        Log.d(TAG, "Re-connect to " + address);
                        if (alarm)
                            mBTConnectThread = new BTConnectThread(address, true);
                        else
                            mBTConnectThread = new BTConnectThread(address);
                        mBTConnectThread.start();

                        mReconnectList.remove(reconnectItem);

                        //replace by new sharepreferences
                        boolean reconnect = ((CommonApplication) getApplicationContext()).getPreferenceConfig().getBoolean(Constants.SETTING_RECONNECT, true);
                        if (reconnect) {
                            reconnectItem = new HashMap<Object, Object>();
                            reconnectItem.put("Address", address);
                            reconnectItem.put("Alarm", false);
                            mReconnectList.add(reconnectItem);
                        }else{
                            Intent intent=new Intent(Constants.BAND_LOST_DIALOG);
                            sendBroadcast(intent);
                        }
                    }
                }

                connectLock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mHandler.postDelayed(mReconnectTrigger, 10000);
        }
    };

    private ArrayList<HashMap<Object, Object>> mReconnectList = new ArrayList<HashMap<Object, Object>>();
    private final BroadcastReceiver mConnectStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(intent.getAction())) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                boolean disconnectByDevice = false;
                for (HashMap item : mConnectedDeviceList) {
                    if (item.get(ListItem.ADDRESS).equals(device.getAddress())) {
                        disconnectByDevice = true;
                        break;
                    }
                }
                if (!disconnectByDevice)
                    return;

                int idx;
                for (idx = 0; idx < mConnectedDeviceList.size(); idx++) {
                    if (mConnectedDeviceList.get(idx).get(ListItem.ADDRESS).equals(device.getAddress())) {
                        try {
                            ((BluetoothSocket) mConnectedDeviceList.get(idx).get(ListItem.SOCKET)).close();
                        } catch (IOException e) {
                            Log.d(TAG, "Close exception");
                        }
                        break;
                    }
                }
                if (idx != mConnectedDeviceList.size()) {
                    mConnectedDeviceList.remove(idx);
                }
                mDeviceListNotice(0);

                try {
                    connectLock.acquire();
                    HashMap<Object, Object> item = new HashMap<Object, Object>();
                    item.put("Address", device.getAddress());
                    item.put("Alarm", true);
                    mReconnectList.add(item);
                    mHandler.removeCallbacks(mReconnectTrigger);
                    mHandler.postDelayed(mReconnectTrigger, 5000);
                    connectLock.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (BraceletService.ACTION_STOP_RECONNECTE.equals(intent.getAction())) {

                // 停止重连
                mHandler.removeCallbacks(mReconnectTrigger);
                BraceletService.isShowDisconnetAlert = false;

                mHandler.removeCallbacks(mBTConnectThread);
                mReconnectList.clear();
                mBtConnectHandler.removeCallbacks(mRingVibrate);
                mBTConnectThread.stopReConnect();
            }
        }
    };

    private BraceletStream mTopStream;

    private class exPhoneCallListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            mTopStream = null;
            for (int idx = 0; idx < mConnectedDeviceList.size(); idx++) {
                if (((BraceletStream) mConnectedDeviceList.get(idx).get(ListItem.DATA_STREAM)).weight) {
                    mTopStream = ((BraceletStream) mConnectedDeviceList.get(idx).get(ListItem.DATA_STREAM));
                    break;
                }
            }

            if (mTopStream == null) {
                Log.d(TAG, "No wear device connected.");
                return;
            }

            boolean callingVibrate = ((CommonApplication) getApplicationContext()).getPreferenceConfig().getBoolean(Constants.SETTING_CALLING_VIBRATE, true);
            if (!callingVibrate) {
                Log.d(TAG, "Vibration disabled.");
                return;
            }

            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    mBtConnectHandler.removeCallbacks(mRingVibrate);
                    mTopStream.DwStreamByRaw("%VIBRATE=0\n");
                    LogUtil.getLogger().d("CALL_STATE_IDLE");
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    mBtConnectHandler.removeCallbacks(mRingVibrate);
                    mTopStream.DwStreamByRaw("%VIBRATE=0\n");
                    LogUtil.getLogger().d("CALL_STATE_OFFHOOK");
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    if (mClient == null) {
                        mTopStream.DwStreamByRaw("%VIBRATE=1,50,500\n");
                        mBtConnectHandler.postDelayed(mRingVibrate, 100);
                        LogUtil.getLogger().d("CALL_STATE_RINGING");
                    }

                    break;
            }
        }
    }

    private Runnable mRingVibrate = new Runnable() {
        @Override
        public void run() {
            mTopStream.DwStreamByRaw("%VIBRATE=1,50,500\n");
            mBtConnectHandler.postDelayed(this, 1000);
        }
    };

    private class BraceletStream extends DeviceDataStream {
        private String mDeviceAddress;
        private byte[] mDeviceMac = new byte[6];

        public BraceletStream(InputStream inputStream, OutputStream outputStream) {
            super(inputStream, outputStream);
            mContext = getBaseContext();
        }

        public BraceletStream(InputStream inputStream, OutputStream outputStream, Handler handler) {
            super(inputStream, outputStream, handler);
            mContext = getBaseContext();
        }

        public void SetAddress(String address) {
            mDeviceAddress = address;

            String[] macAddressParts = mDeviceAddress.split(":");

            for (int i = 0; i < 6; i++) {
                Integer hex = Integer.parseInt(macAddressParts[i], 16);
                mDeviceMac[i] = hex.byteValue();
            }
        }

        public boolean DwStreamByRaw(String cmd) {
            return DwStream(mDeviceAddress + cmd);
        }

        public boolean DwStream(String cmd) {
            boolean rtn = false;
            if (mState != STATE_CONNECTABLE) return rtn;
            if (cmd.length() <= mDeviceAddress.length()) return rtn;

            if (mDeviceAddress.equals(cmd.substring(0, mDeviceAddress.length()))) {
                WriteStream(cmd.substring(mDeviceAddress.length()));
                rtn = true;
            }
            return rtn;
        }

        @Override
        public void UpStream(byte[] raw) {

            byte[] newRaw = new byte[raw.length + mDeviceMac.length];
            for (int i = 0; i < newRaw.length; i++) {
                newRaw[i] = i < mDeviceMac.length ? mDeviceMac[i] : raw[i - mDeviceMac.length];
            }

            if (mClient != null) {
                sendCommand(mClient, Bracelet.CMD_DATA, 0, newRaw);
            }
        }

        @Override
        public void BatteryTooLow() {
            mBackToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            mBackToMain.putExtra(MatchActivity.BATTERY_TOO_LOW_CALL, true);
            mBackToMain.putExtra("ADDRESS", mDeviceAddress);
            getBaseContext().startActivity(mBackToMain);
        }
    }

    // }}}
}
