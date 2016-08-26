package com.cavytech.wear;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import com.cavytech.wear.util.LogUtil;

import java.util.ArrayList;


public class BraceletClient {
    public Context mActivity = null;
    public String mName = "AnonymousClient";
    public boolean mIsBind = false;
    public Messenger mHandler = null;
    public Messenger mService = null;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            LogUtil.getLogger().d("Connect Service");
            mService = new Messenger(service);
            sendCommand(Bracelet.CMD_REG_CLIENT, Bracelet.ARG_REGISTER);
        }

        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
            LogUtil.getLogger().d("Disconnect Service");
        }
    };

    public BraceletClient(Activity activity, String name, Messenger handler) {
        mName = name;
        mHandler = handler;
        mActivity = activity;
    }

    public boolean BindService() {
        if (mIsBind || mService != null)
            return false;

        Intent intent = new Intent("com.cavytech.BraceletService").setPackage("com.cavytech.wear");
        mIsBind = mActivity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        LogUtil.getLogger().d("BindService:" + mIsBind);
        return true;
    }

    public boolean UnbindService() {
        if (!mIsBind || mService == null)
            return false;

        sendCommand(Bracelet.CMD_REG_CLIENT, Bracelet.ARG_UNREGISTER);
        mActivity.unbindService(mConnection);
        mIsBind = false;
        mService = null;
        LogUtil.getLogger().d("UnbindService:");

        return true;
    }

    public int sendCommand(int command, int arg) {
        return Bracelet.sendCommand(mName + "(SEND)", mService, mHandler, command, arg);
    }

    public int sendCommand(int command, int arg, String data) {
        LogUtil.getLogger().d(data);
        return Bracelet.sendCommand(mName + "(SEND)", mService, mHandler, command, arg, data);
    }

    public int sendCommand(int command, int arg, byte[] data) {
        return Bracelet.sendCommand(mName + "(SEND)", mService, mHandler, command, arg, data);
    }

    public int sendCommand(int command, int arg, ArrayList<String> data) {
        return Bracelet.sendCommand(mName + "(SEND)", mService, mHandler, command, arg, data);
    }

    public int sendACK(Message message, boolean result) {
        return Bracelet.sendACK(mName + "(SEND)", message, result);
    }

    public int sendACK(Message message, boolean result, String data) {
        return Bracelet.sendACK(mName + "(SEND)", message, result, data);
    }

    public int sendACK(Message message, boolean result, byte[] data) {
        return Bracelet.sendACK(mName + "(SEND)", message, result, data);
    }

    public int sendACK(Message message, boolean result, ArrayList<String> data) {
        return Bracelet.sendACK(mName + "(SEND)", message, result, data);
    }
}
