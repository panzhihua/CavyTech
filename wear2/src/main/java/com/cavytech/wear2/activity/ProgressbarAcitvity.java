package com.cavytech.wear2.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cavytech.wear2.R;
import com.cavytech.wear2.cavylifeband.InterfaceOfBLECallback;
import com.cavytech.wear2.cavylifeband.LifeBandBLE;
import com.cavytech.wear2.cavylifeband.PedometerData;
import com.cavytech.wear2.util.Constants;
import com.cavytech.wear2.util.FileUtils;
import com.cavytech.wear2.util.LifeBandBLEUtil;
import com.cavytech.widget.CustomDialog;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.HashMap;

public class ProgressbarAcitvity extends AppCompatActivity implements InterfaceOfBLECallback {

    private ProgressBar progressBar;
    private TextView tv_progress;

    private String howShow;

    private int success = 0000;

    private int error=0001;

    private int gujian_end= 2;

    private int gujian_pro= 3;

    private int app = 1;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case 1:
                    int progress = msg.arg2;
                    tv_progress.setText("App正在升级" + progress + "%");
                    break;
                case 2:
                    if((int)msg.arg1==success){
                        CustomDialog.Builder builder = new CustomDialog.Builder(ProgressbarAcitvity.this);
                        builder.setMessage("手环将在重启后自动连接,请稍等");
                        builder.setTitle("固件升级成功");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int which) {
                                dialog.dismiss();
                                startActivity(new Intent(ProgressbarAcitvity.this,HomePager.class));
                                finish();
                            }
                        });
                        builder.create().show();
                    }else {
                        CustomDialog.Builder builder = new CustomDialog.Builder(ProgressbarAcitvity.this);
                        builder.setMessage("请确保手环电量充足，将手环放在手机附近，");
                        builder.setTitle("固件升级失败");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int which) {
                                dialog.dismiss();
                                startActivity(new Intent(ProgressbarAcitvity.this,HomePager.class));
                                finish();
                            }
                        });
                        builder.create().show();
                    }
                    break;

                case 3:
                    int pro =msg.arg2;
                    Log.e("TAG","测试固件进度---"+pro);
                    tv_progress.setText("手环正在升级" + pro + "%");
                    break;

            }

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progressbar_acitvity);

        howShow = getIntent().getStringExtra("howShow");

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        tv_progress = (TextView) findViewById(R.id.tv_progress);

        if(howShow.equals(Constants.GUJIAN)){

            getFromewareVersionFromNet();
//            updateVersion();
            Log.e("TAG","GUJIAN");
        }

        if(howShow.equals(Constants.APP)){

            getAppFromNet();
//            installApk();

            Log.e("TAG","APP");
        }

    }

    /**
     * 从本地获取固件并升级
     */
    private void updateVersion() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        // in onResume() call
        mWakeLock.acquire();
        // in onPause() call
        mWakeLock.release();

        LifeBandBLEUtil.getInstance().setCallBack(ProgressbarAcitvity.this);
//        Log.e("TAG","固件地址----"+FileUtils.getFilePath() + "gujian.bin");
        LifeBandBLEUtil.getInstance().UpdateFirmwareByFilePath(FileUtils.getFilePath() + "gujian.bin");
    }

    /**
     * 从网络获取固件升级
     * 1.0.5固件版本号
     */
    private void getFromewareVersionFromNet() {

        String gujianurl = getIntent().getStringExtra(Constants.GUJIANURL);

        Log.e("TAG","固件下载地址--"+gujianurl);
        OkHttpUtils
                .get()
                .url(gujianurl)
                .build()
                .connTimeOut(5000000)
                .readTimeOut(5000000)
                .writeTimeOut(5000000)
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Wear"+ File.separator, "gujian.bin")
                {
                    @Override
                    public void inProgress(float progress)
                    {
                        Log.e("TAG","F---inProgress---"+progress);
                    }

                    @Override
                    public void onError(Request request, Exception e)
                    {
                        Log.e("TAG", "onError :" + e.getMessage());
                        Toast.makeText(ProgressbarAcitvity.this,"下载失败",Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onResponse(File file)
                    {
                        Log.e("TAG", "onResponse :" + file.getAbsolutePath());
//                        installApk();
                        updateVersion();
                    }
                });

    }

    /**
     * 从网络获取APP升级
     */
    private void getAppFromNet() {

        final int appsize = getIntent().getIntExtra(Constants.APPSIZE, 0);

        Log.e("TAG","传过来的最大值---=="+appsize);

        String appurl = getIntent().getStringExtra(Constants.APPURL);

        Log.e("TAG","app下载地址--"+appurl);

        OkHttpUtils
                .get()
                .url(appurl)
                .build()
                .connTimeOut(5000000)
                .readTimeOut(5000000)
                .writeTimeOut(5000000)
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "gson1.apk")
                {
                    @Override
                    public void inProgress(float progress)
                    {
                        /**
                         * 返回的progress是负的
                         */
                        progressBar.setProgress((int) (progress*100));
//                        tv_progress.setText("App正在升级" + (int)progress*100 + "%");
                        Message m = new Message();
                        m.what=app;
                        m.arg2= (int) (progress*100);
                        handler.sendMessage(m);

//                        Log.e("TAG","A--inProgress---====1="+progress);
//                        Log.e("TAG","A--inProgress---====2="+appsize);
//                        Log.e("TAG","A--inProgress---====3="+(int)progress*100/appsize);
//                        Log.e("TAG","A--inProgress---====4="+(int)-progress*100/appsize);

                        if(progress*100 == 100){
                           finish();
                        }

                    }

                    @Override
                    public void onError(Request request, Exception e)
                    {
                        Log.e("TAG", "onError :" + e.getMessage());
                    }

                    @Override
                    public void onResponse(File file)
                    {
                        Log.e("TAG", "onResponse :" + file.getAbsolutePath());
                        installApk();
                    }
                });



/*        Log.e("token", CacheUtils.getString(ProgressbarAcitvity.this,Constants.TOKEN));

        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        //创建一个Request
        final Request request = new Request.Builder()
                .url(Constants.APPUPDateUrl)
                .header(HttpUtils.AUTH_TOKEN, CacheUtils.getString(ProgressbarAcitvity.this,Constants.TOKEN))
                .build();
        //new call
        Call call = mOkHttpClient.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("TAG",e +"");

            }

            @Override
            public void onResponse(final Response response) throws IOException {

                Log.e("TAG",response.code() +"");
            }
        });*/
    }

    protected void installApk() {
        Log.e("TAG","installApk----");
//        File apkfile = new File("/storage/emulated/0/gson1.apk");
//        if (!apkfile.exists()) {
//            return;
//        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.fromFile(new File("/storage/emulated/0/gson1.apk")),
                "application/vnd.android.package-archive");// File.toString()会返回路径信息
        this.startActivity(i);
    }

    @Override
    public void onButtonClicked() {

    }

    @Override
    public void onButtonLongPressed(byte[] data) {

    }

    @Override
    public void onBatteryData(int status) {

    }

    @Override
    public void onWarningData() {

    }

    @Override
    public void onSystemData(LifeBandBLE.CavyBandSystemData data) {

    }

    @Override
    public void onDataSync(HashMap<Integer, PedometerData> TodayData, HashMap<Integer, PedometerData> YesterdayData) {

    }

    @Override
    public void onDeviceSignature(byte[] data) {

    }

    @Override
    public void BLEConnectionEvents(int eventCode, LifeBandBLE.CavyBandDevice device) {

    }

    @Override
    public void onOADStatusChanged(int statusCode, float progressRate) {
        switch (statusCode) {

            case LifeBandBLE.OAD_END:
                Log.e("TAG", "------------OAD Finished!!");
                Message m = new Message();
                m.what=gujian_end;
                m.arg1= success;
                handler.sendMessage(m);
                break;
            case LifeBandBLE.OAD_START:
                Log.e("TAG", "---------------OAD Start!!");
                tv_progress.setText("手环正在升级");
                break;
            case LifeBandBLE.OAD_UPDATING:
                Log.e("TAG", "----------------OAD Updating, progress rate: " + (int)progressRate);
                Message m2 = new Message();
                m2.what=gujian_pro;
                m2.arg2 =(int)progressRate;
                handler.sendMessage(m2);
                int progress = (int) progressRate;
                progressBar.setProgress(progress);
//                tv_progress.setText("手环正在升级" + progress + "%");
                break;

            default:

                break;
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }
}
