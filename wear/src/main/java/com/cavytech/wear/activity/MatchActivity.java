package com.cavytech.wear.activity;

import android.app.KeyguardManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.basecore.util.core.ListUtils;
import com.basecore.widget.CustomToast;
import com.basecore.widget.dialog.Effectstype;
import com.basecore.widget.dialog.NiftyDialogBuilder;
import com.cavytech.wear.Bracelet;
import com.cavytech.wear.BraceletClient;
import com.cavytech.wear.BraceletService;
import com.cavytech.wear.DeviceDataStream;
import com.cavytech.wear.R;
import com.cavytech.wear.ServiceStarter;
import com.cavytech.wear.application.CommonApplication;
import com.cavytech.wear.entity.DeviceEntity;
import com.cavytech.wear.http.MyHttpClient;
import com.cavytech.wear.util.Constants;
import com.cavytech.wear.util.LogUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import net.sourceforge.opencamera.MainActivity;

import org.apache.http.Header;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

import pedometer.StepCountActivity;


/**
 * Created by ShadowNight on 2015/7/9.
 */
public class MatchActivity extends CommonActivity {
    private static final int TURN_ON_BLUETOOTH = 1;//开启蓝牙
    private static final int RESULT_FORM_SETTING = 2;//从设置界面得到返回结果
    private static final int MSG_REMOVE_FROM_LIST = 1001;
    private static final int MSG_ADD_TO_LIST = 1002;
    private static final int MSG_REBUILD_PAIRED = 1003;
    private static final int MSG_CONNECT_FAILED = 1004;
    private static final String DEVICE_PREFIX = "Cavy";
    private static final String TAG = "MatchActivity";
    private static final String BUNDLE_KEY_LIST = "LIST";
    private static final String BUNDLE_KEY_ADDRESS = "ADDRESS";
    private static final String BUNDLE_KEY_NAME = "NAME";
    public static final String DISCONNECT_CALL = "DisconnectAlert";
    public static final String BATTERY_TOO_LOW_CALL = "BatteryTooLow";
    private ImageView matchsearch;
    private TextView searchtext;
    private ListView listview;
    private AnimationDrawable animationDrawable;
    private NiftyDialogBuilder dialogBuilder;
    private NiftyDialogBuilder disConnectDialogBuilder;
    private BluetoothAdapter mBluetoothAdapter;
    private BraceletClient mBraceletClient;
    private MediaPlayer mAlarmSound;
    private Vibrator mAlarmVibrator;
    private String mStateAddress;
    private boolean mDisconnecting = false;
    private MyAdapter myAdapter;
    private List<DeviceEntity> deviceEntityList = new ArrayList<>();
    private Semaphore mDiscoverLock = new Semaphore(1);
    private Handler mHandler = new MessageHandler(this, 0);
    private Messenger mBraceletMessenger = new Messenger(new MessageHandler(this, 1));

    public ImageButton mSetting;
    public ImageButton mStepCount;
    public ImageButton mCamera;

    private static class MessageHandler extends Handler {
        private final WeakReference<MatchActivity> mActivity;
        private int mMethod = 0;

        public MessageHandler(MatchActivity activity, int methodNum) {
            mActivity = new WeakReference<MatchActivity>(activity);
            mMethod = methodNum;
        }

        @Override
        public void handleMessage(Message msg) {
            MatchActivity activity = mActivity.get();
            if (activity == null)
                return;
            switch (mMethod) {
                case 0:
                    activity.handleMessage(msg);
                    break;
                case 1:
                    activity.serviceMessage(msg);
                    break;
            }
        }
    }

    private Runnable mStopRunnable = new Runnable() {
        @Override
        public void run() {
            stopScan();
        }
    };
    private Runnable mAlertDefence = new Runnable() {
        @Override
        public void run() {
            mAlarmSound.stop();
            mAlarmVibrator.cancel();
            mBraceletClient.sendCommand(Bracelet.CMD_DEVICES, 0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        initView();
        addListener();
        turnOnBlueTooth();
        getBindDevices();
        sendBroadcast(new Intent(ServiceStarter.INVOKE_WEAR_SERVICE));
        // After register client, we need register controller too. But the connections is not build yet, so we register controller when we receive ACK!
        mBraceletClient = new BraceletClient(this, TAG, mBraceletMessenger);
        mBraceletClient.BindService();
        mAlarmSound = MediaPlayer.create(this, R.raw.wear_break);
        mAlarmVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        startScan();
        uploadInfo("", "0");
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mBroadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(mBroadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        //registerReceiver(mBroadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
        registerReceiver(mBroadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        registerReceiver(mBroadcastReceiver, new IntentFilter(DeviceDataStream.STREAM_BROADCAST));
        registerReceiver(mBroadcastReceiver, new IntentFilter(Constants.BAND_LOST_DIALOG));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getIntent().getBooleanExtra(DISCONNECT_CALL, false)) {
            String disconnectConfig = getCoreApplication().getPreferenceConfig().getString(Constants.SETTING_DISCONNECT_VALUE, "0");
            if (disconnectConfig.equals("0")) {
                mAlarmSound.start();
                long[] pattern = {2000, 500, 3000, 500};
                mAlarmVibrator.vibrate(pattern, 2);
            } else if (disconnectConfig.equals("1")) {
                mAlarmSound.start();
            } else if (disconnectConfig.equals("2")) {
                long[] pattern = {2000, 500, 3000, 500};
                mAlarmVibrator.vibrate(pattern, 2);
            } else {
                return;
            }

            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
            wl.acquire();

            KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            final KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
            kl.disableKeyguard();
            showDisConnectDialog(kl);
            getIntent().removeExtra(DISCONNECT_CALL);
            mHandler.postDelayed(mAlertDefence, 60000 * 5);
            wl.release();

        } else if (getIntent().getBooleanExtra(BATTERY_TOO_LOW_CALL, false)) {
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
            wl.acquire();

            KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            final KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
            kl.disableKeyguard();
/*
            String msg = getIntent().getStringExtra( "ADDRESS" ) + " " +  getString( R.string.BatteryIsGoingToDie );

            // motify by tunshu{{
            Toast.makeText( this,
            		msg, Toast.LENGTH_LONG ).show();*/
            mBraceletClient.sendCommand(Bracelet.CMD_DEVICES, 0);
            kl.reenableKeyguard();
/*
            new AlertDialog.Builder(WearActivity.this)
                    .setTitle(R.string.Warning)
                    .setMessage(msg)
                    .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            mBraceletClient.sendCommand(Bracelet.CMD_DEVICES, 0);
                            kl.reenableKeyguard();
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            mBraceletClient.sendCommand(Bracelet.CMD_DEVICES, 0);
                            kl.reenableKeyguard();
                        }
                    }).show(); */

            //}}
            getIntent().removeExtra(BATTERY_TOO_LOW_CALL);
            getIntent().removeExtra("ADDRESS");
            wl.release();
        } else {
            mBraceletClient.sendCommand(Bracelet.CMD_DEVICES, 0);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopScan();
        mBraceletClient.UnbindService();
        unregisterReceiver(mBroadcastReceiver);
    }

    private void initView() {
        findTitle();
        matchsearch = (ImageView) findViewById(R.id.match_search);
        searchtext = (TextView) findViewById(R.id.search_text);
        listview = (ListView) findViewById(R.id.list_view);

        mSetting = (ImageButton)findViewById(R.id.setting);
        mStepCount = (ImageButton)findViewById(R.id.stepcounter);
        mCamera = (ImageButton)findViewById(R.id.camera);

        mCamera.setImageResource(R.drawable.camera);
        mSetting.setImageResource(R.drawable.icon_setting);
        mSetting.setVisibility(View.VISIBLE);
        title.setText(getString(R.string.match));
        searchtext.setText(R.string.researching);
    }

    private void addListener() {
        mCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog(getString(R.string.openingCamera));
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        Intent intent = new Intent(MatchActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }, 500);
            }
        });
        mSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MatchActivity.this, MatchSettingActivity.class);
                startActivityForResult(intent, RESULT_FORM_SETTING);
            }
        });
        matchsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });
        searchtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });

        mStepCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog(getString(R.string.openingStepCount));
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        Intent intent = new Intent(MatchActivity.this, StepCountActivity.class);
                        startActivity(intent);
                    }
                }, 500);

            }
        });
    }

    public void stopFrame() {
//        if (animationDrawable != null && animationDrawable.isRunning()) { //如果正在运行,就停止
//            animationDrawable.stop();
//        }
        matchsearch.clearAnimation();
//        searchtext.setCompoundDrawablesWithIntrinsicBounds(R.drawable.research, 0, 0, 0);
        searchtext.setText(R.string.researching);
    }

    public void runFrame() {
        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.match_search);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        if (operatingAnim != null) {
            matchsearch.startAnimation(operatingAnim);
        }
//        matchsearch.setBackgroundResource(R.anim.match_search);
//        animationDrawable = (AnimationDrawable) matchsearch.getBackground();
        searchtext.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        searchtext.setText(R.string.searching);
//        animationDrawable.start();
    }

    private void showDialog(String message) {
        final LayoutInflater inflater = LayoutInflater.from(MatchActivity.this);
        View layout = inflater.inflate(R.layout.public_progress_dialog, null);
        TextView progressMessage = (TextView) layout.findViewById(R.id.progress_message);
        progressMessage.setText(message);
        dialogBuilder = NiftyDialogBuilder.getInstance(MatchActivity.this);
        dialogBuilder.withTitle(null).withTitleColor("#ffffff").withMessage(null).isCancelableOnTouchOutside(true).isCancelable(true).withDuration(300).withEffect(Effectstype.Fadein)
                .setCustomView(layout, MatchActivity.this).show();

        dialogBuilder.showMsgLinearLayout(View.GONE);
        dialogBuilder.showBtnLinearLayout(View.GONE);
    }

    private void dismissDialog() {
        if (dialogBuilder != null) {
            dialogBuilder.dismiss();
        }
    }

    private void dismissDisconnectDialog() {

        if(disConnectDialogBuilder != null){
            disConnectDialogBuilder.dismiss();
            disConnectDialogBuilder = null;
            mAlarmSound.stop();
            mAlarmVibrator.cancel();
        }
    }

    private void showDisConnectDialog(final KeyguardManager.KeyguardLock kl) {
        disConnectDialogBuilder = NiftyDialogBuilder.getInstance(MatchActivity.this);
        disConnectDialogBuilder.withTitle(getString(R.string.disConnect_title)).withTitleColor("#ffffff").withMessage(getString(R.string.disConnect_msg)).isCancelableOnTouchOutside(false).isCancelable(false).withDuration(300).withEffect(Effectstype.Fadein)
                .withButton1Text(getString(R.string.dismiss)).setButton1Click(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeCallbacks(mAlertDefence);
                mAlarmSound.stop();
                mAlarmVibrator.cancel();
                mBraceletClient.sendCommand(Bracelet.CMD_DEVICES, 0);
                kl.reenableKeyguard();

                dismissDisconnectDialog();

                Intent intent=new Intent(BraceletService.ACTION_STOP_RECONNECTE);
                sendBroadcast(intent);
            }
        }).show();
    }

    private void showConnectFailedDailog(){
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(MatchActivity.this);
        dialogBuilder.withTitle(getString(R.string.connection_failed)).withTitleColor("#ffffff").withMessage(getString(R.string.connection_failed_message)).isCancelableOnTouchOutside(false).isCancelable(false).withDuration(300).withEffect(Effectstype.Fadein)
                .withButton1Text("").withButton2Text(getString(R.string.ok)).setButton1Click(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }).setButton2Click(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.dismiss();
            }
        }).show();
    }


    private void turnOnBlueTooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, TURN_ON_BLUETOOTH);
        }
    }

    private void getBindDevices() {
        if (mBluetoothAdapter != null) {
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().startsWith(DEVICE_PREFIX)) {
                        addDevice(device.getName(), device.getAddress(), "2");
                    }
                }
            }
        }
    }

    private void addDevice(String name, String address, String type) {
        LogUtil.getLogger().d(name + "--" + address + "--" + type);
        DeviceEntity deviceEntity = new DeviceEntity();
        deviceEntity.setDeviceAddress(address);
        deviceEntity.setDeviceName(name);
        deviceEntity.setType(type);
        refreshData(deviceEntity);
    }

    private void startScan() {
        if (mBluetoothAdapter == null) {
            return;
        }
        if (animationDrawable != null && animationDrawable.isRunning()) {
            return;
        }
        try {
            mDiscoverLock.acquire();
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
                mHandler.removeCallbacks(mStopRunnable);
            }
            mBluetoothAdapter.startDiscovery();
            mHandler.postDelayed(mStopRunnable, 8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mDiscoverLock.release();
        }
        runFrame();
    }

    private void stopScan() {
        if (mBluetoothAdapter == null)
            return;
        try {
            mDiscoverLock.acquire();
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mDiscoverLock.release();
        }

        stopFrame();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.getLogger().d("requestCode-->" + requestCode + "resultCode-->" + resultCode);
        switch (requestCode) {
            case TURN_ON_BLUETOOTH:
                if (resultCode == RESULT_OK) {
                    LogUtil.getLogger().d("bluetooth turn on");
                } else if (resultCode == RESULT_CANCELED) {
                    LogUtil.getLogger().d("bluetooth turn off");
                }
                break;
            case RESULT_FORM_SETTING:
                switch (resultCode) {
                    case 1:
                        mBraceletClient.sendCommand(MSG_REBUILD_PAIRED, data.getIntExtra("LINK_LOST", 2));
                        break;
                    case 2:
                        mBraceletClient.sendCommand(MSG_CONNECT_FAILED, data.getIntExtra("HID_ENABLE", 2));
                        break;
                    case 3:
                        try {
                            // GioChen Todo : send command in state machine or thread to avoid frame lost.
                            mBraceletClient.sendCommand(MSG_REBUILD_PAIRED, data.getIntExtra("LINK_LOST", 2));
                            Thread.sleep(1000);
                            mBraceletClient.sendCommand(MSG_CONNECT_FAILED, data.getIntExtra("HID_ENABLE", 2));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.getLogger().d(intent.getAction());
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getName() != null && device.getName().startsWith(DEVICE_PREFIX)) {
                    LogUtil.getLogger().d(device.getName() + "--" + device.getAddress() + "--" + device.getBondState());
                    switch (device.getBondState()) {
                        case BluetoothDevice.BOND_NONE://解除绑定
                            addDevice(device.getName(), device.getAddress(), "1");
                            break;
                        case BluetoothDevice.BOND_BONDING:
                            addDevice(device.getName(), device.getAddress(), "1");
                            break;
                        case BluetoothDevice.BOND_BONDED:
                            addDevice(device.getName(), device.getAddress(), "2");
                            break;
                        default:
                            addDevice(device.getName(), device.getAddress(), "1");
                            break;
                    }
                }

            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction())) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                LogUtil.getLogger().d(device.getBondState());
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_NONE://解除绑定
                        LogUtil.getLogger().d("解除绑定");
                        dismissDialog();
                        addDevice(device.getName(), device.getAddress(), "1");
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        LogUtil.getLogger().d(device.getName() + "--" + device.getAddress());
                        addDevice(device.getName(), device.getAddress(), "2");
                        LogUtil.getLogger().d("完成配对");
                        dismissDialog();
                        break;
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
                LogUtil.getLogger().d("Discovery done");
            } else if (DeviceDataStream.STREAM_BROADCAST.equals(intent.getAction())) {
                LogUtil.getLogger().d(intent.getIntExtra(DeviceDataStream.STREAM_PARAM, DeviceDataStream.PARAM_END_CAL));
                switch (intent.getIntExtra(DeviceDataStream.STREAM_PARAM, DeviceDataStream.PARAM_END_CAL)) {
                    case DeviceDataStream.PARAM_START_CAL:
//                        dismissDialog();
//                        showDialog(getString(R.string.correction_action));
                        break;
                    case DeviceDataStream.PARAM_PROC_CAL:
//                        dismissDialog();
//                        showDialog(getString(R.string.correction_store));
                        break;
                    case DeviceDataStream.PARAM_END_CAL:
                        dismissDialog();
                        break;
                    default:
                        dismissDialog();
                        break;
                }

            } else if(Constants.BAND_LOST_DIALOG.equals(intent.getAction())){

            }
        }
    };

    private void handleMessage(Message msg) {
        int list = msg.getData().getInt(BUNDLE_KEY_LIST);
        String address = msg.getData().getString(BUNDLE_KEY_ADDRESS);
        String name = msg.getData().getString(BUNDLE_KEY_NAME);
        LogUtil.getLogger().d(address);
        LogUtil.getLogger().d(name);
        switch (msg.what) {
            case MSG_REMOVE_FROM_LIST:
                break;
            case MSG_ADD_TO_LIST:
                break;
            case MSG_REBUILD_PAIRED: {
                if (mBluetoothAdapter == null) break;
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        addDevice(device.getName(), device.getAddress(), "2");
                    }
                }
            }
            break;

            case MSG_CONNECT_FAILED:
                CustomToast.showToast(MatchActivity.this, getString(R.string.connection_failed));
                break;
        }
    }

    private void serviceMessage(Message msg) {
        Bracelet.dump(TAG + "(RECV)", msg);
        LogUtil.getLogger().d(msg.what);
        LogUtil.getLogger().d(msg.arg1);
        switch (msg.what) {
            case Bracelet.CMD_ACK:
                // When we receive ACK for CMD_REG_CLIENT, send CMD_REG_CONTROLLER.
                if (msg.arg1 == Bracelet.CMD_REG_CLIENT) {
                    mBraceletClient.sendCommand(Bracelet.CMD_REG_CONTROLLER, Bracelet.ARG_REGISTER);
                } else if (msg.arg1 == Bracelet.CMD_REG_CONTROLLER) {
                    mBraceletClient.sendCommand(Bracelet.CMD_DEVICES, 0);
                } else if (msg.arg1 == Bracelet.CMD_DEVICES) {//联机设备成功
                    dismissDialog();

                    ArrayList<String> deviceList = msg.getData().getStringArrayList(Bracelet.KEY_STRING_ARRAY);
                    if (!ListUtils.isEmpty(deviceList)) {
                        for (String device : deviceList) {
                            LogUtil.getLogger().d(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(device).getName());
                            LogUtil.getLogger().d(device);
                            addDevice(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(device).getName(), device, "3");
                        }
                    }

                } else if (msg.arg1 == Bracelet.CMD_CONNECTION) {//断线
                    if (mDisconnecting) {
                        if (mBluetoothAdapter != null) {
                            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                            if (pairedDevices.size() > 0) {
                                for (BluetoothDevice device : pairedDevices) {
                                    if (device.getName().startsWith(DEVICE_PREFIX)) {
                                        addDevice(device.getName(), device.getAddress(), "2");
                                    }
                                }
                            }
                        }
                        mDisconnecting = false;
                    }
                } else if (msg.arg1 == 1001) {//校正
                    mBraceletClient.sendCommand(1002, 1, mStateAddress);
                } else if (msg.arg1 == 1002) {
                }
                break;
            case Bracelet.CMD_DATA:
                mBraceletClient.sendACK(msg, true);
                // Todo: We get data from Client here.Send data to BT device now!
                break;
            case Bracelet.CMD_CONNECTION:
                String address = new String(msg.getData().getString(Bracelet.KEY_STRING));
                LogUtil.getLogger().d("Connect " + address + " " + (msg.arg1 == 0 ? "OK" : "NG"));
                // Todo : toast OK or NG...
                if (msg.arg1 != 0) {
                    CustomToast.showToast(MatchActivity.this, getString(R.string.connect_to) +
                            new String(msg.getData().getString(Bracelet.KEY_STRING)) + getString(R.string.failed));
                    String name = msg.getData().getString(BUNDLE_KEY_NAME);
                    showConnectFailedDailog();

//                    DataWatchUtil.postToServerInfo(address, String.valueOf(PostCode.BANDCONNECTFAILD.getCode()), getResources().getString(R.string.ConnectionFailed).toString(),
//                            MyApplication.getInstance().getLBS(), WearActivity.this);
                    uploadInfo(address,"2");
                } else {
//                    DataWatchUtil.postToServerInfo(address, String.valueOf(PostCode.BANDCONNECTSUCC.getCode()), getResources().getString(R.string.ConnectionSucc).toString(),
//                            MyApplication.getInstance().getLBS(), WearActivity.this);
                    uploadInfo(address,"1");
                }
                mBraceletClient.sendACK(msg, true);
                mBraceletClient.sendCommand(Bracelet.CMD_DEVICES, 0);
                break;
            default:
                break;
        }
    }

    private void refreshData(DeviceEntity deviceEntity) {
        if (ListUtils.isEmpty(deviceEntityList)) {
            deviceEntityList.add(deviceEntity);
        } else {
            boolean isExist = false;
            for (int i = 0, size = deviceEntityList.size(); i < size; i++) {
                if (deviceEntityList.get(i).getDeviceAddress().equals(deviceEntity.getDeviceAddress())) {
                    deviceEntityList.set(i, deviceEntity);
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                deviceEntityList.add(deviceEntity);
            }
        }
        if (ListUtils.isEmpty(deviceEntityList)) {
            listview.setVisibility(View.INVISIBLE);
        } else {
            if (myAdapter != null) {
                myAdapter.refreshAdapter(deviceEntityList);
            } else {
                myAdapter = new MyAdapter(MatchActivity.this, deviceEntityList);
                listview.setAdapter(myAdapter);
                listview.setVisibility(View.VISIBLE);
            }
        }
    }

    private class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private List<DeviceEntity> deviceList;

        public MyAdapter(Context context, List<DeviceEntity> deviceList) {
            this.mInflater = LayoutInflater.from(context);
            this.deviceList = deviceList;
        }

        public void refreshAdapter(List<DeviceEntity> deviceList) {
            this.deviceList = deviceList;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return deviceList.size();
        }

        @Override
        public Object getItem(int position) {
            return deviceList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public String getType(int position) {
            return deviceList.get(position).getType() == null ? "" : deviceList.get(position).getType();
        }

        public String getDeviceAddress(int position) {
            return deviceList.get(position).getDeviceAddress() == null ? "" : deviceList.get(position).getDeviceAddress();
        }

        public String getDeviceName(int position) {
            return deviceList.get(position).getDeviceName() == null ? "" : deviceList.get(position).getDeviceName();
        }

        public void setDeviceActionStyle(TextView deviceAction, String action) {
            deviceAction.setText(action);
            if (action.equals(getString(R.string.correction)) || action.equals(getString(R.string.disconnect))) {
                deviceAction.setBackgroundResource(R.drawable.down_button_white);
                deviceAction.setTextColor(getResources().getColor(R.color.white));
            } else if (action.equals(getString(R.string.connect)) || action.equals(getString(R.string.pair))) {
                deviceAction.setBackgroundResource(R.drawable.down_button_blue);
                setTextColor(deviceAction);
            } else if (action.equals(getString(R.string.unpair))) {
                deviceAction.setBackgroundResource(R.drawable.down_button_grey);
                deviceAction.setTextColor(Color.parseColor("#4c4c4c"));
            }
        }

        public void setTextColor(TextView appInstall) {
            ColorStateList cl = null;
            try {
                XmlResourceParser xrp = getResources().getXml(R.drawable.bt_text_color_white);
                cl = ColorStateList.createFromXml(getResources(), xrp);
            } catch (Exception ex) {
            }

            if (cl != null) {
                appInstall.setTextColor(cl);
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.activity_match_list_item, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.devicename.setText(getDeviceName(position));
            holder.devicesummary.setText(getDeviceAddress(position));
            holder.devicename.setTextColor(Color.parseColor("#555555"));
            holder.devicesummary.setTextColor(Color.parseColor("#999999"));
            if (getType(position).equals("1")) {//发现设备
                holder.deviceaction1.setVisibility(View.GONE);
                setDeviceActionStyle(holder.deviceaction2, getString(R.string.pair));
            } else if (getType(position).equals("2")) {//设备已配对
                holder.deviceaction1.setVisibility(View.VISIBLE);
                holder.deviceaction2.setVisibility(View.VISIBLE);
                setDeviceActionStyle(holder.deviceaction1, getString(R.string.connect));
                setDeviceActionStyle(holder.deviceaction2, getString(R.string.unpair));
            } else if (getType(position).equals("3")) {//联机成功
                holder.deviceaction1.setVisibility(View.GONE);
                holder.deviceaction2.setVisibility(View.VISIBLE);
                setDeviceActionStyle(holder.deviceaction1, getString(R.string.correction));
                setDeviceActionStyle(holder.deviceaction2, getString(R.string.disconnect));
                holder.devicename.setTextColor(Color.parseColor("#3e76db"));
                holder.devicesummary.setTextColor(Color.parseColor("#3e76db"));

                dismissDisconnectDialog();
            }
            holder.deviceaction1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getType(position).equals("2")) {//联机
                        showDialog(getString(R.string.connecting));
                        mBraceletClient.sendCommand(Bracelet.CMD_CONNECTION, 0, getDeviceAddress(position));
                    } else if (getType(position).equals("3")) {//校正
                        mBraceletClient.sendCommand(1001, 1, getDeviceAddress(position));
                    }
                }
            });
            holder.deviceaction2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getType(position).equals("1")) {//配对
                        showDialog(getString(R.string.pair_ing));
                        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(getDeviceAddress(position));
                        try {
                            Method m = device.getClass().getMethod("createBond", (Class[]) null);
                            m.invoke(device, (Object[]) null);
                        } catch (Exception e) {
                            CustomToast.showToast(MatchActivity.this, getString(R.string.pair_failed));
                            dismissDialog();
                            e.printStackTrace();
                        }
                    } else if (getType(position).equals("2")) {//解除
                        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(getDeviceAddress(position));
                        try {
                            Method m = device.getClass().getMethod("removeBond", (Class[]) null);
                            m.invoke(device, (Object[]) null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //解除绑定后重新获取设备列表
                        getBindDevices();
                    } else if (getType(position).equals("3")) {//断线
                        mBraceletClient.sendCommand(Bracelet.CMD_CONNECTION, 1, getDeviceAddress(position));
                        mDisconnecting = true;
                    }
                }
            });
            return convertView;
        }

        public class ViewHolder {
            public final TextView devicename;
            public final TextView devicesummary;
            public final Button deviceaction2;
            public final Button deviceaction1;
            public final View root;

            public ViewHolder(View root) {
                devicename = (TextView) root.findViewById(R.id.device_name);
                devicesummary = (TextView) root.findViewById(R.id.device_summary);
                deviceaction2 = (Button) root.findViewById(R.id.device_action_2);
                deviceaction1 = (Button) root.findViewById(R.id.device_action_1);
                this.root = root;
            }
        }
    }

    private void uploadInfo(String bandinfo,String errorcode){
        Build bd = new Build();
        String devinfo = bd.MODEL + ","+ bd.ID + "," + bd.DEVICE +  "," + bd.PRODUCT;
        RequestParams params=new RequestParams();
        params.put("ac","onlineFailureLog");
        params.put("serial",bd.SERIAL);
        params.put("devinfo",devinfo);
        params.put("bandinfo",bandinfo);
        params.put("errorcode",errorcode);
        if(errorcode.equals("0")){
            params.put("errormsg",getString(R.string.open_app));
        }else if(errorcode.equals("1")){
            params.put("errormsg",getString(R.string.connection_success_string));
        }else if(errorcode.equals("2")){
            params.put("errormsg",getString(R.string.connection_failed_string));
        }
        params.put("LBS",((CommonApplication)getApplicationContext()).getLBS());
        MyHttpClient.get(this, "common/index", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

}
