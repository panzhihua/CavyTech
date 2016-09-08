package com.cavytech.wear2.activity;


import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.basecore.widget.SystemBarTintManager;
import com.cavytech.wear2.R;
import com.cavytech.wear2.application.CommonApplication;
import com.cavytech.wear2.cavylifeband.InterfaceOfBLECallback;
import com.cavytech.wear2.cavylifeband.LifeBandBLE;
import com.cavytech.wear2.cavylifeband.PedometerData;
import com.cavytech.wear2.entity.BandSleepStepBean;
import com.cavytech.wear2.entity.CheckVersionBean;
import com.cavytech.wear2.entity.CommonEntity;
import com.cavytech.wear2.entity.GPSCityEntity;
import com.cavytech.wear2.entity.GetSleepentity;
import com.cavytech.wear2.entity.GetStepCountBean;
import com.cavytech.wear2.entity.HomeBean;
import com.cavytech.wear2.entity.HomePagerbean;
import com.cavytech.wear2.entity.SleepRetrun;
import com.cavytech.wear2.entity.UserEntity;
import com.cavytech.wear2.entity.WhetherEntity;
import com.cavytech.wear2.fragment.LeftMenuFragment;
import com.cavytech.wear2.fragment.RightMenuFtragment;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.RequestCallback;
import com.cavytech.wear2.util.CacheUtils;
import com.cavytech.wear2.util.CircleTransform;
import com.cavytech.wear2.util.Constants;
import com.cavytech.wear2.util.DateHelper;
import com.cavytech.wear2.util.LifeBandBLEUtil;
import com.cavytech.wear2.util.PhoneUtils;
import com.cavytech.wear2.util.PinYinUtils;
import com.cavytech.wear2.util.SerializeUtils;
import com.cavytech.wear2.util.SleepCount;
import com.cavytech.widget.NoScrollListview;
import com.cavytech.widget.TextPick;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Picasso;
import com.timqi.sectorprogressview.ColorfulRingProgressView;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 首页
 *
 * @author:libin created at 2016/3/25 10:54
 */
public class HomePager extends SlidingFragmentActivity implements TextPick.OnValueChangeListener, InterfaceOfBLECallback {
    private static final String LEFTMENU_TAG = "leftmenu_tag";
    public static final String RIGHT_TAG = "right_tag";
    public static final String PM_25 = "pm25";
    public static final String COND = "cond";
    public static final String WHETHER = "whether";
    private int width;
    private int height;
    private List list = new ArrayList();

    private double latitude;//纬度
    private double longitude;//纬度

    private boolean ifHasWhether = false;

    //第二个页面是否显示
    private boolean secondaryMenuShowing;

    //第一个页面是否显示
    private boolean menuShowing;

    @ViewInject(R.id.walk_click)
    private LinearLayout walk_click;

    @ViewInject(R.id.sleep_click)
    private LinearLayout sleep_click;

    @ViewInject(R.id.crpv_first_weather)
    private ColorfulRingProgressView crpv_first_weather;

    @ViewInject(R.id.crpv_first_walk)
    private ColorfulRingProgressView crpv_first_walk;

    @ViewInject(R.id.iv_first_kongqi_icon)
    private ImageView iv_first_kongqi_icon;

    @ViewInject(R.id.iv_first_kongqi_temperature)
    private TextView iv_first_kongqi_temperature;

    @ViewInject(R.id.tv_youlaignzhongcha)
    private TextView tv_youlaignzhongcha;

    @ViewInject(R.id.tv_step_complete)
    private TextView tv_step_complete;

    @ViewInject(R.id.tv_sleep_percent)
    private TextView tv_sleep_percent;

    @ViewInject(R.id.tv_first_jibu_hour)
    private TextView tv_first_jibu_hour;

    @ViewInject(R.id.tv_first_jibu_hour_list)
    private TextView tv_first_jibu_hour_list;

    @ViewInject(R.id.tv_first_sleep_hour)
    private TextView tv_first_sleep_hour;

    @ViewInject(R.id.tv_first_sleep_minute)
    private TextView tv_first_sleep_minute;

    @ViewInject(R.id.tv_first_sleep_hour_sleep)
    private TextView tv_first_sleep_hour_sleep;

    @ViewInject(R.id.tv_first_sleep_minute_sleep)
    private TextView tv_first_sleep_minute_sleep;

    @ViewInject(R.id.nlv_pk)
    private NoScrollListview nlv_pk;

    @ViewInject(R.id.nlv_healthy)
    private NoScrollListview nlv_healthy;

    @ViewInject(R.id.nlv_achieve)
    private NoScrollListview nlv_achieve;

    @ViewInject(R.id.sliding_shadow)
    private ImageView sliding_shadow;

    @ViewInject(R.id.sliding_menu_switch)
    private ImageView sliding_menu_switch;

    @ViewInject(R.id.sliding_switch_band)
    private ImageView sliding_switch_band;

    @ViewInject(R.id.pull_to_refresh_listview)
    private PullToRefreshListView pull_to_refresh_listview;

    @ViewInject(R.id.date_wheel)
    TextPick dateWheel;

    @ViewInject(R.id.Rl_step1)
    RelativeLayout Rl_step1;

    @ViewInject(R.id.Rl_sleep1)
    RelativeLayout Rl_sleep1;


    DateHelper dateHelper = DateHelper.getInstance();

    BroadcastReceiver mItemViewListClickReceiver;

    /**
     * 成就列表
     */
    private ArrayList<Integer> achieveList;

    /**
     * 健康列表
     */
    private ArrayList<HomePagerbean.HealthListBean> healthList;

    /**
     * Pk列表
     */
    private ArrayList<HomePagerbean.PkListBean> pkList;

    /**
     * data日期
     */
    private String[] dateCount;

    //睡眠目标
    private int stepComplete;

    //计步目标
    private int sleepComplete;

    //注册日期
    private String singUp;

    //当日记步信息
    private int kk;

    //当日睡眠信息
    private int sleeptime;

    public static Boolean isConnectoinNewBand = false;

    private LeftMenuFragment leftMenuFragment;

    private RightMenuFtragment rightMenuFtragment;

//    private List<GetSleepBean.SleepListBean> sleepList;
    //private GetStepCountBean.StepListBean startbean;
    private Handler callHandler = new Handler();

    private Runnable starcCallReminder =new Runnable() {//开始震动
        @Override
        public void run() {
            LifeBandBLEUtil.getInstance().DoVibrate(10, 100, 200, 500);
        }
    };

    private Runnable stopCallReminder =new Runnable() {//停止震动
        @Override
        public void run() {
            LifeBandBLEUtil.getInstance().DoVibrate(0, 100, 200, 500);
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(msg.what == 99){
                String sleepstring = CacheUtils.getString(HomePager.this, Constants.sleep);
                String[] sleepsplit = sleepstring.split(",");
                if (sleepsplit[0].equals(DateHelper.getDateString())) {
                    if (!"".equals(sleepsplit[sleepsplit.length - 1])) {//当日还没有计算，直接计算
                        if (sleepComplete != 0) {
                            int sleepPercent = (int) ((float) Integer.valueOf(sleepsplit[1]) / (float) sleepComplete * 100);
                            if (sleepPercent >= 100) {
                                tv_sleep_percent.setText("完成" + 100 + "%");
                            } else {
                                tv_sleep_percent.setText("完成" + sleepPercent + "%");
                            }
                            Log.e("TAG", "睡眠百分比" + "-----" + sleepPercent);

                            ObjectAnimator animWhether = ObjectAnimator.ofFloat(crpv_first_weather, "percent",
                                    0, sleepPercent);
                            animWhether.setInterpolator(new LinearInterpolator());
                            animWhether.setDuration(1000);
                            animWhether.start();
                        }

                        int hour = sleeptime / 60;
                        int minute = sleeptime % 60;

                        tv_first_sleep_hour.setText(hour + "");
                        tv_first_sleep_minute.setText(minute + "");
                        tv_first_sleep_hour_sleep.setText(hour + "");
                        tv_first_sleep_minute_sleep.setText(minute + "");
                    }
                }
            }



            kk = msg.arg1;
            Log.e("TAG", "计步数---" + kk);
            sleeptime = msg.arg2;

            CacheUtils.putString(HomePager.this, Constants.step, DateHelper.getDateString() + "," + kk);
            CacheUtils.putString(HomePager.this, Constants.sleep, DateHelper.getDateString() + "," + sleeptime);

            tv_first_jibu_hour.setText(kk + "");
            tv_first_jibu_hour_list.setText(kk + "");

            if (stepComplete != 0) {
                int stepPercent = (int) ((float) kk / (float) stepComplete * 100);
                if (stepPercent >= 100) {
                    tv_step_complete.setText("完成" + 100 + "%");
                } else {
                    tv_step_complete.setText("完成" + stepPercent + "%");
                }
                Log.e("TAG", "记步百分比" + "-----" + stepPercent);

                ObjectAnimator animStep = ObjectAnimator.ofFloat(crpv_first_walk, "percent",
                        0, stepPercent);
                animStep.setInterpolator(new LinearInterpolator());
                animStep.setDuration(1000);
                animStep.start();
            }

            if (sleepComplete != 0) {
                int sleepPercent = (int) ((float) sleeptime / (float) sleepComplete * 100);

                if (sleepPercent >= 100) {
                    tv_sleep_percent.setText("完成" + 100 + "%");
                } else {
                    tv_sleep_percent.setText("完成" + sleepPercent + "%");
                }
                Log.e("TAG", "睡眠百分比" + "-----" + sleepPercent);

                ObjectAnimator animWhether = ObjectAnimator.ofFloat(crpv_first_weather, "percent",
                        0, sleepPercent);
                animWhether.setInterpolator(new LinearInterpolator());
                animWhether.setDuration(1000);
                animWhether.start();
            }

            int hour = sleeptime / 60;
            int minute = sleeptime % 60;

            tv_first_sleep_hour.setText(hour + "");
            tv_first_sleep_minute.setText(minute + "");
            tv_first_sleep_hour_sleep.setText(hour + "");
            tv_first_sleep_minute_sleep.setText(minute + "");

            pull_to_refresh_listview.onRefreshComplete();
        }
    };
    private List<BandSleepStepBean> all;
    private AlertDialog.Builder dialog;

    //    private WaitProgress waitProgress;
    private Runnable runnable;
    private int sleep;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.top_bg_color);//通知栏所需颜色
            tintManager.setNavigationBarTintEnabled(true);
            tintManager.setNavigationBarTintResource(R.color.black);
        }
        CommonApplication.isLogin = true;

        setContentView(R.layout.activity_home_pager);
        x.view().inject(this);

        Log.e("TAG", "手机型号---" + android.os.Build.MODEL);

//        waitProgress = WaitProgress.createDialog(this);

        initialize();

        initBandLife();

        initWindowScreen();
        getUserInformation();

/**
 * 计步缓存
 */
        String string = CacheUtils.getString(HomePager.this, Constants.step);
        String[] split = string.split(",");
        if (split[0].equals(DateHelper.getDateString())) {
            if (!"".equals(split[split.length - 1])) {//当日还没有计算，直接计算
                tv_first_jibu_hour.setText(split[1]);
                tv_first_jibu_hour_list.setText(split[1]);
                int anInt = CacheUtils.getInt(HomePager.this, Constants.STEPCOMPLETE);
                if(anInt>0&&anInt!=0){
                    int stepPercent = (int) ((float) Integer.parseInt(split[1]) / (float) anInt * 100);
                    if (stepPercent >= 100) {
                        tv_step_complete.setText("完成" + 100 + "%");
                    } else {
                        tv_step_complete.setText("完成" + stepPercent + "%");
                    }
                    Log.e("TAG", "记步百分比" + "-----" + stepPercent);

                    ObjectAnimator animStep = ObjectAnimator.ofFloat(crpv_first_walk, "percent",
                            0, stepPercent);
                    animStep.setInterpolator(new LinearInterpolator());
                    animStep.setDuration(1000);
                    animStep.start();
                }
            }
        }

        String s = CacheUtils.getString(HomePager.this, Constants.sleep);
        String[] sp = s.split(",");
        if (sp[0].equals(DateHelper.getDateString())) {
            if (!"".equals(sp[sp.length - 1])) {//当日还没有计算，直接计算
                setSleep(Integer.parseInt(sp[1]));
                int anInt = CacheUtils.getInt(HomePager.this, Constants.SLEEPCOMPLETE);
                if(anInt>0&&anInt!=0){
                    int sleepPercent = (int) ((float) Integer.parseInt(sp[1]) / (float) anInt * 100);
                    if (sleepPercent >= 100) {
                        tv_sleep_percent.setText("完成" + 100 + "%");
                    } else {
                        tv_sleep_percent.setText("完成" + sleepPercent + "%");
                    }
                    Log.e("TAG", "睡眠百分比" + "-----" + sleepPercent);

                    ObjectAnimator animStep = ObjectAnimator.ofFloat(crpv_first_weather, "percent",
                            0, sleepPercent);
                    animStep.setInterpolator(new LinearInterpolator());
                    animStep.setDuration(1000);
                    animStep.start();

                    int hour = Integer.valueOf(sp[1])/ 60;
                    int minute = Integer.valueOf(sp[1])% 60;

                    tv_first_sleep_hour.setText(hour + "");
                    tv_first_sleep_minute.setText(minute + "");
                    tv_first_sleep_hour_sleep.setText(hour + "");
                    tv_first_sleep_minute_sleep.setText(minute + "");
                }
            }
        }

        if (CacheUtils.getInt(HomePager.this, PM_25) != -1) {
            iv_first_kongqi_temperature.setText(CacheUtils.getString(HomePager.this, WHETHER) + "℃");
            setWhetherQuality(CacheUtils.getInt(HomePager.this, PM_25));
            setWhetherIcon(CacheUtils.getString(HomePager.this, COND));
        }

        //通过传入的注册日期得到时间滚轮并放到sp里面
        initView();

        initSlidingMenu();

        initStartFragment();

        initFragment();

        getWheelDateCountFromNate();

        getsleepinfo();

        setIcon();

        registerReceiver();

        //监听电话状态
        phoneListener();

        setonClickListener();

//        setSleepAndStep();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getCity();
            }
        }, 2000);

//        setLbs();

        initrefresh();

    }


    private void initialize() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager)
                    getSystemService(Context.BLUETOOTH_SERVICE);

        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
    }

    private void setIcon() {
        int connectionCode = CacheUtils.getInt(HomePager.this, Constants.ISCONNECTIONBAND);

        Log.e("TAG", "connectionCode======" + connectionCode + "");
        if (connectionCode == 2) {
            judgeFWVersion();
        } else {
            sliding_switch_band.setImageResource(R.drawable.nav_band_disable);
        }
    }


    /**
     * 用户手动同步
     */

    private void initrefresh() {

        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    pull_to_refresh_listview.onRefreshComplete();
                    Log.e("TAG", "定时回弹----");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };


        pull_to_refresh_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (LifeBandBLEUtil.getInstance().getConnectionState() == LifeBandBLE.STATE_CONNECTED) {
                    LifeBandBLEUtil.getInstance().DataSync(1, 0);
                    handler.postDelayed(runnable, 12000); //每隔1s执行
                } else {
                    Toast.makeText(getApplicationContext(), "手环未连接", Toast.LENGTH_SHORT).show();
                    pull_to_refresh_listview.onRefreshComplete();
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });

    }

    /**
     * 睡眠数据  从网上得到
     * <p/>
     * 今日之前
     */

    private void getsleepinfo() {

//        boolean network = CacheUtils.isNetworkAvailable(HomePager.this);
//        Log.e("TAG","有网---"+network);

        String start = dateCount[0];
        String end = dateCount[dateCount.length - 1];

        try {
            List<GetSleepentity.DataBean.SleepDataBean> all = CommonApplication.dm.findAll(GetSleepentity.DataBean.SleepDataBean.class);
            if (all != null && all.size() != 0) {
                start = all.get(all.size() - 1).getDate();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        Log.e("TAG", "睡眠---开始--" + start + "--结束---" + end);

        HttpUtils.getInstance().getSleepInfo(start, end, HomePager.this, new RequestCallback<GetSleepentity>() {

            @Override
            public void onError(Request request, Exception e) {
            }

            @Override
            public void onResponse(GetSleepentity response) {
                List<GetSleepentity.DataBean.SleepDataBean> list = response.getData().getSleep_data();
                for (int i = 0; i < list.size(); i++) {
                    GetSleepentity.DataBean.SleepDataBean sb = new GetSleepentity.DataBean.SleepDataBean();
                    sb.setDate(list.get(i).getDate());
                    sb.setTotal_time(list.get(i).getTotal_time());
                    sb.setDeep_time(list.get(i).getDeep_time());
                    try {
                        CommonApplication.dm.saveOrUpdate(sb);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    /**
     * 时间滚轮
     * 通过用户注册信息得到注册日期到当前日期集合
     * YYYY-MM-dd
     */
    private void getDateWheel() {
        List<String> lDate;
        Log.e("TAG", singUp + "-=-=-=-=-=-=-=-=-=zhuce");

        if (singUp == null) {
            lDate = DateHelper.getInstance().findDates(DateHelper.getSystemDateString("yyyy-MM-dd HH:mm:ss"));
        } else {
            lDate = DateHelper.getInstance().findDates(singUp.substring(0, 10));
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lDate.size(); i++) {
            sb.append(lDate.get(i) + ",");
        }
        Log.e("TAG", "测试得到时间滚轮的String---" + sb.toString());

        CacheUtils.putString(HomePager.this, Constants.CESHI, sb.toString());

        dateCount = sb.toString().split(",");

        for (int i = 0; i < dateCount.length; i++) {
            Log.e("TAG", dateCount[i]);
        }


    }
    /**
     * 得到用户信息记步目标和睡眠目标 以及 注册日期 成就
     */
    private void getUserInformation() {
        UserEntity.ProfileEntity unserialize = (UserEntity.ProfileEntity) SerializeUtils.unserialize(Constants.SERIALIZE_USERINFO);
        if (unserialize == null) {
            HttpUtils.getInstance().getUserInfo(HomePager.this, new RequestCallback<UserEntity>() {
                @Override
                public void onError(Request request, Exception e) {

                }

                @Override
                public void onResponse(UserEntity response) {

                    singUp = response.getProfile().getSignupAt();
                    CacheUtils.putString(HomePager.this, Constants.SINGUP, singUp);

                    stepComplete = response.getProfile().getSteps_goal();
                    CacheUtils.putInt(HomePager.this, Constants.STEPCOMPLETE, stepComplete);

                    sleepComplete = response.getProfile().getSleep_time_goal();
                    handler.sendEmptyMessage(99);
                    CacheUtils.putInt(HomePager.this, Constants.SLEEPCOMPLETE, sleepComplete);

                }
            });
        } else {
            singUp = unserialize.getSignupAt();
            CacheUtils.putString(HomePager.this, Constants.SINGUP, singUp);
            stepComplete = unserialize.getSteps_goal();
            CacheUtils.putInt(HomePager.this, Constants.STEPCOMPLETE, stepComplete);
            sleepComplete = unserialize.getSleep_time_goal();
            CacheUtils.putInt(HomePager.this, Constants.SLEEPCOMPLETE, sleepComplete);
        }

    }

    private void registerReceiver() {

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ISCONNECTIONBAND);//建议把它写一个公共的变量，这里方便阅读就不写了。
        mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (LifeBandBLEUtil.getInstance().getConnectionState() == LifeBandBLE.STATE_CONNECTED) {
                    judgeFWVersion();
                } else {
                    sliding_switch_band.setImageResource(R.drawable.nav_band_disable);
                }
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }

    private void unregisterReceiver() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);

        broadcastManager.unregisterReceiver(mItemViewListClickReceiver);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        unregisterReceiver();
    }

    private void initBandLife() {

        LifeBandBLEUtil.getInstance().setCallBack(this);
        if (LifeBandBLEUtil.getInstance().getConnectionState() != LifeBandBLE.STATE_CONNECTED) {

            String macAdress = CacheUtils.getMacAdress(HomePager.this);

            if (null != macAdress && !macAdress.isEmpty()) {
                LifeBandBLEUtil.getInstance().connect(macAdress);
            } else {
//                waitProgress.dismiss();
            }
        } else {
            LifeBandBLEUtil.getInstance().DataSync(1, 0);
        }
    }

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;

    @Override
    protected void onResume() {
        super.onResume();

        LifeBandBLEUtil.getInstance().getBaseSysInfo1();

        getUserInformation();

        LifeBandBLEUtil.getInstance().setCallBack(HomePager.this);
        if (mBluetoothAdapter.isEnabled() == false) {//蓝牙是否开启判断
            Intent _EnableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(_EnableIntent, 1);
            return;
        } else {
            ifConnectionBand();
        }
    }


    /**
     * 设置计步数
     *
     * @param stepTime
     */
    private void setSetp(int stepTime) {

        tv_first_jibu_hour_list.setText(stepTime + "");

    }

    /**
     * 设置睡眠时长
     *
     * @param sleepTime
     */
    private void setSleep(int sleepTime) {
        int hour = sleepTime / 60;
        int minute = sleepTime % 60;


        tv_first_sleep_hour.setText(hour + "");
        tv_first_sleep_hour_sleep.setText(hour + "");
        tv_first_sleep_minute.setText(minute + "");
        tv_first_sleep_minute_sleep.setText(minute + "");


        Log.e("TAg", "hour___" + hour + "__minute___" + minute);

        /*if (pkList.size() == 0 || healthList.size() == 0 || achieveList.size() == 0) {
            iv_first_sleep_line_bottom_sleep_sleep.setVisibility(View.INVISIBLE);
        } else {
            iv_first_sleep_line_bottom_sleep_sleep.setVisibility(View.VISIBLE);
        }*/
    }

    /**
     * 计步网络计算好的  放入数据库
     * <p/>
     * 当日之前数据
     */
    private void getWheelDateCountFromNate() {
/**
 * 从网络增量拿数据，根据数据库表最后一个值确定
 * 此处需要更改
 *
 */
        String start = dateCount[0];
        String end = dateCount[dateCount.length - 1];

        try {
            List<GetStepCountBean.DataBean.StepsDataBean> all = CommonApplication.dm.findAll(GetStepCountBean.DataBean.StepsDataBean.class);
            if (all != null && all.size() != 0) {
                start = all.get(all.size() - 1).getDate();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        Log.e("TAG", "计步---开始--" + start + "--结束---" + end);

        HttpUtils.getInstance().getStepData(start, end, HomePager.this, new RequestCallback<GetStepCountBean>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(GetStepCountBean response) {

                List<GetStepCountBean.DataBean.StepsDataBean> stepsdata = response.getData().getSteps_data();

                if (stepsdata != null) {
                    /**
                     * 按小时存放的数据
                     */
                    for (int i = 0; i < stepsdata.size(); i++) {
                        for (int j = 0; j < 24; j++) {
                            GetStepCountBean.DataBean.StepsDataBean.HoursBean sb = new GetStepCountBean.DataBean.StepsDataBean.HoursBean();
                            sb.setTime(stepsdata.get(i).getDate() + "-" + j);
                            sb.setSteps(stepsdata.get(i).getHours().get(j).getSteps());
                            try {
                                CommonApplication.dm.saveOrUpdate(sb);
                            } catch (DbException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                    /**
                     * 按天存放的数据
                     */
                    for (int i = 0; i < stepsdata.size(); i++) {
                        GetStepCountBean.DataBean.StepsDataBean sb = new GetStepCountBean.DataBean.StepsDataBean();
                        sb.setDate(stepsdata.get(i).getDate());
                        sb.setTotal_steps(stepsdata.get(i).getTotal_steps());
                        sb.setTotal_time(stepsdata.get(i).getTotal_time());
                        try {
                            CommonApplication.dm.saveOrUpdate(sb);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public void intDateWheel() {

        ArrayList whellDateArray = new ArrayList();
        SimpleDateFormat formatter =new SimpleDateFormat("yyyy.MM.dd");
        String date = formatter.format(new java.util.Date());
        for (int i = 0; i < dateCount.length; i++) {

            String subStringData = dateCount[i];

            String replace = subStringData.replace("-", ".");
            if(replace.equals(date)){
                whellDateArray.add("今天");
            }else {
                whellDateArray.add(replace);
            }
        }

        dateWheel.setTextAttrs(16, 12,
                Color.argb(255, 255, 255, 255),
                Color.argb(77, 255, 255, 255),
                false);

        if (dateCount.length > 0) {
            dateWheel.initViewParam(whellDateArray, whellDateArray.size() - 1);
        }
        dateWheel.setValueChangeListener(this);
    }

    /**
     * 设置achieve列表
     */
    private void setAchiceveList() {
        nlv_achieve.setAdapter(new MyAchieveAdapter());

        CacheUtils.putInt(HomePager.this, "achieveSize", achieveSize);
    }

    /**
     * 设置PK列表
     */
    private void setPkList() {
        nlv_pk.setAdapter(new MyPkAdapter());
    }

    /**
     * 设置health列表
     */
    private void setHealthList() {
        nlv_healthy.setAdapter(new MyHealthAdapter());
    }

    /**
     * 获取城市名称
     */
    private void getCity() {

        String url = "http://api.map.baidu.com/geocoder?";
        url += "location=" + CacheUtils.getString(HomePager.this, Constants.LATITUDE) + "," + CacheUtils.getString(HomePager.this, Constants.LONGITUDE);
        url += "&output=" + "json";
        url += "&key=" + "34mQlpiuXAsX1SUUGctFbkGK";
        Log.e("TAG", "查询天气的url---" + url);

        IdentityHashMap<String, String> params = new IdentityHashMap<String, String>();
        params.put("null", "null");  // 需要一个参数，否则会异常

        OkHttpUtils
                .post()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        Log.e("TAG", "天气啊天气---onError--" + e.toString());
                    }

                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG", "天气啊天气---onResponse---" + response.toString());
                        if (response != null && !response.equals("")) {
                            GPSCityEntity gpsCityEntity = new Gson().fromJson(response, GPSCityEntity.class);
                            if(gpsCityEntity!=null){
                                if (gpsCityEntity.getStatus().equals("OK")) {
                                    String city = gpsCityEntity.getResult().getAddressComponent().getCity();
                                    String whetherCity;
                                    String PinYinCity = null;
                                    if (city.length() > 0) {
                                        whetherCity = city.substring(0, city.length() - 1);
                                        PinYinCity = PinYinUtils.getPinYin(whetherCity);
                                    }
                                    if (PinYinCity != null) {
                                        getDateFromnatWhether(PinYinCity);
                                    }
                                }
                            }else {
                                getDateFromnatWhether("hangzhou");
                            }
                        }else {
                            getDateFromnatWhether("hangzhou");
                        }
                    }
                });
    }

    /**
     * 联网获取天气
     *
     * @param pinYinCity
     */
    private void getDateFromnatWhether(String pinYinCity) {

        HttpUtils.getInstance().getWeather(getApplicationContext(), pinYinCity, new RequestCallback<WhetherEntity>() {
            @Override
            public void onError(Request request, Exception e) {
                Log.e("TAG", "查询天气失败----" + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(WhetherEntity response) {
                Log.e("TAG", "查询天气---" + response.getData().getCond());
                if (response.getCode() == 1000) {
                    int pm25 = response.getData().getPm25();
                    String cond = response.getData().getCond();
                    int whether = response.getData().getTmp();

                    iv_first_kongqi_temperature.setText(whether + "℃");
                    setWhetherQuality(pm25);
                    setWhetherIcon(cond);

                    CacheUtils.putInt(HomePager.this, PM_25, pm25);
                    CacheUtils.putString(HomePager.this, COND, cond);
                    CacheUtils.putString(HomePager.this, WHETHER, whether + "");
                } else {

                }
            }
        });
    }

    private void setWhetherIcon(String cond) {
        if (cond.equals("晴")) {
            iv_first_kongqi_icon.setBackgroundResource(R.drawable.icon_weather_sunny);
        } else if (cond.equals("多云")) {
            iv_first_kongqi_icon.setBackgroundResource(R.drawable.icon_weather_cloudy);
        } else if (cond.equals("阴")) {
            iv_first_kongqi_icon.setBackgroundResource(R.drawable.icon_weather_overcast);
        } else if (cond.equals("阵雨")) {
            iv_first_kongqi_icon.setBackgroundResource(R.drawable.icon_weather_rain_occasional);
        } else if (cond.equals("雷阵雨")) {
            iv_first_kongqi_icon.setBackgroundResource(R.drawable.icon_weather_rain_thundery);
        } else if (cond.equals("小雨")) {
            iv_first_kongqi_icon.setBackgroundResource(R.drawable.icon_weather_rain_light);
        } else if (cond.equals("中雨")) {
            iv_first_kongqi_icon.setBackgroundResource(R.drawable.icon_weather_rain);
        } else if (cond.equals("大雨")) {
            iv_first_kongqi_icon.setBackgroundResource(R.drawable.icon_weather_rain_heavy);
        } else if (cond.equals("小雪")) {
            iv_first_kongqi_icon.setBackgroundResource(R.drawable.icon_weather_snow_light);
        } else if (cond.equals("中雪")) {
            iv_first_kongqi_icon.setBackgroundResource(R.drawable.icon_weather_snow);
        } else if (cond.equals("大雪")) {
            iv_first_kongqi_icon.setBackgroundResource(R.drawable.icon_weather_snow_heavy);
        }
    }

    private void setWhetherQuality(int pm25) {
        if (pm25 > 0 && pm25 <= 35) {
            tv_youlaignzhongcha.setText(getString(R.string.actor));
        } else if (pm25 > 35 && pm25 <= 75) {
            tv_youlaignzhongcha.setText(getString(R.string.mild_contamination));
        } else if (pm25 > 75 && pm25 <= 115) {
            tv_youlaignzhongcha.setText(getString(R.string.slightly_polluted));
        } else if (pm25 > 115 && pm25 <= 160) {
            tv_youlaignzhongcha.setText(getString(R.string.Medium_pollution));
        } else {
            tv_youlaignzhongcha.setText(getString(R.string.Hazardous));
            tv_youlaignzhongcha.setBackgroundResource(R.drawable.home_whether_bg);
        }
    }

    /**
     * 天气质量转换
     */


    private void setonClickListener() {
        sliding_menu_switch.setOnClickListener(new MyOnclickListener());
        sliding_switch_band.setOnClickListener(new MyOnclickListener());
        walk_click.setOnClickListener(new MyOnclickListener());
        sleep_click.setOnClickListener(new MyOnclickListener());

//        Rl_step1.setOnClickListener(new MyOnclickListener());
//        Rl_sleep1.setOnClickListener(new MyOnclickListener());

        nlv_achieve.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(HomePager.this, AchieveActivity.class);

                startActivity(intent);
            }
        });
//        getSlidingMenu().setOnClosedListener(new SlidingMenu.OnClosedListener() {
//            @Override
//            public void onClosed() {
//                LifeBandBLEUtil.getInstance().DataSync(2, 0);
//            }
//        });

    }

    private void initView() {

        String data = CacheUtils.getString(HomePager.this, Constants.CESHI);

        if (data == null && data == "") {
            getDateWheel();

        } else {
            dateCount = data.split(",");
            if (!dateCount[dateCount.length - 1].equals(DateHelper.getSystemDateString("yyyy-MM-dd"))) {
                getDateWheel();
            }

        }

        intDateWheel();
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 得到屏幕长、宽
     */
    private void initWindowScreen() {
        WindowManager wm = this.getWindowManager();

        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();

//        Log.e("TAG", "width" + width + "---------------------" + "height" + height);
    }

    /**
     * 初始化侧滑菜单
     */
    private void initSlidingMenu() {
        //设置左侧页面
        setBehindContentView(R.layout.left_fragment_menu);

        //得到SlidingMenu
        SlidingMenu slidingMenu = getSlidingMenu();

        //设置模式一共有三种模式： 设置主页+左侧页面；主页页面+右侧页面；主页页面 +左侧页面+右侧页面
        slidingMenu.setMode(SlidingMenu.TOUCHMODE_NONE);

        //设置右侧菜单
        slidingMenu.setSecondaryMenu(R.layout.right_fragment_menu);

        //在屏幕滑动的模式：全屏，边缘，不可用滑动
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);

        //设置主页面占宽度
        slidingMenu.setBehindOffset(width / 6);

        //设置滑动时菜单的是否淡入淡出
        slidingMenu.setFadeEnabled(true);

        //设置淡入淡出的比例
        slidingMenu.setFadeDegree(0.4f);

        //设置滑动时拖拽效果
        slidingMenu.setBehindScrollScale(0.6f);

        secondaryMenuShowing = slidingMenu.isSecondaryMenuShowing();
        menuShowing = slidingMenu.isMenuShowing();

        slidingMenu.setOnOpenListener(new SlidingMenu.OnOpenListener() {
            @Override
            public void onOpen() {
                sliding_shadow.setVisibility(View.VISIBLE);
            }
        });

        slidingMenu.setSecondaryOnOpenListner(new SlidingMenu.OnOpenListener() {
            @Override
            public void onOpen() {
                sliding_shadow.setVisibility(View.VISIBLE);
            }
        });

        slidingMenu.setOnCloseListener(new SlidingMenu.OnCloseListener() {
            @Override
            public void onClose() {
                sliding_shadow.setVisibility(View.GONE);
            }
        });
    }
    /**
     * 初始化Fragment
     */

    private void initStartFragment(){
        if(leftMenuFragment==null){
            leftMenuFragment=new LeftMenuFragment();
        }
        if(rightMenuFtragment==null){
            rightMenuFtragment=new RightMenuFtragment();
        }
    }
    /**
     * 加载Fragment
     */
    private void initFragment() {
        FragmentManager fm = getSupportFragmentManager();//得到FragmentManager
        FragmentTransaction ft = fm.beginTransaction();//开启事务
        ft.replace(R.id.fl_leftmenu, leftMenuFragment, LEFTMENU_TAG);
        ft.replace(R.id.fl_rightmenu, rightMenuFtragment, RIGHT_TAG);

        //事务的提交
        ft.commit();
    }

    @Override
    public void onValueChange(View wheel, float value) {

    }

    private int achieveSize = 0;

    @Override
    public void onValueChangeEnd(View wheel, float value) throws DbException {
        String WheelReturnData = "";

        if (dateCount.length > 0) {

            if (value == dateCount.length - 1) {
                tv_first_jibu_hour_list.setText(kk + "");

                tv_first_sleep_hour_sleep.setText(sleeptime / 60 + "");

                tv_first_sleep_minute_sleep.setText(sleeptime % 60 + "");

            } else {

                WheelReturnData = (String) dateCount[(int) value];

                Log.e("TAG", value + "value-=-=");
                Log.e("TAG", dateCount.length + "dateCount-=-=");


                List<GetStepCountBean.DataBean.StepsDataBean> DaySteps = CommonApplication.dm.selector(GetStepCountBean.DataBean.StepsDataBean.class).where("date", "LIKE", WheelReturnData + "%").findAll();

                List<GetSleepentity.DataBean.SleepDataBean> daySleep = CommonApplication.dm.selector(GetSleepentity.DataBean.SleepDataBean.class).where("dateTime", "LIKE", WheelReturnData + "%").findAll();

                if (DaySteps.size() != 0 && daySleep.size() != 0) {
                    tv_first_jibu_hour_list.setText(DaySteps.get(0).getTotal_steps() + "");

                    tv_first_sleep_hour_sleep.setText(daySleep.get(0).getTotal_time() / 60 + "");

                    tv_first_sleep_minute_sleep.setText(daySleep.get(0).getTotal_time() % 60 + "");
                } else {
                    tv_first_jibu_hour_list.setText(0 + "");

                    tv_first_sleep_hour_sleep.setText(0 + "");

                    tv_first_sleep_minute_sleep.setText(0 + "");
                }

            }

            Log.e("TAG", "首页资讯----" + WheelReturnData + "-----" + WheelReturnData);

            HttpUtils.getInstance().getHomepagerDate(WheelReturnData, WheelReturnData, HomePager.this, new RequestCallback<HomeBean>() {
                @Override
                public void onError(Request request, Exception e) {
//                    CustomToast.showToast(HomePager.this, e.toString());
                    try {
                        JSONObject jsonObj = new JSONObject(e.getLocalizedMessage());
                        int code = jsonObj.optInt("code");
                        if (HttpUtils.CODE_ACCOUNT_NOT_LOGIN == code) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(HomePager.this);
                            dialog.setCancelable(false);
                            dialog.setMessage(R.string.not_login);
                            dialog.setPositiveButton(getString(R.string.ALERT_DLG_BTN_OK), new android.content.DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    arg0.dismiss();
                                    startActivity(new Intent(HomePager.this, LoginActivity.class));
                                    finish();
                                }
                            });
                            dialog.setNeutralButton(getString(R.string.ALERT_DLG_BTN_CANCEL), new android.content.DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    arg0.dismiss();
                                }
                            });
                            dialog.show();
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                }

                @Override
                public void onResponse(HomeBean response) {
                    Log.e("TAG", "首页信息------" + response);

                    if (response.getData().getDailies_data().size() != 0) {
                        if (response.getData().getDailies_data().get(0).getAwards().size() != 0 && response.getData().getDailies_data() != null) {
                            achieveSize = response.getData().getDailies_data().get(0).getAwards().get(0);
                        }

                        achieveList = new ArrayList<>();

                        Log.e("TAG", achieveSize + "成就");

                        if (achieveSize != 0) {
                            achieveList.add(achieveSize);
                        }

                        setAchiceveList();

                        achieveSize = 0;

                    }

                }
            });

           /* int achieveSize = unserialize.getAwards().size();

            achieveList = new ArrayList<>();

            if(achieveSize != 0){
                for(int i = 0 ; i < achieveSize ;i++){
                    achieveList.add(i);
                }
            }else{

            }

            //设置Achieve列表
            setAchiceveList();*/

            //getHomePagerListFromNet(WheelReturnData);

            //int k = 0;

/*            if (value == dateCount.length - 1) {
                List<GetStepCountBean.StepListBean> persons = CommonApplication.dm.selector(GetStepCountBean.StepListBean.class).where("dateTime", ">=", DateHelper.getInstance().getSystemDateString2() + " 00:00:00").and("dateTime", "<=", DateHelper.getInstance().getSystemDateString2() + " 23:59:59").findAll();
//                int stepCount = persons.get((int) value-1).getStepCount();
                for (int i = 0; i < persons.size(); i++) {
                    k += persons.get(i).getStepCount();
                }
                Log.e("TAG", k + "stepCount-=-=");

                tv_first_jibu_hour.setText(k + "");
                tv_first_jibu_hour_list.setText(k + "");
            }*/
        }
        //List<GetStepCountBean.StepListBean> persons = CommonApplication.dm.selector(GetStepCountBean.StepListBean.class).where("dateTime", ">=", data+" 00:00:00").and("dateTime", "<=", data+" 23:59:59").findAll();


    }


    class MyOnclickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v == sliding_menu_switch) {
                Log.e("TAG", "左侧--");
                getSlidingMenu().toggle();

            } else if (v == sliding_switch_band) {
                Log.e("TAG", "右侧--");
                getSlidingMenu().showSecondaryMenu();

            } else if (v == sleep_click) {
                startActivity(new Intent(HomePager.this, SleepActivity.class));
            } else if (v == walk_click) {
                startActivity(new Intent(HomePager.this, StepsActivity.class));
            } else if (v == Rl_step1) {
//                startActivity(new Intent(HomePager.this, StepsActivity.class));
            } else if (v == Rl_sleep1) {
//                startActivity(new Intent(HomePager.this, SleepActivity.class));
            }
        }
    }

    class MyPkAdapter extends BaseAdapter {

        @Override
        public int getCount() {
//            Log.e("TAG", "________________________________" + pkList.size());
            return pkList.size();
        }

        @Override
        public Object getItem(int position) {
            return pkList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolderPk viewHolderPk = null;
            if (convertView == null) {
                convertView = View.inflate(HomePager.this, R.layout.home_pager_pk, null);

                viewHolderPk = new ViewHolderPk();

                viewHolderPk.tv_first_pk_beizhu = (TextView) convertView.findViewById(R.id.tv_first_pk_beizhu);
                viewHolderPk.tv_first_pk_result = (TextView) convertView.findViewById(R.id.tv_first_pk_result);

                convertView.setTag(viewHolderPk);
            } else {
                viewHolderPk = (ViewHolderPk) convertView.getTag();
            }

            viewHolderPk.tv_first_pk_beizhu.setText(pkList.get(position).getFriendName());
            if (pkList.get(position).getStatus() == 1) {
                viewHolderPk.tv_first_pk_result.setText(R.string.going_pk);
            } else if (pkList.get(position).getStatus() == 2) {
                viewHolderPk.tv_first_pk_result.setText(R.string.pk_finish);
            }


            return convertView;
        }
    }

    static class ViewHolderPk {
        private TextView tv_first_pk_beizhu;
        private TextView tv_first_pk_result;
    }

    class MyHealthAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return healthList.size();
        }

        @Override
        public Object getItem(int position) {
            return healthList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolderHealth viewHolderHealth = null;
            if (convertView == null) {
                convertView = View.inflate(HomePager.this, R.layout.home_pager_health, null);

                viewHolderHealth = new ViewHolderHealth();

                viewHolderHealth.tv_first_health_beizhu = (TextView) convertView.findViewById(R.id.tv_first_health_beizhu);
                viewHolderHealth.iv_first_health_shou = (ImageView) convertView.findViewById(R.id.iv_first_health_shou);

                convertView.setTag(viewHolderHealth);
            } else {
                viewHolderHealth = (ViewHolderHealth) convertView.getTag();
            }

            viewHolderHealth.tv_first_health_beizhu.setText(healthList.get(position).getNickName());
            Picasso.with(HomePager.this)
                    .load(healthList.get(position).getIconUrl())
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.head_boy_normal)
                    .error(R.drawable.head_boy_normal)
                    .into(viewHolderHealth.iv_first_health_shou);
            return convertView;
        }
    }

    static class ViewHolderHealth {
        private TextView tv_first_health_beizhu;
        private ImageView iv_first_health_shou;
    }

    class MyAchieveAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return achieveList.size();
        }

        @Override
        public Object getItem(int position) {
            return achieveList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolderAchieve viewHolderAchieve = null;
            if (convertView == null) {
                convertView = View.inflate(HomePager.this, R.layout.home_pager_success, null);

                viewHolderAchieve = new ViewHolderAchieve();

                viewHolderAchieve.tv_first_success_result = (TextView) convertView.findViewById(R.id.tv_first_success_result);

                convertView.setTag(viewHolderAchieve);
            } else {
                viewHolderAchieve = (ViewHolderAchieve) convertView.getTag();
            }

            viewHolderAchieve.tv_first_success_result.setText(SelectAchieve(achieveList.get(0)));

            return convertView;
        }
    }

    static class ViewHolderAchieve {
        private TextView tv_first_success_result;
    }

    public String SelectAchieve(int position) {
        if (position == 1) {
            return 5000 + "";
        } else if (position == 2) {
            return 20000 + "";

        } else if (position == 3) {
            return 100000 + "";

        } else if (position == 4) {
            return 500000 + "";

        } else if (position == 5) {
            return 1000000 + "";

        } else if (position == 6) {
            return 5000000 + "";

        } else {
            return "";
        }
    }


    private int count = 0;

    @Override
    public void onButtonClicked() {
        count = count + 1;
        Log.e("Tag", "onButtonClicked--------------------点击手环啦" + count);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                count = 0;
            }
        }, 4000);


        Intent intent = new Intent(net.sourceforge.opencamera.MainActivity.TAKE_PHOTO_ACTION);
        sendBroadcast(intent);


        if (count == 4) {

//            CacheUtils.getLoctionAddress(this);

            HttpUtils.getInstance().sendEmergencyMsg(getApplicationContext(), CacheUtils.getString(this, Constants.LONGITUDE), CacheUtils.getString(this, Constants.LATITUDE), new RequestCallback<Object>() {
                @Override
                public void onError(Request request, Exception e) {
                    //Toast.makeText(HomePager.this, "紧急消息发送失败", Toast.LENGTH_SHORT).show();
                    Log.e("Tag", "onButtonClicked--------------------点击手环啦------" + e);
                }

                @Override
                public void onResponse(Object response) {
                    //Toast.makeText(HomePager.this, "紧急消息发送成功", Toast.LENGTH_SHORT).show();
                    LifeBandBLEUtil.getInstance().DoVibrate(1, 100, 200, 0);
                    Log.e("Tag", "onButtonClicked--------------------点击手环啦------" + response);
                }
            });
        }
    }

    @Override
    public void onButtonLongPressed(byte[] data) {
        Log.e("TAG", data + "=--=");
        LifeBandBLEUtil.getInstance().SelectIdleMode();

    }

    @Override
    public void onBatteryData(int status) {
        CacheUtils.putInt(HomePager.this, Constants.STATUS, status);

        Log.e("Tag", "status-------------------" + status);
        //CacheUtils.putInt(HomePager.this,Constants.STATUS,status);
        Intent intent = new Intent(Constants.COM_CAVYTECH_WEAR2_SERVICE_STATUSRECEIVER);
        intent.putExtra("status", status);

//        Log.e("TAg", "Homepager__________________onBatteryData--------------------" + status);
        LocalBroadcastManager.getInstance(HomePager.this).sendBroadcast(intent);
    }

    @Override
    public void onWarningData() {
//        Log.e("Tag", "onWarningData");
    }

    @Override
    public void onSystemData(LifeBandBLE.CavyBandSystemData data) {
        int fwVersion = data.fwVersion;
        int hwVersion = data.hwVersion;

        Log.e("Tag", "fwVersion-------------------" + fwVersion);
        Log.e("Tag", "hwVersion-------------------" + hwVersion);


        CacheUtils.putInt(HomePager.this, Constants.HWVERSION, hwVersion);
        CacheUtils.putInt(HomePager.this, Constants.FWVERSION, fwVersion);

//        Log.e("TAg", "homepager__________________fwVersion--------------------" + fwVersion);

        Intent intent = new Intent(Constants.DATA_FWVERSION);
        intent.putExtra("fwVersion", fwVersion);
        intent.putExtra("hwVersion", hwVersion);
        LocalBroadcastManager.getInstance(HomePager.this).sendBroadcast(intent);

        int funcEnableStatus = data.funcEnableStatus;
        Log.e("TAG", funcEnableStatus + "-=-=-=-=-=-=-");

        if (funcEnableStatus != 126) {
            for (int i = 2; i <= 6; i++) {
                LifeBandBLEUtil.getInstance().ConfigSetup(i, 1);
                Log.e("TAG", "设置Config-=-=-=-=-=-=-");
            }
        }
        judgeFWVersion();
    }

    @Override
    public void onDataSync(HashMap<Integer, PedometerData> TodayData, HashMap<Integer, PedometerData> YesterdayData) {
        Log.e("TAG", "onDataSync---");

        //时间是从00:00:00 开始 所以看到的10:40其实是10:50 不影响大局
        if (YesterdayData != null) {
            for (int i = 0; i < 144; i++) {
                PedometerData data = YesterdayData.get(i + 1);
                String date = dateHelper.timeExchangeData2(data.Time, -1 + "");//得到每个数据对应的时间  date,data.Tilts,data.Steps,data.Time
                Log.e("TAG","date"+date);
                BandSleepStepBean bs = new BandSleepStepBean();
                bs.setData(date);
                bs.setTilts(data.Tilts);
                bs.setSteps(data.Steps);
                bs.setTime(data.Time);
                try {
                    CommonApplication.dm.saveOrUpdate(bs);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        }

        for (int i = 0; i < TodayData.size(); i++) {
            PedometerData data = TodayData.get(i + 1);
            if (data.SearchDay == PedometerData.Today) {
                String date = dateHelper.timeExchangeData2(data.Time, 0 + "");//得到每个数据对应的时间
                Log.e("TAG","date"+date);
                BandSleepStepBean bs = new BandSleepStepBean();
                bs.setData(date);
                bs.setTilts(data.Tilts);
                bs.setSteps(data.Steps);
                bs.setTime(data.Time);
                try {
                    CommonApplication.dm.saveOrUpdate(bs);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        }

        posttonet();

        int stepcountday = SleepCount.stepcountday();

        String string = CacheUtils.getString(HomePager.this, Constants.CANREFRESH);
        String[] split = string.split(",");
        if (split[0].equals(DateHelper.getDateString())) {
            if ("".equals(split[split.length - 1])) {//当日还没有计算，直接计算
                SleepRetrun initsleep = SleepCount.initsleep();
                sleep = initsleep.getSleeptime();
                CacheUtils.putString(HomePager.this, Constants.CANREFRESH, DateHelper.getDateString() + "," + sleep);
                CacheUtils.putString(HomePager.this, Constants.SLEEPSHOW, String.valueOf(sleep));
                Log.e("TAG", "1---");
            } else {//当日已经计算，判断
                if (canrefresh()) {//实时刷新时间段
                    SleepRetrun initsleep = SleepCount.initsleep();
                    sleep = initsleep.getSleeptime();
                    CacheUtils.putString(HomePager.this, Constants.CANREFRESH, DateHelper.getDateString() + "," + sleep);
                    CacheUtils.putString(HomePager.this, Constants.SLEEPSHOW, String.valueOf(sleep));
                    Log.e("TAG", "2---");
                } else {
                    sleep=Integer.parseInt(CacheUtils.getString(HomePager.this, Constants.SLEEPSHOW));
                    Log.e("TAG", "刷新过一次，并且不是刷新时间段");
                }
            }
        } else {//不是当日，直接计算
            SleepRetrun initsleep = SleepCount.initsleep();
            sleep = initsleep.getSleeptime();
            CacheUtils.putString(HomePager.this, Constants.CANREFRESH, DateHelper.getDateString() + "," + sleep);
            CacheUtils.putString(HomePager.this, Constants.SLEEPSHOW, String.valueOf(sleep));
            Log.e("TAG", "3---");
        }
        Message m = new Message();
        m.arg1 = stepcountday;
        m.arg2 = sleep;//睡眠最后结果
        handler.sendMessage(m);
    }


    /**
     * 计步睡眠上报
     * <p/>
     * 根据sp中存入的上一次成功上报的最后一条数据   上报数据给后台
     */
    private void posttonet() {

        String post = CacheUtils.getString(HomePager.this, "post");
        Log.e("TAG", "判断增量全量---" + post);
        if (!post.equals("")) {
            try {
                Log.e("TAG", "增量上传");
                List<BandSleepStepBean> date = CommonApplication.dm.selector(BandSleepStepBean.class).where("date", ">", post).findAll();
                all = date;
            } catch (DbException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Log.e("TAG", "全量上传");
                all = CommonApplication.dm.findAll(BandSleepStepBean.class);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

        if (all != null) {
            if (all.size() >= 1440) {
                for (int i = 0; i < all.size() - 1440; i++) {
                    all.remove(i);
                }
            }
            HttpUtils.getInstance().setStepCount(all, HomePager.this, new RequestCallback<Object>() {
                @Override
                public void onError(Request request, Exception e) {
                }

                @Override
                public void onResponse(Object response) {
                    Log.e("TAG", "手环计步上传--" + response.toString());
                    if (all.size() != 0) {
                        String data = all.get(all.size() - 1).getData();
                        CacheUtils.putString(HomePager.this, "post", data);
                        Log.e("TAG", "lalalasp===" + data);
                    }

                    /**
                     * 再拉取一次数据
                     */
                    if (all != null && all.size() != 0) {
                        HttpUtils.getInstance().getSleepInfo(all.get(0).getData(), dateCount[dateCount.length - 1], HomePager.this, new RequestCallback<GetSleepentity>() {
                            @Override
                            public void onError(Request request, Exception e) {
                            }

                            @Override
                            public void onResponse(GetSleepentity response) {
                                Log.e("TAG", "再拉取一次数据----");
                                List<GetSleepentity.DataBean.SleepDataBean> list = response.getData().getSleep_data();
                                for (int i = 0; i < list.size(); i++) {
                                    GetSleepentity.DataBean.SleepDataBean sb = new GetSleepentity.DataBean.SleepDataBean();
                                    sb.setDate(list.get(i).getDate());
                                    sb.setTotal_time(list.get(i).getTotal_time());
                                    sb.setDeep_time(list.get(i).getDeep_time());
                                    try {
                                        CommonApplication.dm.saveOrUpdate(sb);
                                    } catch (DbException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });

                        HttpUtils.getInstance().getStepData(all.get(0).getData(), dateCount[dateCount.length - 1], HomePager.this, new RequestCallback<GetStepCountBean>() {
                            @Override
                            public void onError(Request request, Exception e) {

                            }

                            @Override
                            public void onResponse(GetStepCountBean response) {
                                Log.e("TAG", "再拉取一次数据----");
                                List<GetStepCountBean.DataBean.StepsDataBean> stepsdata = response.getData().getSteps_data();

                                if (stepsdata != null) {
                                    /**
                                     * 按小时存放的数据
                                     */
                                    for (int i = 0; i < stepsdata.size(); i++) {
                                        for (int j = 0; j < 24; j++) {
                                            GetStepCountBean.DataBean.StepsDataBean.HoursBean sb = new GetStepCountBean.DataBean.StepsDataBean.HoursBean();
                                            sb.setTime(stepsdata.get(i).getDate() + "-" + j);
                                            sb.setSteps(stepsdata.get(i).getHours().get(j).getSteps());
                                            try {
                                                CommonApplication.dm.saveOrUpdate(sb);
                                            } catch (DbException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }
                                    /**
                                     * 按天存放的数据
                                     */
                                    for (int i = 0; i < stepsdata.size(); i++) {
                                        GetStepCountBean.DataBean.StepsDataBean sb = new GetStepCountBean.DataBean.StepsDataBean();
                                        sb.setDate(stepsdata.get(i).getDate());
                                        sb.setTotal_steps(stepsdata.get(i).getTotal_steps());
                                        sb.setTotal_time(stepsdata.get(i).getTotal_time());
                                        try {
                                            CommonApplication.dm.saveOrUpdate(sb);
                                        } catch (DbException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        });
                    }

                }
            });
        }

    }


    @Override
    public void onDeviceSignature(byte[] data) {
//        Log.d("TAG", "===========onDeviceSignature==============================\n" + data);

    }

    @Override
    public void BLEConnectionEvents(int eventCode, LifeBandBLE.CavyBandDevice device) {
        CacheUtils.putInt(HomePager.this, Constants.ISCONNECTIONBAND, eventCode);
        Intent intent = new Intent(Constants.ISCONNECTIONBAND);
        intent.putExtra("eventCode", eventCode);
        LocalBroadcastManager.getInstance(HomePager.this).sendBroadcast(intent);

//        Log.e("TAG","lalalala"+eventCode+"----");

        switch (eventCode) {
            case LifeBandBLE.STATE_CONNECTED:

                CacheUtils.saveMacAdress(HomePager.this, device.GetDevice().getAddress());

                LifeBandBLEUtil.getInstance().SelectOperation();

                LifeBandBLEUtil.getInstance().getBaseSysInfo();

                pull_to_refresh_listview.firstRefreshing();

//                LifeBandBLEUtil.getInstance().DataSync(1, 0);

                CacheUtils.putString(HomePager.this, Constants.MCONNECTEDMACADDRESS, device.GetDevice().getAddress());

                // mBLE_DeviceAdapter.notifyDataSetChanged();
                SetSystemTime();

                Log.e("TAG", "---homepager---手环连线--");


                break;
            case LifeBandBLE.STATE_CONNECTING:
                break;
            case LifeBandBLE.STATE_DISCONNECTED:
           /*     LifeBandBLEUtil.getInstance().Disconnect();

                CacheUtils.putString(HomePager.this, Constants.MCONNECTEDMACADDRESS, device.GetDevice().getAddress());

                reStartScan();
               */

                Log.e("TAG", "homepager--手环断线---");

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("device_serial", PhoneUtils.getUDID(HomePager.this));
                map.put("device_model", android.os.Build.MODEL + "-" + Build.VERSION.SDK_INT);
                map.put("band_mac", CacheUtils.getMacAdress(HomePager.this));
                map.put("user_id", CommonApplication.userID);
                map.put("longitude", CacheUtils.getString(HomePager.this, Constants.LONGITUDE));
                map.put("latitude", CacheUtils.getString(HomePager.this, Constants.LATITUDE));

                MobclickAgent.onEvent(HomePager.this, Constants.BAND_DISCONNECT, map);

                HttpUtils.getInstance().activities(HomePager.this, CacheUtils.getString(HomePager.this, Constants.LONGITUDE),
                        CacheUtils.getString(HomePager.this, Constants.LATITUDE), CacheUtils.getMacAdress(HomePager.this), "" + Build.VERSION.SDK_INT, "BAND_DISCONNECT", new RequestCallback<CommonEntity>() {

                            @Override
                            public void onError(Request request, Exception e) {
                                Log.e("TAG", "活动上传失败--BAND_DISCONNECT--" + e.getLocalizedMessage() + e.getMessage().toString());
                            }

                            @Override
                            public void onResponse(CommonEntity response) {
                                Log.e("TAG", "活动上传成功--BAND_DISCONNECT--" + response.getCode());
                            }
                        });

                break;
            default:
            case LifeBandBLE.STATE_FIND_NEW_BAND:
                //ScanDevicePreferTable.put(device.GetDevice().getName(), device.IsConnectionFirstPriority() ? 1 : 0);
                //mBLE_DeviceAdapter.add(device.GetDevice().getName(), device.GetDevice().getAddress());
                //mBLE_DeviceAdapter.notifyDataSetChanged();

                break;
        }

//        Log.d("ConnectionTest", "code: " + eventCode + ", DeviceName: " + device.GetDevice().getName());

    }

    //=====================================
    //      OAD Callback
    //=====================================
    //=====================================
    //      OAD Callback
    //=====================================
    public void onOADStatusChanged(int statusCode, float progressRate) {
        switch (statusCode) {
            default:
            case LifeBandBLE.OAD_END:
                Log.e("TAG", "OAD Finished!!");
                break;
            case LifeBandBLE.OAD_START:
                Log.e("TAG", "OAD Start!!");
                break;
            case LifeBandBLE.OAD_UPDATING:
                Log.e("TAG", "OAD Updating, progress rate: " + progressRate);
                break;
        }
    }

    // 定义一个变量，来标识是否退出
    private static boolean isExit = false;

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("device_serial", PhoneUtils.getUDID(HomePager.this));
            map.put("device_model", android.os.Build.MODEL + "-" + Build.VERSION.SDK_INT);
            map.put("band_mac", CacheUtils.getMacAdress(HomePager.this));
            map.put("user_id", CommonApplication.userID);
            map.put("longitude", CacheUtils.getString(HomePager.this, Constants.LONGITUDE));
            map.put("latitude", CacheUtils.getString(HomePager.this, Constants.LATITUDE));

            MobclickAgent.onEvent(HomePager.this, Constants.APP_QUIT, map);

            HttpUtils.getInstance().activities(getApplicationContext(), CacheUtils.getString(this, Constants.LONGITUDE),
                    CacheUtils.getString(this, Constants.LATITUDE), CacheUtils.getMacAdress(getApplicationContext()), "" + Build.VERSION.SDK_INT, "APP_QUIT", new RequestCallback<CommonEntity>() {

                        @Override
                        public void onError(Request request, Exception e) {
                            Log.e("TAG", "活动上传失败--APP_QUIT--" + e.getLocalizedMessage() + e.getMessage().toString());
                            finish();
                            MobclickAgent.onKillProcess(HomePager.this);
                            System.exit(0);
                        }

                        @Override
                        public void onResponse(CommonEntity response) {
                            Log.e("TAG", "活动上传成功--APP_QUIT--" + response.getCode());
                            finish();
                            MobclickAgent.onKillProcess(HomePager.this);
                            System.exit(0);
                        }
                    });


        }
    }

    private void reStartScan() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                LifeBandBLEUtil.getInstance().StartScanCavyBand();
                if (LifeBandBLEUtil.getInstance().getConnectionState() == LifeBandBLE.STATE_CONNECTING ||
                        LifeBandBLEUtil.getInstance().getConnectionState() == LifeBandBLE.STATE_CONNECTED
                        ) {
                    return;
                }
                LifeBandBLEUtil.getInstance().StartScanCavyBand();
                LifeBandBLEUtil.getInstance().getBaseSysInfo1();
                SetSystemTime();
                reStartScan();
                String macAdress = CacheUtils.getMacAdress(HomePager.this);

                if (null != macAdress && !macAdress.isEmpty() && !isConnectoinNewBand) {
                    LifeBandBLEUtil.getInstance().connect(macAdress);
                }
            }
        }, 5000);
    }

    public void SetSystemTime() {

        LifeBandBLEUtil.getInstance().SetSystemTime();
    }

    /**
     * 是否连接到手环
     */
    private void ifConnectionBand() {

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ISCONNECTIONBAND);//建议把它写一个公共的变量，这里方便阅读就不写了。
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (LifeBandBLEUtil.getInstance().getConnectionState() == LifeBandBLE.STATE_DISCONNECTED) {
                    LifeBandBLEUtil.getInstance().StartScanCavyBand();
                    /*if (mBluetoothAdapter.isEnabled() == false) {
                        Intent _EnableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(_EnableIntent, 1);
                        return;
                    }else {
                        reStartScan();
                    }*/
                    reStartScan();
                }

            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }

    /**
     * 来电提醒
     */
    class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE://空闲
                    break;
                case TelephonyManager.CALL_STATE_RINGING://来电
                    int minute = 0;
                    minute = CacheUtils.getInt(HomePager.this, Constants.PHONENOTICE) * 1000;
                    Log.e("TAG", minute + "-=-=-=-=-=-=-=");
                    if (CacheUtils.getBoolean(HomePager.this, Constants.PHONEISCHECKED)) {
                        callHandler.postDelayed(starcCallReminder, minute);
                    } else {
                        break;
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://摘机（正在通话中）
                    if (CacheUtils.getBoolean(HomePager.this, Constants.PHONEISCHECKED)) {
                        callHandler.removeCallbacks(starcCallReminder);//让还未震动的手环永远不震动
                        callHandler.postDelayed(stopCallReminder, 0);//让已经震动的手环停止震动
                    } else {
                        break;
                    }
                    break;
            }
        }
    }

    public void phoneListener() {
        //获取电话通讯服务
        TelephonyManager tpm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //创建一个监听对象，监听电话状态改变事件
        tpm.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);
    }

    public class BandConnectionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }


    /**
     * 修正睡眠算法
     * 6点之前 一直刷新
     * 6点之后 刷一次
     * <p/>
     * retrue :可以刷新
     */

    public boolean canrefresh() {
        String nowdate = DateHelper.getDateString();
        String yesterdate = DateHelper.getInstance().getStrDate("-1");
        String systemdate = DateHelper.getSystemDateString("yyyy-MM-dd HH:mm:ss");
        Log.e("TAG", "----今天---" + nowdate + "---昨天---" + yesterdate + "---系统时间---" + systemdate);
        /**
         * 当前>昨天24:00:00
         * 今天06:00:00>当前
         * false:后一个比较大
         * true: 前一个比较大
         *
         */
        boolean b1 = DateHelper.getInstance().compare_date(systemdate, yesterdate + " 24:00:00");
        boolean b2 = DateHelper.getInstance().compare_date(nowdate + " 06:00:00", systemdate);
        Log.e("TAG", "比较大小---" + b1 + "---" + b2);
        if (b1 && b2) {
            Log.e("TAG", "可以刷新----");
            return true;
        } else {
            Log.e("TAG", "刷新一次----");
            return false;
        }

    }

    /**
     * 判断固件版本是否最新
     */
    public void judgeFWVersion(){
        HttpUtils.getInstance().getGuJianVersion(this, new RequestCallback<CheckVersionBean>() {
            @Override
            public void onError(Request request, Exception e) {
                sliding_switch_band.setImageResource(R.drawable.icon_band);
            }
            @Override
            public void onResponse(final CheckVersionBean response) {

                int hwversion = CacheUtils.getInt(HomePager.this, Constants.HWVERSION);
                int fwversion = CacheUtils.getInt(HomePager.this, Constants.FWVERSION);

                String hw = hwversion + "";
                String fw = fwversion + "";

                String now = hw + "." + fw;
                boolean b = DateHelper.getInstance().CompareVersion(now, response.getData().getVersion());
                if (b) {
                    sliding_switch_band.setImageResource(R.drawable.icon_fw);
                    if(rightMenuFtragment!=null) {
                        rightMenuFtragment.getIv_fw().setVisibility(View.VISIBLE);
                    }
                } else {
                    sliding_switch_band.setImageResource(R.drawable.icon_band);
                    if(rightMenuFtragment!=null) {
                        rightMenuFtragment.getIv_fw().setVisibility(View.INVISIBLE);
                    }
                }

            }
        });
    }

}
