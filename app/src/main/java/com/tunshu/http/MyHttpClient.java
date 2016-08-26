package com.tunshu.http;

import android.content.Context;

import com.basecore.util.core.ListUtils;
import com.basecore.util.netstate.NetWorkUtil;
import com.basecore.widget.CustomToast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.tunshu.R;
import com.tunshu.util.Constants;
import com.tunshu.util.LogUtil;

public class MyHttpClient {
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(Context context, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        if (NetWorkUtil.isNetworkConnected(context)) {
            if (NetWorkUtil.isNetworkAvailable(context)) {
                client.setURLEncodingEnabled(false);
                PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
                client.setCookieStore(myCookieStore);
                client.get(context, Constants.URL,params, responseHandler);
            } else {
                CustomToast.showToast(context, context.getString(R.string.network_not_available));
            }
        } else {
            CustomToast.showToast(context,  context.getString(R.string.network_connection));
        }
    }

    public static void get(Context context, String url, AsyncHttpResponseHandler responseHandler) {
        if (NetWorkUtil.isNetworkConnected(context)) {
            if (NetWorkUtil.isNetworkAvailable(context)) {
                client.setURLEncodingEnabled(false);
                PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
                client.setCookieStore(myCookieStore);
                client.get(context, Constants.URL + url, responseHandler);
            } else {
                CustomToast.showToast(context, context.getString(R.string.network_not_available));
            }
        } else {
            CustomToast.showToast(context,context.getString(R.string.network_connection));
        }
    }

    public static void get(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        if (NetWorkUtil.isNetworkConnected(context)) {
            if (NetWorkUtil.isNetworkAvailable(context)) {
                client.setURLEncodingEnabled(false);
                PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
                client.setCookieStore(myCookieStore);
                client.get(context, Constants.URL + url, params, responseHandler);
            } else {
                CustomToast.showToast(context, context.getString(R.string.network_not_available));
            }
        } else {
            CustomToast.showToast(context, context.getString(R.string.network_connection));
        }
    }

    public static void post(Context context, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        if (NetWorkUtil.isNetworkConnected(context)) {
            if (NetWorkUtil.isNetworkAvailable(context)) {
                client.post(context, Constants.URL, params, responseHandler);
            } else {
                CustomToast.showToast(context, context.getString(R.string.network_not_available));
            }
        } else {
            CustomToast.showToast(context, context.getString(R.string.network_connection));
        }
    }

    public static void post(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        LogUtil.getLogger().d(Constants.URL + url);
        if (NetWorkUtil.isNetworkConnected(context)) {
            if (NetWorkUtil.isNetworkAvailable(context)) {
                PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
                client.setCookieStore(myCookieStore);
                client.post(context, Constants.URL + url, params, responseHandler);
            } else {
                CustomToast.showToast(context, context.getString(R.string.network_not_available));
            }
        } else {
            CustomToast.showToast(context, context.getString(R.string.network_connection));
        }
    }



}
