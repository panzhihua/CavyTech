package com.cavytech.wear2.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cavytech.wear2.R;
import com.cavytech.wear2.activity.AccountInfoActivity;
import com.cavytech.wear2.activity.HelpFeedBackActivity;
import com.cavytech.wear2.activity.OtherActivity;
import com.cavytech.wear2.activity.PkAtateActivity;
import com.cavytech.wear2.activity.TargetActivity;
import com.cavytech.wear2.base.BaseFragment;
import com.cavytech.wear2.entity.PklistBean;
import com.cavytech.wear2.entity.UserEntity;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.RequestCallback;
import com.cavytech.wear2.slidingmenu.AboutActivity;
import com.cavytech.wear2.slidingmenu.FriendActivity;
import com.cavytech.wear2.slidingmenu.PkActivity;
import com.cavytech.wear2.util.CacheUtils;
import com.cavytech.wear2.util.Constants;
import com.cavytech.wear2.util.GlideCircleTransform;
import com.cavytech.wear2.util.SerializeUtils;
import com.squareup.okhttp.Request;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 作者：李彬
 * 邮箱：bin.li@tunshu .com
 */

public class LeftMenuFragment extends BaseFragment {
    @ViewInject(R.id.ll_target)
    private LinearLayout ll_target;

    @ViewInject(R.id.ll_information)
    private LinearLayout ll_information;

    @ViewInject(R.id.ll_sliding_menu_friend)
    private LinearLayout ll_sliding_menu_friend;

    @ViewInject(R.id.ll_pk)
    private LinearLayout ll_pk;

    @ViewInject(R.id.ll_about)
    private LinearLayout ll_about;

    @ViewInject(R.id.ll_feedback)
    private LinearLayout ll_feedback;

    @ViewInject(R.id.ll_realative)
    private LinearLayout ll_realative;

    @ViewInject(R.id.ll_sliding_memn)
    private LinearLayout ll_sliding_memn;

    @ViewInject(R.id.iv_slidingmenu_head_icon)
    private ImageView iv_slidingmenu_head_icon;

    @ViewInject(R.id.user_accout)
    private TextView user_accout;

    @ViewInject(R.id.user_nickname)
    private TextView user_nickname;

    private UpdateReceiver updateReceiver = new UpdateReceiver();

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.left_menu, null);
        x.view().inject(this,view);

        setOnClickListener();

        user_accout.setText(CacheUtils.getString(getActivity(), Constants.USERNAME));

        updateUserInfo();

        getActivity().registerReceiver(updateReceiver, new IntentFilter(Constants.UPDATE_USERINFO_RECEIVER));

        return view;
    }

    @Override
    public void onDestroy() {

        getActivity().unregisterReceiver(updateReceiver);
        super.onDestroy();
    }

    @Override
    public void initData() {
        super.initData();
    }

    private class UpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUserInfo();
        }
    }

    private void updateUserInfo(){
        UserEntity.ProfileEntity userInfo = (UserEntity.ProfileEntity)SerializeUtils.unserialize(Constants.SERIALIZE_USERINFO);

//        userInfo.getSex();
        if(null != userInfo){

            /**
             * 更改
             */
            if(userInfo.getAvatar() != null && !userInfo.getAvatar().isEmpty() ){
                Glide.with(LeftMenuFragment.this)
                        .load(userInfo.getAvatar())
                        .transform(new GlideCircleTransform(mActivity))
                        .placeholder(R.drawable.head)
                        .error(R.drawable.head)
                        .into(iv_slidingmenu_head_icon);
            }

            user_nickname.setText(userInfo.getNickname());
        }
        //user_nickname.setText(userInfo.getNickname());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }
    /**
     * 侧滑点击事件
     */
    private void setOnClickListener() {
        ll_target.setOnClickListener(new MySlidingMenuOnClickListener());
        ll_information.setOnClickListener(new MySlidingMenuOnClickListener());
        ll_sliding_menu_friend.setOnClickListener(new MySlidingMenuOnClickListener());
        ll_pk.setOnClickListener(new MySlidingMenuOnClickListener());
        ll_about.setOnClickListener(new MySlidingMenuOnClickListener());
        ll_feedback.setOnClickListener(new MySlidingMenuOnClickListener());
        ll_realative.setOnClickListener(new MySlidingMenuOnClickListener());

        iv_slidingmenu_head_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AccountInfoActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    class MySlidingMenuOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            if(v == ll_target){//目标

                Intent intent = new Intent(mActivity, TargetActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_ISEDIT,true);
                startActivity(intent);

            }else if( v == ll_information){

                startActivity(new Intent(getActivity(),PublicUserInfoActivity.class));

            }else if( v == ll_sliding_menu_friend){

                startActivity(new Intent(getActivity(),FriendActivity.class));

             }else if( v == ll_pk){

//                Toast.makeText(getActivity(),"这里判断啦---",Toast.LENGTH_SHORT).show();

                HttpUtils.getInstance().getPkList(new RequestCallback<PklistBean>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Log.e("TAG","Pk------++++onError");
                    }

                    @Override
                    public void onResponse(PklistBean pkbean) {

//                        waitList.addAll(pkbean.getWaitList());
//                        goingList.addAll(pkbean.getDueList());
//                        successList.addAll(pkbean.getFinishList());

                        if(pkbean.getWaitList().size()==0&&pkbean.getDueList().size()==0&&pkbean.getFinishList().size()==0){
                            Log.e("TAG","进入1---");
                            startActivity(new Intent(getActivity(), PkActivity.class));
                        }else {
                            Log.e("TAG","进入2---");
                            startActivity(new Intent(getActivity(),PkAtateActivity.class));
                        }
                    }
                });

//                startActivity(new Intent(getActivity(),PkAtateActivity.class));

            }else if( v == ll_about){//关于

                startActivity(new Intent(getActivity(),AboutActivity.class));

            }else if( v == ll_feedback){//帮助与反馈

                startActivity(new Intent(getActivity(),HelpFeedBackActivity.class));

            }else if( v == ll_realative){//体感应用
                startActivity(new Intent(getActivity(),OtherActivity.class));

            }
        }
    }
}
