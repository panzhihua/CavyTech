package com.cavytech.wear2.slidingmenu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cavytech.wear2.R;
import com.cavytech.wear2.activity.FunctionActivity;
import com.cavytech.wear2.activity.LoginActivity;
import com.cavytech.wear2.activity.ProgressbarAcitvity;
import com.cavytech.wear2.activity.WebActivity;
import com.cavytech.wear2.base.AppCompatActivityEx;
import com.cavytech.wear2.entity.CheckVersionBean;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.RequestCallback;
import com.cavytech.wear2.util.Constants;
import com.cavytech.wear2.util.LanguageUtil;
import com.cavytech.widget.CustomDialog;
import com.squareup.okhttp.Request;

import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by hf on 2016/5/9.
 */
public class AboutActivity extends AppCompatActivityEx {
    @ViewInject(R.id.title)
    private TextView title;

    @ViewInject(R.id.back)
    private ImageView back;

    @ViewInject(R.id.rl_about_check)
    private TextView rl_about_check;

    @ViewInject(R.id.textView9)
    private TextView textView9;

    @ViewInject(R.id.textView11)
    private TextView textView11;


    @ViewInject(R.id.xiyi)
    private TextView xiyi;


    @ViewInject(R.id.rl_about_web)
    private TextView rl_about_web;

    @ViewInject(R.id.function)
    private TextView function;


    private String downloadUrl;
    private int appsize;
    private int localVersion = 0;

    private Handler callHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        x.view().inject(this);

        title.setText(this.getString(R.string.about_tittle));
        initview();
        setToolBar();
        initlistener();
    }

    private void initview() {
        if(LanguageUtil.isZh(this)){
            xiyi.setVisibility(View.VISIBLE);
        }else{
            xiyi.setVisibility(View.GONE);
        }
    }

    private void initlistener() {

        try {
            localVersion = AboutActivity.this.getPackageManager().getPackageInfo(AboutActivity.this.getPackageName(), 0).versionCode;
            textView9.setText(AboutActivity.this.getPackageManager().getPackageInfo(AboutActivity.this.getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }

        textView11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textView11.setText(getString(R.string.detecting));

                HttpUtils.getInstance().getVersion(AboutActivity.this, new RequestCallback<CheckVersionBean>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        textView11.setText(getString(R.string.detect_completed));
                        try {
                            JSONObject jsonObj = new JSONObject(e.getLocalizedMessage());
                            int code = jsonObj.optInt("code");
                            if(HttpUtils.CODE_ACCOUNT_NOT_LOGIN==code){
                                AlertDialog.Builder dialog = new AlertDialog.Builder(AboutActivity.this);
                                dialog.setCancelable(false);
                                dialog.setMessage(getString(R.string.not_login));
                                dialog.setPositiveButton(getString(R.string.ALERT_DLG_BTN_OK), new android.content.DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        arg0.dismiss();
                                        startActivity(new Intent(AboutActivity.this,LoginActivity.class));
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
                        } finally {
                            callHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            textView11.setText(getString(R.string.check_for_updates));
                                        }
                                    });
                                }
                            }, 5000);
                        }
                    }

                    @Override
                    public void onResponse(final CheckVersionBean response) {
                        textView11.setText(getString(R.string.detect_completed));
                        try {
                            downloadUrl = response.getData().getUrl();
                            appsize = response.getData().getSize();
                            if (response.getData().getUrl().length() > 0 && localVersion < response.getData().getReversion()) {
                                CustomDialog.Builder builder = new CustomDialog.Builder(AboutActivity.this);
                                builder.setMessage(getString(R.string.current_version));
                                builder.setTitle(getString(R.string.install_new_app_version) + response.getData().getVersion());
                                builder.setMessage(response.getData().getDescription());
                                builder.setPositiveButton(getString(R.string.queding), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        //设置你的操作事项

                                        Intent intent = new Intent(AboutActivity.this, ProgressbarAcitvity.class);
                                        intent.putExtra(Constants.HOWSHOW, Constants.APP);
                                        intent.putExtra(Constants.APPURL, response.getData().getUrl());
                                        intent.putExtra(Constants.APPSIZE, appsize);
                                        AboutActivity.this.startActivity(intent);
                                    }
                                });

                                builder.setNegativeButton(getString(R.string.ALERT_DLG_BTN_CANCEL),
                                        new android.content.DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                builder.create().show();
                            }else {
                                Toast.makeText(AboutActivity.this,getString(R.string.current_firmware_is_the_latest_version),Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            callHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            textView11.setText(getString(R.string.check_for_updates));
                                        }
                                    });
                                }
                            }, 5000);
                        }
                    }
                });

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        rl_about_web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Uri uri = Uri.parse(dataModel.getData(Constant.CODE_CONTENT));
                Uri uri = Uri.parse("http://www.tunshu.com/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        xiyi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AboutActivity.this, WebActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_WEBURL, Constants.LICENSE_URL);
                intent.putExtra(Constants.INTENT_EXTRA_TITLE, getString(R.string.treaty));
                startActivity(intent);
            }
        });

        function.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutActivity.this,FunctionActivity.class));
//                finish();
            }
        });


    }

}
