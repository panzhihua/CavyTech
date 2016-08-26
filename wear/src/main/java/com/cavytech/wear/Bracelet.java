package com.cavytech.wear;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;


import com.cavytech.wear.util.LogUtil;

import java.util.ArrayList;

/**
 * Created by ShadowNight on 2015/7/10.
 */
public class Bracelet {
    public static int mCommendIndex = 0;

    public static final int CMD_ACK = 0;
    public static final int CMD_REG_CLIENT = 1;
    public static final int CMD_REG_CONTROLLER = 2;
    public static final int CMD_CONNECTION = 3;
    public static final int CMD_DATA = 4;
    public static final int CMD_DEVICES = 5;
    public static final int CMD_INT = -1;

    public static final int ARG_UNREGISTER = 0;
    public static final int ARG_REGISTER = 1;

    public static final String KEY_ACK = "ACK";
    public static final String KEY_BYTE_ARRAY = "BA";
    public static final String KEY_STRING_ARRAY = "SA";
    public static final String KEY_STRING = "S";


    public static int sendCommand(String prefix, Messenger sendTo, Messenger replyTo, int cmd, int arg, Bundle bundle) {
        if (sendTo == null || replyTo == null)
            return 0;

        Message message = Message.obtain(null, cmd, arg, ++(Bracelet.mCommendIndex));
        message.replyTo = replyTo;
        message.setData(bundle);

        dump(prefix, message);
        try {
            sendTo.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return message.arg2;
    }

    public static int sendCommand(String prefix, Messenger sendTo, Messenger replyTo, int cmd, int arg) {
        return sendCommand(prefix, sendTo, replyTo, cmd, arg, new Bundle());
    }

    public static int sendCommand(String prefix, Messenger sendTo, Messenger replyTo, int cmd, int arg, String data) {
        Bundle bundle = new Bundle();
        LogUtil.getLogger().d(data);
        bundle.putString(KEY_STRING, data);

        return sendCommand(prefix, sendTo, replyTo, cmd, arg, bundle);
    }

    public static int sendCommand(String prefix, Messenger sendTo, Messenger replyTo, int cmd, int arg, byte[] data) {
        Bundle bundle = new Bundle();
        bundle.putByteArray(KEY_BYTE_ARRAY, data);

        return sendCommand(prefix, sendTo, replyTo, cmd, arg, bundle);
    }

    public static int sendCommand(String prefix, Messenger sendTo, Messenger replyTo, int cmd, int arg, ArrayList<String> data) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(KEY_STRING_ARRAY, data);

        return sendCommand(prefix, sendTo, replyTo, cmd, arg, bundle);
    }

    public static int sendACK(String prefix, Message message, boolean result, Bundle bundle) {
        if (message.replyTo == null)
            return 0;

        Message ack = Message.obtain(null, CMD_ACK, message.what, message.arg2);
        bundle.putBoolean(KEY_ACK, result);
        ack.setData(bundle);

        dump(prefix, ack);
        try {
            message.replyTo.send(ack);
        } catch (RemoteException e) {
        }

        return message.arg2;
    }

    public static int sendACK(String prefix, Message message, boolean result) {
        return sendACK(prefix, message, result, new Bundle());
    }

    public static int sendACK(String prefix, Message message, boolean result, String data) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_STRING, data);

        return sendACK(prefix, message, result, bundle);
    }

    public static int sendACK(String prefix, Message message, boolean result, byte[] data) {
        Bundle bundle = new Bundle();
        bundle.putByteArray(KEY_BYTE_ARRAY, data);

        return sendACK(prefix, message, result, bundle);
    }

    public static int sendACK(String prefix, Message message, boolean result, ArrayList<String> data) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(KEY_STRING_ARRAY, data);

        return sendACK(prefix, message, result, bundle);
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String ToDataString(byte[] data) {
        char[] hexChars = new char[data.length * 3];
        for (int j = 0; j < data.length; j++) {
            int v = data[j] & 0xFF;
            hexChars[j * 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 1] = hexArray[v & 0x0F];
            hexChars[j * 3 + 2] = ' ';
        }

        return new String(hexChars);
    }

    public static void dump(String prefix, Message message) {
        String logString = "";

        Bundle bundle = message.getData();
        if (bundle == null) {
            LogUtil.getLogger().d("Illegal Message!");
            return;
        }


        if (message.what != CMD_ACK)
            logString = "MSG:" + message.what + ", ARG:" + message.arg1 + ",IDX:" + message.arg2;
        else
            logString = "ACK:" + message.arg1 + ", IDX:" + message.arg2 + ", RES:" + bundle.getBoolean(KEY_ACK);

        String dataString = bundle.getString(KEY_STRING);
        if (dataString != null)
            logString += "::" + dataString;

        byte[] dataByteArray = bundle.getByteArray(KEY_BYTE_ARRAY);
        if (dataByteArray != null)
            logString += "::" + ToDataString(dataByteArray);

        ArrayList<String> dataStringArray = bundle.getStringArrayList(KEY_STRING_ARRAY);
        if (dataStringArray != null) {
            for (int idx = 0; idx < dataStringArray.size(); idx++)
                logString += "::(" + idx + ")" + dataStringArray.get(idx);
        }

        LogUtil.getLogger().d(logString);
    }

}
