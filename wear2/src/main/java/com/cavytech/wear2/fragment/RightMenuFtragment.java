package com.cavytech.wear2.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cavytech.wear2.R;
import com.cavytech.wear2.activity.BandConnectActivity;
import com.cavytech.wear2.activity.LoginActivity;
import com.cavytech.wear2.activity.ProgressbarAcitvity;
import com.cavytech.wear2.activity.SecurityActivity;
import com.cavytech.wear2.application.CommonApplication;
import com.cavytech.wear2.base.BaseFragment;
import com.cavytech.wear2.cavylifeband.LifeBandBLE;
import com.cavytech.wear2.entity.BandInfoEntity;
import com.cavytech.wear2.entity.CheckVersionBean;
import com.cavytech.wear2.entity.CommonEntity;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.RequestCallback;
import com.cavytech.wear2.slidingmenu.KnowClockActivity;
import com.cavytech.wear2.slidingmenu.NoticeActivity;
import com.cavytech.wear2.util.CacheUtils;
import com.cavytech.wear2.util.Constants;
import com.cavytech.wear2.util.DateHelper;
import com.cavytech.wear2.util.LifeBandBLEUtil;
import com.cavytech.wear2.util.PhoneUtils;
import com.cavytech.wear2.util.SerializeUtils;
import com.cavytech.widget.CustomDialog;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;
import com.squareup.okhttp.Request;
import com.umeng.analytics.MobclickAgent;

import net.sourceforge.opencamera.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;

/**
 * Created by libin on 4/14 0014.
 * 邮箱：bin.li@tunshu.com
 */
public class RightMenuFtragment extends BaseFragment {

    @ViewInject(R.id.tv_rom_name)
    private TextView tv_rom_name;

    @ViewInject(R.id.textView3)
    private TextView textView3;

    @ViewInject(R.id.iv_no_band_icon)
    private ImageView iv_no_band_icon;

    @ViewInject(R.id.tv_electricityProgress)
    private TextView tv_electricityProgress;

    @ViewInject(R.id.circularFillableLoaders)
    private CircularFillableLoaders circularFillableLoaders;

    @ViewInject(R.id.tv_rom_number)
    private TextView tv_rom_number;

    @ViewInject(R.id.ll_comtral_camera)
    private LinearLayout ll_comtral_camera;

    @ViewInject(R.id.ll_band_notice)
    private LinearLayout ll_band_notice;

    @ViewInject(R.id.ll_know_clock)
    private LinearLayout ll_know_clock;

    @ViewInject(R.id.ll_safe)
    private LinearLayout ll_safe;

    @ViewInject(R.id.ll_rom_update)
    private LinearLayout ll_rom_update;

    @ViewInject(R.id.tv_right_carmer)
    private TextView tv_right_carmer;

    @ViewInject(R.id.tv_right_tixing)
    private TextView tv_right_tixing;

    @ViewInject(R.id.tv_notice)
    private TextView tv_notice;

    @ViewInject(R.id.tv_right_sercity)
    private TextView tv_right_sercity;

    @ViewInject(R.id.tv_right_gujian)
    private TextView tv_right_gujian;


    @ViewInject(R.id.iv_right_carmer)
    private ImageView iv_right_carmer;

    @ViewInject(R.id.iv_right_tixing)
    private ImageView iv_right_tixing;

    @ViewInject(R.id.iv_right_notice)
    private ImageView iv_right_notice;

    @ViewInject(R.id.iv_right_sercity)
    private ImageView iv_right_sercity;

    @ViewInject(R.id.iv_fw)
    private ImageView iv_fw;


    @ViewInject(R.id.ll_new_band_cavy)
    private LinearLayout ll_new_band_cavy;

    private UpdateReceiver updateReceiver = new UpdateReceiver();

    private class UpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            updateBandInfo();
        }
    }

    @Override
    public View initView() {

        View view = View.inflate(mActivity, R.layout.right_menu, null);

        x.view().inject(this, view);

        setOnClickListener();

        noConnection();

        getActivity().registerReceiver(updateReceiver, new IntentFilter(Constants.UPDATE_BANDINFO_RECEIVER));

        getBandStudsReceiver();

        getFwVision();

        registerReceiver();

        setIcon();

        return view;
    }

    private void registerReceiver() {

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getContext());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ISCONNECTIONBAND);//建议把它写一个公共的变量，这里方便阅读就不写了。
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (LifeBandBLEUtil.getInstance().getConnectionState() == 2) {
                    textView3.setText(context.getString(R.string.cavy_band_3));
                    updateBandInfo();
                    iv_no_band_icon.setVisibility(View.GONE);
                }else {
                    iv_no_band_icon.setVisibility(View.VISIBLE);
                    textView3.setText(context.getString(R.string.disconnected));
                    tv_rom_name.setText(context.getString(R.string.turn_Bluetooth_pres_CavyBand));
                    tv_rom_number.setText(context.getString(R.string.to_connect));
                    ifconn();

                    reStartScan();
                }
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }

    /**
     * 是否连接到手环
     */
    private void setIcon() {

        int connectionCode = CacheUtils.getInt(getContext(), Constants.ISCONNECTIONBAND);

        //if (connectionCode == 2 || connectionCode == 1) {
        if (connectionCode == 2 ) {
            textView3.setText(this.getString(R.string.cavy_band_3));
            updateBandInfo();
            iv_no_band_icon.setVisibility(View.GONE);

            HashMap<String,String> map = new HashMap<String,String>();
            map.put("device_serial", PhoneUtils.getUDID(getActivity()));
            map.put("device_model",android.os.Build.MODEL+"-"+Build.VERSION.SDK_INT);
            map.put("band_mac",CacheUtils.getMacAdress(getActivity()));
            map.put("user_id", CommonApplication.userID);
            map.put("longitude",CacheUtils.getString(getActivity(), Constants.LONGITUDE));
            map.put("latitude",CacheUtils.getString(getActivity(), Constants.LATITUDE));

            MobclickAgent.onEvent(getActivity(), Constants.BAND_CONNECT,map);

            HttpUtils.getInstance().activities(getActivity(),CacheUtils.getString(getActivity(), Constants.LONGITUDE),
                        CacheUtils.getString(getActivity(), Constants.LATITUDE), CacheUtils.getMacAdress(getActivity()),""+ Build.VERSION.SDK_INT, "BAND_CONNECT",new RequestCallback<CommonEntity>(){

                            @Override
                            public void onError(Request request, Exception e) {
//                                Log.e("TAG","活动上传失败--BAND_CONNECT--"+e.getLocalizedMessage()+e.getMessage().toString());
                            }

                            @Override
                            public void onResponse(CommonEntity response) {
//                                Log.e("TAG","活动上传成功--BAND_CONNECT--"+response.getCode());
                            }
                        });


        } else {
            iv_no_band_icon.setVisibility(View.VISIBLE);
            textView3.setText(this.getString(R.string.disconnected));
            tv_rom_name.setText(this.getString(R.string.turn_Bluetooth_pres_CavyBand));
            tv_rom_number.setText(this.getString(R.string.to_connect));
            ifconn();

            reStartScan();
        }
    }

    private void ifconn() {
        tv_right_carmer.setTextColor(Color.parseColor("#91e4c2"));
        tv_right_tixing.setTextColor(Color.parseColor("#91e4c2"));
        tv_notice.setTextColor(Color.parseColor("#91e4c2"));
        tv_right_sercity.setTextColor(Color.parseColor("#91e4c2"));
        tv_right_gujian.setTextColor(Color.parseColor("#91e4c2"));

        iv_right_carmer.setImageResource(R.drawable.icon_camera_disable);
        iv_right_tixing.setImageResource(R.drawable.icon_bandnotice_disable);
        iv_right_notice.setImageResource(R.drawable.icon_clock_disable);
        iv_right_sercity.setImageResource(R.drawable.icon_safe_disable);

        ll_comtral_camera.setClickable(false);
        ll_band_notice.setClickable(false);
        ll_know_clock.setClickable(false);
        ll_safe.setClickable(false);
        ll_rom_update.setClickable(false);

        iv_fw.setVisibility(View.GONE);

    }

    private void reStartScan() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                LifeBandBLEUtil.getInstance().StopScanCavyBand();
                if (LifeBandBLEUtil.getInstance().getConnectionState() == LifeBandBLE.STATE_CONNECTING ||
                        LifeBandBLEUtil.getInstance().getConnectionState() == LifeBandBLE.STATE_CONNECTED
                        ) {
                    return;
                }
                LifeBandBLEUtil.getInstance().StartScanCavyBand();
                SetSystemTime();
                reStartScan();
            }
        }, 10000);
    }

    public void SetSystemTime() {

        LifeBandBLEUtil.getInstance().SetSystemTime();
    }

    public void noConnection() {
        iv_no_band_icon.setVisibility(View.VISIBLE);
        textView3.setText(this.getString(R.string.disconnected));
        tv_rom_name.setText(this.getString(R.string.turn_Bluetooth_pres_CavyBand));
        tv_rom_number.setText(this.getString(R.string.to_connect));
        ifconn();

    }

    /**
     * 手环没连接时候的设置
     *
     * @param eventCode
     */
    public void NoBandSetRightFragment(int eventCode) {
//        if(eventCode != 2 ){
//
//            iv_no_band_icon.setVisibility(View.VISIBLE);
//            textView3.setText("手环未连接");
//            tv_rom_name.setText("开启蓝牙，按下手环");
//            tv_rom_number.setText("按钮自动连接");
//
//        }else{
//            textView3.setText("我的CavyBand");
//            updateBandInfo();
//            iv_no_band_icon.setVisibility(View.GONE);
//        }
    }

    /*private void setTitleMessage() {
        int anInt = CacheUtils.getInt(mActivity, Constants.FWVISION);

        tv_rom_number.setText("固件版本:" + anInt);

        int anInt1 = CacheUtils.getInt(mActivity, Constants.STATUS);

        circularFillableLoaders.setProgress(100 - anInt1);
        tv_electricityProgress.setText(anInt1 + "%");


    }*/

    /**
     * fw版本号
     */
    private void getFwVision() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.DATA_FWVERSION);//建议把它写一个公共的变量，这里方便阅读就不写了。
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int fwVersion = intent.getIntExtra("fwVersion", 0);
                int hwVersion = intent.getIntExtra("hwVersion", 0);
                CacheUtils.putInt(mActivity, Constants.FWVISION, fwVersion);
                CacheUtils.putInt(mActivity, Constants.HWVISION, hwVersion);
                tv_rom_number.setText(context.getString(R.string.firmware_version) + hwVersion+"."+fwVersion);
                updateBandInfo();
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }

    /**
     * 手环电量
     */
    private void getBandStudsReceiver() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.cavytech.wear2.service.StatusReceiver");//建议把它写一个公共的变量，这里方便阅读就不写了。
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int bandStatus = intent.getIntExtra("status", 0);
                CacheUtils.putInt(mActivity, Constants.STATUS, bandStatus);
                circularFillableLoaders.setProgress(100 - bandStatus);
                tv_electricityProgress.setText(bandStatus + "%");
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {

        getActivity().unregisterReceiver(updateReceiver);
        super.onDestroy();
    }

    private void updateBandInfo() {
        BandInfoEntity bandInfo = (BandInfoEntity) SerializeUtils.unserialize(Constants.SERIALIZE_BAND_INFO);
        int anInt = CacheUtils.getInt(mActivity, Constants.FWVISION);
        int anIn = CacheUtils.getInt(mActivity, Constants.HWVISION);
        int anInt1 = CacheUtils.getInt(mActivity, Constants.STATUS);

        if (bandInfo != null) {
            tv_rom_name.setText(bandInfo.getName());
        }
        iv_no_band_icon.setVisibility(View.GONE);
        try {
            textView3.setText(mActivity.getString(R.string.cavy_band_3));
            if (anInt == -1 && anInt1 == -1) {
                anInt = 0;
                anInt1 = 100;
            }
            tv_rom_number.setText(mActivity.getString(R.string.firmware_version) + anIn + "." + anInt);
        }catch(Exception e){
            e.printStackTrace();
        }
        circularFillableLoaders.setProgress(100 - anInt1);
        tv_electricityProgress.setText(anInt1 + "%");
        tv_right_carmer.setTextColor(Color.WHITE);
        tv_right_tixing.setTextColor(Color.WHITE);
        tv_notice.setTextColor(Color.WHITE);
        tv_right_sercity.setTextColor(Color.WHITE);
        tv_right_gujian.setTextColor(Color.WHITE);

        iv_right_carmer.setImageResource(R.drawable.icon_camera);
        iv_right_tixing.setImageResource(R.drawable.icon_bandnotice);
        iv_right_notice.setImageResource(R.drawable.icon_clock);
        iv_right_sercity.setImageResource(R.drawable.icon_safe);

        ll_comtral_camera.setClickable(true);
        ll_band_notice.setClickable(true);
        ll_know_clock.setClickable(true);
        ll_safe.setClickable(true);
        ll_rom_update.setClickable(true);

        iv_fw.setVisibility(View.GONE);
    }

    @Override
    public void initData() {
        super.initData();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 侧滑点击事件
     */
    private void setOnClickListener() {
        ll_comtral_camera.setOnClickListener(new MyRightFragmentOnClickListener());
        ll_band_notice.setOnClickListener(new MyRightFragmentOnClickListener());
        ll_know_clock.setOnClickListener(new MyRightFragmentOnClickListener());
        ll_safe.setOnClickListener(new MyRightFragmentOnClickListener());
        ll_rom_update.setOnClickListener(new MyRightFragmentOnClickListener());
        ll_new_band_cavy.setOnClickListener(new MyRightFragmentOnClickListener());
    }

    class MyRightFragmentOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v == ll_comtral_camera) {//遥控相机

                startActivity(new Intent(getActivity(), MainActivity.class));

            } else if (v == ll_know_clock) {//手环闹钟
                startActivity(new Intent(getActivity(), KnowClockActivity.class));
                //Toast.makeText(getActivity(),"ll_know_clock",Toast.LENGTH_SHORT).show();
            } else if (v == ll_band_notice) {//来电提醒
                startActivity(new Intent(getActivity(), NoticeActivity.class));
                // Toast.makeText(getActivity(),"ll_band_notice",Toast.LENGTH_SHORT).show();
            } else if (v == ll_safe) {//紧急求救
                //Toast.makeText(getActivity(), "ll_safe", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), SecurityActivity.class));
            } else if (v == ll_rom_update) {//固件升级
                Toast.makeText(getActivity(), R.string.detecting, Toast.LENGTH_SHORT).show();
                HttpUtils.getInstance().getGuJianVersion(mActivity, new RequestCallback<CheckVersionBean>() {
                    @Override
                    public void onError(Request request, Exception e) {

                        try {
                            JSONObject jsonObj = new JSONObject(e.getLocalizedMessage());
                            int code = jsonObj.optInt("code");
                            if(HttpUtils.CODE_ACCOUNT_NOT_LOGIN==code){
                                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                                dialog.setCancelable(false);
                                dialog.setMessage(getString(R.string.not_login));
                                dialog.setPositiveButton(getString(R.string.ALERT_DLG_BTN_OK), new android.content.DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        arg0.dismiss();
                                        startActivity(new Intent(getActivity(),LoginActivity.class));
//                                        finish();
                                    }
                                });
                                dialog.setNeutralButton(getString(R.string.ALERT_DLG_BTN_CANCEL), new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        arg0.dismiss();
                                    }
                                } );
                                dialog.show();
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        } catch (Exception e2){
                            e2.printStackTrace();
                        }

                    }

                    @Override
                    public void onResponse(final CheckVersionBean response) {

                        Log.e("TAG", "固件升级-----");
                        /**
                         * 固件是否更新----此处需要更改
                         */

                        int hwversion = CacheUtils.getInt(getContext(), Constants.HWVERSION);
                        int fwversion = CacheUtils.getInt(getContext(), Constants.FWVERSION);

                        String hw = hwversion + "";
                        String fw = fwversion + "";

                        String now = hw + "." + fw;

                        boolean b = DateHelper.getInstance().CompareVersion(now, response.getData().getVersion());
                        if (b) {
                            CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
                            builder.setMessage(getString(R.string.current_version));
                            builder.setTitle(getString(R.string.install_new_version_firmware));
                            builder.setMessage(response.getData().getDescription());
                            builder.setPositiveButton(getString(R.string.queding), new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //设置你的操作事项
                                    Intent intent = new Intent(getActivity(), ProgressbarAcitvity.class);
                                    intent.putExtra(Constants.HOWSHOW, Constants.GUJIAN);
                                    intent.putExtra(Constants.GUJIANURL, response.getData().getUrl());

                                    int status = CacheUtils.getInt(getActivity(), Constants.STATUS);

                                    if(status <= 20){
                                        dialog.dismiss();

                                        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
                                        builder.setTitle(getString(R.string.insufficient_battery_power_CavyBand));
                                        builder.setMessage(getString(R.string.insufficient_battery_power_CavyBand_please));
                                        builder.setPositiveButton(getString(R.string.try_again_after_charging), new DialogInterface.OnClickListener() {
                                            public void onClick(final DialogInterface dialog, int which) {
                                                dialog.dismiss();

                                            }
                                        });

                                        builder.create().show();
                                    }else {
                                        startActivity(new Intent(intent));
                                    }

                                }
                            });

                            builder.setNegativeButton(getString(R.string.cancel),
                                    new android.content.DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                            builder.create().show();
                        } else {
                            Toast.makeText(getContext(), getString(R.string.current_firmware_is_the_latest_version), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                //LifeBandBLEUtil.getInstance().UpdateFirmwareByFilePath(FileUtils.getFilePath()+"Cavy2H23F15D3.bin");
            } else if (v == ll_new_band_cavy) {//绑定新手环
                AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
                dialog.setCancelable(false);
                dialog.setMessage(getString(R.string.bind_new_bracelets_bracelets));
                dialog.setPositiveButton(getString(R.string.ALERT_DLG_BTN_OK), new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        LifeBandBLEUtil.getInstance().Disconnect();
                        Intent it = new Intent(getActivity(), BandConnectActivity.class);

                        it.putExtra(Constants.INTENT_EXTRA_RECONNECTDEV, true);
                        startActivity(it);
                    }
                });
                dialog.setNeutralButton(getString(R.string.ALERT_DLG_BTN_CANCEL), new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                } );
                dialog.show();
            }
        }
    }

    public ImageView getIv_fw() {
        return iv_fw;
    }

}
