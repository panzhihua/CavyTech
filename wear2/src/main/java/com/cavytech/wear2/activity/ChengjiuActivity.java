package com.cavytech.wear2.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.basecore.widget.CustomToast;
import com.cavytech.wear2.R;
import com.cavytech.wear2.adapter.AchievementAdapter;
import com.cavytech.wear2.base.FullyGridLayoutManager;
import com.cavytech.wear2.entity.UserEntity;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.RequestCallback;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by hf on 2016/5/25.
 * 好友信息---成就
 */
public class ChengjiuActivity extends AppCompatActivity {
    private FullyGridLayoutManager mLayoutManager;

    private AchievementAdapter mAdapter;

    @ViewInject(R.id.achievement_value)
    private TextView achievement_value;

    @ViewInject(R.id.achievement_recyclerView)
    private RecyclerView recyclerView_achievement;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chengjiu);
        x.view().inject(this);

        initRecyclerView();

        getdatafromnet();

    }

    private void getdatafromnet() {

        HttpUtils.getInstance().getUserInfo(ChengjiuActivity.this,new RequestCallback<UserEntity>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        CustomToast.showToast(ChengjiuActivity.this, e.toString());
                        try {
                            JSONObject jsonObj = new JSONObject(e.getLocalizedMessage());
                            int code = jsonObj.optInt("code");
                            if(HttpUtils.CODE_ACCOUNT_NOT_LOGIN==code){
                                AlertDialog.Builder dialog = new AlertDialog.Builder(ChengjiuActivity.this);
                                dialog.setCancelable(false);
                                dialog.setMessage(R.string.not_login);
                                dialog.setPositiveButton(getString(R.string.ALERT_DLG_BTN_OK), new android.content.DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        arg0.dismiss();
                                        startActivity(new Intent(ChengjiuActivity.this,LoginActivity.class));
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
                    public void onResponse(UserEntity response) {
                        if (response.getCode() == HttpUtils.newSUCCESS) {

                            /**
                             * 更改
                             */
                            //setItemView(response.getAchievementType(),response.getStepNum()+"");
                            //Log.e("TAG","成就----"+response.getAchievementType());

                        } else {
                            CustomToast.showToast(ChengjiuActivity.this, response.getMsg());
                        }
                    }
                });
    }

    /**
     * 更改
     */
    /*void setItemView(final String type, final String steps) {

        final float density = getResources().getDisplayMetrics().density;

        final ViewTreeObserver vto = recyclerView_achievement.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                recyclerView_achievement.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int width = recyclerView_achievement.getMeasuredWidth() / (int) density;
                int spanCount = 3;  // 列数
                int itemWidth = (width - 16 * 2) / spanCount;

                recyclerView_achievement.addItemDecoration(new GridSpacingItemDecoration(ChengjiuActivity.this, spanCount, itemWidth));

                //创建适配器，并且设置
                // test
                List<UserEntity.AchievementEntity> testAch = new ArrayList<>();

                for(int i=0;i<6;i++){
                    UserEntity user = new UserEntity();
                    UserEntity.AchievementEntity ach = user.new AchievementEntity();
                    testAch.add(ach);
                }
                testAch.get(0).setAchievementText("累计5000步");
                testAch.get(1).setAchievementText("20000步");
                testAch.get(2).setAchievementText("100000步");
                testAch.get(3).setAchievementText("500000步");
                testAch.get(4).setAchievementText("1000000步");
                testAch.get(5).setAchievementText("5000000步");

                if(steps!=""){
                    achievement_value.setText("累计"+steps+"步");
                }else {
                    achievement_value.setText("累计0步");
                }

                switch (type){
                    case "0":
//                        testAch.get(0).setAchievementText("点亮0");
//                        testAch.get(0).setIconUrl("null");
                        break;
                    case "1":
                        testAch.get(0).setIconUrl("null");
                        break;
                    case "2":
                        testAch.get(0).setIconUrl("null");
                        testAch.get(1).setIconUrl("null");
                        break;
                    case "4":
                        testAch.get(0).setIconUrl("null");
                        testAch.get(1).setIconUrl("null");
                        testAch.get(3).setIconUrl("null");
                        break;
                    case "5":
                        testAch.get(0).setIconUrl("null");
                        testAch.get(1).setIconUrl("null");
                        testAch.get(2).setIconUrl("null");
                        testAch.get(3).setIconUrl("null");
                        break;
                    case "6":
                        testAch.get(0).setIconUrl("null");
                        testAch.get(1).setIconUrl("null");
                        testAch.get(2).setIconUrl("null");
                        testAch.get(3).setIconUrl("null");
                        testAch.get(4).setIconUrl("null");
                        break;
                    default:
                        break;
                }
                mAdapter = new AchievementAdapter(testAch, itemWidth * (int) density);
                recyclerView_achievement.setAdapter(mAdapter);
            }
        });
    }*/

    private void initRecyclerView() {

        //创建线性布局
        mLayoutManager = new FullyGridLayoutManager(this,3);

        //垂直方向
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);

        //给RecyclerView设置布局管理器
        recyclerView_achievement.setLayoutManager(mLayoutManager);

        recyclerView_achievement.setHasFixedSize(true);


    }
}
