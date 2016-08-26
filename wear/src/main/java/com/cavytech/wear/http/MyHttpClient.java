package com.cavytech.wear.http;

import android.content.Context;

import com.basecore.util.netstate.NetWorkUtil;
import com.basecore.widget.CustomToast;
import com.cavytech.wear.util.Constants;
import com.cavytech.wear.util.LogUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

public class MyHttpClient {
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(Context context, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        if (NetWorkUtil.isNetworkConnected(context)) {
            if (NetWorkUtil.isNetworkAvailable(context)) {
                client.setURLEncodingEnabled(false);
                PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
                client.setCookieStore(myCookieStore);
                client.get(context, Constants.URL, params, responseHandler);
            }
        }
    }

    public static void get(Context context, String url, AsyncHttpResponseHandler responseHandler) {
        if (NetWorkUtil.isNetworkConnected(context)) {
            if (NetWorkUtil.isNetworkAvailable(context)) {
                client.setURLEncodingEnabled(false);
                PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
                client.setCookieStore(myCookieStore);
                client.get(context, Constants.URL + url, responseHandler);
            }
        }
    }

    public static void get(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        if (NetWorkUtil.isNetworkConnected(context)) {
            if (NetWorkUtil.isNetworkAvailable(context)) {
                client.setURLEncodingEnabled(false);
                PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
                client.setCookieStore(myCookieStore);
                client.get(context, Constants.URL + url, params, responseHandler);
            }
        }
    }

    public static void post(Context context, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        if (NetWorkUtil.isNetworkConnected(context)) {
            if (NetWorkUtil.isNetworkAvailable(context)) {
                client.post(context, Constants.URL, params, responseHandler);
            }
        }
    }

    public static void post(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        LogUtil.getLogger().d(Constants.URL + url);
        if (NetWorkUtil.isNetworkConnected(context)) {
            if (NetWorkUtil.isNetworkAvailable(context)) {
                PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
                client.setCookieStore(myCookieStore);
                client.post(context, Constants.URL + url, params, responseHandler);
            }
        }
    }


}
