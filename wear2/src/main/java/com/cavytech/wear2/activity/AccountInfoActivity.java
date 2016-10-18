package com.cavytech.wear2.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.basecore.widget.CustomToast;
import com.bumptech.glide.Glide;
import com.cavytech.wear2.R;
import com.cavytech.wear2.application.CommonApplication;
import com.cavytech.wear2.entity.BandSleepStepBean;
import com.cavytech.wear2.entity.CommonEntity;
import com.cavytech.wear2.entity.DBfriendBean;
import com.cavytech.wear2.entity.GetSleepentity;
import com.cavytech.wear2.entity.GetStepCountBean;
import com.cavytech.wear2.entity.IconBean;
import com.cavytech.wear2.entity.UserEntity;
import com.cavytech.wear2.entity.UserInfoKVEntity;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.RequestCallback;
import com.cavytech.wear2.util.CacheUtils;
import com.cavytech.wear2.util.Constants;
import com.cavytech.wear2.util.FileUtils;
import com.cavytech.wear2.util.GlideCircleTransform;
import com.cavytech.wear2.util.LanguageUtil;
import com.cavytech.wear2.util.LifeBandBLEUtil;
import com.cavytech.wear2.util.PhoneUtils;
import com.cavytech.wear2.util.SerializeUtils;
import com.squareup.okhttp.Request;
import com.taig.pmc.PopupMenuCompat;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by longjining on 16/3/30.
 */
public class AccountInfoActivity extends CommonActivity {

    /**
     * 上下文
     */
    private Activity mActivity;
/*    private AchievementAdapter mAdapter;
    private UserInfoAdapter mUserInfoAdapter;
    private FullyGridLayoutManager mLayoutManager;
    private FullyLinearLayoutManager mUserInfoLayoutManager;*/

    @ViewInject(R.id.achievement_recyclerView)
    private RecyclerView recyclerView_achievement;

//    @ViewInject(R.id.usernfo_recyclerView)
//    private RecyclerView recyclerView_usernfo;

    @ViewInject(R.id.medal_1)
    private ImageView medal_1;

    @ViewInject(R.id.medal_2)
    private ImageView medal_2;

    @ViewInject(R.id.medal_3)
    private ImageView medal_3;

    @ViewInject(R.id.medal_4)
    private ImageView medal_4;

    @ViewInject(R.id.medal_5)
    private ImageView medal_5;

    @ViewInject(R.id.medal_6)
    private ImageView medal_6;

    @ViewInject(R.id.back)
    private ImageView back;

    @ViewInject(R.id.title)
    private TextView title;

    @ViewInject(R.id.tv_sex)
    private TextView tv_sex;

    @ViewInject(R.id.tv_hight)
    private TextView tv_hight;

    @ViewInject(R.id.tv_weight)
    private TextView tv_weight;

    @ViewInject(R.id.tv_birthday)
    private TextView tv_birthday;

    @ViewInject(R.id.tv_address)
    private TextView tv_address;

    @ViewInject(R.id.tv_steps)
    private TextView tv_steps;

    @ViewInject(R.id.achievement_value)
    private TextView achievement_value;

    @ViewInject(R.id.avatar)
    private ImageView avatar;

    @ViewInject(R.id.nickname)
    private TextView nickname;

    @ViewInject(R.id.edit_nickname)
    private ImageView edit_nickname;

    @ViewInject(R.id.accout)
    private TextView accout;

    @ViewInject(R.id.rl_1)
    private RelativeLayout rl_1;

    @ViewInject(R.id.rl_2)
    private RelativeLayout rl_2;

    @ViewInject(R.id.rl_3)
    private RelativeLayout rl_3;

    @ViewInject(R.id.rl_4)
    private RelativeLayout rl_4;

    @ViewInject(R.id.rl_5)
    private RelativeLayout rl_5;

    private UserEntity.ProfileEntity userInfo = null;

    private UserEntity.ProfileEntity editUserInfo = null;

    private final int REQUESTCODE_NICKNAME = 1;
    private final int REQUESTCODE_ADDRESS = 2;

    ArrayList<UserInfoKVEntity> userInfoList = new ArrayList<>();

    private boolean isEditUserInfo = false;

    private final int ADREESMAXLEN = 10;

    @Event(value = R.id.exit,
            type = View.OnClickListener.class/*可选参数, 默认是View.OnClickListener.class*/)
    private void onExitClick(View view) {//退出账号

        HashMap<String,String> map = new HashMap<String,String>();
        map.put("device_serial", PhoneUtils.getUDID(AccountInfoActivity.this));
        map.put("device_model",android.os.Build.MODEL+"-"+ Build.VERSION.SDK_INT);
        map.put("band_mac",CacheUtils.getMacAdress(AccountInfoActivity.this));
        map.put("user_id",CommonApplication.userID);
        map.put("longitude",CacheUtils.getString(AccountInfoActivity.this, Constants.LONGITUDE));
        map.put("latitude",CacheUtils.getString(AccountInfoActivity.this, Constants.LATITUDE));
        MobclickAgent.onEvent(AccountInfoActivity.this, Constants.USER_LOGOUT,map);

        CacheUtils.putString(AccountInfoActivity.this, Constants.PASSWORD, "");
        CommonApplication.isLogin = false;

        LifeBandBLEUtil.getInstance().StopScanCavyBand();
        LifeBandBLEUtil.getInstance().Disconnect();
        LifeBandBLEUtil.getInstance().Close();

        if (CommonApplication.dm != null) {
            try {
                CommonApplication.dm.dropTable(DBfriendBean.class);
                CommonApplication.dm.dropTable(GetStepCountBean.DataBean.StepsDataBean.HoursBean.class);
                CommonApplication.dm.dropTable(GetStepCountBean.DataBean.StepsDataBean.class);
                CommonApplication.dm.dropTable(GetSleepentity.DataBean.SleepDataBean.class);
                CommonApplication.dm.dropTable(BandSleepStepBean.class);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        HttpUtils.getInstance().logout(getApplicationContext(), new RequestCallback<Object>() {
            @Override
            public void onError(Request request, Exception e) {
                Log.e("TAG","退出帐号失败---"+e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Object response) {
                Log.e("TAG","退出帐号成功---");
            }
        });

        Intent intent = new Intent(AccountInfoActivity.this, GuideActivity.class);
        startActivity(intent);
//        finish();
//        CacheUtils.clearData(AccountInfoActivity.this, "post");
//        FileUtils.deleteFile(Constants.SERIALIZE_USERINFO);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account_info);

        //  setToolBar();

        x.view().inject(this);

        mActivity = this;

        //initRecyclerView();

        //设置用户成就
        getUserInfo();

        userInfo = (UserEntity.ProfileEntity) SerializeUtils.unserialize(Constants.SERIALIZE_USERINFO);

        updateUserInfo();

        onListener();

        //initUserInfoRecyclerView();

        setStatusBar(R.color.com_titlebar2);

        title.setText(R.string.title_account);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isEditUserInfo) {
            userInfo = (UserEntity.ProfileEntity) SerializeUtils.unserialize(Constants.SERIALIZE_USERINFO);
            makeUserInfo();
            //mUserInfoAdapter.resetDatas(userInfoList);
        }
    }

    private void onListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccountInfoActivity.this, HomePager.class));
                finish();
            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopu(avatar);
            }
        });

        nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNickname();
            }
        });

        edit_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNickname();
            }
        });

        rl_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, SexActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_ISEDIT, true);
                isEditUserInfo = true;
                startActivity(intent);
            }
        });

        rl_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, HeightActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_ISEDIT, true);
                isEditUserInfo = true;
                startActivity(intent);
            }
        });

        rl_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, WeightActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_ISEDIT, true);
                isEditUserInfo = true;
                startActivity(intent);
            }
        });

        rl_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, BirthDayAcivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_ISEDIT, true);
                isEditUserInfo = true;
                startActivity(intent);
            }
        });

        rl_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEditUserInfo = false;
                editAddress();

            }
        });

    }

    private void editNickname() {
        Intent intent = new Intent();
        intent.putExtra(Constants.INTENT_EXTRA_HINT, getString(R.string.hint_input_nickname));
        intent.putExtra(Constants.INTENT_EXTRA_TITLE, getString(R.string.edit_nickname));
        intent.putExtra(Constants.INTENT_EXTRA_BTNTEXT, getString(R.string.ok));
        intent.setClass(AccountInfoActivity.this, VerficatonActivity.class);
        // 通过该方法可以获取后边的activity返回的数据（主要还是通过intent进行交互的）
        startActivityForResult(intent, REQUESTCODE_NICKNAME);
    }

    private void getUserInfo() {

        HttpUtils.getInstance().getUserInfo(AccountInfoActivity.this, new RequestCallback<UserEntity>() {

            @Override
            public void onError(Request request, Exception e) {
                try {
                    JSONObject jsonObj = new JSONObject(e.getLocalizedMessage());
                    int code = jsonObj.optInt("code");
                    if(HttpUtils.CODE_ACCOUNT_NOT_LOGIN==code) {
                        CustomToast.showToast(mActivity, R.string.not_login);
                    }else{
                        CustomToast.showToast(mActivity, e.toString());
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                } catch (Exception e2){
                    e2.printStackTrace();
                }

                try {
                    JSONObject jsonObj = new JSONObject(e.getLocalizedMessage());
                    int code = jsonObj.optInt("code");
                    if(HttpUtils.CODE_ACCOUNT_NOT_LOGIN==code){
                        AlertDialog.Builder dialog = new AlertDialog.Builder(AccountInfoActivity.this);
                        dialog.setCancelable(false);
                        dialog.setMessage(R.string.not_login);
                        dialog.setPositiveButton(getString(R.string.ALERT_DLG_BTN_OK), new android.content.DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.dismiss();
                                startActivity(new Intent(AccountInfoActivity.this,LoginActivity.class));
                                finish();
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
            public void onResponse(UserEntity response) {

                Log.e("TAG", response.getProfile().getNickname()+"用户信息--getNickname-");

                boolean isNeedUpdate = false;

                if (userInfo != null) {
                    if (!userInfo.equals(response)) {
                        isNeedUpdate = true;
                    } else {
                        return;
                    }
                }

                SerializeUtils.serialize(response.getProfile(), Constants.SERIALIZE_USERINFO);
                userInfo = response.getProfile();

                makeUserInfo();
                if(userInfo.getNickname()!=null) {
                    if (!userInfo.getNickname().equals("")) {
                        nickname.setText(userInfo.getNickname());
                    }
                }
                accout.setText(CacheUtils.getString(AccountInfoActivity.this, Constants.USERNAME));

                if (isNeedUpdate) {
                    updateUserInfo();
                    //mUserInfoAdapter.resetDatas(userInfoList);
                    sendUpdate();
                }
                if (LanguageUtil.isZh(AccountInfoActivity.this)) {
                    tv_steps.setText(AccountInfoActivity.this.getString(R.string.total)+response.getProfile().getSteps()+ AccountInfoActivity.this.getString(R.string.step));
                }else{
                    tv_steps.setText(AccountInfoActivity.this.getString(R.string.total)+ " "+AccountInfoActivity.this.getString(R.string.step)+"："+response.getProfile().getSteps());
                }

                /**
                 * 更改
                 */
                //setItemView(response.getAwards().size());
                if (response.getProfile().getAwards() != null) {
                    setAchieveView(response.getProfile().getAwards().size());

                    Log.e("TAG","成就----"+response.getProfile().getAwards().size());

                }
            }
        });
//                new OkHttpClientManager.ResultCallback<UserEntity>() {
//                    @Override
//                    public void onError(Request request, Exception e) {
//                        CustomToast.showToast(mActivity, e.toString());
//                    }
//
//                    @Override
//                    public void onResponse(UserEntity response) {
//
//
//                        if (response.getCode().equals(HttpUtils.SUCCESS)) {
//
//                            boolean isNeedUpdate = false;
//
//                            if (userInfo != null) {
//                                if (!userInfo.equals(response)) {
//                                    isNeedUpdate = true;
//                                } else {
//                                    return;
//                                }
//                            }
//
//                            SerializeUtils.serialize(response, Constants.SERIALIZE_USERINFO);
//                            userInfo = response;
//
//                            if (isNeedUpdate) {
//                                updateUserInfo();
//                                mUserInfoAdapter.resetDatas(userInfoList);
//                            }
//                        } else {
//                            CustomToast.showToast(mActivity, response.getMsg());
//                        }
//                    }
//                });
    }

    /**
     * 设置成就图标
     *
     * @param size
     */
    private void setAchieveView(int size) {
        if (size == 1) {
            medal_1.setBackgroundResource(R.drawable.medal_1_lighted);
        } else if (size == 2) {
            medal_1.setBackgroundResource(R.drawable.medal_1_lighted);
            medal_2.setBackgroundResource(R.drawable.medal_2_lighted);
        } else if (size == 3) {
            medal_1.setBackgroundResource(R.drawable.medal_1_lighted);
            medal_2.setBackgroundResource(R.drawable.medal_2_lighted);
            medal_3.setBackgroundResource(R.drawable.medal_3_lighted);
        } else if (size == 4) {
            medal_1.setBackgroundResource(R.drawable.medal_1_lighted);
            medal_2.setBackgroundResource(R.drawable.medal_2_lighted);
            medal_3.setBackgroundResource(R.drawable.medal_3_lighted);
            medal_4.setBackgroundResource(R.drawable.medal_4_lighted);
        } else if (size == 5) {
            medal_1.setBackgroundResource(R.drawable.medal_1_lighted);
            medal_2.setBackgroundResource(R.drawable.medal_2_lighted);
            medal_3.setBackgroundResource(R.drawable.medal_3_lighted);
            medal_4.setBackgroundResource(R.drawable.medal_4_lighted);
            medal_5.setBackgroundResource(R.drawable.medal_5_lighted);
        } else if (size == 6) {
            medal_1.setBackgroundResource(R.drawable.medal_1_lighted);
            medal_2.setBackgroundResource(R.drawable.medal_2_lighted);
            medal_3.setBackgroundResource(R.drawable.medal_3_lighted);
            medal_4.setBackgroundResource(R.drawable.medal_4_lighted);
            medal_5.setBackgroundResource(R.drawable.medal_5_lighted);
            medal_6.setBackgroundResource(R.drawable.medal_6_lighted);
        }
    }

    private void updateUserInfo() {

        if (userInfo == null) {
            return;
        }
        if (userInfo.getAvatar() != null && !userInfo.getAvatar().isEmpty()) {
            Glide.with(AccountInfoActivity.this)
                    .load(userInfo.getAvatar())
                    .transform(new GlideCircleTransform(this))
                    .placeholder(R.drawable.head)
                    .error(R.drawable.head)
                    .into(avatar);
        }
        if(userInfo.getNickname()!=null) {
            if (!userInfo.getNickname().equals("")) {
                nickname.setText(userInfo.getNickname());
            }
        }

        accout.setText(CacheUtils.getString(AccountInfoActivity.this, Constants.USERNAME));

        makeUserInfo();
    }

    private void makeUserInfo() {
        /*userInfoList.clear();

        userInfoList.add(new UserInfoKVEntity(getString(R.string.sex), sexCodeToName(userInfo.getSex())));
        userInfoList.add(new UserInfoKVEntity(getString(R.string.hight), userInfo.getHeight() + "cm"));
        userInfoList.add(new UserInfoKVEntity(getString(R.string.weight), userInfo.getWeight() + "kg"));
        userInfoList.add(new UserInfoKVEntity(getString(R.string.birhtday), userInfo.getBirthday() + ""));
        userInfoList.add(new UserInfoKVEntity(getString(R.string.address), userInfo.getAddress()));*/

        tv_sex.setText(sexCodeToName(userInfo.getSex()));
        tv_address.setText(userInfo.getAddress());
//        tv_birthday.setText(userInfo.getBirthday().substring(0,10));
        tv_birthday.setText(userInfo.getBirthday() + "");
        tv_hight.setText((int)(userInfo.getHeight()) + "cm");
        tv_weight.setText(userInfo.getWeight() + "kg");
    }

    private String sexCodeToName(int code) {

        if (code == Constants.SEX_BOY) {
            return getString(R.string.sex_boy);
        } else {
            return getString(R.string.sex_girl);
        }
    }

    /*private void initUserInfoRecyclerView() {

        //创建线性布局
        mUserInfoLayoutManager = new FullyLinearLayoutManager(this);

        //垂直方向
        mUserInfoLayoutManager.setOrientation(OrientationHelper.VERTICAL);

        //给RecyclerView设置布局管理器
        recyclerView_usernfo.setLayoutManager(mUserInfoLayoutManager);

        mUserInfoAdapter = new UserInfoAdapter(userInfoList);
        recyclerView_usernfo.setAdapter(mUserInfoAdapter);

        mUserInfoAdapter.setOnItemClickListener(new UserinfoOnItemClickListener());
    }
*/
    /***
     * 想要获取后边的activity中返回的内容就要重写该方法 该方法将会在后边的activity调用setResult方法后被触发。
     * requestCode 请求码
     * 即我们在startActivityForResult中指定的那个1，我们可以通过这个标识判断，后边activity返回的是不是我们请求的东西
     * resultCode 返回的结果码    后边的activity给我们的一个标识状态信息的值
     * data 返回的intent对象
     */
    @Override

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                if (requestCode == REQUESTCODE_NICKNAME || requestCode == REQUESTCODE_ADDRESS) {
                    Bundle bundle = data.getExtras();
                    String editText = bundle.getString(Constants.INTENT_EXTRA_EDITTEXT);
                    editUserInfo = userInfo;

                    if (requestCode == REQUESTCODE_NICKNAME) {

                        editUserInfo.setNickname(editText);
                    }
                    if (requestCode == REQUESTCODE_ADDRESS) {
                        editUserInfo.setAddress(editText);
                    }

                    setUserInfo();
                } else {
                    super.onActivityResult(requestCode, resultCode, data);
                }

                break;

            default:
                break;
        }
    }

    private void setUserInfo() {

        HttpUtils.getInstance().setUserInfo(editUserInfo, AccountInfoActivity.this, new RequestCallback<CommonEntity>() {
            @Override
            public void onError(Request request, Exception e) {
                Toast.makeText(AccountInfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObj = new JSONObject(e.getLocalizedMessage());
                    int code = jsonObj.optInt("code");
                    if(HttpUtils.CODE_ACCOUNT_NOT_LOGIN==code){
                        AlertDialog.Builder dialog = new AlertDialog.Builder(AccountInfoActivity.this);
                        dialog.setCancelable(false);
                        dialog.setMessage(R.string.not_login);
                        dialog.setPositiveButton(getString(R.string.ALERT_DLG_BTN_OK), new android.content.DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.dismiss();
                                startActivity(new Intent(AccountInfoActivity.this,LoginActivity.class));
                                finish();
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
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void onResponse(CommonEntity response) {

                if (response.getCode() == HttpUtils.newSUCCESS) {
                    userInfo = editUserInfo;

                    updateUserInfo();
                    //mUserInfoAdapter.resetDatas(userInfoList);
                    CustomToast.showToast(AccountInfoActivity.this, getString(R.string.edit_success));

                    SerializeUtils.serialize(editUserInfo, Constants.SERIALIZE_USERINFO);
                    sendUpdate();
                } else {
                    CustomToast.showToast(AccountInfoActivity.this, response.getMsg());
                }
            }
        });
    }

    private void sendUpdate() {
        Intent intent = new Intent(Constants.UPDATE_USERINFO_RECEIVER);
        sendBroadcast(intent);
    }

    /*class UserinfoOnItemClickListener implements BaseRecyclerAdapter.OnItemClickListener {

        @Override
        public void onItemClick(int position, Object data) {

            Intent intent = null;
            switch (position ) {
                case 0:
                    intent = new Intent(mActivity, SexActivity.class);
                    break;
                case 1:
                    intent = new Intent(mActivity, HeightActivity.class);
                    break;
                case 2:
                    intent = new Intent(mActivity, WeightActivity.class);
                    break;
                case 3:
                    intent = new Intent(mActivity, BirthDayAcivity.class);
                    break;
                case 4:
                    editAddress();
                    return;

                default:
                    break;
            }
            if (position != 4) {

                intent.putExtra(Constants.INTENT_EXTRA_ISEDIT, true);
                isEditUserInfo = true;
            } else {
                isEditUserInfo = false;
            }

            startActivity(intent);
        }
    }*/

    private void editAddress() {
        Intent intent = new Intent();
        intent.putExtra(Constants.INTENT_EXTRA_HINT, getString(R.string.input_address));
        intent.putExtra(Constants.INTENT_EXTRA_TITLE, getString(R.string.edit_address));
        intent.putExtra(Constants.INTENT_EXTRA_BTNTEXT, getString(R.string.ok));
        intent.putExtra(Constants.INTENT_EXTRA_MAXLEN, ADREESMAXLEN);

        intent.setClass(AccountInfoActivity.this, VerficatonActivity.class);
        // 通过该方法可以获取后边的activity返回的数据（主要还是通过intent进行交互的）
        startActivityForResult(intent, REQUESTCODE_ADDRESS);
    }

    private void initRecyclerView() {

        //创建线性布局
        //mLayoutManager = new FullyGridLayoutManager(this, 3);

        //垂直方向
        //mLayoutManager.setOrientation(OrientationHelper.VERTICAL);

        /*//给RecyclerView设置布局管理器
        recyclerView_achievement.setLayoutManager(mLayoutManager);

        recyclerView_achievement.setHasFixedSize(true);*/


    }

    /*void setItemView(final int type ){

        final float density = getResources().getDisplayMetrics().density;

        final ViewTreeObserver vto = recyclerView_achievement.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                recyclerView_achievement.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int width = recyclerView_achievement.getMeasuredWidth() / (int) density;
                int spanCount = 3;  // 列数
                int itemWidth = (width - 16 * 2) / spanCount;

                recyclerView_achievement.addItemDecoration(new GridSpacingItemDecoration(AccountInfoActivity.this, spanCount, itemWidth));

                //创建适配器，并且设置
                // test
                *//*List<UserEntity.ProfileEntity.AwaresEntity> testAch = new ArrayList<>();

                for(int i=0;i<6;i++){
                    UserEntity user = new UserEntity();
                    UserEntity.ProfileEntity.AwaresEntity ach = new UserEntity.ProfileEntity.AwaresEntity();
                    testAch.add(ach);
                }
                testAch.get(0).setNumber("累计5000步");
                testAch.get(1).setNumber("20000步");
                testAch.get(2).setNumber("100000步");
                testAch.get(3).setNumber("500000步");
                testAch.get(4).setNumber("1000000步");
                testAch.get(5).setNumber("5000000步");
                if(steps!=""){
                    achievement_value.setText("累计"+steps+"步");
                }else {
                    achievement_value.setText("累计0步");
                }*//*

                switch (type){
                    case 1:
                        testAch.get(0).setIconUrl("null");
                        break;
                    case 2:
                        testAch.get(0).setIconUrl("null");
                        testAch.get(1).setIconUrl("null");
                        break;
                    case 3:
                        testAch.get(0).setIconUrl("null");
                        testAch.get(1).setIconUrl("null");
                        testAch.get(3).setIconUrl("null");
                        break;
                    case 4:
                        testAch.get(0).setIconUrl("null");
                        testAch.get(1).setIconUrl("null");
                        testAch.get(2).setIconUrl("null");
                        testAch.get(3).setIconUrl("null");
                        break;
                    case 5:
                        testAch.get(0).setIconUrl("null");
                        testAch.get(1).setIconUrl("null");
                        testAch.get(2).setIconUrl("null");
                        testAch.get(3).setIconUrl("null");
                        testAch.get(4).setIconUrl("null");
                        break;
                    case 6:
                        testAch.get(0).setIconUrl("null");
                        testAch.get(1).setIconUrl("null");
                        testAch.get(2).setIconUrl("null");
                        testAch.get(3).setIconUrl("null");
                        testAch.get(4).setIconUrl("null");
                        break;
                    default:
                        break;
                }

                for (int i = 0; i < 6; i++) {
                    UserEntity user = new UserEntity();
                    UserEntity.AchievementEntity ach = user.new AchievementEntity();
//                    if(i<=Integer.valueOf(type)){
                    if(i<=1){
                        ach.setAchievementText("呵呵" + i);
                        ach.setIconUrl("null");
                    }else {
                        ach.setAchievementText("哈哈哈" + i);
                        ach.setIconUrl("null");
                    }
                    testAch.add(ach);
                }

                mAdapter = new AchievementAdapter(testAch, itemWidth * (int) density);
                recyclerView_achievement.setAdapter(mAdapter);
            }
        });
    }*/

    private void showPopu(final View view) {
        PopupMenuCompat menu = PopupMenuCompat.newInstance(AccountInfoActivity.this, view);
        menu.inflate(R.menu.menu_choice_picture);
        menu.setOnMenuItemClickListener(new PopupMenuCompat.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals(getString(R.string.take_a_picture))) {
                    doCamera(true, 200, 200);
                } else if (item.getTitle().equals(getString(R.string.album))) {
                    doGallery(true, 200, 200);
                }
                return false;
            }
        });
        menu.show();
    }

    @Override
    public void onReturnImageUri(String imageUri) {
        super.onReturnImageUri(imageUri);
        if (imageUri != null) {
            Log.e("pipa","方法返回的---"+imageUri);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(imageUri));
                uploadFile(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadFile(Bitmap bitmap) {

/*        HttpUtils.getInstance().uploadFile(CacheUtils.getString(AccountInfoActivity.this, Constants.USERID), imageUri,
                new OkHttpClientManager.ResultCallback<UpLoadFileEntity>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        Toast.makeText(AccountInfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(UpLoadFileEntity response) {

                        if (response.getCode() == HttpUtils.newSUCCESS) {

                            userInfo.setAvatar(response.getIconUrl());

                            Picasso.with(AccountInfoActivity.this)
                                    .load(userInfo.getAvatar())
                                    .transform(new CircleTransform())
                                    .placeholder(R.drawable.head_boy_normal)
                                    .error(R.drawable.head_boy_normal)
                                    .into(avatar);
                            SerializeUtils.serialize(userInfo, Constants.SERIALIZE_USERINFO);

                            sendUpdate();
                        } else {
                            Toast.makeText(AccountInfoActivity.this, response.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/


        String s = FileUtils.bitmaptoString(bitmap);

        HttpUtils.getInstance().uploadFile(getApplicationContext(), s, new RequestCallback<IconBean>() {
            @Override
            public void onError(Request request, Exception e) {
                try {
                    JSONObject jsonObj = new JSONObject(e.getLocalizedMessage());
                    int code = jsonObj.optInt("code");
                    if(HttpUtils.CODE_ACCOUNT_NOT_LOGIN==code){
                        AlertDialog.Builder dialog = new AlertDialog.Builder(AccountInfoActivity.this);
                        dialog.setCancelable(false);
                        dialog.setMessage(R.string.not_login);
                        dialog.setPositiveButton(getString(R.string.ALERT_DLG_BTN_OK), new android.content.DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.dismiss();
                                startActivity(new Intent(AccountInfoActivity.this,LoginActivity.class));
                                finish();
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
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }

            @Override
            public void onResponse(IconBean response) {

                if (response.getCode() == HttpUtils.newSUCCESS) {
                    userInfo.setAvatar(response.getData().getUrl());
                    try{
                        Thread.sleep(1000);//避免立刻读取网络图片
                    }catch(Exception e){
                        e.getMessage();
                    }
                    Glide.with(AccountInfoActivity.this)
                            .load(userInfo.getAvatar())
                            .transform(new GlideCircleTransform(AccountInfoActivity.this))
                            .placeholder(null)
                            .error(R.drawable.head)
                            .into(avatar);
                    SerializeUtils.serialize(userInfo, Constants.SERIALIZE_USERINFO);

                    sendUpdate();
                } else {
                    Toast.makeText(AccountInfoActivity.this, response.getMsg(), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

}
