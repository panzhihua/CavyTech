package com.tunshu.fragment;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.tunshu.R;
import com.tunshu.util.Constants;
import com.tunshu.view.activity.AboutActivity;
import com.tunshu.view.activity.DownloadManagerActivity;
import com.tunshu.view.activity.FeedbackActivity;
import com.tunshu.view.activity.LoginActivity;
import com.tunshu.view.activity.MyGameActivity;
import com.tunshu.view.activity.NoticeActivity;
import com.tunshu.view.activity.SettingActivity;
import com.tunshu.view.activity.UserInfoActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomePageBehindFragment extends CommonFragment {
    private SimpleDraweeView menuPhoto;
    private TextView menuName;
    private TextView menuDownload;
    private TextView menuGame;
    private TextView menuNotice;
    private TextView menuHelp;
    private TextView menuFeedback;
    private TextView menuSetting;
    private TextView menuAbout;
    private UserInfoReceiver userInfoReceiver = new UserInfoReceiver();


    public HomePageBehindFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_page_behind, container, false);
        assignViews(rootView);
        return rootView;
    }

    private void assignViews(View rootView) {
        menuPhoto = (SimpleDraweeView) rootView.findViewById(R.id.menu_photo);
        menuName = (TextView) rootView.findViewById(R.id.menu_name);
        menuDownload = (TextView) rootView.findViewById(R.id.menu_download);
        menuGame = (TextView) rootView.findViewById(R.id.menu_game);
        menuNotice = (TextView) rootView.findViewById(R.id.menu_notice);
        menuFeedback = (TextView) rootView.findViewById(R.id.menu_feedback);
        menuSetting = (TextView) rootView.findViewById(R.id.menu_setting);
        menuAbout = (TextView) rootView.findViewById(R.id.menu_about);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUserInfo();
        addListener();
        getActivity().registerReceiver(userInfoReceiver, new IntentFilter(Constants.USER_INFO_REFRESH));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(userInfoReceiver);
    }
    private void setUserInfo() {
        menuPhoto.setImageURI(Uri.parse(getCoreApplication().getPreferenceConfig().getString(Constants.USER_ICON, "")));
        menuName.setText(getCoreApplication().getPreferenceConfig().getString(Constants.NICKNAME, getString(R.string.not_login)));
    }

    private class UserInfoReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            setUserInfo();
        }
    }

    private void addListener(){
        menuAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
            }
        });
        menuSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });
        menuFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FeedbackActivity.class);
                startActivity(intent);
            }
        });

        menuNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NoticeActivity.class);
                startActivity(intent);
            }
        });
        menuPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCoreApplication().getPreferenceConfig().getString(Constants.TOKEN, "").length() > 0) {
                    Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        menuDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DownloadManagerActivity.class);
                startActivity(intent);
            }
        });
        menuGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyGameActivity.class);
                startActivity(intent);
            }
        });
    }


}
