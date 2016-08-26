package com.cavytech.wear2.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cavytech.wear2.R;
import com.cavytech.wear2.adapter.BaseRecyclerAdapter;
import com.cavytech.wear2.adapter.HelpAdapter;
import com.cavytech.wear2.base.AppCompatActivityEx;
import com.cavytech.wear2.base.FullyLinearLayoutManager;
import com.cavytech.wear2.entity.HelpEntity;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.RequestCallback;
import com.cavytech.wear2.util.Constants;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by longjining on 16/5/17.
 *
 * 帮助与反馈
 *
 *
 */
public class HelpFeedBackActivity extends AppCompatActivityEx {

    @ViewInject(R.id.help_recyclerView)
    private RecyclerView recyclerView_help;

    @ViewInject(R.id.back)
    private ImageView back;

    @ViewInject(R.id.title)
    private TextView title;

    @ViewInject(R.id.feedback)
    private TextView feedback;

//    @ViewInject(R.id.go_text)
//    private TextView go_text;


    HelpAdapter mHelpAdapter;

    private List<HelpEntity.DataBean> userInfoList = new ArrayList<HelpEntity.DataBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_help_feedback);
        x.view().inject(this);

        setToolBar();

        getData();

        onListener();

        initRecyclerView();

        setTitle();
    }

    private void setTitle(){
        title.setText(R.string.Help_and_feedback);
//        go_text.setVisibility(View.VISIBLE);
//        go_text.setText(R.string.feedback);
    }
    private void getData(){
        /**
         * 此处联网获取帮助列表
         */
        HttpUtils.getInstance().getHelpList(HelpFeedBackActivity.this,new RequestCallback<HelpEntity>() {
            @Override
            public void onError(Request request, Exception e) {
                try {
                    JSONObject jsonObj = new JSONObject(e.getLocalizedMessage());
                    int code = jsonObj.optInt("code");
                    if(HttpUtils.CODE_ACCOUNT_NOT_LOGIN==code){
                        AlertDialog.Builder dialog = new AlertDialog.Builder(HelpFeedBackActivity.this);
                        dialog.setCancelable(false);
                        dialog.setMessage(R.string.not_login);
                        dialog.setPositiveButton(getString(R.string.ALERT_DLG_BTN_OK), new android.content.DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.dismiss();
                                startActivity(new Intent(HelpFeedBackActivity.this,LoginActivity.class));
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
                }
            }
            @Override
            public void onResponse(HelpEntity response) {
//                if(response.isSuccess()){
                    userInfoList = response.getData();
                    mHelpAdapter.resetDatas(userInfoList);
//                }else{
//                    CustomToast.showToast(HelpFeedBackActivity.this, response.getMsg());
//                }
            }
        });
    }

    private void initRecyclerView() {
        //创建线性布局
        FullyLinearLayoutManager layoutManager = new FullyLinearLayoutManager(this);
        //垂直方向
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //给RecyclerView设置布局管理器
        recyclerView_help.setLayoutManager(layoutManager);
        mHelpAdapter = new HelpAdapter(userInfoList);
        Log.e("TAG","测试大小----"+userInfoList.size());
        recyclerView_help.setAdapter(mHelpAdapter);
        mHelpAdapter.setOnItemClickListener(new HelpItemOnItemClickListener());
    }
    private void onListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.e("TAG","意见反馈");
                startActivity(new Intent(HelpFeedBackActivity.this, FeedbackActivity.class));
            }
        });


//        go_text.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(HelpFeedBackActivity.this, FeedbackActivity.class));
//            }
//        });
    }
    class HelpItemOnItemClickListener implements BaseRecyclerAdapter.OnItemClickListener {

        @Override
        public void onItemClick(int position, Object data) {
            Intent intent = new Intent(HelpFeedBackActivity.this, WebActivity.class);
            intent.putExtra(Constants.INTENT_EXTRA_TITLE, getString(R.string.Help_and_feedback));
            intent.putExtra(Constants.INTENT_EXTRA_WEBURL, userInfoList.get(position).getUrl());
            startActivity(intent);
        }
    }
}
