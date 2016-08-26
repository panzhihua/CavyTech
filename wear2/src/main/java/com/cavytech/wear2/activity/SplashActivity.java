package com.cavytech.wear2.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.cavytech.wear2.R;
import com.cavytech.wear2.util.PermissionsChecker;
import com.cavytech.wear2.util.UpdateManger;

import org.xutils.DbManager;

public class SplashActivity extends Activity {

    private UpdateManger mUpdateManger;
    public static double Longitude;//经度
    public static double Latitude;//纬度
    private static final int REQUEST_CODE = 0; // 请求码
    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
    };
    private PermissionsChecker mPermissionsChecker; // 权限检测器

    private DbManager.DaoConfig daoconfig;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);


    /*    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
            this.startActivityForResult(intent,REQUEST_CODE);
        }*/

        mPermissionsChecker = new PermissionsChecker(this);
            // 缺少权限时, 进入权限配置页面
            if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
                PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
            }


//        initdao();

//        getLoctionAddress();

        mUpdateManger = new UpdateManger(SplashActivity.this);

        mUpdateManger.checkVersion();

        /**
         * 检查版本更新
         */

//        CheckVerson.getInstance(SplashActivity.this).checkversion(SplashActivity.this);

    }

    @Override
    public void onPause() {
        super.onPause();

//        JPushInterface.onPause(this);
    }

    @Override
    public void onResume() {
        super.onResume();

//        JPushInterface.onResume(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            finish();
//            Helper.showToast("拒绝权限将导致部分功能无法使用");
            Toast.makeText(SplashActivity.this,"拒绝权限将导致部分功能无法使用",Toast.LENGTH_SHORT).show();
        }

        if (requestCode == REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(this)) {
                    //检查返回结果
                    Toast.makeText(SplashActivity.this, "WRITE_SETTINGS permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SplashActivity.this, "WRITE_SETTINGS permission not granted", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(SplashActivity.this, "不需要申请权限 ", Toast.LENGTH_SHORT).show();
            }
        }

/*        if(requestCode == REQUEST_CODE){
            if(resultCode == PermissionsActivity.PERMISSIONS_DENIED){

            }else {
                int permissionCheck = ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_CALENDAR);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    //检查返回结果
                    Toast.makeText(SplashActivity.this, "WRITE_SETTINGS permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SplashActivity.this, "WRITE_SETTINGS permission not granted", Toast.LENGTH_SHORT).show();
                }
            }

        }*/

    }

/*        private void initdao() {

        daoconfig = new DbManager.DaoConfig()
                .setDbName("CB_LIST.db")
//                .setDbDir(new File("/sdcard"))
                .setDbVersion(1);
        CommonApplication.dm = x.getDb(daoconfig);

    }*/

}
