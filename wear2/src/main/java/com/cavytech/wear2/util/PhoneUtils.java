package com.cavytech.wear2.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.basecore.widget.CustomProgressDialog;
import com.cavytech.wear2.R;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by hf on 2016/4/28.
 * 获取通讯录
 */
public class PhoneUtils {

    protected static final String PREFS_FILE = "gank_device_id.xml";
    protected static final String PREFS_DEVICE_ID = "gank_device_id";
    protected static String uuid;
    private static CustomProgressDialog mProgressDialog;

    public static ArrayList<HashMap<String, String>> getPeopleInPhone2(Context context, boolean isReplaceSpace) {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);  // 获取手机联系人
        while (cursor.moveToNext()) {
            HashMap<String, String> map = new HashMap<String, String>();
            int indexPeopleName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);  // people name
            int indexPhoneNum = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);    // phone number
            String strPeopleName = cursor.getString(indexPeopleName);
            String strPhoneNum = cursor.getString(indexPhoneNum);
            map.put("peopleName", strPeopleName);
            if(isReplaceSpace){
                strPhoneNum.replace(" ", "");
            }
            map.put("phoneNum", strPhoneNum);
            list.add(map);
        }
        if (!cursor.isClosed()) {
            cursor.close();
            cursor = null;
        }
        return list;
    }

    static public String getUDID(Context context)
    {
        if( uuid ==null ) {
            synchronized (context) {
                if( uuid == null) {
                    final SharedPreferences prefs = context.getSharedPreferences( PREFS_FILE, 0);
                    final String id = prefs.getString(PREFS_DEVICE_ID, null );
                    if (id != null) {
                        uuid = id;
                    } else {

                        final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

                        try {
                            if (!"9774d56d682e549c".equals(androidId)) {
                                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8")).toString();
                            } else {
                                final String deviceId = ((TelephonyManager) context.getSystemService( Context.TELEPHONY_SERVICE )).getDeviceId();
                                uuid = deviceId!=null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")).toString() : UUID.randomUUID().toString();
                            }
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        prefs.edit().putString(PREFS_DEVICE_ID, uuid).commit();
                    }
                }
            }
        }
        return uuid;
    }

    public static ProgressDialog showProgressDialog(Context context) {
        LogUtil.getLogger().d("showProgressDialog");
        if (mProgressDialog == null) {
            CustomProgressDialog dialog = new CustomProgressDialog(context, R.style.dialog);
            dialog.setMessage("loading_data_please_wait");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            mProgressDialog = dialog;
        }
        try {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            mProgressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mProgressDialog;
    }

    public static void dismissProgressDialog() {
        try {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            mProgressDialog = null;
        } catch (Exception e) {
            // We don't mind. android cleared it for us.
            mProgressDialog = null;
        }
    }
}
